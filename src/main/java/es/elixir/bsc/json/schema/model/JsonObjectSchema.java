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

package es.elixir.bsc.json.schema.model;

/**
 * JSON Schema for the JSON Object type
 * 
 * @author Dmitry Repchevsky
 */

public interface JsonObjectSchema extends AbstractJsonSchema {

    public final static String DEFINITIONS = "definitions";
    public final static String PROPERTIES = "properties";
    public final static String REQUIRED = "required";
    public final static String DEPENDENCIES = "dependencies";
    public final static String DEPENDENT_SCHEMAS = "dependentSchemas";
    public final static String DEPENDENT_REQUIRED = "dependentRequired";
    public final static String ADDITIONAL_PROPERTIES = "additionalProperties";
    public final static String PATTERN_PROPERTIES = "patternProperties";
    public final static String UNEVALUATED_PROPERTIES = "unevaluatedProperties";
    public final static String PROPERTY_NAMES = "propertyNames";
    public final static String IF = "if";
    public final static String THEN = "then";
    public final static String ELSE = "else";
    
    JsonDefinitions getDefinitions();
    JsonProperties getProperties();
    StringArray getRequired();
    JsonProperties getDependentSchemas();
    JsonDependentProperties getDependentRequired();
    AbstractJsonSchema getPropertyNames();
    AbstractJsonSchema getAdditionalProperties();
    Boolean isAdditionalProperties();
    JsonProperties getPatternProperties();
    AbstractJsonSchema getUnevaluatedProperties();
    Boolean isUnevaluatedProperties();
    AbstractJsonSchema getIf();
    AbstractJsonSchema getThen();
    AbstractJsonSchema getElse();
}
