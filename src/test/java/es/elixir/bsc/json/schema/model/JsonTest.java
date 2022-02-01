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
 * *****************************************************************************
 */

package es.elixir.bsc.json.schema.model;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.ValidationError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonValue;
import org.junit.Assert;

/**
 * @author Dmitry Repchevsky
 */

public class JsonTest {

    public List<ValidationError> test(String fschema, String file) {

        List<ValidationError> errors = new ArrayList<>();
        
        try (InputStream in = JsonTest.class.getClassLoader().getResourceAsStream(file)) {
            
            URL url = JsonTest.class.getClassLoader().getResource(fschema);
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonValue json = Json.createReader(in).readValue();
            
            schema.validate(json, errors);
        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
        
        return errors;
    }
}
