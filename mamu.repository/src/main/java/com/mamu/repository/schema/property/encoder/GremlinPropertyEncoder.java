package com.mamu.repository.schema.property.encoder;

/**
 * Created by gman on 22/06/15.
 */
public interface GremlinPropertyEncoder {

    Object encode(Object obj);

    Object decode(Object obj);
}
