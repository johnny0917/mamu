package com.mamu.repository.core.titan;

import com.thinkaurelius.titan.core.TitanGraph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.core.SimpleGremlinRepository;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.tx.GremlinGraphFactory;
import com.mamu.repository.tx.titan.TitanGremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Titan的扩展实现，继承自 {@link SimpleGremlinRepository} ，提供自定义的方法实现，包括 {@code count()}
 * , {@code deleteAll()}, {@code findAll(Pageable)} 和 {@code findAll()}.
 *
 * @author Johnny
 */
public class TitanGremlinRepository<T> extends SimpleGremlinRepository<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TitanGremlinRepository.class);

	TitanGremlinGraphFactory graphFactory;

	public TitanGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> mapper) {
		super(dbf, graphAdapter, mapper);
		this.graphFactory = (TitanGremlinGraphFactory) dbf;
	}

	@Transactional(readOnly = false)
	protected Vertex createVertex(Graph graph) {
		Vertex vertex = ((TitanGraph) graph).addVertex(schema.getClassName());
		return vertex;
	}

	@Override
	@Transactional
	public long count() {
		long count = 0;
		try {
			for (Element el : findAllElementsForSchema()) {
				count++;
			}
		} catch (Exception e) {
		}
		return count;
	}

	@Transactional
	@Override
	public void deleteAll() {
		for (Element element : findAllElementsForSchema()) {
			element.remove();
		}
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		List<T> result = new ArrayList<T>();
		int total = 0;
		int prevOffset = pageable.getOffset();
		int offset = pageable.getOffset() + pageable.getPageSize();
		for (Element element : findAllElementsForSchema()) {
			if (total >= prevOffset && total < offset) {
				result.add(schema.loadFromGraph(element));
			}
			total++;
		}
		return new PageImpl<T>(result, pageable, total);
	}

	@Override
	public Iterable<T> findAll() {
		List<T> result = new ArrayList<T>();
		for (Element vertex : findAllElementsForSchema()) {
			result.add(schema.loadFromGraph(vertex));
		}
		return result;
	}

	public Iterable<Element> findAllElementsForSchema() {

		if (schema.isVertexSchema()) {
			return findALlVerticiesForSchema();
		} else if (schema.isEdgeSchema()) {
			return findAllEdgesForSchema();
		} else {
			throw new IllegalStateException("GremlinSchema is neither VERTEX or EDGE!!");
		}
	}

	public Iterable<Element> findALlVerticiesForSchema() {
		List<Element> result = new ArrayList<>();
		GraphTraversalSource g = graphFactory.graph().traversal();
		Iterator<Vertex> vIt = g.V().hasLabel(schema.getClassName());
		while (vIt != null && vIt.hasNext()) {
			result.add(vIt.next());

		}
		return result;
	}

	public Iterable<Element> findAllEdgesForSchema() {
		List<Element> result = new ArrayList<>();
		GraphTraversalSource g = graphFactory.graph().traversal();
		Iterator<Edge> eIt = g.E().hasLabel(schema.getClassName());
		while (eIt != null && eIt.hasNext()) {
			result.add(eIt.next());
		}
		return result;
	}

}
