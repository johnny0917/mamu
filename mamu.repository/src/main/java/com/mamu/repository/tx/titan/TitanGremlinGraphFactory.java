package com.mamu.repository.tx.titan;

import static org.springframework.util.Assert.notNull;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mamu.repository.tx.AbstractGremlinGraphFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

/**
 * An {@link AbstractGremlinGraphFactory} for OrentDB providing an {@link TitanGraph} implementation of {@link com.tinkerpop.blueprints.Graph}.
 *
 * @author Johnny
 */
public class TitanGremlinGraphFactory extends AbstractGremlinGraphFactory<TitanGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanGremlinGraphFactory.class);

    private TitanGraph graph = null;
    private Configuration configuration;

    @Override
    protected void createPool() {
        if(configuration != null){
            graph = TitanFactory.open(configuration);
        } else {
            notNull(url);
            graph = TitanFactory.open(url);
        }
    }

    @Override
    public boolean isActive(TitanGraph graph) {
    	return graph.openManagement().isOpen();
        //return graph.getManagementSystem().isOpen();
    	//return graph.isOpen();
    }

    @Override
    public boolean isClosed(TitanGraph graph) {
        return graph.isClosed();
    }

    @Override
    public void beginTx(TitanGraph graph) {
        graph.newTransaction();
    }

    @Override
    public void commitTx(TitanGraph graph) {
        graph.tx().commit();
    }

    @Override
    public void rollbackTx(TitanGraph graph) {
        graph.tx().rollback();
    }

    @Override
    public TitanGraph openGraph() {
        if (graph == null || graph.isClosed()) {
            createPool();
        }
        return graph;
    }

    @Override
    protected void createGraph() {
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
