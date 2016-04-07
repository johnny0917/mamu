package com.mamu.repository.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.mamu.repository.schema.property.mapper.GremlinCollectionViaPropertyMapper;

/**
 * A concrete {@link GremlinRelatedProperty} for a Collection via a relational entity
 *
 * @author Johnny
 */
public class GremlinCollectionViaProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionViaProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinCollectionViaPropertyMapper(), CARDINALITY.ONE_TO_MANY);
    }
}
