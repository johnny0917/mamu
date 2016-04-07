package com.mamu.repository.core.titan;

import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mamu.repository.core.GremlinGraphAdapter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Johnny
 */
public class TitanGraphAdapter extends GremlinGraphAdapter<TitanGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanGraphAdapter.class);

    @Override
    @Transactional(readOnly = false)
    public Vertex createVertex(TitanGraph graph, String className) {
        Vertex vertex = graph.addVertex(className);
        
        return vertex;
    }

}
