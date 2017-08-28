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

package es.elixir.bsc.json.schema.model;

import java.util.List;

/**
 * JSON Schema for the JSON Array type
 * 
 * @author Dmitry Repchevsky
 */

public interface JsonArraySchema extends JsonSchema {
    
    public final static String ITEMS = "items";
    public final static String ADDITIONAL_ITEMS = "additionalItems";
    
    public final static String MIN_ITEMS = "minItems";
    public final static String MAX_ITEMS = "maxItems";
    
    Long getMinItems();
    void setMinItems(Long minItems);

    Long getMaxItems();
    void setMaxItems(Long maxItems);

    /**
     * In case there is only one schema in the list it is either {...} or [{...}].
     * Setting 'additionalItems' to <b>NULL</b> means there is a single schema and 
     * not an array with only one schema defined.
     * 
     * @return list of schemas
     */
    List<JsonSchema> getItems();
    
    /**
     * @return 'additionalSchema' JsonSchema, <b>NULL</b> if FALSE (or not set), 
     *         EmptyJsonSchema if TRUE
     */
    JsonSchema getAdditionalItems();
    void setAdditionalItems(JsonSchema schema);
    
    /**
     * Setting 'additionalItems' to <b>NULL</b> means FALSE if there are
     * more than one 'items' schemas.
     * 
     * @param additionalItems 
     */
    void setAdditionalItems(Boolean additionalItems);
}
