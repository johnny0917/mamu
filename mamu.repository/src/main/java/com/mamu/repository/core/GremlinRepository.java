package com.mamu.repository.core;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The Gremlin specific extension of {@link org.springframework.data.repository.PagingAndSortingRepository}.
 * <i>Note: </i> This implementation expects an if of type String. To be fixed.
 *
 * @param <T> the generic type to handle
 * @author Johnny
 */
@NoRepositoryBean
public interface GremlinRepository<T> extends PagingAndSortingRepository<T, String> { }
