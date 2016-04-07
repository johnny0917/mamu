package com.mamu.repository.schema.generator;

import java.lang.annotation.Annotation;

/**
 * Interface defining an annotated {@link SchemaGenerator} providing the entity and embedded annotation types.
 *
 * @author Johnny
 */
public interface AnnotatedSchemaGenerator extends SchemaGenerator {
    Class<? extends Annotation> getVertexAnnotationType();

    Class<? extends Annotation> getEmbeddedAnnotationType();

    Class<? extends Annotation> getEdgeAnnotationType();
}
