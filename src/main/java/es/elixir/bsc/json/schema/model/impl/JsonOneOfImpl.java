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

import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonOneOf;
import java.util.ArrayList;
import java.util.List;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;

/**
 * @author Dmitry Repchevsky
 */

public class JsonOneOfImpl extends SchemaArrayImpl 
                           implements JsonOneOf {

    @Override
    public void validate(String jsonPointer, JsonValue value, JsonValue parent, 
            List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) {

        int matches = 0;
        
        final List<ValidationError> err = new ArrayList<>();
        for (AbstractJsonSchema schema : this) {
            final int nerrors = err.size();
            schema.validate(jsonPointer, value, parent, err, callback);
            if (nerrors == err.size()) {
                matches++;
            }
        }
        
        // An instance validates successfully if it validates against 
        // exactly one schema defined by this keyword's value
        
        if (matches != 1) {
            errors.addAll(err);
            errors.add(new ValidationError(getId(), getJsonPointer(), jsonPointer,
                    ValidationMessage.OBJECT_ONE_OF_CONSTRAINT_MSG));
        }
    }
}
