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

import es.elixir.bsc.json.schema.model.JsonSchema;
import java.net.URL;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Dmitry Repchevsky
 */

public interface JsonSchemaReader {
    
    /**
     * 
     * @param url the URL to read Json Schema from.
     * @return
     * @throws JsonSchemaException 
     */
    JsonSchema read(URL url) throws JsonSchemaException;
    JsonSchema read(JsonSchemaLocator locator) throws JsonSchemaException;
    
    public static JsonSchemaReader getReader() {
        ServiceLoader<JsonSchemaReader> loader = ServiceLoader.load(JsonSchemaReader.class);
        Iterator<JsonSchemaReader> iterator = loader.iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }
}
