package com.mamu.repository.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.mamu.repository.schema.property.mapper.GremlinLinkPropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Johnny
 */
public class GremlinLinkProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinLinkPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
