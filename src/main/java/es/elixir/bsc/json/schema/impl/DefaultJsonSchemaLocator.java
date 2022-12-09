/**
 * *****************************************************************************
 * Copyright (C) 2022 ELIXIR ES, Spanish National Bioinformatics Institute (INB)
 * and Barcelona Supercomputing Center (BSC)
 *
 * Modifications to the initial code base are copyright of their respective
 * authors, or their employers as appropriate.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *****************************************************************************
 */

package es.elixir.bsc.json.schema.impl;

import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.model.JsonSchema;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Repchevsky
 */
    
public class DefaultJsonSchemaLocator extends JsonSchemaLocator {

    protected final Map<URI, JsonObject> schemas;

    public DefaultJsonSchemaLocator(URI uri) {
        this(uri, new HashMap<>());
    }
    
    protected DefaultJsonSchemaLocator(URI uri, Map<URI, JsonObject> schemas) {
        super(uri);
        this.schemas = schemas;
    }

    @Override
    public void setSchema(JsonObject schema) {
        schemas.put(uri, schema);
    }

    @Override
    public JsonObject getSchema(String jsonPointer) 
            throws IOException, JsonException {
        return getSchema(this.uri, jsonPointer);
    }

    @Override
    public JsonObject getSchema(URI uri, String jsonPointer)
            throws IOException, JsonException {
        
        JsonObject schema = schemas.get(uri);
        if (schema == null) {
            final JsonReaderFactory factory = Json.createReaderFactory(Collections.EMPTY_MAP);
            try (InputStream in = uri.toURL().openStream()){
                final JsonReader reader = factory.createReader(in);
                final JsonValue val = reader.readValue();

                if (JsonValue.ValueType.OBJECT == val.getValueType()) {
                    schema = val.asJsonObject();
                }
                setSchema(schema);
            }
        }

        if ("/".endsWith(jsonPointer)) {
            return schema;
        }
        
        final JsonPointer pointer = Json.createPointer(jsonPointer);
        
        // there is a bug as containsValue() rises an exception when not found. 
        if (pointer.containsValue(schema)) {
            final JsonValue subschema = pointer.getValue(schema);
            if (JsonValue.ValueType.OBJECT == subschema.getValueType()) {
                return subschema.asJsonObject();
            }
        }

        return null;
    }

    @Override
    public JsonSchemaLocator resolve(URI uri) {
        return new DefaultJsonSchemaLocator(super.uri.resolve(uri), schemas);
    }
    
    @Override
    public void putSchema(JsonSchema schema) {
        
    }

}
