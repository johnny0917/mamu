package com.mamu.repository.annotation;

import java.lang.annotation.*;

/**
 * Created by Johnny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Edge {

    /**
     * The name of the Edge. If left blank the name of the Class is used.
     * @return
     */
    String value() default "";

    /**
     * pseudonym for value().
     * @return 
     */
    String name() default "";

}
