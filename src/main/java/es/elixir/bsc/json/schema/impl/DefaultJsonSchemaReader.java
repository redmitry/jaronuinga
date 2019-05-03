/**
 * *****************************************************************************
 * Copyright (C) 2017 ELIXIR ES, Spanish National Bioinformatics Institute (INB)
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

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.model.JsonSchema;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Repchevsky
 */

public class DefaultJsonSchemaReader implements JsonSchemaReader {
    
    private final Map<URI, JsonSchema> schemas;
    
    public DefaultJsonSchemaReader() {
        schemas = new HashMap<>();
    }
    
    @Override
    public JsonSchema read(URL url) throws JsonSchemaException {
        try {
            return read(new DefaultJsonSchemaLocator(url.toURI()));
        } catch (URISyntaxException ex) {
            throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                          new Object[] {url.toString()}));
        }
    }
    
    @Override
    public JsonSchema read(final JsonSchemaLocator locator) throws JsonSchemaException {
        JsonSchema schema = schemas.get(locator.uri);
        if (schema == null) {
            JsonReaderFactory factory = Json.createReaderFactory(Collections.EMPTY_MAP);

            try (InputStream in = locator.getInputStream()){
                JsonReader reader = factory.createReader(in);
                JsonValue val = reader.readValue();

                if (JsonValue.ValueType.OBJECT != val.getValueType()) {
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_OBJECT_TYPE, 
                        new Object[] {"root object", val.getValueType().name(), JsonValue.ValueType.OBJECT.toString()}));
                }
                schema = new DefaultJsonSchemaParser(locator).parse(null, "/", val.asJsonObject());
                schemas.put(locator.uri, schema);
            } catch (IOException ex) {
                throw new JsonSchemaException(new ParsingError(ex.getMessage()));
            }
        }
        return schema;
    }
}
