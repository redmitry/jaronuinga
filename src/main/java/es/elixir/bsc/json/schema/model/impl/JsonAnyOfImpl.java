/**
 * *****************************************************************************
 * Copyright (C) 2023 ELIXIR ES, Spanish National Bioinformatics Institute (INB)
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
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonAnyOf;
import java.util.ArrayList;
import java.util.List;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.JsonType;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class JsonAnyOfImpl extends SchemaArrayImpl 
                           implements JsonAnyOf {
    
    public SchemaArrayImpl read(final JsonSubschemaParser parser, 
                                final JsonSchemaLocator locator,
                                final JsonSchemaElement parent,
                                final String jsonPointer, 
                                final JsonObject object,
                                final JsonArray types) throws JsonSchemaException {
        
        this.id = locator.uri;
        this.parent = parent;
        this.jsonPointer = jsonPointer.isEmpty() ? "/" : jsonPointer;
        
        if (types == null) {
            for (JsonType val : JsonType.values()) {
                try {
                    final AbstractJsonSchema s = parser.parse(locator, this, jsonPointer, object, val);
                    if (s != null) {
                        add(s);
                    }
                } catch(JsonSchemaException ex) {
                    // do nothing
                }
            }
        } else {
            for (JsonValue val : types) {
                if (JsonValue.ValueType.STRING != val.getValueType()) {
                    
                }
                try {
                     final JsonType type = JsonType.fromValue(((JsonString)val).getString());
                     add(parser.parse(locator, parent, jsonPointer, object, type));
                } catch(IllegalArgumentException ex) {
                    throw new JsonSchemaException(
                        new ParsingError(ParsingMessage.UNKNOWN_OBJECT_TYPE, new Object[] {val}));
                }
            }            
        }        
        return this;
    }

    @Override
    public boolean validate(String jsonPointer, JsonValue value, JsonValue parent, 
            List<String> evaluated, List<ValidationError> errors,
            JsonSchemaValidationCallback<JsonValue> callback) {
        
        List<ValidationError> err = new ArrayList<>();
        for (AbstractJsonSchema schema : this) {
            final List<String> eva = new ArrayList();
            if (schema.validate(jsonPointer, value, parent, eva, err, callback)) {
                evaluated.addAll(eva);
                return true; // found the schema that matches
            }
        }

        errors.addAll(err);
        errors.add(new ValidationError(getId(), getJsonPointer(), 
                jsonPointer, ValidationMessage.OBJECT_ANY_OF_CONSTRAINT_MSG));
        
        return false;
    }
}
