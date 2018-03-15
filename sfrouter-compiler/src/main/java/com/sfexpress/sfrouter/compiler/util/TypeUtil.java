package com.sfexpress.sfrouter.compiler.util;

import com.sfexpress.sfrouter.annotation.enums.FieldTypeEnum;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 类型转换Util，通过Java原生的Types和Elements，获取指定数据类型对应的int序号值。
 * Created by sf-zhangpeng on 2018/3/9.
 */
public class TypeUtil {

    private Types types;
    private Elements elements;
    private TypeMirror parcelableType;

    public TypeUtil(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;

        parcelableType = this.elements.getTypeElement(StaticConsts.PARCELABLE).asType();
    }

    /**
     * 通过Element对象获取该元素实际对应的Java类，返回值为int类型
     */
    public int getTypeFromElement(Element element) {

        TypeMirror typeMirror = element.asType();

        // 如果是基础数据类型，直接返回Java对应的type枚举序号即可
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        //自定义FieldTypeEnum枚举，因为Java自身的TypeKind没有String和Parcelable等类型
        switch (typeMirror.toString()) {
            case StaticConsts.BYTE:
                return FieldTypeEnum.BYTE.ordinal();
            case StaticConsts.SHORT:
                return FieldTypeEnum.SHORT.ordinal();
            case StaticConsts.INTEGER:
                return FieldTypeEnum.INT.ordinal();
            case StaticConsts.LONG:
                return FieldTypeEnum.LONG.ordinal();
            case StaticConsts.FLOAT:
                return FieldTypeEnum.FLOAT.ordinal();
            case StaticConsts.DOUBEL:
                return FieldTypeEnum.DOUBLE.ordinal();
            case StaticConsts.BOOLEAN:
                return FieldTypeEnum.BOOLEAN.ordinal();
            case StaticConsts.STRING:
                return FieldTypeEnum.STRING.ordinal();
            default:    // 如果以上判断没有命中，查看是否是parcelable类型
                if (types.isSubtype(typeMirror, parcelableType)) {  // PARCELABLE
                    return FieldTypeEnum.PARCELABLE.ordinal();
                } else {    // 其他类型一律按Object处理
                    return FieldTypeEnum.OBJECT.ordinal();
                }
        }
    }
}
