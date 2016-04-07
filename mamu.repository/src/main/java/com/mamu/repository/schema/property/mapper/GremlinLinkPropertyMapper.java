package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.mamu.repository.core.GremlinGraphAdapter;
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
public class GremlinLinkPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = null;

        // get the current edge for this property
        Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getName());
        if (edges.hasNext()) {
            Edge linkedEdge = edges.next();
            
            Iterator<Vertex> vertexs = linkedEdge.vertices(property.getDirection().opposite());
            while(vertexs!=null&&vertexs.hasNext()){
            	if(!vertexs.next().equals(vertex)){
            		linkedVertex = vertex;
            	}
            }
        } else {
            // No current edge, get it
            linkedVertex = (Vertex) cascadingSchemas.get(val);
            if (linkedVertex == null) {
                String id = property.getRelatedSchema().getGraphId(val);
                if (id != null) {
                    linkedVertex = graphAdapter.getVertex(id);
                } else {
                    if (linkedVertex == null) {
                        // No linked vertex yet, create it
                        linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
                    }
                }
            }
            if (property.getDirection() == Direction.OUT) {
                graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
            } else {
                graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
            }
        }

        // Updates or saves the val into the linkedVertex
        property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {

        Object val = null;
        Iterator<Edge> outEdges = vertex.edges(property.getDirection(), property.getName());
        while(outEdges!=null&&outEdges.hasNext()){
        	Edge outEdge = outEdges.next();
        	Iterator<Vertex> vertexs = outEdge.vertices(property.getDirection().opposite());
        	while(vertexs!=null&&vertexs.hasNext()){
        		Vertex cascadingVertex = vertexs.next();
        		if(!cascadingVertex.equals(vertex)){
        			val = property.getRelatedSchema().cascadeLoadFromGraph(cascadingVertex, cascadingSchemas);
        		}
        	}
        }

        return val;
    }
}
