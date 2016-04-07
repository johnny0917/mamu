package com.mamu.repository.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinLinkProperty;
import com.mamu.repository.schema.property.GremlinRelatedProperty;

import java.util.*;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s.
 * There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge.
 * If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with
 * this property mapper</li>
 * </ul>
 *
 * @author Johnny
 */
public class GremlinCollectionViaPropertyMapper extends GremlinLinkPropertyMapper {

	@Override
	public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex,
			Object val, Map<Object, Object> cascadingSchemas) {

		// Get the Set of existing linked vertices for this property
		Set<Edge> existingLinkedEdges = new HashSet<>();
		Set<Edge> actualLinkedEdges = new HashSet<>();
		Iterator<Edge> eIt = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
		while (eIt != null && eIt.hasNext()) {
			existingLinkedEdges.add(eIt.next());
		}

		GremlinAdjacentProperty adjacentProperty = property.getAdjacentProperty();

		// Check we found the adjacent property
		if (adjacentProperty != null) {

			// Now go through the collection of linked Objects
			for (Object linkedObj : (Collection) val) {

				Object adjacentObj = adjacentProperty.getAccessor().get(linkedObj);
				if (adjacentObj != null) {
					Vertex adjacentVertex = graphAdapter.findOrCreateVertex(
							adjacentProperty.getRelatedSchema().getObjectId(adjacentObj),
							adjacentProperty.getRelatedSchema().getClassName());

					// If we have the adjacent vertex then we can continue
					if (adjacentVertex != null) {

						Edge linkedEdge = null;

						// get the current edge for this property
						Iterator<Edge> edges = vertex.edges(property.getDirection(),
								property.getRelatedSchema().getClassName());
						while (edges.hasNext()) {
							Edge edge = edges.next();
							Iterator<Vertex> vIt = edge.vertices(property.getDirection().opposite());
							while (vIt != null && vIt.hasNext()) {
								Vertex v = vIt.next();
								if (v != null && v.equals(adjacentVertex)) {
									existingLinkedEdges.add(edge);
									linkedEdge = edge;
									break;
								}
							}
						}

						if (linkedEdge == null) {
							if (property.getDirection() == Direction.OUT) {
								linkedEdge = graphAdapter.addEdge(null, vertex, adjacentVertex,
										property.getRelatedSchema().getClassName());
							} else {
								linkedEdge = graphAdapter.addEdge(null, adjacentVertex, vertex,
										property.getRelatedSchema().getClassName());
							}
						}

						existingLinkedEdges.add(linkedEdge);
						actualLinkedEdges.add(linkedEdge);
						// Updates or saves the val into the linkedVertex
						adjacentProperty.getRelatedSchema().cascadeCopyToGraph(graphAdapter, adjacentVertex,
								adjacentObj, cascadingSchemas);
					}
				}
			}
		}

		// For each disjointed edge, remove it
		for (Edge vertexToDelete : CollectionUtils.disjunction(existingLinkedEdges, actualLinkedEdges)) {
			vertexToDelete.remove();
		}

	}

	@Override
	public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex,
			Map<Object, Object> cascadingSchemas) {
		return loadCollection(property.getRelatedSchema(), property, vertex, cascadingSchemas);
	}

	private <V> Set<V> loadCollection(GremlinSchema<V> schema, GremlinRelatedProperty property, Vertex vertex,
			Map<Object, Object> cascadingSchemas) {
		Set<V> collection = new HashSet<V>();

		Iterator<Edge> eIt = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
		while (eIt != null && eIt.hasNext()) {
			Edge e = eIt.next();
			V linkedObject = schema.cascadeLoadFromGraph(e, cascadingSchemas);
			collection.add(linkedObject);
		}
		return collection;
	}
}
