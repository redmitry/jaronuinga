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
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonDefinitions;
import es.elixir.bsc.json.schema.model.JsonObjectSchema;
import es.elixir.bsc.json.schema.model.JsonProperties;
import es.elixir.bsc.json.schema.model.JsonSchema;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;
import es.elixir.bsc.json.schema.model.StringArray;
import javax.json.JsonArray;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.JsonDependentProperties;
import javax.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class JsonObjectSchemaImpl extends PrimitiveSchemaImpl
                                  implements JsonObjectSchema {

    private JsonDefinitions definitions;
    private JsonProperties properties;
    private JsonStringArray required;
    private JsonProperties dependentSchemas;
    private JsonDependentProperties dependentRequired;
    
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
            required = new JsonStringArray();
        }
        return required;
    }

    @Override
    public JsonProperties getDependentSchemas() {
        if (dependentSchemas == null) {
            dependentSchemas = new JsonPropertiesImpl();
        }
        return dependentSchemas;
    }

    @Override
    public JsonDependentProperties getDependentRequired() {
        if (dependentRequired == null) {
            dependentRequired = new JsonDependentPropertiesImpl();
        }
        return dependentRequired;
    }

    @Override
    public JsonObjectSchemaImpl read(final JsonSubschemaParser parser, 
                                     final JsonSchemaLocator locator, 
                                     final String jsonPointer, 
                                     final JsonObject object,
                                     final JsonType type) throws JsonSchemaException {
        
        super.read(parser, locator, jsonPointer, object, type);

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
            required = new JsonStringArray().read(jrequired);
        }

        final JsonObject jdependentSchemas = JsonSchemaUtil.check(object.get(DEPENDENT_SCHEMAS), ValueType.OBJECT);
        if (jdependentSchemas != null) {
            dependentSchemas = new JsonPropertiesImpl().read(parser, locator, jsonPointer + DEPENDENT_SCHEMAS + "/", jdependentSchemas);
        }

        final JsonObject jdependentRequired = JsonSchemaUtil.check(object.get(DEPENDENT_REQUIRED), ValueType.ARRAY);
        if (jdependentRequired != null) {
            dependentRequired = new JsonDependentPropertiesImpl().read(parser, locator, jsonPointer + DEPENDENT_REQUIRED + "/", jdependentRequired);
        }
        
        final JsonObject jdependencies = JsonSchemaUtil.check(object.get(DEPENDENCIES), ValueType.OBJECT);
        if (jdependencies != null) {
            for (Map.Entry<String, JsonValue> dependency : jdependencies.entrySet()) {
                final String name = dependency.getKey();
                final JsonValue value = dependency.getValue();
                switch(value.getValueType()) {
                    case OBJECT: final JsonSchema schema = new JsonObjectSchemaImpl().read(parser, locator, jsonPointer + DEPENDENCIES + "/" + name + "/", value.asJsonObject(), JsonType.OBJECT);
                                 getDependentSchemas().put(name, schema);
                                 break;
                    case ARRAY:  final StringArray arr = new JsonStringArray().read(value.asJsonArray());
                                 getDependentRequired().put(name, arr);
                                 break;
                    default:     throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_OBJECT_TYPE, 
                                   new Object[] {name + " dependentRequired schema ", 
                                       value.getValueType().name(), JsonValue.ValueType.OBJECT.name() + " or " + JsonValue.ValueType.ARRAY.name()}));
                }
            }
        }
        
        return this;
    }

    @Override
    public void validate(JsonValue value, JsonValue parent, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) {

        if (value.getValueType() != JsonValue.ValueType.OBJECT) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.OBJECT_EXPECTED, value.getValueType().name()));
            return;
        }
        
        JsonObject object = value.asJsonObject();

        StringArray req = new JsonStringArray(required);

        if (properties != null) {
            for (Map.Entry<String, JsonSchema> property : properties) {
                final String name = property.getKey();
                final JsonValue val = object.get(name);
                if (val != null) {
                    property.getValue().validate(val, value, errors, callback);
                    req.remove(name);
                }
            }
        }
        
        for (Iterator<String> i = req.iterator(); i.hasNext();) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.OBJECT_REQUIRED_PROPERTY_CONSTRAINT, i.next()));
        }
        
        if (dependentSchemas != null) {
            for (Map.Entry<String, JsonSchema> property : dependentSchemas) {
                final String name = property.getKey();
                if (properties != null && properties.contains(name)) {
                    final JsonSchema dependentSchema = property.getValue();
                    dependentSchema.validate(value, parent, errors, callback);
                }
            }
        }

        if (dependentRequired != null) {
            for (Map.Entry<String, StringArray> property : dependentRequired) {
                final String name = property.getKey();
                if (properties != null && properties.contains(name)) {
                    final StringArray arr = property.getValue();
                    for (String p : arr) {
                        if (!properties.contains(name)) {
                            errors.add(new ValidationError(getId(), getJsonPointer(),
                                ValidationMessage.OBJECT_DEPENDENT_REQUIRED_CONSTRAINT, name));                            
                        }
                    }
                }
            }
        }

        if (callback != null) {
            callback.validated(this, value, parent, errors);
        }
    }
}
