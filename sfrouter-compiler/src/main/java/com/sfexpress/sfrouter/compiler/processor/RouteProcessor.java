package com.sfexpress.sfrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.sfexpress.sfrouter.annotation.AutoBind;
import com.sfexpress.sfrouter.annotation.Route;
import com.sfexpress.sfrouter.annotation.enums.RouteTypeEnum;
import com.sfexpress.sfrouter.annotation.models.RouteBaseData;
import com.sfexpress.sfrouter.compiler.util.LogUtil;
import com.sfexpress.sfrouter.compiler.util.StaticConsts;
import com.sfexpress.sfrouter.compiler.util.TypeUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sfexpress.sfrouter.compiler.util.StaticConsts.ACTIVITY;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.ANNOTATION_TYPE_AUTOBIND;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.ANNOTATION_TYPE_ROUTE;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.FRAGMENT;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.FRAGMENT_V4;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.IPROVIDER_GROUP;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.IROUTE_GROUP;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.ITROUTE_ROOT;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.KEY_MODULE_NAME;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.METHOD_LOAD_INTO;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.NAME_OF_GROUP;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.NAME_OF_PROVIDER;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.NAME_OF_ROOT;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.PACKAGE_OF_GENERATE_FILE;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.SEPARATOR;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.SERVICE;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 路由注解处理器
 * Created by sf-zhangpeng on 2018/3/10.
 * TODO DOC.
 */
@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOBIND})
public class RouteProcessor extends AbstractProcessor {
    private Map<String, Set<RouteBaseData>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    private Map<String, String> rootMap = new TreeMap<>();  // Map of root metas, used for generate class file in order.
    private Filer mFiler;       // File util, write class file into disk.
    private LogUtil logUtil;
    private Types types;
    private Elements elements;
    private TypeUtil typeUtils;
    private String moduleName = null;   // Module name, maybe its 'app' or others
    private TypeMirror iProvider = null;

    /**
     * Initializes the processor with the processing environment by
     * setting the {@code processingEnv} field to the value of the
     * {@code processingEnv} argument.  An {@code
     * IllegalStateException} will be thrown if this method is called
     * more than once on the same object.
     *
     * @param processingEnv environment to access facilities the tool framework
     *                      provides to the processor
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();                  // Generate class.
        types = processingEnv.getTypeUtils();            // Get type utils.
        elements = processingEnv.getElementUtils();      // Get class meta.

        typeUtils = new TypeUtil(types, elements);
        logUtil = new LogUtil(processingEnv.getMessager());   // Package the log utils.

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logUtil.i("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logUtil.e("These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }

        iProvider = elements.getTypeElement(StaticConsts.IPROVIDER).asType();

        logUtil.i(">>> RouteProcessor init. <<<");
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                logUtil.i(">>> Found routes, start... <<<");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                logUtil.e(e);
            }
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            // Perpare the type an so on.
            logUtil.i(">>> Found routes, size is " + routeElements.size() + " <<<");
            rootMap.clear();

            TypeMirror type_Activity = elements.getTypeElement(ACTIVITY).asType();
            TypeMirror type_Service = elements.getTypeElement(SERVICE).asType();
            TypeMirror fragmentTm = elements.getTypeElement(FRAGMENT).asType();
            TypeMirror fragmentTmV4 = elements.getTypeElement(FRAGMENT_V4).asType();

            // Interface of SFRouter
            TypeElement type_IRouteGroup = elements.getTypeElement(IROUTE_GROUP);
            TypeElement type_IProviderGroup = elements.getTypeElement(IPROVIDER_GROUP);
            ClassName routeBaseDataCn = ClassName.get(RouteBaseData.class);
            ClassName routeTypeCn = ClassName.get(RouteTypeEnum.class);

            /*
               Build input type, format as :

               ```Map<String, Class<? extends IRouteGroup>>```
             */
            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                    )
            );

            /*

              ```Map<String, RouteMeta>```
             */
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteBaseData.class)
            );

            /*
              Build input param name.
             */
            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "providers").build();  // Ps. its param type same as groupParamSpec!

            /*
              Build method : 'loadInto'
             */
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(rootParamSpec);

            //  Follow a sequence, find out metas of group first, generate java file, then statistics them as root.
            for (Element element : routeElements) {
                TypeMirror tm = element.asType();
                Route route = element.getAnnotation(Route.class);
                RouteBaseData routeBaseData = null;

                if (types.isSubtype(tm, type_Activity)) {                 // Activity
                    logUtil.i(">>> Found activity route: " + tm.toString() + " <<<");

                    // Get all fields annotation by @Autowired
                    Map<String, Integer> paramsType = new HashMap<>();
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(AutoBind.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            // It must be field, then it has annotation, but it not be provider.
                            AutoBind paramConfig = field.getAnnotation(AutoBind.class);
                            paramsType.put(StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() : paramConfig.name(), typeUtils.getTypeFromElement(field));
                        }
                    }
                    routeBaseData = new RouteBaseData(route, element, RouteTypeEnum.ACTIVITY, paramsType);
                } else if (types.isSubtype(tm, iProvider)) {         // IProvider
                    logUtil.i(">>> Found provider route: " + tm.toString() + " <<<");
                    routeBaseData = new RouteBaseData(route, element, RouteTypeEnum.PROVIDER, null);
                } else if (types.isSubtype(tm, type_Service)) {           // Service
                    logUtil.i(">>> Found service route: " + tm.toString() + " <<<");
                    routeBaseData = new RouteBaseData(route, element, RouteTypeEnum.parse(SERVICE), null);
                } else if (types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                    logUtil.i(">>> Found fragment route: " + tm.toString() + " <<<");
                    routeBaseData = new RouteBaseData(route, element, RouteTypeEnum.parse(FRAGMENT), null);
                } else {
                    throw new RuntimeException("ARouter::Compiler >>> Found unsupported class type, type = [" + types.toString() + "].");
                }

                categories(routeBaseData);
                // if (StringUtils.isEmpty(moduleName)) {   // Hasn't generate the module name.
                //     moduleName = ModuleUtils.generateModuleName(element, logger);
                // }
            }

            MethodSpec.Builder loadIntoMethodOfProviderBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(providerParamSpec);

            // Start generate java source, structure is divided into upper and lower levels, used for demand initialization.
            for (Map.Entry<String, Set<RouteBaseData>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();

                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(groupParamSpec);

                // Build group method body
                Set<RouteBaseData> groupData = entry.getValue();
                for (RouteBaseData routeMeta : groupData) {
                    switch (routeMeta.getType()) {
                        case PROVIDER:  // Need cache provider's super class
                            List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getRawType()).getInterfaces();
                            for (TypeMirror tm : interfaces) {
                                if (types.isSameType(tm, iProvider)) {   // Its implements iProvider interface himself.
                                    // This interface extend the IProvider, so it can be used for mark provider
                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            (routeMeta.getRawType()).toString(),
                                            routeBaseDataCn,
                                            routeTypeCn,
                                            ClassName.get((TypeElement) routeMeta.getRawType()),
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                } else if (types.isSubtype(tm, iProvider)) {
                                    // This interface extend the IProvider, so it can be used for mark provider
                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            tm.toString(),    // So stupid, will duplicate only save class name.
                                            routeBaseDataCn,
                                            routeTypeCn,
                                            ClassName.get((TypeElement) routeMeta.getRawType()),
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    // Make map body for paramsType
                    StringBuilder mapBodyBuilder = new StringBuilder();
                    Map<String, Integer> paramsType = routeMeta.getParamsType();
                    if (MapUtils.isNotEmpty(paramsType)) {
                        for (Map.Entry<String, Integer> types : paramsType.entrySet()) {
                            mapBodyBuilder.append("put(\"").append(types.getKey()).append("\", ").append(types.getValue()).append("); ");
                        }
                    }
                    String mapBody = mapBodyBuilder.toString();

                    loadIntoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + (StringUtils.isEmpty(mapBody) ? null : ("new java.util.HashMap<String, Integer>(){{" + mapBodyBuilder.toString() + "}}")) + ", " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                            routeMeta.getPath(),
                            routeBaseDataCn,
                            routeTypeCn,
                            ClassName.get((TypeElement) routeMeta.getRawType()),
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase());
                }

                // Generate groups
                String groupFileName = NAME_OF_GROUP + groupName;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup))
                                .addModifiers(PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(mFiler);

                logUtil.i(">>> Generated group: " + groupName + "<<<");
                rootMap.put(groupName, groupFileName);
            }

            if (MapUtils.isNotEmpty(rootMap)) {
                // Generate root meta by group name, it must be generated before root, then I can find out the class of group.
                for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                    loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
                }
            }

            // Wirte provider into disk
            String providerMapFileName = NAME_OF_PROVIDER + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(providerMapFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IProviderGroup))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfProviderBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logUtil.i(">>> Generated provider map, name is " + providerMapFileName + " <<<");

            // Write root meta into disk.
            String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(elements.getTypeElement(ITROUTE_ROOT)))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logUtil.i(">>> Generated root, name is " + rootFileName + " <<<");
        }
    }

    /**
     * Sort metas in group.
     */
    private void categories(RouteBaseData routeBaseData) {
        if (routeVerify(routeBaseData)) {
            logUtil.i(">>> Start categories, group = " + routeBaseData.getGroup() + ", path = " + routeBaseData.getPath() + " <<<");
            Set<RouteBaseData> routeMetas = groupMap.get(routeBaseData.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteBaseData> routeMetaSet = new TreeSet<>(new Comparator<RouteBaseData>() {
                    @Override
                    public int compare(RouteBaseData r1, RouteBaseData r2) {
                        try {
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
                            logUtil.e(npe.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeBaseData);
                groupMap.put(routeBaseData.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeBaseData);
            }
        } else {
            logUtil.w(">>> Route meta verify error, group is " + routeBaseData.getGroup() + " <<<");
        }
    }

    /**
     * Verify the route meta
     */
    private boolean routeVerify(RouteBaseData data) {
        String path = data.getPath();

        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }

        if (StringUtils.isEmpty(data.getGroup())) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                data.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logUtil.e("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
