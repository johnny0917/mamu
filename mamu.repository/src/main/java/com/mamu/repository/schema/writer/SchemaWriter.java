package com.mamu.repository.schema.writer;

import com.mamu.repository.schema.GremlinSchema;
import com.mamu.repository.tx.GremlinGraphFactory;

/**
 * Interface defining schema writer implementations.
 *
 * @author Johnny
 */
public interface SchemaWriter {

    void writeSchema(GremlinGraphFactory dbf, GremlinSchema<?> schema) throws SchemaWriterException;

}
