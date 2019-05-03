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
import es.elixir.bsc.json.schema.model.JsonStringSchema;
import java.math.BigInteger;
import java.util.List;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.ValidationException;
import es.elixir.bsc.json.schema.impl.DefaultJsonStringFormatValidator;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import java.util.regex.Pattern;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;

/**
 * @author Dmitry Repchevsky
 */

public class JsonStringSchemaImpl extends PrimitiveSchema
                                  implements JsonStringSchema {
    
    private Long minLength;
    private Long maxLength;
    
    private String format;
    private Pattern pattern;
    
    @Override
    public Long getMinLength() {
        return minLength;
    }
    
    @Override
    public void setMinLength(Long minLength) {
        this.minLength = minLength;
    }
    
    @Override
    public Long getMaxLength() {
        return maxLength;
    }
    
    @Override
    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }
    
    @Override
    public String getFormat() {
        return format;
    }
    
    @Override
    public String getPattern() {
        return pattern == null ? null : pattern.pattern();
    }

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern == null ? null : Pattern.compile(pattern);
        
    }
    
    @Override
    public void setFormat(String format) {
        this.format = format;
    }
    
    @Override
    public JsonStringSchemaImpl read(final JsonSubschemaParser parser, 
                                     final JsonSchemaLocator locator, 
                                     final String jsonPointer, 
                                     final JsonObject object,
                                     final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, jsonPointer, object, type);
        
        final JsonNumber min = JsonSchemaUtil.check(object.getJsonNumber(MIN_LENGTH), JsonValue.ValueType.NUMBER);
        final JsonNumber max = JsonSchemaUtil.check(object.getJsonNumber(MAX_LENGTH), JsonValue.ValueType.NUMBER);
        
        minLength = getLength(min);
        maxLength = getLength(max);
        
        final JsonString jformat = JsonSchemaUtil.check(object.getJsonString(FORMAT), JsonValue.ValueType.STRING);
        if (jformat != null) {
            setFormat(jformat.getString());
        }

        final JsonString jpattern = JsonSchemaUtil.check(object.getJsonString(PATTERN), JsonValue.ValueType.STRING);
        if (jpattern != null) {
            setPattern(jpattern.getString());
        }

        return this;
    }

    private Long getLength(JsonNumber length) throws JsonSchemaException {
        if (length == null) {
            return null;
        }
        
        if (length.isIntegral()) {
            final BigInteger bi = length.bigIntegerValue();
            if (bi.bitLength() > Integer.SIZE) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                              new Object[] {"string length", bi.toString(), "is too big"}));
            } else if (bi.signum() < 0) {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                              new Object[] {"string length", bi.toString(), "cant be negative"}));
            }
            
            return bi.longValue();
        } else {
                throw new JsonSchemaException(new ParsingError(ParsingMessage.INVALID_REFERENCE,
                                              new Object[] {"string length", length.bigDecimalValue(), "cant be decimal"}));
        }
    }
    
    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {
        
        if (value.getValueType() != JsonValue.ValueType.STRING) {
            return;
        }
        JsonString string = (JsonString)value;
        String str = string.getString();
        
        if (minLength != null && str.length() < minLength) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.STRING_MIN_LENGTH_CONSTRAINT, str.length(), minLength));
        }
        
        if (maxLength != null && str.length() > maxLength) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.STRING_MAX_LENGTH_CONSTRAINT, str.length(), maxLength));

        }
        
        if (pattern != null && !pattern.matcher(str).find()) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.STRING_PATTERN_CONSTRAINT, pattern, str));
            
        }
        
        if (format != null && !format.isEmpty()) {
            try {
                DefaultJsonStringFormatValidator.validate(this, str);
            } catch (ValidationException ex) {
                errors.add(ex.error);
            }
        }

        if (callback != null) {
            callback.validated(this, value, errors);
        }
    }
}
