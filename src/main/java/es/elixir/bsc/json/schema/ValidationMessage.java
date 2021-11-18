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

package es.elixir.bsc.json.schema;

/**
 * @author Dmitry Repchevsky
 */

public enum ValidationMessage {
    
    OBJECT_EXPECTED(1, "object expected, found: %s"),
    ARRAY_EXPECTED(2, "array expected, found: %s"),
    STRING_EXPECTED(3, "string expected, found: %s"),
    NUMBER_EXPECTED(4, "number expected, found: %s"),
    BOOLEAN_EXPECTED(5, "boolean expected, found: %s"),
    
    OBJECT_REQUIRED_PROPERTY_CONSTRAINT(6, "object missed required property: %s"),
    OBJECT_DEPENDENT_REQUIRED_CONSTRAINT(7, "object missed required dependent property: %s"),
    
    OBJECT_ADDITIONAL_PROPERTY_CONSTRAINT(8, "no additional properties allowed: %s"),
    
    OBJECT_ALL_OF_CONSTRAINT(9, "object allOf constraint failed"),
    OBJECT_ANY_OF_CONSTRAINT(10, "object anyOf constraint failed"),
    OBJECT_ONE_OF_CONSTRAINT(11, "object oneOf constraint failed"),
    OBJECT_NOT_CONSTRAINT(12, "object not constraint failed"),

    ARRAY_MIN_ITEMS_CONSTRAINT(13, "minItems = %d > array.items.size = %d"),
    ARRAY_MAX_ITEMS_CONSTRAINT(14, "maxItems = %d < array.items.size = %d"),
    ARRAY_LENGTH_MISMATCH(15, "array.length = %d not equals items.length = %d"),

    STRING_MIN_LENGTH_CONSTRAINT(16, "string.length %d < minLength = %d"),
    STRING_MAX_LENGTH_CONSTRAINT(17, "string.length %d > maxLength = %d"),
    
    STRING_PATTERN_CONSTRAINT(18, "string.pattern constraint '%s' %s"),
    STRING_DATE_TIME_FORMAT_CONSTRAINT(19, "invalid datetime format: '%s'"),
    STRING_EMAIL_FORMAT_CONSTRAINT(20, "invalid email format: '%s'"),
    STRING_HOSTNAME_FORMAT_CONSTRAINT(21, "invalid hostname format: '%s'"),
    STRING_IP4_FORMAT_CONSTRAINT(22, "invalid IPv4 format: '%s'"),
    STRING_IP6_FORMAT_CONSTRAINT(23, "invalid IPv6 format: '%s'"),
    STRING_URI_FORMAT_CONSTRAINT(24, "invalid URI format: '%s'"),
    
    NUMBER_MIN_CONSTRAINT(25, "value = %s %s number.minimum = %s"),
    NUMBER_MAX_CONSTRAINT(26, "value = %s %s number.maximum = %s"),

    INTEGER_MIN_CONSTRAINT(27, "value = %d %s integer.minimum = %d"),
    INTEGER_MAX_CONSTRAINT(28, "value = %d %s integer.maximum = %d"),
    
    ENUM_INVALID_VALUE_TYPE(29, "invalid value type for enum: %s"),
    ENUM_INVALID_VALUE(30, "value = %s enum.values = %s");
    
    public final int CODE;
    public final String VALUE;
    
    private ValidationMessage(final int code, final String value) {
        CODE = code;
        VALUE = value;
    }
}
