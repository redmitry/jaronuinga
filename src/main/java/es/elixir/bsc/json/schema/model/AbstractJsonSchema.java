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

package es.elixir.bsc.json.schema.model;

import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationException;
import java.util.List;
import javax.json.JsonValue;

/**
 * This interface is used internally and exists only for the purpose to hide
 * actual validate() method from the outside.
 * 
 * @author Dmitry Repchevsky
 */

public interface AbstractJsonSchema extends JsonSchema {

    void validate(String jsonPointer, JsonValue value, JsonValue parent, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException;

    @Override
    default void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException {
        validate("/", value, null, errors, callback);
    }
}