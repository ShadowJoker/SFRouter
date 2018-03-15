package com.sfexpress.sfrouter.compiler.util;

/**
 * 静态变量存放处
 * Created by sf-zhangpeng on 2018/3/9.
 */
public class StaticConsts {

    // 用于文件生成的静态变量
    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "SFRouter";
    public static final String TAG = PROJECT + "::";
    public static final String WARNING_TIPS = "SFRouter自动生成的文件,不要编辑这个文件!!!";
    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String METHOD_INJECT = "inject";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root";
    public static final String NAME_OF_PROVIDER = PROJECT + SEPARATOR + "Providers";
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String NAME_OF_INTERCEPTOR = PROJECT + SEPARATOR + "Interceptors";
    public static final String NAME_OF_AUTOWIRED = SEPARATOR + PROJECT + SEPARATOR + "AutoBind";
    public static final String PACKAGE_OF_GENERATE_FILE = "com.sfexpress.sfrouter.routes";

    // 系统类名
    public static final String ACTIVITY = "android.app.Activity";
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    public static final String SERVICE = "android.app.Service";

    // Java数据类包名
    public static final String BYTE = "java.lang.Byte";
    public static final String SHORT = "java.lang.Short";
    public static final String INTEGER = "java.lang.Integer";
    public static final String LONG = "java.lang.Long";
    public static final String FLOAT = "java.lang.Float";
    public static final String DOUBEL = "java.lang.Double";
    public static final String BOOLEAN = "java.lang.Boolean";
    public static final String STRING = "java.lang.String";
    public static final String PARCELABLE = "android.os.Parcelable";


    // 路由库内部自定义的包名
    private static final String BASE_PACKAGE = "com.sfexpress.sfrouter";
    private static final String TEMPLATE_PACKAGE = ".template";
    private static final String SERVICE_PACKAGE = ".service";
    public static final String IPROVIDER = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IProvider";
    public static final String IPROVIDER_GROUP = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IProviderGroup";
    public static final String IINTERCEPTOR = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IInterceptor";
    public static final String IINTERCEPTOR_GROUP = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IInterceptorGroup";
    public static final String ITROUTE_ROOT = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IRouteRoot";
    public static final String IROUTE_GROUP = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IRouteGroup";
    public static final String IINJECTOT = BASE_PACKAGE + TEMPLATE_PACKAGE + ".IInjector";
    public static final String JSON_SERVICE = BASE_PACKAGE + SERVICE_PACKAGE + ".SerializationService";

    // Log
    static final String PREFIX_OF_LOG = PROJECT + "::Compiler ";

    // Options of processor
    public static final String KEY_MODULE_NAME = "moduleName";

    // Annotation type
    public static final String ANNOTATION_TYPE_INTECEPTOR = BASE_PACKAGE + ".annotation.RouteInterceptor";
    public static final String ANNOTATION_TYPE_ROUTE = BASE_PACKAGE + ".annotation.Route";
    public static final String ANNOTATION_TYPE_AUTOBIND = BASE_PACKAGE + ".annotation.AutoBind";
}
