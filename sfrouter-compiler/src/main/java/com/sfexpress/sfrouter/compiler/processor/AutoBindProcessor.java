package com.sfexpress.sfrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.sfexpress.sfrouter.annotation.AutoBind;
import com.sfexpress.sfrouter.annotation.enums.FieldTypeEnum;
import com.sfexpress.sfrouter.compiler.util.LogUtil;
import com.sfexpress.sfrouter.compiler.util.StaticConsts;
import com.sfexpress.sfrouter.compiler.util.TypeUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sfexpress.sfrouter.compiler.util.StaticConsts.ANNOTATION_TYPE_AUTOBIND;
import static com.sfexpress.sfrouter.compiler.util.StaticConsts.KEY_MODULE_NAME;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 参数自动绑定的处理器
 * Created by sf-zhangpeng on 2018/3/9.
 */
@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ANNOTATION_TYPE_AUTOBIND})
public class AutoBindProcessor extends AbstractProcessor {

    private Filer mFiler;       // File util, write class file into disk.
    private Types types;
    private TypeUtil typeUtil;
    private Elements elements;
    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();   // HashMap包含了需要自动绑定的参数以及它的父类 Contain field need autowired and his super class.
    private static final ClassName SFRouterClass = ClassName.get("com.sfexpress.sfrouter.launcher", "SFRouterManager");
    private static final ClassName AndroidLog = ClassName.get("android.util", "Log");
    private LogUtil logUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        typeUtil = new TypeUtil(types, elements);
        logUtil = new LogUtil(processingEnv.getMessager());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            try {
                logUtil.i(">>> Found AutoBind field, start... <<<");
                categoryElements(roundEnvironment.getElementsAnnotatedWith(AutoBind.class));
                generateHelperClass();
            } catch (Exception e) {
                logUtil.e(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 将自动绑定的参数进行分类，保存为一个map结构，便于后续处理
     * key是参数对应的类，value是该类中需要绑定的参数的list
     */
    private void categoryElements(Set<? extends Element> elements) throws IllegalAccessException {
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new IllegalAccessException("自动绑定的属性不能为'private'， 检查 [" + enclosingElement.getQualifiedName() + "] 类的 [" + element.getSimpleName() + "] 属性");
                }
                if (parentAndChild.containsKey(enclosingElement)) { // 如果已经有对应的分类，添加进去
                    parentAndChild.get(enclosingElement).add(element);
                } else { // 如果没有对应的分类，创建一个
                    List<Element> childs = new ArrayList<>();
                    childs.add(element);
                    parentAndChild.put(enclosingElement, childs);
                }
            }
            logUtil.i("categoryElements success");
        }
    }

    /**
     * 利用JavaPoet生成自动绑定的Helper类，类名是：Activity名$$SFRouter$$AutoBind，每个Activity对应一个helper文件
     */
    private void generateHelperClass() throws IOException, IllegalAccessException {
        TypeElement typeIInjector = elements.getTypeElement(StaticConsts.IINJECTOT);
        TypeElement typeJsonService = elements.getTypeElement(StaticConsts.JSON_SERVICE);
        TypeMirror iProvider = elements.getTypeElement(StaticConsts.IPROVIDER).asType();
        TypeMirror activityTm = elements.getTypeElement(StaticConsts.ACTIVITY).asType();
        TypeMirror fragmentTm = elements.getTypeElement(StaticConsts.FRAGMENT).asType();
        TypeMirror fragmentTmV4 = elements.getTypeElement(StaticConsts.FRAGMENT_V4).asType();

        // 设置方法的输入参数，就是一个obj，实际上是需要绑定的Activity
        ParameterSpec objectParamSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();

        if (MapUtils.isNotEmpty(parentAndChild)) {
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {
                // 创建inject方法
                MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(StaticConsts.METHOD_INJECT)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(objectParamSpec);

                TypeElement parent = entry.getKey();
                List<Element> childs = entry.getValue();
                String qualifiedName = parent.getQualifiedName().toString();
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                String fileName = parent.getSimpleName() + StaticConsts.NAME_OF_AUTOWIRED;

                logUtil.i(">>> 开始处理 " + parent.getSimpleName() + " 类的 " + childs.size() + " 参数. <<<");

                // 生成helper类
                TypeSpec.Builder helper = TypeSpec.classBuilder(fileName)
                        .addJavadoc(StaticConsts.WARNING_TIPS)
                        .addSuperinterface(ClassName.get(typeIInjector))
                        .addModifiers(PUBLIC);

                FieldSpec jsonServiceField = FieldSpec.builder(TypeName.get(typeJsonService.asType()), "serializationService", Modifier.PRIVATE).build();
                helper.addField(jsonServiceField);

                injectMethodBuilder.addStatement("serializationService = $T.INSTANCE.navigation($T.class)", SFRouterClass, ClassName.get(typeJsonService));
                injectMethodBuilder.addStatement("$T targetActivity = ($T)target", ClassName.get(parent), ClassName.get(parent));

                // 生成方法内容，循环遍历所有的元素，生成对应的注入代码
                for (Element element : childs) {
                    AutoBind fieldConfig = element.getAnnotation(AutoBind.class);
                    String fieldName = element.getSimpleName().toString();
                    if (types.isSubtype(element.asType(), iProvider)) {  // 如果参数是一个provider，需要执行navigation操作
                        if ("".equals(fieldConfig.name())) {    // 没设定name的用byType
                            injectMethodBuilder.addStatement(
                                    "targetActivity." + fieldName + " = $T.INSTANCE.navigation($T.class)",
                                    SFRouterClass,
                                    ClassName.get(element.asType())
                            );
                        } else { // 设定了name直接用name
                            injectMethodBuilder.addStatement(
                                    "targetActivity." + fieldName + " = ($T)$T.INSTANCE.build($S).navigation();",
                                    ClassName.get(element.asType()),
                                    SFRouterClass,
                                    fieldConfig.name()
                            );
                        }
                        // 判空验证逻辑
                        if (fieldConfig.required()) {
                            injectMethodBuilder.beginControlFlow("if (targetActivity." + fieldName + " == null)");
                            injectMethodBuilder.addStatement(
                                    "throw new RuntimeException(\"The field '" + fieldName + "' is null, in class '\" + $T.class.getName() + \"!\")", ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        }
                    } else {    // 普通的intent参数
                        String originalValue = "targetActivity." + fieldName;
                        String statement = "targetActivity." + fieldName + " = targetActivity.";
                        boolean isActivity = false;
                        if (types.isSubtype(parent.asType(), activityTm)) {  // Activity, then use getIntent()
                            isActivity = true;
                            statement += "getIntent().";
                        } else if (types.isSubtype(parent.asType(), fragmentTm) || types.isSubtype(parent.asType(), fragmentTmV4)) {   // Fragment, then use getArguments()
                            statement += "getArguments().";
                        } else {
                            throw new IllegalAccessException("The field [" + fieldName + "] need autowired from intent, its parent must be activity or fragment!");
                        }

                        statement = buildStatement(originalValue, statement, typeUtil.getTypeFromElement(element), isActivity);
                        if (statement.startsWith("serializationService.")) {   // Not mortals
                            injectMethodBuilder.beginControlFlow("if (null != serializationService)");
                            injectMethodBuilder.addStatement(
                                    "targetActivity." + fieldName + " = " + statement,
                                    (StringUtils.isEmpty(fieldConfig.name()) ? fieldName : fieldConfig.name()),
                                    ClassName.get(element.asType())
                            );
                            injectMethodBuilder.nextControlFlow("else");
                            injectMethodBuilder.addStatement(
                                    "$T.e(\"" + StaticConsts.TAG + "\", \"You want automatic inject the field '" + fieldName + "' in class '$T' , then you should implement 'SerializationService' to support object auto inject!\")", AndroidLog, ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        } else {
                            injectMethodBuilder.addStatement(statement, StringUtils.isEmpty(fieldConfig.name()) ? fieldName : fieldConfig.name());
                        }

                        // 判空验证逻辑
                        if (fieldConfig.required() && !element.asType().getKind().isPrimitive()) {  // Primitive wont be check.
                            injectMethodBuilder.beginControlFlow("if (null == targetActivity." + fieldName + ")");
                            injectMethodBuilder.addStatement(
                                    "$T.e(\"" + StaticConsts.TAG + "\", \"The field '" + fieldName + "' is null, in class '\" + $T.class.getName() + \"!\")", AndroidLog, ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        }
                    }
                }
                helper.addMethod(injectMethodBuilder.build());
                // 生成自动绑定helper文件
                JavaFile.builder(packageName, helper.build()).build().writeTo(mFiler);
                logUtil.i(">>> " + parent.getSimpleName() + " 已经被处理,生成文件： " + fileName + " <<<");
            }
            logUtil.i(">>> AutoBind处理器执行完成 <<<");
        }
    }

    private String buildStatement(String originalValue, String statement, int type, boolean isActivity) {
        if (type == FieldTypeEnum.BOOLEAN.ordinal()) {
            statement += (isActivity ? ("getBooleanExtra($S, " + originalValue + ")") : ("getBoolean($S)"));
        } else if (type == FieldTypeEnum.BYTE.ordinal()) {
            statement += (isActivity ? ("getByteExtra($S, " + originalValue + "") : ("getByte($S)"));
        } else if (type == FieldTypeEnum.SHORT.ordinal()) {
            statement += (isActivity ? ("getShortExtra($S, " + originalValue + ")") : ("getShort($S)"));
        } else if (type == FieldTypeEnum.INT.ordinal()) {
            statement += (isActivity ? ("getIntExtra($S, " + originalValue + ")") : ("getInt($S)"));
        } else if (type == FieldTypeEnum.LONG.ordinal()) {
            statement += (isActivity ? ("getLongExtra($S, " + originalValue + ")") : ("getLong($S)"));
        } else if (type == FieldTypeEnum.CHAR.ordinal()) {
            statement += (isActivity ? ("getCharExtra($S, " + originalValue + ")") : ("getChar($S)"));
        } else if (type == FieldTypeEnum.FLOAT.ordinal()) {
            statement += (isActivity ? ("getFloatExtra($S, " + originalValue + ")") : ("getFloat($S)"));
        } else if (type == FieldTypeEnum.DOUBLE.ordinal()) {
            statement += (isActivity ? ("getDoubleExtra($S, " + originalValue + ")") : ("getDouble($S)"));
        } else if (type == FieldTypeEnum.STRING.ordinal()) {
            statement += (isActivity ? ("getStringExtra($S)") : ("getString($S)"));
        } else if (type == FieldTypeEnum.PARCELABLE.ordinal()) {
            statement += (isActivity ? ("getParcelableExtra($S)") : ("getParcelable($S)"));
        } else if (type == FieldTypeEnum.OBJECT.ordinal()) {
            statement = "serializationService.parseObject(targetActivity." + (isActivity ? "getIntent()." : "getArguments().") + (isActivity ? "getStringExtra($S)" : "getString($S)") + ", new com.sfexpress.sfrouter.annotation.models.TypeWrapper<$T>(){}.getType())";
        }
        return statement;
    }
}
