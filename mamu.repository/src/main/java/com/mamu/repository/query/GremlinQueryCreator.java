package com.mamu.repository.query;

import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.structure.Direction;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.FilterStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.OrStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.StartStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.schema.GremlinSchemaFactory;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinProperty;
import com.mamu.repository.schema.property.GremlinRelatedProperty;
import com.mamu.repository.tx.GremlinGraphFactory;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * 创建基于 {@link AbstractQueryCreator} 的Gremlin查询构建器.
 *
 * @author Johnny
 */
public class GremlinQueryCreator extends AbstractQueryCreator<GraphTraversal, GraphTraversal> {

    private static final Logger logger = LoggerFactory.getLogger(GremlinQueryCreator.class);

    private final PartTree tree;

    private GremlinGraphFactory factory;

    private GremlinSchemaFactory schemaFactory;

    private ParameterAccessor accessor;

    private GremlinSchema schema;

    public GremlinQueryCreator(GremlinGraphFactory factory, GremlinSchemaFactory mapperfactory, Class<?> domainClass, PartTree tree, ParameterAccessor accessor) {
        super(tree, accessor);

        this.factory = factory;
        this.tree = tree;
        this.schemaFactory = mapperfactory;
        this.accessor = accessor;
        this.schema = schemaFactory.getSchema(domainClass);
    }

    @Override
    protected GraphTraversal create(Part part, Iterator<Object> iterator) {
        return toCondition(part, iterator);
    }

    @Override
    protected GraphTraversal and(Part part, GraphTraversal base, Iterator<Object> iterator) {
    	//GraphTraversal lastPipe = (GraphTraversal) base.getPipes().get(base.getPipes().size() - 1);
    	GraphTraversal lastPipe = (GraphTraversal) base.barrier().next(Integer.parseInt(base.count().value().toString()) - 1);
        if (lastPipe instanceof FilterStep) {
        	
            return base.and(toCondition(part, iterator));
        }
        GraphTraversal andPipeline = (GraphTraversal) GraphTraversalSource.standard().create(factory.graph());
        andPipeline.and(base, toCondition(part, iterator));
        return andPipeline;
    }

    @Override
    protected GraphTraversal or(GraphTraversal base, GraphTraversal criteria) {
    	GraphTraversal g = (GraphTraversal) factory.graph().traversal();
    	return g.or(base,criteria);
    }

    public boolean isCountQuery() {
        return tree.isCountProjection();
    }

    @Override
    protected GraphTraversal complete(GraphTraversal criteria, Sort sort) {
        //Pageable pageable = accessor.getPageable();
        GraphTraversal pipeline = (GraphTraversal) factory.graph().traversal();
        if (schema.isEdgeSchema()) {
            //pipeline = pipeline.V().add(criteria);
            criteria.addV();
        } else if (schema.isVertexSchema()) {
            pipeline = pipeline.V().and(criteria);
        }
//        pipeline = pipeline.and(criteria);
//        pipeline = pipeline.add(criteria);
        return pipeline;
    }

    protected GraphTraversal toCondition(Part part, Iterator<Object> iterator) {

    	GraphTraversal pipeline = (GraphTraversal) factory.graph().traversal();
        PropertyPath path = part.getProperty();
        PropertyPath leafProperty = path.getLeafProperty();
        String leafSegment = leafProperty.getSegment();
//        Class<?> type = leafProperty.getOwningType().getType();
//
//        GremlinSchema schema = schemaFactory.getSchema(type);
//        if (schema.isVertexSchema()) {
//            pipeline = pipeline.V();
//        } else if (schema.isEdgeSchema()) {
//            pipeline = pipeline.E();
//        }


        if(schema.isEdgeSchema()) {
            includeCondition(part.getType(), leafSegment, pipeline, iterator);
        }
        while (path != null && path.hasNext()) {

            String segment = path.getSegment();
            Class<?> type = path.getOwningType().getType();
            schema = schemaFactory.getSchema(type);
            GremlinProperty gremlinProperty = schema.getPropertyForFieldname(segment);

            if (schema.isVertexSchema()) {
                if (gremlinProperty instanceof GremlinRelatedProperty) {

                    GremlinRelatedProperty adjacentProperty = (GremlinRelatedProperty) gremlinProperty;
                    Direction direction = adjacentProperty.getDirection();
                    if (direction == Direction.IN) {
                        pipeline.inE(gremlinProperty.getName()).outV();
                    } else {
                        pipeline.outE(gremlinProperty.getName()).inV();
                    }
                }

            } else if (schema.isEdgeSchema()) {
                if (gremlinProperty instanceof GremlinAdjacentProperty) {
                    GremlinAdjacentProperty adjacentProperty = (GremlinAdjacentProperty)gremlinProperty;
                    Direction direction = adjacentProperty.getDirection();
                    if (direction == Direction.IN) {
                        pipeline.inE(schema.getClassName());
                    } else {
                        pipeline.outE(schema.getClassName());
                    }
                }
            }

            path = path.next();

        }

        if(schema.isVertexSchema()) {
            includeCondition(part.getType(), leafSegment, pipeline, iterator);
        }

        return pipeline;

//        if (schema.isVertexSchema()) {
//            Spliterator<PropertyPath> it = path.spliterator();
//            it.forEachRemaining(new Consumer<PropertyPath>() {
//                @Override
//                public void accept(PropertyPath propertyPath) {
//
//                    if (propertyPath.hasNext()) {
//                        String segment = propertyPath.getSegment();
//                        Class<?> type = propertyPath.getOwningType().getType();
//                        GremlinSchema schema = schemaFactory.getSchema(type);
//                        if (schema.isVertexSchema()) {
//                            GremlinProperty gremlinProperty = schema.getPropertyForFieldname(segment);
//                            String projectedName = gremlinProperty.getName();
//                            //                            pipeline.outE(projectedName).inV();
//                        } else {
//                            //                            pipeline.outE(schema.getClassName());
//                        }
//                    }
//                }
//            });
//        }

    }

    private GraphTraversal includeCondition(Part.Type type, String property, GraphTraversal pipeline, Iterator iterator) {
        switch (type) {
        case AFTER:
        case GREATER_THAN:
            pipeline.has(property, Compare.gt.toString(), iterator.next());
            break;
        case GREATER_THAN_EQUAL:
            pipeline.has(property, Compare.gte.toString(), iterator.next());
            break;
        case BEFORE:
        case LESS_THAN:
            pipeline.has(property, Compare.lt.toString(), iterator.next());
            break;
        case LESS_THAN_EQUAL:
            pipeline.has(property, Compare.lte.toString(), iterator.next());
            break;
        case BETWEEN:
            Object val = iterator.next();
            pipeline.has(property, Compare.lt.toString(), val).has(property, Compare.gt.toString(), val);
            break;
        case IS_NULL:
            pipeline.has(property);
            break;
        case IS_NOT_NULL:
            pipeline.has(property);
            break;
        case IN:
            pipeline.has(property, Contains.within.toString(), iterator.next());
            break;
        case NOT_IN:
            pipeline.has(property, Contains.without.toString(), iterator.next());
            break;
        case LIKE:
        	//pipeline.has(property, StartStep<S>, iterator.next());
            pipeline.has(property, Like.IS.toString(), iterator.next());
            break;
        case NOT_LIKE:
            pipeline.has(property, Like.NOT.toString(), iterator.next());
            break;
        case STARTING_WITH:
            pipeline.has(property, StartsWith.DOES.toString(), iterator.next());
            break;
        case ENDING_WITH:
            pipeline.has(property, EndsWith.DOES.toString(), iterator.next());
            break;
        case CONTAINING:
            pipeline.has(property, Like.IS.toString(), iterator.next());
            break;
        case SIMPLE_PROPERTY:
            pipeline.has(property, iterator.next());
            break;
        case NEGATING_SIMPLE_PROPERTY:
            //pipeline.hasNot(property, iterator.next()); 标注一下，此处有修改过
        	pipeline.hasNot(property);
            break;
        case TRUE:
            pipeline.has(property, true);
            break;
        case FALSE:
            pipeline.has(property, false);
            break;
        default:
            throw new IllegalArgumentException("Unsupported keyword!");
        }

//        return new GremlinPipeline().and(pipeline);
        return pipeline;
    }


    private enum StartsWith implements BiPredicate<Object, Object> {
        DOES,
        NOT;

		@Override
		public boolean test(final Object first, final Object second) {
            if (first instanceof String && second instanceof String) {
                return this == DOES && ((String) second).startsWith((String) first);
            }
            return false;
        }
    }

    private enum EndsWith implements BiPredicate<Object, Object> {
        DOES,
        NOT;

		@Override
		public boolean test(final Object first, final Object second) {
			if (first instanceof String && second instanceof String) {
                return this == DOES && ((String) second).endsWith((String) first);
            }
            return false;
		}
    }

    private enum Like implements BiPredicate<Object, Object> {

        IS,
        NOT;

		@Override
		public boolean test(final Object first, final Object second) {
            if (first instanceof String && second instanceof String) {
                return this == IS && first.toString().toLowerCase().contains(second.toString().toLowerCase());
            }
            return false;
        }
    }
}
