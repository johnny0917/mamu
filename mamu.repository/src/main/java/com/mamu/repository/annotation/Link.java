package com.mamu.repository.annotation;


import java.lang.annotation.*;

import org.apache.tinkerpop.gremlin.structure.Direction;

/**
 * Created by Johnny 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Link {

    String value() default "";

    String name() default "";

    Direction direction() default Direction.OUT;
}
