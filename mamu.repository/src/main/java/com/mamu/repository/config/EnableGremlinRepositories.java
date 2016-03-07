package com.mamu.repository.config;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

import com.mamu.repository.support.MamuRepositoryFactoryBean;

import java.lang.annotation.*;

/**
 *支持Titan Gremlin的注解，注解后会自动扫描指定的package
 *
 * @author johnny
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(GremlinRepositoryRegistrar.class)
public @interface EnableGremlinRepositories {

    /**
     * {@link #basePackages()} 属性别名.允许更方便的指定扫描路径，符合spring的使用习惯  e.g.:
     * {@code @EnableGremlinRepositories("org.my.pkg")} 代替  {@code @EnableGremlinRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * 指定扫描的基本路径
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     */
    String repositoryImplementationPostfix() default "Impl";

    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link org.springframework.data.repository.query.QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link org.springframework.beans.factory.FactoryBean} class to be used for each repository instance. Defaults to
     * {@link GremlinRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default MamuRepositoryFactoryBean.class;
}
