package com.mamu.repository.annotation;

import java.lang.annotation.*;

/**
 * Created by Johnny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Vertex {

    /**
     * 指定Node 节点对象的名称，如果不填写，默认为类名称
     * @return
     */
    String value() default "";

    /**
     * 
     * @return
     */
    String name() default "";

}
