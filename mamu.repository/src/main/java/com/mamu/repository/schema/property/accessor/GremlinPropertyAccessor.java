package com.mamu.repository.schema.property.accessor;

import com.mamu.repository.schema.property.GremlinProperty;

import java.lang.reflect.Field;

/**
 * Interface defining an accessor of a {@link GremlinProperty}
 *
 * @param <V> The result value type of the accessor
 * @author Johnny
 */
public interface GremlinPropertyAccessor<V> {
    V get(Object object);

    void set(Object object, V val);

    Field getField();
}
