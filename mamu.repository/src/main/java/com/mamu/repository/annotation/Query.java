package com.mamu.repository.annotation;

import java.lang.annotation.*;

/**
 * 此注解，定义查询脚本
 *
 * @author Johnny
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * 定义 Gremlin查询语句
     */
    String value() default "";

    /**
     * 定义查询语句是否返回数量，默认不返回查询数量
     */
    boolean count() default false;

    /**
     * 
     * 定义一个修改查询，返回在数据中的修改数量
     */
    boolean modify() default false;

    /**
     * 指定当前查询为本地查询，和使用的图形数据库息息相关
     */
    boolean nativeQuery() default false;
}
