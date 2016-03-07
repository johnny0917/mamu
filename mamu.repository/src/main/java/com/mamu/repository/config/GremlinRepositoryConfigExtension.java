package com.mamu.repository.config;

import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import com.mamu.repository.support.MamuRepositoryFactoryBean;

/**
 * {@link org.springframework.data.repository.config.RepositoryConfigurationExtension} for Gremlin.
 *
 * @author Johnny
 */
public class GremlinRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryClassName()
     * 获得定制化的FactoryBean
     */
    public String getRepositoryFactoryClassName() {
        return MamuRepositoryFactoryBean.class.getName();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getModulePrefix()
     * 定义spring module的前缀名称  默认为 gremlin
     */
    @Override
    protected String getModulePrefix() {
        return "gremlin";
    }


}
