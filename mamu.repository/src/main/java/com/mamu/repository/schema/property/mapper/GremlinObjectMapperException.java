package com.mamu.repository.schema.property.mapper;

/**
 * @author Johnny
 */
public class GremlinObjectMapperException extends Throwable {
    public GremlinObjectMapperException(String msg) {super(msg);}

    public GremlinObjectMapperException(String msg, Exception e) {
        super(msg, e);
    }
}
