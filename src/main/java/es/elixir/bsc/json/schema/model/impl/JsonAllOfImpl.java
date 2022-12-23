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

import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonAllOf;
import java.util.List;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.model.AbstractJsonSchema;
import javax.json.JsonValue;
import java.util.ArrayList;

/**
 * @author Dmitry Repchevsky
 */

public class JsonAllOfImpl extends SchemaArrayImpl
                           implements JsonAllOf {

    @Override
    public boolean validate(String jsonPointer, JsonValue object, JsonValue parent, 
            List<String> evaluated, List<ValidationError> errors,
            JsonSchemaValidationCallback<JsonValue> callback) {

        final int nerrors = errors.size();
        
        for (AbstractJsonSchema schema : this) {
            final List<String> eva = new ArrayList();
            if (schema.validate(jsonPointer, object, parent, eva, errors, callback)) {
                evaluated.addAll(eva);
            }
        }
        
        if (nerrors != errors.size()) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    jsonPointer, ValidationMessage.OBJECT_ALL_OF_CONSTRAINT_MSG));
        }
        
        return nerrors == errors.size();
    }
}
