package com.mamu.repository.query;


import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.RangeGlobalStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.RangeLocalStep;
import org.springframework.data.domain.Pageable;
import com.mamu.repository.schema.GremlinSchemaFactory;
import com.mamu.repository.tx.GremlinGraphFactory;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * A concrete {@link AbstractGremlinQuery} implementation based on a {@link PartTree}.
 *
 * @author Johnny
 */
public class PartTreeGremlinQuery extends AbstractGremlinQuery {

    /** The domain class. */
    private final Class<?> domainClass;

    /** The tree. */
    private final PartTree tree;

    private final GremlinQueryMethod method;

    private final GremlinGraphFactory dbf;

    /**
     * Instantiates a new {@link PartTreeGremlinQuery} from given {@link GremlinQueryMethod}.
     *
     * @param method the query method
     */
    public PartTreeGremlinQuery(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinQueryMethod method) {
        super(schemaFactory, method);

        this.dbf = dbf;
        this.method = method;
        this.domainClass = method.getEntityInformation().getJavaType();
        this.tree = new PartTree(method.getName(), domainClass);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.AbstractOrientQuery#doRunQuery(java.lang.Object[])
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected Traversal doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);

        GremlinQueryCreator creator = new GremlinQueryCreator(dbf, schemaFactory, domainClass, tree, accessor);

        GraphTraversal pipeline = creator.createQuery();

        Pageable pageable = accessor.getPageable();
        if (pageable != null && !ignorePaging) {
        	return pipeline.range(pageable.getOffset(), pageable.getOffset() + pageable.getPageSize() - 1);  
        }
        return pipeline;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.AbstractOrientQuery#isCountQuery()
     */
    @Override
    protected boolean isCountQuery() {
        return tree.isCountProjection();
    }

    @Override
    protected boolean isModifyingQuery() {
        return tree.isDelete();
    }
}
