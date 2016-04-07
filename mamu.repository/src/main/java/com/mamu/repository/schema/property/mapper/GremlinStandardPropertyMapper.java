package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.property.GremlinProperty;

import java.util.Map;

/**
 * A concrete {@link GremlinPropertyMapper} for mapping stadard Java types to Vertex properties.
 *
 * @author Johnny
 */
public class GremlinStandardPropertyMapper implements GremlinPropertyMapper<GremlinProperty, Element> {

    @Override
    public void copyToVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Element element, Object val, Map<Object, Object> cascadingSchemas) {
        element.property(property.getName(), val);
    }

    @Override
    public <K> Object loadFromVertex(GremlinProperty property, Element element, Map<Object, Object> cascadingSchemas) {
        return element.property(property.getName());
    }
}
