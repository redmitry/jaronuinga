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

public enum ParsingMessage {
    
    UNRESOLVABLE_SCHEMA(1, "failed to resolve schema: '%s'"),
    JSON_PARSING_ERROR(2, "json parsing error: '%s'"),
    INVALID_ATTRIBUTE_TYPE(3, "invalid attribute type: '%s': '%s' (must be '%s')."),
    MISSED_ATTRIBUTE(4, "mandatory attribute missed: '%s'."),
    INVALID_OBJECT_TYPE(5, "invalid object type: %s type '%s' (must be '%s')."),
    UNKNOWN_OBJECT_TYPE(6, "unknown object type: '%s'."),
    INVALID_REFERENCE(7, "invalid reference: '$ref': '%s'."),
    UNRESOLVABLE_REFERENCE(8, "unable to resolve reference. '$ref': '%s'."),
    CONSTRAINT_ERROR(9, "%s constraint: '%s' '%s."),
    EMPTY_ENUM(10, "empty enum.");
    
    public final int CODE;
    public final String VALUE;
    
    private ParsingMessage(final int code, final String value) {
        CODE = code;
        VALUE = value;
    }
}
