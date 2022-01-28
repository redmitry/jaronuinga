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

public final class ValidationErrorCode {

    public final static int OBJECT_EXPECTED = 1;
    public final static int ARRAY_EXPECTED = 2;
    public final static int STRING_EXPECTED = 3;
    public final static int NUMBER_EXPECTED = 4;
    public final static int BOOLEAN_EXPECTED = 5;
    
    public final static int OBJECT_REQUIRED_PROPERTY_CONSTRAINT = 6;
    public final static int OBJECT_DEPENDENT_REQUIRED_CONSTRAINT = 7;
    
    public final static int OBJECT_ADDITIONAL_PROPERTY_CONSTRAINT = 8;
    
    public final static int OBJECT_ALL_OF_CONSTRAINT = 9;
    public final static int OBJECT_ANY_OF_CONSTRAINT = 10;
    public final static int OBJECT_ONE_OF_CONSTRAINT = 11;
    public final static int OBJECT_NOT_CONSTRAINT = 12;

    public final static int ARRAY_MIN_ITEMS_CONSTRAINT = 13;
    public final static int ARRAY_MAX_ITEMS_CONSTRAINT = 14;
    public final static int ARRAY_LENGTH_MISMATCH = 15;

    public final static int STRING_MIN_LENGTH_CONSTRAINT = 16;
    public final static int STRING_MAX_LENGTH_CONSTRAINT = 17;
    
    public final static int STRING_PATTERN_CONSTRAINT = 18;
    public final static int STRING_DATE_TIME_FORMAT_CONSTRAINT = 19;
    public final static int STRING_EMAIL_FORMAT_CONSTRAINT = 20;
    public final static int STRING_HOSTNAME_FORMAT_CONSTRAINT = 21;
    public final static int STRING_IP4_FORMAT_CONSTRAINT = 22;
    public final static int STRING_IP6_FORMAT_CONSTRAINT = 23;
    public final static int STRING_URI_FORMAT_CONSTRAINT = 24;
    
    public final static int NUMBER_MIN_CONSTRAINT = 25;
    public final static int NUMBER_MAX_CONSTRAINT = 26;

    public final static int INTEGER_MIN_CONSTRAINT = 27;
    public final static int INTEGER_MAX_CONSTRAINT = 28;
    
    public final static int ENUM_INVALID_VALUE_TYPE = 29;
    public final static int ENUM_INVALID_VALUE = 30;

}