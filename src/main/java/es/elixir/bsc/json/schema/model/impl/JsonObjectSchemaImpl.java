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
import es.elixir.bsc.json.schema.model.JsonAllOf;
import es.elixir.bsc.json.schema.model.JsonAnyOf;
import es.elixir.bsc.json.schema.model.JsonDefinitions;
import es.elixir.bsc.json.schema.model.JsonObjectSchema;
import es.elixir.bsc.json.schema.model.JsonOneOf;
import es.elixir.bsc.json.schema.model.JsonProperties;
import es.elixir.bsc.json.schema.model.JsonSchema;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import es.elixir.bsc.json.schema.model.StringArray;
import javax.json.JsonArray;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.model.JsonNot;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;

/**
 * @author Dmitry Repchevsky
 */

public class JsonObjectSchemaImpl extends PrimitiveSchema
                                  implements JsonObjectSchema {

    private JsonDefinitions definitions;
    private JsonProperties properties;
    private JsonRequiredImpl required;
    
    private JsonAllOfImpl allOf;
    private JsonAnyOfImpl anyOf;
    private JsonOneOfImpl oneOf;
    private JsonNotImpl not;
    
    @Override
    public JsonDefinitions getDefinitions() {
        if (definitions == null) {
            definitions = new JsonDefinitionsImpl();
        }
        return definitions;
    }

    @Override
    public JsonProperties getProperties() {
        if (properties == null) {
            properties = new JsonPropertiesImpl();
        }
        return properties;
    }

    @Override
    public StringArray getRequired() {
        if (required == null) {
            required = new JsonRequiredImpl();
        }
        return required;
    }

    @Override
    public JsonAllOf getAllOf() {
        if (allOf == null) {
            allOf = new JsonAllOfImpl();
        }
        return allOf;
    }
    
    @Override
    public JsonAnyOf getAnyOf() {
        if (anyOf == null) {
            anyOf = new JsonAnyOfImpl();
        }
        return anyOf;
    }
    
    @Override
    public JsonOneOf getOneOf() {
        if (oneOf == null) {
            oneOf = new JsonOneOfImpl();
        }
        return oneOf;
    }
    
    @Override
    public JsonNot getNot() {
        if (not == null) {
            not = new JsonNotImpl();
        }
        return not;
    }

    @Override
    public JsonObjectSchemaImpl read(JsonSchemaParser parser, JsonSchemaLocator locator, String jsonPointer, JsonObject object) throws JsonSchemaException {
        super.read(parser, locator, jsonPointer, object);

        final JsonObject jdefinitions = JsonSchemaUtil.check(object.get(DEFINITIONS), ValueType.OBJECT);
        if (jdefinitions != null) {
            definitions = new JsonDefinitionsImpl().read(parser, locator, jsonPointer + DEFINITIONS + "/", jdefinitions);
        }
        
        final JsonObject jpropertues = JsonSchemaUtil.check(object.get(PROPERTIES), ValueType.OBJECT);
        if (jpropertues != null) {
            properties = new JsonPropertiesImpl().read(parser, locator, jsonPointer + PROPERTIES + "/", jpropertues);
        }
        
        final JsonArray jrequired = JsonSchemaUtil.check(object.get(REQUIRED), ValueType.ARRAY);
        if (jrequired != null) {
            required = new JsonRequiredImpl().read(jrequired);
        }

        final JsonArray jallOf = JsonSchemaUtil.check(object.get(ALL_OF), ValueType.ARRAY);
        if (jallOf != null) {
            allOf = new JsonAllOfImpl();
            allOf.read(parser, locator, jsonPointer + ALL_OF + "/", jallOf);
        }
        
        final JsonArray janyOf = JsonSchemaUtil.check(object.get(ANY_OF), ValueType.ARRAY);
        if (janyOf != null) {
            anyOf = new JsonAnyOfImpl();
            anyOf.read(parser, locator, jsonPointer + ANY_OF + "/", janyOf);
        }
        
        final JsonArray joneOf = JsonSchemaUtil.check(object.get(ONE_OF), ValueType.ARRAY);
        if (joneOf != null) {
            oneOf = new JsonOneOfImpl();
            oneOf.read(parser, locator, jsonPointer + ONE_OF + "/", joneOf);
        }

        final JsonObject jnot = JsonSchemaUtil.check(object.get(NOT), ValueType.OBJECT);
        if (jnot != null) {
            not = new JsonNotImpl();            
            not.read(parser, locator, jsonPointer + NOT + "/", jnot);
        }

        return this;
    }

    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {

        if (value.getValueType() != JsonValue.ValueType.OBJECT) {
            return;
        }
        
        JsonObject object = value.asJsonObject();

        StringArray req = new JsonRequiredImpl(required);

        if (properties != null) {
            for (Map.Entry<String, JsonSchema> property : properties) {
                final String name = property.getKey();
                final JsonValue val = object.get(name);
                if (val != null) {
                    property.getValue().validate(val, errors, callback);
                    req.remove(name);
                }
            }
        }
        
        for (Iterator<String> i = req.iterator(); i.hasNext();) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.OBJECT_REQUIRED_PROPERTY_CONSTRAINT, i.next()));
        }
        
        if (allOf != null) {
            allOf.validate(value, errors, callback);
        }
        
        if (anyOf != null) {
            anyOf.validate(value, errors, callback);
        }

        if (oneOf != null) {
            oneOf.validate(value, errors, callback);
        }

        if (not != null) {
            not.validate(value, errors, callback);
        }
        
        if (callback != null) {
            callback.validated(this, object, errors);
        }
    }
}
