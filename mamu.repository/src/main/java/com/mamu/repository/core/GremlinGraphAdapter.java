package com.mamu.repository.core;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.tx.GremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for creating verticies and edges on the Graph. This class can be
 * subclassed for concrete implementations if need be.
 */
public class GremlinGraphAdapter<G extends Graph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinGraphAdapter.class);

    @Autowired
    private GremlinGraphFactory<G> graphFactory;

    @Transactional(readOnly = false)
    public Vertex createVertex(String className) {
        G graph = graphFactory.graph();
        return createVertex(graph, className);
    }
/**
 * 构建一个空的Vertex
 * @param graph
 * @param className
 * @return
 */
    @Transactional(readOnly = false)
    public Vertex createVertex(G graph, String className) {
        LOGGER.info("CREATING VERTEX: " + className);
        Vertex vertex = graph.addVertex(new HashMap<>()); 
        LOGGER.info("CREATING VERTEX:ID="+vertex.id());
        return vertex;
    }

    @Transactional(readOnly = true)
    public Vertex findOrCreateVertex(String id, String className) {
        Vertex playerVertex = findVertexById(id);
        if (playerVertex == null) {
            playerVertex = createVertex(className);
        }
        return playerVertex;
    }

    @Transactional(readOnly = true)
    public Vertex findVertexById(String id) {
        if (id == null) {
            return null;
        }
        G graph = graphFactory.graph();
        Iterator<Vertex> vertexs = graph.vertices(decodeId(id));
        while(vertexs!=null&&vertexs.hasNext()){
        	return vertexs.next();
        }
        
        Iterator<Vertex> vertexsNoDecode = graph.vertices(id);
        while(vertexsNoDecode!=null&&vertexsNoDecode.hasNext()){
        	return vertexsNoDecode.next();
        }
//        Vertex vertex = graph.getVertex(decodeId(id));
//        if (vertex == null) {
//            vertex = graph.getVertex(id);
//        }
        return null;
    }

    @Transactional(readOnly = true)
    public Edge findEdgeById(String id) {
    	if(id==null||"".equals(id)){
    		return null;
    	}
        G graph = graphFactory.graph();
        
        Iterator<Edge> edges = graph.edges(decodeId(id));
        while(edges!=null&&edges.hasNext()){
        	return edges.next();
        }
        
        Iterator<Edge> edgesNoDecode = graph.edges(id);
        while(edgesNoDecode!=null&&edgesNoDecode.hasNext()){
        	return edgesNoDecode.next();
        }
        
//        Edge edge = graph.getEdge(decodeId(id));
//        if (edge == null) {
//            edge = graph.getEdge(id);
//        }
        return null;
    }

    @Transactional(readOnly = true)
    public Vertex getVertex(String id) {
//        if (id == null||"".equals(id)) {
//            return null;
//        }
        return this.findVertexById(id);
    }

    @Transactional(readOnly = true)
    public Edge getEdge(String id) {
        return this.findEdgeById(id);
    }

    @Transactional(readOnly = false)
    public Vertex createVertex(GremlinSchema schema) {
        return createVertex(schema.getClassName());
    }

    @Transactional(readOnly = false)
    public Edge addEdge(Object o, Vertex outVertex, Vertex inVertex, String name) {
        Edge edge = outVertex.addEdge(name, inVertex);
        return edge;
    }

    @Transactional(readOnly = false)
    public void removeEdge(Edge edge) {
    	edge.remove();
    }

    @Transactional(readOnly = false)
    public void removeVertex(Vertex vertexToDelete) {
        vertexToDelete.remove();
    }

    public String encodeId(String id) {
        return id;
    }

    public String decodeId(String id) {
        return id;
    }

}
