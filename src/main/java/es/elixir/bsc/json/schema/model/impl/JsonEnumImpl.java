/**
 * *****************************************************************************
 * Copyright (C) 2021 ELIXIR ES, Spanish National Bioinformatics Institute (INB)
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
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonEnum;
import es.elixir.bsc.json.schema.model.JsonType;
import java.util.List;
import javax.json.JsonObject;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.json.Json;

/**
 * @author Dmitry Repchevsky
 */

public class JsonEnumImpl extends PrimitiveSchemaImpl implements JsonEnum {

    private List<JsonValue> values;

    @Override
    public List<JsonValue> getValues() {
        return values;
    }

    @Override
    public void setValues(List<JsonValue> values) {
        this.values = values;
    }
    
    @Override
    public JsonEnumImpl read(final JsonSubschemaParser parser, 
                             final JsonSchemaLocator locator, 
                             final String jsonPointer, 
                             final JsonObject object,
                             final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, jsonPointer, object, type);
        
        values = JsonSchemaUtil.check(object.get(ENUM), JsonValue.ValueType.ARRAY);
        
        return this;
    }

    @Override
    public void validate(JsonValue value, JsonValue parent, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) {
        
        if (value.getValueType() == JsonValue.ValueType.ARRAY || 
            value.getValueType() == JsonValue.ValueType.OBJECT) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ENUM_INVALID_VALUE_TYPE, value.getValueType().name()));
        } else if (values == null || !values.contains(value)) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ENUM_INVALID_VALUE, value.toString(), values.toString()));
         
        }
        
        super.validate(value, parent, errors, callback);
    }    
}
