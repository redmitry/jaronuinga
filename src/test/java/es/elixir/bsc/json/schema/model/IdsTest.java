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

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.impl.DefaultJsonSchemaLocator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Repchevsky
 */

public class IdsTest {

    private final static String JSON_SCHEMA_FILE = "draft4/schemas/ids.json";
    private final static String JSON_OTHER_SCHEMA_ID = "other.json";

    @Test
    public void test_01() {
        
        try {
            URL url = IdsTest.class.getClassLoader().getResource(JSON_SCHEMA_FILE);
            DefaultJsonSchemaLocator locator = new DefaultJsonSchemaLocator(url.toURI());
            JsonSchema schema = JsonSchemaReader.getReader().read(locator);

            URI other = schema.getId().resolve(JSON_OTHER_SCHEMA_ID);
            Assert.assertNotNull("unresolved " + JSON_OTHER_SCHEMA_ID, locator.getSchema(other, "/"));

        } catch (JsonSchemaException | URISyntaxException | IOException | JsonException ex) {
            Logger.getLogger(IdsTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
