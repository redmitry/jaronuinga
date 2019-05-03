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

package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.model.JsonSchema;
import es.elixir.bsc.json.schema.model.JsonType;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.model.SchemaArray;
import java.net.URI;
import java.util.HashSet;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;

/**
 * @author Dmitry Repchevsky
 */

public abstract class SchemaArrayImpl extends HashSet<JsonSchema>
                                      implements SchemaArray {
    
    private URI id;
    private String jsonPointer;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }
    
    public String getJsonPointer() {
        return jsonPointer;
    }

    @Override
    public boolean remove(JsonSchema schema) {
        return super.remove(schema);
    }

    @Override
    public boolean contains(JsonSchema schema) {
        return super.contains(schema);
    }

    public SchemaArrayImpl read(final JsonSubschemaParser parser, 
                                final JsonSchemaLocator locator, 
                                final String jsonPointer, 
                                final JsonArray array,
                                final JsonType type) throws JsonSchemaException {

        this.id = locator.uri;
        this.jsonPointer = jsonPointer;

        for (int i = 0, n = array.size(); i < n; i++) {
            final JsonValue value = array.get(i);
            final JsonObject object = JsonSchemaUtil.check(value, JsonValue.ValueType.OBJECT);
            final JsonSchema schema = parser.parse(locator, jsonPointer + "/" + Integer.toString(i) + "/", object, type);
            add(schema);
        }

        return this;
    }
}