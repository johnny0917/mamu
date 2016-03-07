package com.mamu.repository.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar} 允许支持 {@link EnableGremlinRepositories} 注解.
 *
 * @author Johnny
 */
public class GremlinRepositoryRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableGremlinRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new GremlinRepositoryConfigExtension();
    }
}
