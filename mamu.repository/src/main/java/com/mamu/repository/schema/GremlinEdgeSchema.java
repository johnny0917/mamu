package com.mamu.repository.schema;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.core.GremlinRepository;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinProperty;
import com.mamu.repository.schema.property.accessor.GremlinFieldPropertyAccessor;
import com.mamu.repository.schema.property.accessor.GremlinIdFieldPropertyAccessor;
import com.mamu.repository.schema.property.accessor.GremlinPropertyAccessor;
import com.mamu.repository.schema.property.encoder.GremlinPropertyEncoder;
import com.mamu.repository.schema.property.mapper.GremlinPropertyMapper;
import com.mamu.repository.tx.GremlinGraphFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


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
public class GremlinEdgeSchema<V> extends GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinEdgeSchema.class);

    public GremlinEdgeSchema(Class<V> classType) {
        super(classType);
    }

    public GremlinEdgeSchema() {
        super();
    }

    private GremlinAdjacentProperty outProperty;
    private GremlinAdjacentProperty inProperty;

    public void addProperty(GremlinProperty property) {
        super.addProperty(property);
        if (property instanceof GremlinAdjacentProperty) {
            GremlinAdjacentProperty adjacentProperty = (GremlinAdjacentProperty) property;
            if (adjacentProperty.getDirection() == Direction.OUT) {
                outProperty = adjacentProperty;
            } else {
                inProperty = adjacentProperty;
            }
        }
    }

    public GremlinAdjacentProperty getOutProperty() {
        return outProperty;
    }

    public GremlinAdjacentProperty getInProperty() {
        return inProperty;
    }
}
