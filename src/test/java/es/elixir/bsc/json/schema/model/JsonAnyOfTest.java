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
import javax.json.JsonStructure;
import org.junit.Test;

/**
 * @author Dmitry Repchevsky
 */

public class JsonAnyOfTest {

    @Test
    public void test_01() {
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream("draft4/data/anyOf_01.json")) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource("draft4/schemas/anyOf.json");
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors);

        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void test_02() {
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream("draft4/data/anyOf_02.json")) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource("draft4/schemas/anyOf.json");
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors, null);

        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
