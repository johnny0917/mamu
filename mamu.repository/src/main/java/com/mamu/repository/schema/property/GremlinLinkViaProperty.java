package com.mamu.repository.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.mamu.repository.schema.property.mapper.GremlinLinkViaPropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Johnny
 */
public class GremlinLinkViaProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkViaProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinLinkViaPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
