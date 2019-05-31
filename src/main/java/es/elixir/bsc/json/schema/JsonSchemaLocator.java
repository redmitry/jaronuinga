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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import javax.json.JsonObject;

/**
 * @author Dmitry Repchevsky
 */
    
public abstract class JsonSchemaLocator {
   
    public final URI uri;
    
    public JsonSchemaLocator(final URI uri) {
        this.uri = uri;
    }

    /**
     * @return a map of json schema objects defined in the locator's URI.
     * The map keys are the json pointers.
     */
    public final Map<String, JsonObject> getSchemas() {
        return getSchemas(uri);
    }
    
    public abstract Map<String, JsonObject> getSchemas(URI uri);
    
    /**
     * @param uri the URI to be resolved in a context this locator.
     * @return new locator that is able to return Json Schema.
     */
    public abstract JsonSchemaLocator resolve(URI uri);
    
    /**
     * @return the input stream for the Json Schema located by this locator
     * @throws IOException 
     */
    public abstract InputStream getInputStream() throws IOException;
}
