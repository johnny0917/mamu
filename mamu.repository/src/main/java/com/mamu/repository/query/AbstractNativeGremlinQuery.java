package com.mamu.repository.query;

import com.mamu.repository.schema.GremlinSchemaFactory;
import com.mamu.repository.tx.GremlinGraphFactory;

/**
 * Native query abstraction for {@link AbstractGremlinQuery}s.
 *
 * @author Johnny
 */
public abstract class AbstractNativeGremlinQuery extends AbstractGremlinQuery {

    /** The query method. */
    protected final GremlinGraphFactory dbf;
    protected final GremlinQueryMethod method;
    protected final GremlinSchemaFactory schemaFactory;
    protected final String query;
    private boolean countQuery;
    private boolean modifyingQuery;

    public AbstractNativeGremlinQuery(GremlinGraphFactory dbf, GremlinQueryMethod method, GremlinSchemaFactory schemaFactory, String query) {
        super(schemaFactory, method);

        this.dbf = dbf;
        this.method = method;
        this.schemaFactory = schemaFactory;
        this.query = query;
        this.countQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().count();
        this.modifyingQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().modify();
    }

    public GremlinGraphFactory getDbf() {
        return dbf;
    }

    public GremlinQueryMethod getMethod() {
        return method;
    }

    public GremlinSchemaFactory getSchemaFactory() {
        return schemaFactory;
    }

    public String getQuery() {
        return query;
    }

    @Override
    protected boolean isCountQuery() {
        return countQuery;
    }

    @Override
    protected boolean isModifyingQuery() {
        return modifyingQuery;
    }
}
