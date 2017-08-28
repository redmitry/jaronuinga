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
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonNot;
import es.elixir.bsc.json.schema.model.JsonSchema;
import java.net.URI;
import java.util.List;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class JsonNotImpl implements JsonNot {
    
    private URI id;
    private String jsonPointer;

    public URI getId() {
        return id;
    }
    
    public String getJsonPointer() {
        return jsonPointer;
    }

    private JsonSchema schema;
    
    public JsonNotImpl() {}
    
    public JsonNotImpl(JsonSchema schema) {
        this.schema = schema;
    }
    
    @Override
    public JsonSchema getJsonSchema() {
        return schema;
    }

    @Override
    public void setJsonSchema(JsonSchema schema) {
        this.schema = schema;
    }
    
    public JsonNotImpl read(JsonSchemaParser parser, 
                            JsonSchemaLocator locator, 
                            String jsonPointer, 
                            JsonObject object) throws JsonSchemaException {

        this.id = locator.uri;
        this.jsonPointer = jsonPointer;
        
        this.schema = parser.parse(locator, jsonPointer, object);
        
        return this;
    }
    
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {
        
        final int nerrors = errors.size();
        schema.validate(value, errors, callback);
        if (errors.size() == nerrors) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.OBJECT_NOT_CONSTRAINT));
        }
    }
}
