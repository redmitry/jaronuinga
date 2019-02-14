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

package es.elixir.bsc.json.schema.model;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationException;
import es.elixir.bsc.json.schema.model.impl.JsonSchemaUtil;
import java.net.URI;
import java.util.List;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * Primitive empty Json Schema of any type ("object", "array", "string", etc.)
 * 
 * @author Dmitry Repchevsky
 */

public class PrimitiveSchema implements JsonSchema {
    
    private URI id;
    private String jsonPointer;

    private String title;
    private String description;
    
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

    public PrimitiveSchema read(JsonSchemaParser parser, JsonSchemaLocator locator, String jsonPointer, JsonObject object) throws JsonSchemaException {
        this.jsonPointer = jsonPointer;
        
        id = locator.uri;

        final JsonString jtitle = JsonSchemaUtil.check(object.get(TITLE), JsonValue.ValueType.STRING);
        setTitle(jtitle == null ? null : jtitle.getString());
        
        final JsonString jdescription = JsonSchemaUtil.check(object.get(DESCRIPTION), JsonValue.ValueType.STRING);
        setDescription(jdescription == null ? null : jdescription.getString());
        
        return this;
    }

    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) throws ValidationException {
    }
}
