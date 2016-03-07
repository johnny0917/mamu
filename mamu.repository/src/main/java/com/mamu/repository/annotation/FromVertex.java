package com.mamu.repository.annotation;

import java.lang.annotation.*;

/**
 * Created by Johnny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface FromVertex { }
