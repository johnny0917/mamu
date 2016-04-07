package com.mamu.repository.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.mamu.repository.schema.property.mapper.GremlinCollectionPropertyMapper;

/**
 * A concrete {@link GremlinRelatedProperty} for a Collection
 *
 * @author Johnny
 */
public class GremlinCollectionProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinCollectionPropertyMapper(), CARDINALITY.ONE_TO_MANY);
    }
}
