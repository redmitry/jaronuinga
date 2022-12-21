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

package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonArraySchema;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.model.JsonType;
import java.util.ArrayList;
import java.util.List;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.TRUE;

/**
 * @author Dmitry Repchevsky
 */

public class JsonArraySchemaImpl extends PrimitiveSchemaImpl
                                 implements JsonArraySchema {

    private List<AbstractJsonSchema> items;
    private Boolean additionalItems;
    private Boolean uniqueItems;
    private AbstractJsonSchema additionalItemsSchema;
    
    private Long minItems;
    private Long maxItems;
    
    @Override
    public List<AbstractJsonSchema> getItems() {
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
    public Boolean isUniqueItems() {
        return uniqueItems;
    }

    @Override
    public AbstractJsonSchema getAdditionalItems() {
        return additionalItemsSchema;
    }
    
    @Override
    public JsonArraySchemaImpl read(final JsonSubschemaParser parser, 
                                    final JsonSchemaLocator locator,
                                    final JsonSchemaElement parent,
                                    final String jsonPointer, 
                                    final JsonObject object,
                                    final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, parent, jsonPointer, object, type);
        
        final JsonNumber min = JsonSchemaUtil.check(object.getJsonNumber(MIN_ITEMS), JsonValue.ValueType.NUMBER);
        if (min != null) {
            minItems = min.longValue();
        }
        
        final JsonNumber max = JsonSchemaUtil.check(object.getJsonNumber(MAX_ITEMS), JsonValue.ValueType.NUMBER);
        if (max != null) {
            maxItems = max.longValue();
        }

        final JsonValue juniqueItems = object.get(UNIQUE_ITEMS);
        if (juniqueItems != null) {
            switch(juniqueItems.getValueType()) {
                case TRUE: uniqueItems = true; break;
                case FALSE: uniqueItems = false; break;
                default: throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {UNIQUE_ITEMS, juniqueItems.getValueType().name(), "must be boolean"}));
            }
        }

        JsonValue jitems = object.get(ITEMS);
        if (jitems == null) {
            // Omitting this keyword has the same behavior as an empty schema.
            return this;
        }
        
        if (jitems instanceof JsonObject) {
            final AbstractJsonSchema schema = parser.parse(locator, this, jsonPointer + "/" + ITEMS, jitems.asJsonObject(), null);
            getItems().add(schema);
        } else if (jitems instanceof JsonArray) {
            additionalItems = true;
            for (int i = 0, n = jitems.asJsonArray().size(); i < n; i++) {
                final JsonValue value = jitems.asJsonArray().get(i);
                final JsonObject o = JsonSchemaUtil.check(value, JsonValue.ValueType.OBJECT);
                final AbstractJsonSchema schema = parser.parse(locator, this, jsonPointer + "/" + Integer.toString(i), o, null);
                getItems().add(schema);                
            }
        } else {
            throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                new Object[] {ITEMS, jitems.getValueType().name(), "either an object or an array"}));
        }

        if (additionalItems != null) {
            final JsonValue jadditionalItems = object.get(ADDITIONAL_ITEMS);
            if (jadditionalItems != null) {
                switch(jadditionalItems.getValueType()) {
                    case OBJECT: additionalItems = null;
                                 additionalItemsSchema = parser.parse(locator, this, jsonPointer + "/" + ADDITIONAL_ITEMS, jadditionalItems.asJsonObject(), type);
                                 break;
                    case TRUE:   additionalItems = true; break;
                    case FALSE:  additionalItems = false; break;
                    default:     throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {ADDITIONAL_ITEMS, jitems.getValueType().name(), "either object or boolean"}));
                }
            }
        }
        
        return this;
    }

    @Override
    public boolean validate(String jsonPointer, JsonValue value, JsonValue parent, 
            List<String> evaluated, List<ValidationError> errors,
            JsonSchemaValidationCallback<JsonValue> callback) {

        if (value.getValueType() != JsonValue.ValueType.ARRAY) {
            errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer, 
                    ValidationMessage.ARRAY_EXPECTED_MSG, value.getValueType().name()));
            return false;
        }

        final int nerrors = errors.size();
        
        final JsonArray array = value.asJsonArray();

        if (minItems != null && array.size() < minItems) {
            errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                    ValidationMessage.ARRAY_MIN_ITEMS_CONSTRAINT_MSG, minItems, items == null ? 0 : array.size()));
        }

        if (maxItems != null && array.size() > maxItems) {
            errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                    ValidationMessage.ARRAY_MAX_ITEMS_CONSTRAINT_MSG, maxItems, items == null ? 0 : items.size()));
        }

        if (items != null) {
            if (items.size() == 1 && additionalItems == null && additionalItemsSchema == null) {
                // items is a json object - all values must match the schema
                final AbstractJsonSchema schema = items.get(0);
                for (int i = 0, n = array.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    schema.validate(jsonPointer + "/" + i, val, value, new ArrayList(), errors, callback);
                }
            } else if (array.size() <= items.size()) {
                for (int i = 0, n = array.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    items.get(i).validate(jsonPointer + "/" + i, val, value, new ArrayList(), errors, callback);
                }
            } else if (Boolean.FALSE.equals(additionalItems)) {
                errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                        ValidationMessage.ARRAY_LENGTH_MISMATCH_MSG, array.size(), items.size()));
            } else {
                for (int i = 0, n = items.size(); i < n; i++) {
                    final JsonValue val = array.get(i);
                    items.get(i).validate(jsonPointer + "/" + i, val, value, new ArrayList(), errors, callback);
                }

                if (additionalItemsSchema != null) {
                    for (int i = items.size(), n = array.size(); i < n; i++) {
                        final JsonValue val = array.get(i);
                        additionalItemsSchema.validate(jsonPointer + "/" + i, val, value, new ArrayList(), errors, callback);
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(uniqueItems)) {
            final Set values = new HashSet();
            for (int i = 0, n = array.size(); i < n; i++) {
                final JsonValue val = array.get(i);
                final Object o;
                switch(val.getValueType()) {
                    case NUMBER: o = ((JsonNumber)val).doubleValue(); break;
                    default: o = val;
                }
                if (values.contains(o)) {
                    errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                            ValidationMessage.ARRAY_UNIQUE_ITEMS_CONSTRAINT_MSG, val.toString()));
                } else {
                    values.add(o);
                }
            }
        }

        if (callback != null) {
            callback.validated(this, jsonPointer, value, parent, errors);
        }
        
        return nerrors == errors.size();
    }
}
