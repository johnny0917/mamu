package com.mamu.repository.query;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;

import org.springframework.data.domain.Pageable;
import com.mamu.repository.schema.GremlinSchemaFactory;
import com.mamu.repository.tx.GremlinGraphFactory;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParametersParameterAccessor;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


/**
 * A concrete {@link AbstractGremlinQuery} which handles String based gremlin queries defined using the {@link com.mamu.repository.query.annotation.Query} annotation.
 *
 * @author Johnny
 */
public class StringBasedGremlinQuery extends AbstractGremlinQuery {

    private GremlinGraphFactory dbf;

    private String queryString;

    private boolean countQuery;

    private boolean modifyingQuery;

    public StringBasedGremlinQuery(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, String query, GremlinQueryMethod method) {
        super(schemaFactory, method);
        this.dbf = dbf;
        this.queryString = query;
        this.countQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().count();
        this.modifyingQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().modify();
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Traversal doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {

        ScriptEngine engine = new GremlinGroovyScriptEngine();
        Bindings bindings = engine.createBindings();
        Graph graph = dbf.graph();
        bindings.put("g", graph);
        bindings.put("graph", graph);
        bindings.put("G", graph);

        String queryString = this.queryString;

        for (Parameter param : parameters.getBindableParameters()) {
            String paramName = param.getName();
            String placeholder = param.getPlaceholder();
            Object val = values[param.getIndex()];
            if (paramName == null) {
                placeholder = "placeholder_" + param.getIndex();
                queryString = queryString.replaceFirst("\\?", placeholder);
                bindings.put(placeholder, val);
            } else {
                queryString = queryString.replaceFirst(placeholder, paramName);
                bindings.put(paramName, val);
            }
        }

        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        Pageable pageable = accessor.getPageable();
        if (pageable != null && !ignorePaging) {
            queryString = String.format("%s[%d..%d]", queryString, pageable.getOffset(), pageable.getOffset() + pageable.getPageSize() - 1);
        }

        try {
            return (Traversal) engine.eval(queryString, bindings);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(String.format("Could not evaluate Gremlin query String %s. Error: %s ", queryString, e.getMessage()), e);
        }
    }

    @Override
    protected boolean isCountQuery() {
        return this.countQuery;
    }

    @Override
    protected boolean isModifyingQuery() {
        return this.modifyingQuery;
    }

}
