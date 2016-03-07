package com.mamu.repository.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.mamu.repository.annotation.Property.SerialisableType.STANDARD;

/**
 * Created by Johnny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Property {

    /**
     * 指定属性名称，如果不指定，默认使用字段名称
     * @return
     */
    String value() default "";

    /**
     *
     * @return
     */
    String name() default "";

    /**
     *
     * 如果需要序列化的对象是个Json，则提供mixin 类进行处理
     * @return
     */
    Class<?> jsonMixin() default Void.class;

    /**
     * 可选项，序列化类型
     */
    SerialisableType type() default STANDARD;

    enum SerialisableType {
        STANDARD,
        SERIALIZABLE,
        JSON
    }
}
