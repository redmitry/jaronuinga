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
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.ext.ExtendedJsonSchemaLocatorInterface;
import es.elixir.bsc.json.schema.model.CompoundSchema;
import static es.elixir.bsc.json.schema.model.JsonEnum.ENUM;
import es.elixir.bsc.json.schema.model.JsonObjectSchema;
import static es.elixir.bsc.json.schema.model.JsonObjectSchema.ALL_OF;
import static es.elixir.bsc.json.schema.model.JsonObjectSchema.ANY_OF;
import static es.elixir.bsc.json.schema.model.JsonObjectSchema.NOT;
import static es.elixir.bsc.json.schema.model.JsonObjectSchema.ONE_OF;
import es.elixir.bsc.json.schema.model.JsonSchema;
import static es.elixir.bsc.json.schema.model.JsonSchema.TYPE;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.impl.JsonAllOfImpl;
import es.elixir.bsc.json.schema.model.impl.JsonAnyOfImpl;
import es.elixir.bsc.json.schema.model.impl.JsonArraySchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonBooleanSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonEnumImpl;
import es.elixir.bsc.json.schema.model.impl.JsonIntegerSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonNotImpl;
import es.elixir.bsc.json.schema.model.impl.JsonNullSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonNumberSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonObjectSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonOneOfImpl;
import es.elixir.bsc.json.schema.model.impl.JsonSchemaUtil;
import es.elixir.bsc.json.schema.model.impl.JsonStringSchemaImpl;
import java.net.URI;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class DefaultJsonSchemaParser implements JsonSchemaParser {
    
    public final JsonSchemaLocator locator;
    
    public DefaultJsonSchemaParser(JsonSchemaLocator locator) {
        this.locator = locator;
    }

    @Override
    public JsonSchema parse(JsonSchemaLocator locator, String jsonPointer, JsonObject object) throws JsonSchemaException {
        JsonValue ref = object.get("$ref");
        if (ref != null) {
            if (JsonValue.ValueType.STRING != ref.getValueType()) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                       new Object[] {"$ref", ref.getValueType().name(), JsonValue.ValueType.STRING.name()}));
            }

            final String value = ((JsonString)ref).getString();

            try {
                final URI uri = URI.create(value);
                if (value.startsWith("#/" + JsonObjectSchema.DEFINITIONS + "/")) {
                    Map<String, JsonObject> jsubschemas = locator.getSchemas();
                    if (jsubschemas == null) {
                        throw new JsonSchemaException(new ParsingError(ParsingMessage.CRITICAL_PARSING_ERROR, null));
                    }
                    
                    JsonObject jsubschema = jsubschemas.get(value);
                    if (jsubschema != null) {
                            return parse(locator, value, jsubschema);
                    }
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.UNRESOLVABLE_REFERENCE,
                                                  new Object[] {value}));
                } else {
                    return JsonSchemaReader.getReader().read(locator.resolve(uri));
                }
            } catch(IllegalArgumentException ex) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                              new Object[] {value}));
            }
        }


        if (locator == null) {
            locator = this.locator;
        } else {
            // subschema (not root)
            JsonValue id = object.get("id");
            if (id != null) {
                if (id.getValueType() != JsonValue.ValueType.STRING) {
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                       new Object[] {"id", id.getValueType().name(), JsonValue.ValueType.STRING.name()}));
                }

                final String value = ((JsonString)id).getString();

                try {
                    locator = locator.resolve(URI.create(value));
                } catch(IllegalArgumentException ex) {
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                                  new Object[] {value}));
                }
            }
        }

        if (locator instanceof ExtendedJsonSchemaLocatorInterface) {
            locator.getSchemas().put(jsonPointer, object);
        }
        
        final JsonArray _enum = JsonSchemaUtil.check(object.get(ENUM), JsonValue.ValueType.ARRAY);
        if (_enum != null) {
            if (_enum.isEmpty()) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.EMPTY_ENUM));
            }
            return new JsonEnumImpl().read(this, locator, jsonPointer, object);
        }

        JsonValue value = object.get(TYPE);
        if (value == null) {
            
            final JsonArray jallOf = JsonSchemaUtil.check(object.get(ALL_OF), JsonValue.ValueType.ARRAY);
            if (jallOf != null) {
                final JsonAllOfImpl allOf = new JsonAllOfImpl();
                allOf.read(this, locator, jsonPointer + ALL_OF + "/", jallOf);
                return allOf;
            }

            final JsonArray janyOf = JsonSchemaUtil.check(object.get(ANY_OF), JsonValue.ValueType.ARRAY);
            if (janyOf != null) {
                final JsonAnyOfImpl anyOf = new JsonAnyOfImpl();
                anyOf.read(this, locator, jsonPointer + ANY_OF + "/", janyOf);
                return anyOf;
            }

            final JsonArray joneOf = JsonSchemaUtil.check(object.get(ONE_OF), JsonValue.ValueType.ARRAY);
            if (joneOf != null) {
                JsonOneOfImpl oneOf = new JsonOneOfImpl();
                oneOf.read(this, locator, jsonPointer + ONE_OF + "/", joneOf);
                return oneOf;
            }

//            final JsonObject jnot = JsonSchemaUtil.check(object.get(NOT), JsonValue.ValueType.OBJECT);
//            if (jnot != null) {
//                final JsonNotImpl not = new JsonNotImpl();            
//                not.read(this, locator, jsonPointer + NOT + "/", jnot);
//                return not;
//            }

            CompoundSchema schema = new CompoundSchema();
            for (JsonType val : JsonType.values()) {
                try {
                    JsonSchema s = parse(locator, jsonPointer, object, val);
                    if (s != null) {
                        schema.add(s);
                    }
                } catch(JsonSchemaException ex) {
                    // do nothing
                }
            }
            return schema;
        }
            
        switch(value.getValueType()) {
            case STRING: return parse(locator, jsonPointer, object, getType(value));
            case ARRAY: CompoundSchema schema = new CompoundSchema();
                        for (JsonValue val : value.asJsonArray()) {
                            schema.add(parse(locator, jsonPointer, object, getType(val)));
                        }
                        return schema;
        }
        
        throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
            new Object[] {"type", value.getValueType().name(), "either a string or an array"}));
    }
    
    private JsonSchema parse(JsonSchemaLocator locator, String jsonPointer, JsonObject object, JsonType value) throws JsonSchemaException {
        switch(value) {
            case OBJECT: return new JsonObjectSchemaImpl().read(this, locator, jsonPointer, object);
            case ARRAY: return new JsonArraySchemaImpl().read(this, locator, jsonPointer, object);
            case STRING: return new JsonStringSchemaImpl().read(this, locator, jsonPointer, object);
            case NUMBER: return new JsonNumberSchemaImpl().read(this, locator, jsonPointer, object);
            case INTEGER: return new JsonIntegerSchemaImpl().read(this, locator, jsonPointer, object);
            case BOOLEAN: return new JsonBooleanSchemaImpl().read(this, locator, jsonPointer, object);
            case NULL: return new JsonNullSchemaImpl().read(this, locator, jsonPointer, object);
        }
        return null;
    }
    
    private JsonType getType(JsonValue value) throws JsonSchemaException {
        final JsonString jstring = JsonSchemaUtil.require(value, JsonValue.ValueType.STRING);
        try {
            return JsonType.fromValue(jstring.getString());
        } catch(IllegalArgumentException ex) {
            throw new JsonSchemaException(new ParsingError(ParsingMessage.UNKNOWN_OBJECT_TYPE, 
                new Object[] {jstring.getString()}));
        }
    }
}
