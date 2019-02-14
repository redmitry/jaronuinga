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
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonArraySchema;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.model.JsonSchema;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonNumber;

/**
 * @author Dmitry Repchevsky
 */

public class JsonArraySchemaImpl extends PrimitiveSchema
                                 implements JsonArraySchema {

    private List<JsonSchema> items;
    private Boolean additionalItems;
    private JsonSchema additionalItemsSchema;
    
    private Long minItems;
    private Long maxItems;
    
    @Override
    public List<JsonSchema> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        
        return items;
    }

    @Override
    public Long getMinItems() {
        return minItems;
    }
    
    @Override
    public void setMinItems(Long minItems) {
        this.minItems = minItems;
    }

    @Override
    public Long getMaxItems() {
        return maxItems;
    }
    
    @Override
    public void setMaxItems(Long maxItems) {
        this.maxItems = maxItems;
    }
    
    @Override
    public JsonSchema getAdditionalItems() {
        return additionalItemsSchema;
    }

    @Override
    public void setAdditionalItems(JsonSchema schema) {
        this.additionalItemsSchema = schema;
        this.additionalItems = null;
    }

    @Override
    public void setAdditionalItems(Boolean additionalItems) {
        this.additionalItems = additionalItems;
        this.additionalItemsSchema = null;
    }
    
    @Override
    public JsonArraySchemaImpl read(JsonSchemaParser parser, JsonSchemaLocator locator, String jsonPointer, JsonObject object) throws JsonSchemaException {
        super.read(parser, locator, jsonPointer, object);
        
        JsonValue jitems = object.get(ITEMS);
        if (jitems == null) {
            // Omitting this keyword has the same behavior as an empty schema.
            return this;
        }
        
        if (jitems instanceof JsonObject) {
            final JsonSchema schema = parser.parse(locator, jsonPointer + ITEMS + "/", jitems.asJsonObject());
            getItems().add(schema);
        } else if (jitems instanceof JsonArray) {
            additionalItems = false;
            for (int i = 0, n = jitems.asJsonArray().size(); i < n; i++) {
                final JsonValue value = jitems.asJsonArray().get(i);
                final JsonObject o = JsonSchemaUtil.check(value, JsonValue.ValueType.OBJECT);
                final JsonSchema schema = parser.parse(locator, jsonPointer + Integer.toString(i) + "/", o);
                getItems().add(schema);                
            }
        } else {
            throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                new Object[] {ITEMS, jitems.getValueType().name(), "either an object or an array"}));
        }

        JsonValue jadditionalItems = object.get(ADDITIONAL_ITEMS);
        if (jadditionalItems != null) {
            switch(jadditionalItems.getValueType()) {
                case OBJECT: additionalItems = null;
                             additionalItemsSchema = parser.parse(locator, jsonPointer + ADDITIONAL_ITEMS + "/", jadditionalItems.asJsonObject());
                             break;
                case TRUE:   additionalItems = true; break;
                case FALSE:  additionalItems = false; break;
                default:     throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                   new Object[] {ADDITIONAL_ITEMS, jitems.getValueType().name(), "either object or boolean"}));
            }
        }

        final JsonNumber min = JsonSchemaUtil.check(object.getJsonNumber(MIN_ITEMS), JsonValue.ValueType.NUMBER);
        if (min != null) {
            minItems = min.longValue();
        }
        
        final JsonNumber max = JsonSchemaUtil.check(object.getJsonNumber(MAX_ITEMS), JsonValue.ValueType.NUMBER);
        if (max != null) {
            maxItems = max.longValue();
        }
        
        return this;
    }

    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {

        if (value.getValueType() != JsonValue.ValueType.ARRAY) {
            return; // ???
        }

        JsonArray array = value.asJsonArray();

        if (minItems != null && array.size() < minItems) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ARRAY_MIN_ITEMS_CONSTRAINT, minItems, items == null ? 0 : array.size()));
        }

        if (maxItems != null && array.size() > maxItems) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ARRAY_MAX_ITEMS_CONSTRAINT, maxItems, items == null ? 0 : items.size()));
        }

        if (items == null) {
            return;
        }
        
        if (additionalItemsSchema == null &&
            additionalItems == null) {

            if (items.size() == 1) {
                // all values must match the schema
                final JsonSchema schema = items.get(0);
                for (int i = 0, n = array.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    schema.validate(val, errors, callback);
                }                
            } else if (items.size() > 1) {
                if (array.size() > items.size()) {
                    errors.add(new ValidationError(getId(), getJsonPointer(),
                            ValidationMessage.ARRAY_LENGTH_MISMATCH, array.size(), items.size()));
                }
                // all values must match a correspondent schema
                for (int i = 0, n = array.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    items.get(i).validate(val, errors, callback);
                }
            }
        } else if (array.size() <= items.size()) {
            for (int i = 0, n = array.size(); i < n; i++) {
                final JsonValue val = array.get(i);
                items.get(i).validate(val, errors, callback);
            }
        } else if (additionalItems == false) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ARRAY_LENGTH_MISMATCH, array.size(), items.size()));
        } else {
            for (int i = 0, n = items.size(); i < n; i++) {
                final JsonValue val = array.get(i);
                items.get(i).validate(val, errors, callback);
            }

            if (additionalItemsSchema != null) {
                for (int i = items.size(), n = array.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    additionalItemsSchema.validate(val, errors, callback);
                }
            }
        }

        if (callback != null) {
            callback.validated(this, array, errors);
        }
    }
}
