package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinLinkProperty;
import com.mamu.repository.schema.property.GremlinRelatedProperty;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s. There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge. If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with this property mapper</li>
 * </ul>
 *
 * @author Johnny
 */
public class GremlinLinkViaPropertyMapper extends GremlinLinkPropertyMapper {

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {

        GremlinAdjacentProperty adjacentProperty = property.getAdjacentProperty();

        // Check we found the adjacent property
        if (adjacentProperty != null) {

            Object adjacentObj = adjacentProperty.getAccessor().get(val);
            if (adjacentObj != null) {
                Vertex adjacentVertex = graphAdapter.findOrCreateVertex(adjacentProperty.getRelatedSchema().getObjectId(adjacentObj), adjacentProperty.getRelatedSchema().getClassName());


                // If we have the adjacent vertex then we can continue
                if (adjacentVertex != null) {

                    Edge linkedEdge = null;

                    // get the current edge for this property
                    Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
                    while (edges.hasNext()) {
                        Edge edge = edges.next();
                        Iterator<Vertex> vertexs = edge.vertices(property.getDirection().opposite());
                        while(vertexs!=null&&vertexs.hasNext()){
                        	Vertex v = vertexs.next();
                        	if(v.equals(adjacentVertex)){
                        		 linkedEdge = edge;
                                 break;
                        	}
                        }
                    }

                    if (linkedEdge == null) {
                        if (property.getDirection() == Direction.OUT) {
                            linkedEdge = graphAdapter.addEdge(null, vertex, adjacentVertex, property.getRelatedSchema().getClassName());
                        } else {
                            linkedEdge = graphAdapter.addEdge(null, adjacentVertex, vertex, property.getRelatedSchema().getClassName());
                        }
                    }
                    // Updates or saves the val into the linkedVertex
                    property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedEdge, val, cascadingSchemas);
                }
            }
        }


    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {

        //        GremlinRelatedProperty adjacentProperty = getAdjacentProperty(property);

        Object val = null;
        Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
        while(edges!=null&&edges.hasNext()){
        	Edge linkedEdge = edges.next();
        	val = property.getRelatedSchema().cascadeLoadFromGraph(linkedEdge, cascadingSchemas);
        }
        return val;
    }

}
