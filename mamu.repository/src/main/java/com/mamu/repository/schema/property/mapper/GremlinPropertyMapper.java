package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.property.GremlinProperty;

import java.util.Map;

/**
 * Defines mapping a {@link GremlinProperty} to a {@link Vertex}.
 *
 * @author Johnny
 */
public interface GremlinPropertyMapper<E extends GremlinProperty, V extends Element> {

    void copyToVertex(E property, GremlinGraphAdapter graphAdapter, V element, Object val, Map<Object, Object> cascadingSchemas);

    <K> Object loadFromVertex(E property, V element, Map<Object, Object> cascadingSchemas);
}
