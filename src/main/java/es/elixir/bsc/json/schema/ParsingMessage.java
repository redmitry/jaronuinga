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

package es.elixir.bsc.json.schema;

/**
 * @author Dmitry Repchevsky
 */

public enum ParsingMessage {
    
    JSON_PARSING_ERROR(1, "json parsing error: '%s'"),
    SCHEMA_OBJECT_ERROR(2, "schema json type error: '%s' (must be either 'true', 'false' or 'object')"),
    UNRESOLVABLE_SCHEMA(3, "failed to resolve schema: '%s'"),
    INVALID_ATTRIBUTE_TYPE(4, "invalid attribute type: '%s': '%s' (must be '%s')."),
    MISSED_ATTRIBUTE(5, "mandatory attribute missed: '%s'."),
    INVALID_OBJECT_TYPE(6, "invalid object type: %s type '%s' (must be '%s')."),
    UNKNOWN_OBJECT_TYPE(7, "unknown object type: '%s'."),
    INVALID_REFERENCE(8, "invalid reference: '$ref': '%s'."),
    UNRESOLVABLE_REFERENCE(9, "unable to resolve reference. '$ref': '%s'."),
    CONSTRAINT_ERROR(10, "%s constraint: '%s' '%s."),
    EMPTY_ENUM(11, "empty enum.");
    
    public final int CODE;
    public final String VALUE;
    
    private ParsingMessage(final int code, final String value) {
        CODE = code;
        VALUE = value;
    }
}
