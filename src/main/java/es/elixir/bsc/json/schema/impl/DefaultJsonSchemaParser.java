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

package es.elixir.bsc.json.schema.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import static es.elixir.bsc.json.schema.model.JsonEnum.ENUM;
import es.elixir.bsc.json.schema.model.JsonObjectSchema;
import static es.elixir.bsc.json.schema.model.JsonSchema.TYPE;
import es.elixir.bsc.json.schema.model.JsonType;
import static es.elixir.bsc.json.schema.model.PrimitiveSchema.ALL_OF;
import static es.elixir.bsc.json.schema.model.PrimitiveSchema.ANY_OF;
import static es.elixir.bsc.json.schema.model.PrimitiveSchema.NOT;
import static es.elixir.bsc.json.schema.model.PrimitiveSchema.ONE_OF;
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
import javax.json.JsonValue.ValueType;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import static es.elixir.bsc.json.schema.model.JsonConst.CONST;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.impl.JsonConstImpl;

/**
 * @author Dmitry Repchevsky
 */

public class DefaultJsonSchemaParser implements JsonSubschemaParser {
    
    public final JsonSchemaLocator locator;
    
    public DefaultJsonSchemaParser(final JsonSchemaLocator locator) {
        this.locator = locator;
    }

    @Override
    public AbstractJsonSchema parse(JsonSchemaLocator locator,
                                    JsonSchemaElement parent,
                                    String jsonPointer, 
                                    JsonObject object,
                                    JsonType type) throws JsonSchemaException {
        
        JsonValue ref = object.get("$ref");
        if (ref != null) {
            if (JsonValue.ValueType.STRING != ref.getValueType()) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                       new Object[] {"$ref", ref.getValueType().name(), JsonValue.ValueType.STRING.name()}));
            }

            String value = ((JsonString)ref).getString();

            try {
                final int idx_fragment = value.indexOf("#/" + JsonObjectSchema.DEFINITIONS + "/");
                if (idx_fragment > 0) {
                    final URI uri = URI.create(value);
                    locator = locator.resolve(uri);
                    JsonSchemaReader.getReader().read(locator);
                    value = value.substring(idx_fragment);
                } else if (idx_fragment < 0) {
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                                  new Object[] {value}));                    
                }
                final Map<String, JsonObject> jsubschemas = locator.getSchemas(locator.uri);
                if (jsubschemas == null) {
                    throw new JsonSchemaException(new ParsingError(ParsingMessage.CRITICAL_PARSING_ERROR, null));
                }

                final JsonObject jsubschema = jsubschemas.get(value);
                if (jsubschema != null) {
                    return parse(locator, parent, value, jsubschema, type);
                }
                throw new JsonSchemaException(new ParsingError(ParsingMessage.UNRESOLVABLE_REFERENCE,
                                              new Object[] {value}));
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

        locator.putSchema(jsonPointer, object);
        
        final JsonValue value = object.get(TYPE);
        final ValueType vtype;
        if (value == null) {
            vtype = null;
        } else {
            vtype = value.getValueType();
            if (vtype != ValueType.STRING && vtype != ValueType.ARRAY) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                    new Object[] {"type", value.getValueType().name(), "either a string or an array"}));
            }
            type = getType(value);
        }
        
        final JsonArray jenum = JsonSchemaUtil.check(object.get(ENUM), JsonValue.ValueType.ARRAY);
        if (jenum != null) {
            if (jenum.isEmpty()) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.EMPTY_ENUM));
            }
            final JsonEnumImpl _enum = new JsonEnumImpl();
            _enum.read(this, locator, parent, jsonPointer, object, type);
            locator.putSchema(_enum);
            return _enum;
        }

        final JsonValue jconst = object.get(CONST);
        if (jconst != null) {
            final JsonConstImpl _const = new JsonConstImpl();
            _const.read(this, locator, parent, jsonPointer, object, type);
            locator.putSchema(_const);
            return _const;
        }
        
        if (type == null) {
            final JsonArray jallOf = JsonSchemaUtil.check(object.get(ALL_OF), JsonValue.ValueType.ARRAY);
            if (jallOf != null) {
                final JsonAllOfImpl allOf = new JsonAllOfImpl();
                allOf.read(this, locator, parent, jsonPointer + ALL_OF + "/", jallOf, type);
                locator.putSchema(allOf);
                return allOf;
            }

            final JsonArray janyOf = JsonSchemaUtil.check(object.get(ANY_OF), JsonValue.ValueType.ARRAY);
            if (janyOf != null) {
                final JsonAnyOfImpl anyOf = new JsonAnyOfImpl();
                anyOf.read(this, locator, parent, jsonPointer + ANY_OF + "/", janyOf, type);
                locator.putSchema(anyOf);
                return anyOf;
            }

            final JsonArray joneOf = JsonSchemaUtil.check(object.get(ONE_OF), JsonValue.ValueType.ARRAY);
            if (joneOf != null) {
                final JsonOneOfImpl oneOf = new JsonOneOfImpl();
                oneOf.read(this, locator, parent, jsonPointer + ONE_OF + "/", joneOf, type);
                locator.putSchema(oneOf);
                return oneOf;
            }

            final JsonObject jnot = JsonSchemaUtil.check(object.get(NOT), JsonValue.ValueType.OBJECT);
            if (jnot != null) {
                final JsonNotImpl not = new JsonNotImpl();            
                not.read(this, locator, parent, jsonPointer + NOT + "/", jnot);
                locator.putSchema(not);
                return not;
            }

            final JsonAnyOfImpl anyOf = new JsonAnyOfImpl();
            anyOf.read(this, locator, parent, jsonPointer, object, null);
            locator.putSchema(anyOf);
            return anyOf;
        }
        
        if (vtype == ValueType.ARRAY) {
            final JsonAnyOfImpl anyOf = new JsonAnyOfImpl();
            anyOf.read(this, locator, parent, jsonPointer, object, value.asJsonArray());
            locator.putSchema(anyOf);
            return anyOf;
        }

        final AbstractJsonSchema schema;
        switch(type) {
            case OBJECT: schema = new JsonObjectSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case ARRAY: schema = new JsonArraySchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case STRING: schema = new JsonStringSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case NUMBER: schema = new JsonNumberSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case INTEGER: schema = new JsonIntegerSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case BOOLEAN: schema = new JsonBooleanSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            case NULL: schema = new JsonNullSchemaImpl().read(this, locator, parent, jsonPointer, object, type); break;
            default: return null;
        }

        locator.putSchema(schema);

        return schema;
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
