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

package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonNumberSchema;
import java.math.BigDecimal;
import java.util.List;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;

/**
 * @author Dmitry Repchevsky
 */

public class JsonNumberSchemaImpl extends NumericSchemaImpl<BigDecimal>
                                  implements JsonNumberSchema {
    
    @Override
    public JsonNumberSchemaImpl read(final JsonSubschemaParser parser, 
                                     final JsonSchemaLocator locator, 
                                     final String jsonPointer, 
                                     final JsonObject object,
                                     final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, jsonPointer, object, type);

        final JsonNumber min = JsonSchemaUtil.check(object.getJsonNumber(MINIMUM), JsonValue.ValueType.NUMBER);
        if (min != null) {
            minimum = min.bigDecimalValue();
        }
        
        final JsonNumber max = JsonSchemaUtil.check(object.getJsonNumber(MAXIMUM), JsonValue.ValueType.NUMBER);
        if (max != null) {
            maximum = max.bigDecimalValue();
        }
        
        return this;
    }

    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {

        if (value.getValueType() != JsonValue.ValueType.NUMBER) {
            return;
        }
        
        JsonNumber number = (JsonNumber)value;
        BigDecimal dec = number.bigDecimalValue();

        if (minimum != null) {
            if (exclusiveMinimum != null && exclusiveMinimum) {
                if (dec.compareTo(minimum) <= 0) {
                    errors.add(new ValidationError(getId(), getJsonPointer(),
                            ValidationMessage.NUMBER_MIN_CONSTRAINT, dec, "<=", minimum));
                }
            } else if (dec.compareTo(minimum) < 0) {
                    errors.add(new ValidationError(getId(), getJsonPointer(),
                            ValidationMessage.NUMBER_MIN_CONSTRAINT, dec, "<", minimum));
            }
        }
        
        if (maximum != null) {
            if (exclusiveMaximum != null && exclusiveMaximum) {
                if (dec.compareTo(maximum) >= 0) {
                    errors.add(new ValidationError(getId(), getJsonPointer(),
                            ValidationMessage.NUMBER_MAX_CONSTRAINT, dec.toPlainString(), ">=", maximum));
                }
            } else if (dec.compareTo(maximum) > 0) {
                    errors.add(new ValidationError(getId(), getJsonPointer(),
                            ValidationMessage.NUMBER_MAX_CONSTRAINT, dec.toPlainString(), ">", maximum));
            }
        }
        
        if (callback != null) {
            callback.validated(this, value, errors);
        }
    }
}
