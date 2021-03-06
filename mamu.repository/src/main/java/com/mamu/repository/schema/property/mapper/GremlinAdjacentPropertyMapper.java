package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinLinkProperty;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinAdjacentProperty}s.
 *
 * @author Johnny
 */
public class GremlinAdjacentPropertyMapper implements GremlinPropertyMapper<GremlinAdjacentProperty, Edge> {

	@Override
	public void copyToVertex(GremlinAdjacentProperty property, GremlinGraphAdapter graphAdapter, Edge edge, Object val,
			Map<Object, Object> cascadingSchemas) {

		Iterator<Vertex> vertexs = edge.vertices(property.getDirection());

		while (vertexs != null && vertexs.hasNext()) {
			Vertex linkedVertex = vertexs.next();
			if (linkedVertex == null) {
				linkedVertex = (Vertex) cascadingSchemas.get(val);
			}

			if (linkedVertex != null) {
				// Updates or saves the val into the linkedVertex
				property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
			}
		}

	}

	@Override
	public <K> Object loadFromVertex(GremlinAdjacentProperty property, Edge edge,
			Map<Object, Object> cascadingSchemas) {
		Object val = null;
		
		Iterator<Vertex> linkedVertexs = edge.vertices(property.getDirection());
		while(linkedVertexs!=null&&linkedVertexs.hasNext()){
			Vertex linkedVertex = linkedVertexs.next();
			if (linkedVertex != null) {
				val = property.getRelatedSchema().cascadeLoadFromGraph(linkedVertex, cascadingSchemas);
			}
		}
		
		return val;
	}
}
