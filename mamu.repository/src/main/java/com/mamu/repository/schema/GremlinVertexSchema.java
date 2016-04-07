package com.mamu.repository.schema;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinProperty;
import com.mamu.repository.schema.property.accessor.GremlinFieldPropertyAccessor;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (VERTEX, EDGE) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Johnny
 */
public class GremlinVertexSchema<V> extends GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinVertexSchema.class);

    public GremlinVertexSchema(Class<V> classType) {
        super(classType);
    }

    public GremlinVertexSchema() {
        super();
    }

}
