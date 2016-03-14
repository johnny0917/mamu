package com.mamu.repository.query.execution;

import org.apache.tinkerpop.gremlin.structure.Element;
import com.mamu.repository.query.AbstractGremlinQuery;
import com.mamu.repository.query.CompositeResult;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.schema.GremlinSchemaFactory;
import com.mamu.repository.utils.GenericsUtil;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query execution strategies.
 *
 * @author Gman
 */
public abstract class AbstractGremlinExecution {

    /** The parameters. */
    protected final DefaultParameters parameters;

    protected final GremlinSchemaFactory schemaFactory;

    public AbstractGremlinExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters) {
        super();
        this.schemaFactory = schemaFactory;
        this.parameters = parameters;
    }

    /**
     * Executes the given {@link AbstractGremlinQuery} with the given {@link Object[]} values.
     *
     * @param query  the orient query
     * @param values the parameters values
     * @return the result
     */
    public Object execute(AbstractGremlinQuery query, Object[] values) {
        return doExecute(query, values);
    }

    /**
     * Method to implement by executions.
     *
     * @param query  the orient query
     * @param values the parameters values
     * @return the result
     */
    protected abstract Object doExecute(AbstractGremlinQuery query, Object[] values);


    protected Map<String, Object> elementToMap(Element element) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : element.keys()) {
            map.put(key, element.property(key));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    protected List<Object> buildList(AbstractGremlinQuery query, Class<?> mappedType, Object[] values) {

        Iterable<Element> result = (Iterable<Element>) query.runQuery(parameters, values);

        List<Object> objects = new ArrayList<Object>();
        if (mappedType.isAssignableFrom(Map.class)) {

            for (Element element : result) {
                Map<String, Object> map = elementToMap(element);
                objects.add(map);
            }
        } else if (mappedType == CompositeResult.class) {

            for (Element element : result) {
                Map<String, Object> map = elementToMap(element);
                Class<?> type = GenericsUtil.getGenericType(query.getQueryMethod().getMethod());
                GremlinSchema mapper = schemaFactory.getSchema(type);
                Object entity = mapper.loadFromGraph(element);
                objects.add(new CompositeResult<Object>(entity, map));
            }
        } else {
            GremlinSchema mapper = schemaFactory.getSchema(mappedType);
            for (Element element : result) {
                objects.add(mapper.loadFromGraph(element));
            }
        }
        return objects;
    }

}