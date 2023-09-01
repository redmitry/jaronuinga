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
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.JsonReference;
import es.elixir.bsc.json.schema.model.JsonType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 */

public class JsonReferenceImpl extends AbstractJsonReferenceImpl implements JsonReference {

    private AbstractJsonSchema schema;

    private String ref;
    private String ref_pointer;
    private JsonSchemaLocator ref_locator;
    private JsonSubschemaParser parser;
    
    public JsonReferenceImpl(JsonSchemaImpl parent, JsonSchemaLocator locator,
            String jsonPointer) {
        super(parent, locator, jsonPointer);
    }

    @Override
    public AbstractJsonSchema getSchema() throws JsonSchemaException {
        if (schema == null) {
            try {
                JsonValue jsubschema = ref_locator.getSchema(ref_pointer);
                if (jsubschema == null) {
                    throw new JsonSchemaException(
                            new ParsingError(ParsingMessage.UNRESOLVABLE_REFERENCE, new Object[] {ref}));
                }

                schema = parser.parse(ref_locator, getParent(), ref_pointer, jsubschema, null);
            } catch(IOException | JsonException | IllegalArgumentException ex) {
                throw new JsonSchemaException(
                    new ParsingError(ParsingMessage.INVALID_REFERENCE, new Object[] {ref}));
            }
        }
        return schema;
    }

    @Override
    public JsonReferenceImpl read(final JsonSubschemaParser parser,
                                  final JsonObject object, 
                                  final JsonType type) throws JsonSchemaException {

        super.read(parser, object, type);

        this.parser = parser;

        final String ref = object.getString(REF);
        try {
            final URI uri = URI.create(ref);
            final String fragment = uri.getFragment();
            if (fragment == null) {
                ref_pointer = "";
                ref_locator = getCurrentScope().resolve(uri);
            } else if ("#".equals(ref)) {
                ref_pointer = "";
                ref_locator = getCurrentScope();
            } else if (fragment.startsWith("/")){
                ref_pointer = fragment;
                if (ref.startsWith("#")) {
                    ref_locator = getCurrentScope();
                } else {
                    ref_locator = getCurrentScope().resolve(
                        new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null));                        
                }
            } else {
                ref_pointer = "";
                ref_locator = getCurrentScope().resolve(uri);
            }
        } catch(JsonException | IllegalArgumentException | URISyntaxException ex) {
            throw new JsonSchemaException(
                    new ParsingError(ParsingMessage.INVALID_REFERENCE, new Object[] {ref}));
        }
        
        return this;
    }
}
