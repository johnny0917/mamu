package com.mamu.repository.schema.generator.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import com.mamu.repository.annotation.Index;
import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.schema.generator.AnnotatedSchemaGenerator;
import com.mamu.repository.schema.generator.BasicSchemaGenerator;
import com.mamu.repository.schema.generator.SchemaGeneratorException;
import com.mamu.repository.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * A concrete {@link BasicSchemaGenerator} allowing for annotating entities with the commonly used JPA 2.0
 * specified schema generation annotations.
 * <br/>
 * Annotations currently implemented:
 * <li>
 * Entity
 * </li>
 * <li>
 * Id
 * </li>
 * <li>
 * Embedded
 * </li>
 * <li>
 * Transient
 * </li>
 * <li>
 * Column
 * </li>
 * <li>
 * AttributeOverrides
 * </li>
 * <li>
 * AttributeOverride
 * </li>
 * <li>
 * Enumerated
 * </li>
 * <li>
 * OneToOne
 * </li>
 * <li>
 * ManyToOne
 * </li>
 * <li>
 * OneToMany
 * </li>
 *
 * @author Gman
 */
public class JpaSchemaGenerator extends BasicSchemaGenerator implements AnnotatedSchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaSchemaGenerator.class);

    public JpaSchemaGenerator() {
        this(null);
    }

    public JpaSchemaGenerator(GremlinPropertyEncoder idEncoder) {
        super(idEncoder, new JpaGremlinPropertyFactory());
    }

    /**
     * Returns the Vertex name. By default the Class' simple name is used. If it is annotated with @Entity and the name parameter is
     * not empty, then that is used.
     *
     * @param clazz The Class to find the name of
     * @return The vertex name of the class
     */
    @Override
    protected String getVertexName(Class<?> clazz) {

        String className = super.getVertexName(clazz);
        Entity entity = AnnotationUtils.getAnnotation(clazz, Entity.class);
        if (entity != null && !StringUtils.isEmpty(entity.name())) {
            className = entity.name();
        }

        return className;
    }

    @Override
    protected boolean shouldProcessField(GremlinSchema schema, Field field) {
        boolean noTransientAnnotation = AnnotationUtils.getAnnotation(field, Transient.class) == null;
        return super.shouldProcessField(schema, field) && noTransientAnnotation;
    }

    @Override
    protected Field getIdField(Class<?> cls) throws SchemaGeneratorException {
        final Field[] idFields = { null };

        ReflectionUtils.doWithFields(cls, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                Id id = AnnotationUtils.getAnnotation(field, Id.class);
                if (id != null) {
                    idFields[0] = field;
                }
            }
        });
        if (idFields[0] == null) {
            try {
                idFields[0] = super.getIdField(cls);
            } catch (SchemaGeneratorException e) {
                throw new SchemaGeneratorException("Cannot generate schema as there is no ID field. You must have a field of type Long or String annotated with @Id or named 'id'.");
            }
        }
        return idFields[0];
    }

    @Override
    protected Class<?> getEnumType(Field field) {
        Enumerated enumerated = AnnotationUtils.getAnnotation(field, Enumerated.class);
        if (enumerated != null && enumerated.value() == EnumType.ORDINAL) {
            return Integer.class;
        } else {
            return super.getEnumType(field);
        }
    }

    @Override
    protected Index.IndexType getIndexType(Field field) {
        Index.IndexType index = super.getIndexType(field);
        if (index == null || index == Index.IndexType.NONE) {
            if (isPropertyUnique(field)) {
                index = Index.IndexType.UNIQUE;
            }
        }
        return index;
    }

    @Override
    protected boolean isPropertyUnique(Field field) {

        boolean unique = super.isPropertyUnique(field);

        if (!unique) {
            // If annotated with @Column, use the unique parameter of the annotation
            Column column = AnnotationUtils.getAnnotation(field, Column.class);
            if (column != null) {
                // Get the unique param while we're here
                unique = column.unique();
            }
        }

        return unique;
    }

    @Override
    protected String getPropertyName(Field field, Field rootEmbeddedField) {
        String name = field.getName();

        // If annotated with @Column, use the name parameter of the annotation
        Column column = AnnotationUtils.getAnnotation(field, Column.class);

        // If this is an embedded field then look for an AttributeOverride Column
        if (rootEmbeddedField != null) {
            AttributeOverride override = checkAttributeOverrides(rootEmbeddedField, field);
            if (override != null) {
                column = override.column();
            }
        }

        if (column != null) {
            if (!StringUtils.isEmpty(column.name())) {
                name = column.name();
            }
        }

        return name;
    }

    private AttributeOverride checkAttributeOverrides(Field embeddedField, Field field) {

        // First check for @AttributeOverride
        AttributeOverride attributeOverride = checkAttributeOverride(embeddedField, field);
        if (attributeOverride == null) {
            // Then check @AttributeOverrides
            AttributeOverrides attributeOverrides = embeddedField.getAnnotation(AttributeOverrides.class);
            if (attributeOverrides != null && attributeOverrides.value() != null && attributeOverrides.value().length > 0) {
                for (AttributeOverride ao : attributeOverrides.value()) {
                    // Find the first that matches
                    attributeOverride = checkAttributeOverride(field, ao);
                    if (attributeOverride != null) {
                        break;
                    }
                }
            }
        }
        return attributeOverride;
    }

    private AttributeOverride checkAttributeOverride(Field embeddedField, Field field) {

        AttributeOverride attributeOverride = embeddedField.getAnnotation(AttributeOverride.class);
        return checkAttributeOverride(field, attributeOverride);
    }

    private AttributeOverride checkAttributeOverride(Field field, AttributeOverride attributeOverride) {
        if (attributeOverride != null && !StringUtils.isEmpty(attributeOverride.name()) && attributeOverride.column() != null) {
            if (field.getName().equals(attributeOverride.name())) {
                return attributeOverride;
            }
        }
        return null;
    }

    @Override
    protected boolean isLinkField(Class<?> cls, Field field) {
        return isVertexClass(cls) && (AnnotationUtils.getAnnotation(field, OneToOne.class) != null || AnnotationUtils.getAnnotation(field, ManyToOne.class) != null);
    }


    @Override
    protected boolean isLinkOutward(Class<?> cls, Field field) {
        OneToOne oneToOne = AnnotationUtils.getAnnotation(field, OneToOne.class);
        //        if (oneToOne == null) {
        //            throw new IllegalStateException("Expected @OneToOne, but was null!");
        //        }
        if (oneToOne != null) {
            return oneToOne.mappedBy().length() == 0;
        }


        ManyToOne manyToOne = AnnotationUtils.getAnnotation(field, ManyToOne.class);
        if (manyToOne != null) {
            return true;
        }

        OneToMany oneToMany = AnnotationUtils.getAnnotation(field, OneToMany.class);
        if (oneToMany != null) {
            return oneToMany.mappedBy().length() == 0;
        }

        return true;

    }

    @Override
    protected boolean isCollectionField(Class<?> cls, Field field) {
        return super.isCollectionField(cls, field) && AnnotationUtils.getAnnotation(field, OneToMany.class) != null;
    }

    @Override
    protected boolean isEmbeddedField(Class<?> cls, Field field) {
        return isEmbeddedClass(cls) && AnnotationUtils.getAnnotation(field, Embedded.class) != null;
    }

    @Override
    public Class<? extends Annotation> getVertexAnnotationType() {
        return Entity.class;
    }

    @Override
    public Class<? extends Annotation> getEmbeddedAnnotationType() {
        return Embeddable.class;
    }

    @Override
    public Class<? extends Annotation> getEdgeAnnotationType() {
        return null;
    }
}
