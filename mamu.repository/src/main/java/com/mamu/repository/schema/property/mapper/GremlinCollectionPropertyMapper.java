package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerProperty;
import org.apache.commons.collections4.CollectionUtils;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.schema.property.GremlinRelatedProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A concrete {@link GremlinPropertyMapper} mapping a vertices property to a Collection.
 *
 * @author Johnny
 */
public class GremlinCollectionPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {


        // Get the Set of existing linked vertices for this property
        Set<Vertex> existingLinkedVertices = new HashSet<Vertex>();
        Set<Vertex> actualLinkedVertices = new HashSet<Vertex>();
        Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getName());
        while(edges!=null&&edges.hasNext()){
        	Edge currentEdge = edges.next();
        	Iterator<Vertex> existingVertexs = currentEdge.bothVertices();
        	while(existingVertexs!=null&&existingVertexs.hasNext()){
        		existingLinkedVertices.add(existingVertexs.next());
        	}
        	
        }

        // Now go through the collection of linked Objects
        for (Object linkedObj : (Collection) val) {

            // Find the linked vertex mapped to this linked Object
            Vertex linkedVertex = (Vertex) cascadingSchemas.get(linkedObj);
            if (linkedVertex == null) {
                String id = property.getRelatedSchema().getGraphId(linkedObj);
                if (id != null) {
                    linkedVertex = graphAdapter.getVertex(id);
                }
                if (linkedVertex == null) {
                    // No linked vertex yet, create it
                    linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
                }
            }

            // If this linked Object is new it will not be in the existingLinkedVertices Set
            if (!existingLinkedVertices.contains(linkedVertex)) {
                // New linked Object - add an Edge
                if (property.getDirection() == Direction.OUT) {
                    graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
                } else {
                    graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
                }
                // Add to existingLinkedVertices so to not delete it later on when cascading deletes
                existingLinkedVertices.add(linkedVertex);
            }

            // Add the linkedVertex to the actual linked vertices.
            actualLinkedVertices.add(linkedVertex);

            // Updates or saves the linkedObj into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, linkedObj, cascadingSchemas);
        }

        // For each disjointed vertex, remove it and the Edge associated with this property
        for (Vertex vertexToDelete : CollectionUtils.disjunction(existingLinkedVertices, actualLinkedVertices)) {
        	Iterator<Edge> edgesToDelete = vertexToDelete.edges(property.getDirection().opposite(), property.getName());
            while (edgesToDelete!=null&&edgesToDelete.hasNext()) {
                graphAdapter.removeEdge(edgesToDelete.next());
            }
            graphAdapter.removeVertex(vertexToDelete);
        }
    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(GremlinSchema<V> schema, GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        Set<V> collection = new HashSet<V>();
        Iterator<Edge> outEdges = vertex.edges(property.getDirection(), property.getName());
        while(outEdges!=null&&outEdges.hasNext()){
        	Edge outEdge = outEdges.next();
        	Iterator<Vertex> inVertexs = outEdge.vertices(property.getDirection().opposite());
        	while(inVertexs!=null&&inVertexs.hasNext()){
        		V linkedObject = schema.cascadeLoadFromGraph(inVertexs.next(), cascadingSchemas);
        		collection.add(linkedObject);
        	}
            
            
        }

        return collection;
    }
}
