package com.mamu.repository.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.mamu.repository.annotation.Enumerated.EnumeratedType.ORDINAL;

/**
 * Created by Johnny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Enumerated {

    /**
     * (Optional) The type used in mapping an enum type.
     */
    EnumeratedType value() default ORDINAL;

    enum EnumeratedType {

        ORDINAL(Integer.class),
        STRING(String.class);

        private final Class<?> type;

        EnumeratedType(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
    }
}
