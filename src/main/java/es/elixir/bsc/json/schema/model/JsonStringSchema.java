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

package es.elixir.bsc.json.schema.model;

/**
 * @author Dmitry Repchevsky
 */

public interface JsonStringSchema extends JsonSchema {
    
    public final static String MIN_LENGTH = "minLength";
    public final static String MAX_LENGTH = "maxLength";
    
    public final static String FORMAT = "format";
    public final static String PATTERN = "pattern";

    Long getMinLength();
    void setMinLength(Long minLength);
    
    Long getMaxLength();
    void setMaxLength(Long maxLength);
    
    String getFormat();    
    void setFormat(String format);
    
    String getPattern();
    void setPattern(String pattern);
}
