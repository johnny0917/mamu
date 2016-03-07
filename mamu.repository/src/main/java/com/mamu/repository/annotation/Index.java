package com.mamu.repository.annotation;

import java.lang.annotation.*;

/**
 * 
 * 索引的注解，在生成schema的时候，构建索引
 * @author Johnny
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * 定义索引的名称
     */
    String[] value() default "";

    /**
     * 定义索引的类型
     *
     * @return
     */
    IndexType type() default IndexType.NON_UNIQUE;

    enum IndexType {
        NONE,
        UNIQUE,
        NON_UNIQUE,
        SPATIAL_LATITUDE,
        SPATIAL_LONGITUDE
    }
}
