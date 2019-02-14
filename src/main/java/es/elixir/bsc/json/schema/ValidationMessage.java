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

package es.elixir.bsc.json.schema;

/**
 * @author Dmitry Repchevsky
 */

public enum ValidationMessage {
    
    OBJECT_REQUIRED_PROPERTY_CONSTRAINT(1, "object missed required property: %s"),
    
    OBJECT_ALL_OF_CONSTRAINT(2, "object allOf constraint failed"),
    OBJECT_ANY_OF_CONSTRAINT(3, "object anyOf constraint failed"),
    OBJECT_ONE_OF_CONSTRAINT(4, "object oneOf constraint failed"),
    OBJECT_NOT_CONSTRAINT(5, "object not constraint failed"),

    ARRAY_MIN_ITEMS_CONSTRAINT(6, "minItems = %d > array.items.size = %d"),
    ARRAY_MAX_ITEMS_CONSTRAINT(7, "maxItems = %d < array.items.size = %d"),
    ARRAY_LENGTH_MISMATCH(8, "array.length = %d not equals items.length = %d"),

    STRING_MIN_LENGTH_CONSTRAINT(9, "string.length %d < minLength = %d"),
    STRING_MAX_LENGTH_CONSTRAINT(10, "string.length %d < minLength = %d"),
    
    STRING_PATTERN_CONSTRAINT(11, "string.pattern constraint '%s' %s"),
    STRING_DATE_TIME_FORMAT_CONSTRAINT(12, "invalid datetime format: '%s'"),
    STRING_EMAIL_FORMAT_CONSTRAINT(13, "invalid email format: '%s'"),
    STRING_HOSTNAME_FORMAT_CONSTRAINT(14, "invalid hostname format: '%s'"),
    STRING_IP4_FORMAT_CONSTRAINT(15, "invalid IPv4 format: '%s'"),
    STRING_IP6_FORMAT_CONSTRAINT(16, "invalid IPv6 format: '%s'"),
    STRING_URI_FORMAT_CONSTRAINT(17, "invalid URI format: '%s'"),
    
    NUMBER_MIN_CONSTRAINT(18, "value = %s %s number.minimum = %s"),
    NUMBER_MAX_CONSTRAINT(19, "value = %s %s number.maximum = %s"),

    INTEGER_MIN_CONSTRAINT(20, "value = %d %s integer.minimum = %d"),
    INTEGER_MAX_CONSTRAINT(21, "value = %d %s integer.maximum = %d");
    
    public final int CODE;
    public final String VALUE;
    
    private ValidationMessage(final int code, final String value) {
        CODE = code;
        VALUE = value;
    }
}
