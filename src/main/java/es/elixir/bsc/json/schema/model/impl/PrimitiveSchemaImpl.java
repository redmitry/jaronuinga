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
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationException;
import java.util.List;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import es.elixir.bsc.json.schema.model.JsonAllOf;
import es.elixir.bsc.json.schema.model.JsonAnyOf;
import es.elixir.bsc.json.schema.model.JsonNot;
import es.elixir.bsc.json.schema.model.JsonOneOf;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import java.util.ArrayList;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Primitive empty Json Schema of any type ("object", "array", "string", etc.)
 * 
 * @author Dmitry Repchevsky
 */

public class PrimitiveSchemaImpl extends JsonSchemaImpl<JsonObject>
        implements PrimitiveSchema {

    private String title;
    private String description;
    
    private JsonAllOfImpl allOf;
    private JsonAnyOfImpl anyOf;
    private JsonOneOfImpl oneOf;
    private JsonNotImpl not;
    
    private AbstractJsonSchema _if;
    private AbstractJsonSchema _then;
    private AbstractJsonSchema _else;

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public JsonAllOf getAllOf() {
        return allOf;
    }
    
    @Override
    public JsonAnyOf getAnyOf() {
        return anyOf;
    }
    
    @Override
    public JsonOneOf getOneOf() {
        return oneOf;
    }
    
    @Override
    public JsonNot getNot() {
        return not;
    }

    @Override
    public AbstractJsonSchema getIf() {
        return _if;
    }

    @Override
    public AbstractJsonSchema getThen() {
        return _then;
    }

    @Override
    public AbstractJsonSchema getElse() {
        return _else;
    }

    @Override
    public PrimitiveSchemaImpl read(final JsonSubschemaParser parser, 
                                    final JsonSchemaLocator locator,
                                    final JsonSchemaElement parent,
                                    final String jsonPointer,
                                    final JsonObject object, 
                                    final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, parent, jsonPointer, object, type);

        final JsonString jtitle = JsonSchemaUtil.check(object.get(TITLE), JsonValue.ValueType.STRING);
        setTitle(jtitle == null ? null : jtitle.getString());
        
        final JsonString jdescription = JsonSchemaUtil.check(object.get(DESCRIPTION), JsonValue.ValueType.STRING);
        setDescription(jdescription == null ? null : jdescription.getString());
        
        final JsonArray jallOf = JsonSchemaUtil.check(object.get(ALL_OF), JsonValue.ValueType.ARRAY);
        if (jallOf != null) {
            allOf = new JsonAllOfImpl();
            allOf.read(parser, locator, this, jsonPointer + "/" + ALL_OF, jallOf, type);
            locator.putSchema(allOf);
        }
        
        final JsonArray janyOf = JsonSchemaUtil.check(object.get(ANY_OF), JsonValue.ValueType.ARRAY);
        if (janyOf != null) {
            anyOf = new JsonAnyOfImpl();
            anyOf.read(parser, locator, this, jsonPointer + "/" + ANY_OF, janyOf, type);
            locator.putSchema(anyOf);
        }
        
        final JsonArray joneOf = JsonSchemaUtil.check(object.get(ONE_OF), JsonValue.ValueType.ARRAY);
        if (joneOf != null) {
            oneOf = new JsonOneOfImpl();
            oneOf.read(parser, locator, this, jsonPointer + "/" + ONE_OF, joneOf, type);
            locator.putSchema(oneOf);
        }

        final JsonValue jnot = object.get(NOT);
        if (jnot != null) {
            switch(jnot.getValueType()) {
                case OBJECT: not = new JsonNotImpl();
                             not.read(parser, locator, this, jsonPointer + "/" + NOT, jnot.asJsonObject());
                             break;
                case TRUE:
                case FALSE: BooleanJsonSchemaImpl bool = new BooleanJsonSchemaImpl();
                            not = new JsonNotImpl(bool.read(parser, locator, parent, jsonPointer, jnot, null));
                            break;
                default: throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {NOT, jnot.getValueType().name(), "either object or boolean"}));
            }
            locator.putSchema(not);            
        }

        final JsonValue jif = object.get(IF);
        if (jif != null) {
            switch(jif.getValueType()) {
                case OBJECT: _if = parser.parse(locator, this, jsonPointer + "/" + IF, jif, null);
                             break;
                case TRUE:
                case FALSE:  _if = new BooleanJsonSchemaImpl().read(parser, locator, parent, jsonPointer, jif, null);
                             break;
                default: throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {IF, jif.getValueType().name(), "either object or boolean"}));                             
            }
        }

        final JsonValue jelse = object.get(ELSE);
        if (jelse != null) {
            switch(jelse.getValueType()) {
                case OBJECT: _else = parser.parse(locator, this, jsonPointer + "/" + ELSE, jelse, null);
                             break;
                case TRUE:
                case FALSE:  _else = new BooleanJsonSchemaImpl().read(parser, locator, parent, jsonPointer, jelse, null);
                             break;
                default: throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {ELSE, jelse.getValueType().name(), "either object or boolean"}));                             
            }
        }

        final JsonValue jthen = object.get(THEN);
        if (jthen != null) {
            switch(jthen.getValueType()) {
                case OBJECT: _then = parser.parse(locator, this, jsonPointer + "/" + THEN, jthen, null);
                             break;
                case TRUE:
                case FALSE:  _then = new BooleanJsonSchemaImpl().read(parser, locator, parent, jsonPointer, jthen, null);
                             break;
                default: throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_ATTRIBUTE_TYPE, 
                                       new Object[] {THEN, jthen.getValueType().name(), "either object or boolean"}));                             
            }
        }

        return this;
    }

    @Override
    public boolean validate(String jsonPointer, JsonValue value, JsonValue parent, 
            List<String> evaluated, List<ValidationError> errors,
            JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException {

        final int nerrors = errors.size();
        
        if (allOf != null) {
            allOf.validate(jsonPointer, value, parent, evaluated, errors, callback);
        }
        
        if (anyOf != null) {
            anyOf.validate(jsonPointer, value, parent, evaluated, errors, callback);
        }

        if (oneOf != null) {
            oneOf.validate(jsonPointer, value, parent, evaluated, errors, callback);
        }

        if (not != null) {
            not.validate(jsonPointer, value, parent, evaluated, errors, callback);
        }

        if (_if != null) {
            final List<ValidationError> err = new ArrayList<>();
            _if.validate(value, err);
            final AbstractJsonSchema choice = err.isEmpty() ? _then : _else;
            if (choice != null) {
                choice.validate(jsonPointer, value, parent, evaluated, errors, callback);
            }
        }
        
        return nerrors == errors.size();
    }
}
