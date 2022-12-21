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
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationException;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import es.elixir.bsc.json.schema.model.JsonReference;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.JsonType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class JsonReferenceImpl extends JsonSchemaImpl<JsonString> implements JsonReference {

    private AbstractJsonSchema schema;

    private String ref;
    private String ref_pointer;
    private JsonSchemaLocator ref_locator;
    private JsonSubschemaParser parser;
    
    @Override
    public AbstractJsonSchema getSchema() throws JsonSchemaException {
        if (schema == null) {
            try {
                JsonObject jsubschema = ref_locator.getSchema(ref_pointer);
                if (jsubschema == null) {
                    throw new JsonSchemaException(
                            new ParsingError(ParsingMessage.UNRESOLVABLE_REFERENCE, new Object[] {ref}));
                }

                schema = parser.parse(ref_locator, getParent(), ref_pointer, jsubschema, null);
                ref_locator.putSchema(schema);
            } catch(IOException | JsonException | IllegalArgumentException ex) {
                throw new JsonSchemaException(
                    new ParsingError(ParsingMessage.INVALID_REFERENCE, new Object[] {ref}));
            }
        }
        return schema;
    }

    @Override
    public JsonReferenceImpl read(JsonSubschemaParser parser, 
                                  JsonSchemaLocator locator,
                                  JsonSchemaElement parent,
                                  String jsonPointer,
                                  JsonString jref, 
                                  JsonType type) throws JsonSchemaException {

        this.parser = parser;
        
        ref = jref.getString();
        try {
            final URI uri = URI.create(ref);
            final String fragment = uri.getFragment();
            if (fragment == null) {
                ref_pointer = "";
                ref_locator = locator.resolve(uri);
            } else if ("#".equals(ref)) {
                ref_pointer = "";
                ref_locator = locator;
            } else if (fragment.startsWith("/")){
                ref_pointer = fragment.replaceAll("/$", "");
                if (ref.startsWith("#")) {
                    ref_locator = locator;
                } else {
                    ref_locator = locator.resolve(
                        new URI(uri.getScheme(), uri.getUserInfo(), 
                                uri.getHost(), uri.getPort(), uri.getPath(), 
                                null, null));                        
                }
            } else {
                ref_pointer = "";
                ref_locator = locator.resolve(uri);
            }
        } catch(JsonException | IllegalArgumentException | URISyntaxException ex) {
            throw new JsonSchemaException(
                    new ParsingError(ParsingMessage.INVALID_REFERENCE, new Object[] {ref}));
        }
        
        return this;
    }

    @Override
    public boolean validate(String jsonPointer, JsonValue value, JsonValue parent, 
            List<String> evaluated, List<ValidationError> errors, 
            JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException {

        try {
            final AbstractJsonSchema sch = getSchema();
            return sch.validate(jsonPointer, value, parent, evaluated, errors, callback);
        } catch (JsonSchemaException ex) {
            errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                    ValidationMessage.REFERENCE_UNRESOLVED_MSG, ref));
        }
        return false;
    }
}
