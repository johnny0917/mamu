package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;

import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s. There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge. If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with this property mapper</li>
 * </ul>
 *
 * @author Gman
 */
public class GremlinLinkPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = null;

        // get the current edge for this property
        Iterable<Edge> edges = vertex.getEdges(property.getDirection(), property.getName());
        if (edges.iterator().hasNext()) {
            Edge linkedEdge = edges.iterator().next();
            linkedVertex = linkedEdge.getVertex(property.getDirection().opposite());
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
        for (Edge outEdge : vertex.getEdges(property.getDirection(), property.getName())) {

            Vertex cascadingVertex = outEdge.getVertex(property.getDirection().opposite());
            val = property.getRelatedSchema().cascadeLoadFromGraph(cascadingVertex, cascadingSchemas);
            //            val = property.getRelatedSchema().loadFromGraph(cascadingVertex);
            break;
        }

        return val;
    }
}
