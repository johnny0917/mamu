package com.mamu.repository.query.execution;

import com.mamu.repository.query.AbstractGremlinQuery;
import com.mamu.repository.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

/**
 * Executes the query to return a sum of entities.
 *
 * @author Johnny
 */
public class ModifyExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public ModifyExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters) {
        super(schemaFactory, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        return query.runQuery(parameters, values, true);
    }
}
