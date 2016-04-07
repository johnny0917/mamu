package com.mamu.repository.schema;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mamu.repository.core.GremlinGraphAdapter;
import com.mamu.repository.core.GremlinRepository;
import com.mamu.repository.schema.property.GremlinAdjacentProperty;
import com.mamu.repository.schema.property.GremlinProperty;
import com.mamu.repository.schema.property.accessor.GremlinFieldPropertyAccessor;
import com.mamu.repository.schema.property.accessor.GremlinIdFieldPropertyAccessor;
import com.mamu.repository.schema.property.accessor.GremlinPropertyAccessor;
import com.mamu.repository.schema.property.encoder.GremlinPropertyEncoder;
import com.mamu.repository.schema.property.mapper.GremlinPropertyMapper;
import com.mamu.repository.tx.GremlinGraphFactory;
import com.mamu.repository.utils.GenericsUtil;

import java.util.*;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (VERTEX, EDGE) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Johnny
 */
public abstract class GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinSchema.class);

    public GremlinSchema(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinSchema() {
        classType = (Class<V>) GenericsUtil.getGenericType(this.getClass());
    }

    private String className;
    private Class<V> classType;
    private GremlinRepository<V> repository;
    private GremlinGraphFactory graphFactory;
    private GremlinIdFieldPropertyAccessor idAccessor;
    private GremlinPropertyMapper idMapper;
    private GremlinPropertyEncoder idEncoder;

    private GremlinAdjacentProperty outProperty;
    private GremlinAdjacentProperty inProperty;

    private Map<String, GremlinProperty> propertyMap = new HashMap<String, GremlinProperty>();
    private Map<String, GremlinProperty> fieldToPropertyMap = new HashMap<String, GremlinProperty>();
    private Multimap<Class<?>, GremlinProperty> typePropertyMap = LinkedListMultimap.create();

    private Set<GremlinProperty> properties = new HashSet<GremlinProperty>();

    public void addProperty(GremlinProperty property) {
        property.setSchema(this);
        if (property instanceof GremlinAdjacentProperty) {
            if (((GremlinAdjacentProperty) property).getDirection() == Direction.OUT) {
                outProperty = (GremlinAdjacentProperty) property;
            } else {
                inProperty = (GremlinAdjacentProperty) property;
            }
        }
        properties.add(property);
        propertyMap.put(property.getName(), property);
        fieldToPropertyMap.put(property.getAccessor().getField().getName(), property);
        typePropertyMap.put(property.getType(), property);
    }

    public GremlinProperty getPropertyForFieldname(String fieldname) {
        return fieldToPropertyMap.get(fieldname);
    }

    public GremlinPropertyMapper getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(GremlinPropertyMapper idMapper) {
        this.idMapper = idMapper;
    }

    public GremlinGraphFactory getGraphFactory() {
        return graphFactory;
    }

    public void setGraphFactory(GremlinGraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }

    public GremlinRepository<V> getRepository() {
        return repository;
    }

    public void setRepository(GremlinRepository<V> repository) {
        this.repository = repository;
    }

    public GremlinPropertyEncoder getIdEncoder() {
        return idEncoder;
    }

    public void setIdEncoder(GremlinPropertyEncoder idEncoder) {
        this.idEncoder = idEncoder;
    }

    public GremlinIdFieldPropertyAccessor getIdAccessor() {
        return idAccessor;
    }

    public void setIdAccessor(GremlinIdFieldPropertyAccessor idAccessor) {
        this.idAccessor = idAccessor;
    }

    public Collection<String> getPropertyNames() {
        return propertyMap.keySet();
    }

    public GremlinPropertyAccessor getAccessor(String property) {
        return propertyMap.get(property).getAccessor();
    }

    public Collection<GremlinProperty> getProperties() {
        return properties;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<V> getClassType() {
        return classType;
    }

    public void setClassType(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinProperty getProperty(String property) {
        return propertyMap.get(property);
    }

    public Collection<GremlinProperty> getPropertyForType(Class<?> type) {
        return typePropertyMap.get(type);
    }

    public boolean isVertexSchema() {
        return this instanceof GremlinVertexSchema;
    }

    public boolean isEdgeSchema() {
        return this instanceof GremlinEdgeSchema;
    }

    public GremlinAdjacentProperty getOutProperty() {
        return outProperty;
    }

    public GremlinAdjacentProperty getInProperty() {
        return inProperty;
    }

    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Object... noCascade) {
        Map<Object, Element> noCascadingMap = new HashMap<>();
        for (Object skip : noCascade) {
            noCascadingMap.put(skip, element);
        }
        cascadeCopyToGraph(graphAdapter, element, obj, noCascadingMap);
    }

    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj) {
        cascadeCopyToGraph(graphAdapter, element, obj, new HashMap<Object, Element>());
    }

    public void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Map<Object, Element> noCascadingMap) {

        if (noCascadingMap.containsKey(obj)) {
            return;
        }
        noCascadingMap.put(obj, element);

        for (GremlinProperty property : getProperties()) {

            try {

                GremlinPropertyAccessor accessor = property.getAccessor();
                Object val = accessor.get(obj);

                if (val != null) {
                    property.copyToVertex(graphAdapter, element, val, noCascadingMap);
                }
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()), e);
            }
        }
    }

    public V loadFromGraph(Element element) {

        return cascadeLoadFromGraph(element, new HashMap<>());
    }

    public V cascadeLoadFromGraph(Element element, Map<Object, Object> noCascadingMap) {

        V obj = (V) noCascadingMap.get(element.id());
        if (obj == null) {
            try {
                obj = getClassType().newInstance();

                GremlinPropertyAccessor idAccessor = getIdAccessor();
                idAccessor.set(obj, encodeId(element.id().toString()));
                noCascadingMap.put(element.id(), obj);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
            }
            for (GremlinProperty property : getProperties()) {

                Object val = property.loadFromVertex(element, noCascadingMap);

                GremlinPropertyAccessor accessor = property.getAccessor();
                try {
                    accessor.set(obj, val);
                } catch (Exception e) {
                    LOGGER.warn(String.format("Could not load property %s of %s", property, obj.toString()), e);
                }
            }
        }
        return obj;
    }

    public String getGraphId(Object obj) {
        return decodeId(getIdAccessor().get(obj));
    }

    public void setObjectId(V obj, Element element) {
        getIdAccessor().set(obj, encodeId(element.id().toString()));
    }

    public String getObjectId(V obj) {
        String id = getIdAccessor().get(obj);
        if (id != null) {
            return decodeId(id);
        }
        return null;
    }

    public String encodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.encode(id).toString();
        }
        return id;
    }

    public String decodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.decode(id).toString();
        }
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinSchema{");
        sb.append("className='").append(className).append('\'');
        sb.append(", classType=").append(classType);
        sb.append('}');
        return sb.toString();
    }
}
