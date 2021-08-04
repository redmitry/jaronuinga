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
import es.elixir.bsc.json.schema.ValidationException;
import java.net.URI;
import java.util.List;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.JsonAllOf;
import es.elixir.bsc.json.schema.model.JsonAnyOf;
import es.elixir.bsc.json.schema.model.JsonNot;
import es.elixir.bsc.json.schema.model.JsonOneOf;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import javax.json.JsonArray;

/**
 * Primitive empty Json Schema of any type ("object", "array", "string", etc.)
 * 
 * @author Dmitry Repchevsky
 */

public class PrimitiveSchemaImpl implements PrimitiveSchema {
    
    private URI id;
    private String jsonPointer;

    private String title;
    private String description;
    
    private JsonAllOfImpl allOf;
    private JsonAnyOfImpl anyOf;
    private JsonOneOfImpl oneOf;
    private JsonNotImpl not;
    
    @Override
    public URI getId() {
        return id;
    }
    
    @Override
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * Returns Json Pointer to locate Json Schema object in the Json Schema document.
     * The pointer is relative to the schema id.
     * 
     * @return Json Pointer to this schema
     */
    @Override
    public String getJsonPointer() {
        return jsonPointer;
    }
    
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

    public PrimitiveSchemaImpl read(final JsonSubschemaParser parser, 
                                final JsonSchemaLocator locator, 
                                final String jsonPointer, 
                                final JsonObject object, 
                                final JsonType type) throws JsonSchemaException {

        this.jsonPointer = jsonPointer;
        
        id = locator.uri;

        final JsonString jtitle = JsonSchemaUtil.check(object.get(TITLE), JsonValue.ValueType.STRING);
        setTitle(jtitle == null ? null : jtitle.getString());
        
        final JsonString jdescription = JsonSchemaUtil.check(object.get(DESCRIPTION), JsonValue.ValueType.STRING);
        setDescription(jdescription == null ? null : jdescription.getString());
        
        final JsonArray jallOf = JsonSchemaUtil.check(object.get(ALL_OF), JsonValue.ValueType.ARRAY);
        if (jallOf != null) {
            allOf = new JsonAllOfImpl();
            allOf.read(parser, locator, jsonPointer + ALL_OF + "/", jallOf, type);
        }
        
        final JsonArray janyOf = JsonSchemaUtil.check(object.get(ANY_OF), JsonValue.ValueType.ARRAY);
        if (janyOf != null) {
            anyOf = new JsonAnyOfImpl();
            anyOf.read(parser, locator, jsonPointer + ANY_OF + "/", janyOf, type);
        }
        
        final JsonArray joneOf = JsonSchemaUtil.check(object.get(ONE_OF), JsonValue.ValueType.ARRAY);
        if (joneOf != null) {
            oneOf = new JsonOneOfImpl();
            oneOf.read(parser, locator, jsonPointer + ONE_OF + "/", joneOf, type);
        }

        final JsonObject jnot = JsonSchemaUtil.check(object.get(NOT), JsonValue.ValueType.OBJECT);
        if (jnot != null) {
            not = new JsonNotImpl();            
            not.read(parser, locator, jsonPointer + NOT + "/", jnot);
        }

        return this;
    }

    @Override
    public void validate(JsonValue value, JsonValue parent, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException {

        if (allOf != null) {
            allOf.validate(value, parent, errors, callback);
        }
        
        if (anyOf != null) {
            anyOf.validate(value, parent, errors, callback);
        }

        if (oneOf != null) {
            oneOf.validate(value, parent, errors, callback);
        }

        if (not != null) {
            not.validate(value, parent, errors, callback);
        }
    }    
}
