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

package es.elixir.bsc.json.schema.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import static es.elixir.bsc.json.schema.model.JsonEnum.ENUM;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.impl.JsonAnyOfImpl;
import es.elixir.bsc.json.schema.model.impl.JsonArraySchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonBooleanSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonEnumImpl;
import es.elixir.bsc.json.schema.model.impl.JsonIntegerSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonNullSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonNumberSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonObjectSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonSchemaUtil;
import es.elixir.bsc.json.schema.model.impl.JsonStringSchemaImpl;
import java.net.URI;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import static es.elixir.bsc.json.schema.model.JsonConst.CONST;
import es.elixir.bsc.json.schema.model.JsonReference;
import es.elixir.bsc.json.schema.model.JsonSchema;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import static es.elixir.bsc.json.schema.model.PrimitiveSchema.TYPE;
import es.elixir.bsc.json.schema.model.impl.BooleanJsonSchemaImpl;
import es.elixir.bsc.json.schema.model.impl.JsonConstImpl;
import es.elixir.bsc.json.schema.model.impl.JsonReferenceImpl;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

/**
 * @author Dmitry Repchevsky
 */

public class DefaultJsonSchemaParser implements JsonSubschemaParser {
    
    public final JsonSchemaLocator locator;
    
    public DefaultJsonSchemaParser(JsonSchemaLocator locator) {
        this.locator = locator;
    }

    @Override
    public AbstractJsonSchema parse(JsonSchemaLocator locator,
            JsonSchemaElement parent, String jsonPointer,
            JsonValue value, JsonType type) throws JsonSchemaException {

        if (locator == null) {
            locator = this.locator;
        }

        if (value.getValueType() == ValueType.TRUE ||
            value.getValueType() == ValueType.FALSE) {
            return new BooleanJsonSchemaImpl().read(this, locator, parent, jsonPointer, value, type);
        }

        if (value.getValueType() != ValueType.OBJECT) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.SCHEMA_OBJECT_ERROR, 
                   new Object[] {value.getValueType()}));
        }

        JsonObject object = value.asJsonObject();

        JsonValue jref = object.get(JsonReference.REF);
        if (jref != null) {
            if (JsonValue.ValueType.STRING != jref.getValueType()) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                       new Object[] {JsonReference.REF, jref.getValueType().name(), JsonValue.ValueType.STRING.name()}));
            }

            return new JsonReferenceImpl().read(this, locator, parent, jsonPointer, (JsonString)jref, null);
        }

        JsonValue $id = object.get(JsonSchema.ID);
        if ($id == null) {
            $id = object.get("id"); // draft4
        } 

        if ($id != null) {
            if ($id.getValueType() != JsonValue.ValueType.STRING) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                   new Object[] {"id", $id.getValueType().name(), JsonValue.ValueType.STRING.name()}));
            }

            final String id = ((JsonString)$id).getString();

            try {
                locator = locator.resolve(URI.create(id));
                locator.setSchema(object);
            } catch(IllegalArgumentException ex) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                              new Object[] {id}));
            }
        }
        
        final JsonValue type_value = object.get(TYPE);
        final ValueType vtype;
        if (type_value == null) {
            vtype = null;
        } else {
            vtype = type_value.getValueType();
            switch(vtype) {
                case STRING: type = getType(type_value);
                case ARRAY: break;
                default: 
                    throw new JsonSchemaException(new ParsingError(
                            ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                            new Object[] {"type", type_value.getValueType().name(), 
                                          "either a string or an array"}));

            }
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
            final JsonAnyOfImpl anyOf = new JsonAnyOfImpl();
            anyOf.read(this, locator, parent, jsonPointer, object, 
                    vtype == ValueType.ARRAY ? type_value.asJsonArray() : null);
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
