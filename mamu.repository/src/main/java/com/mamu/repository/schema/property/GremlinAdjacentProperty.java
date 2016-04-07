package com.mamu.repository.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.mamu.repository.schema.property.mapper.GremlinAdjacentPropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Johnny
 */
public class GremlinAdjacentProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinAdjacentProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinAdjacentPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
