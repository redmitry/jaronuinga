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
import es.elixir.bsc.json.schema.impl.DefaultJsonSchemaLocator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonStructure;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Repchevsky
 */

public class JsonEnumTest {

    private final static String JSON_SCHEMA_FILE = "draft4/schemas/enum.json";
    private final static String JSON_FILE_01 = "draft4/data/enum_01.json";
    private final static String JSON_FILE_02 = "draft4/data/enum_02.json";
    private final static String JSON_FILE_03 = "draft4/data/enum_03.json";

    @Test
    public void test_01() {
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream(JSON_FILE_01)) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource(JSON_SCHEMA_FILE);
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors);
            
            Assert.assertTrue(errors.isEmpty());

        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void test_02() {
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream(JSON_FILE_02)) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource(JSON_SCHEMA_FILE);
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors);
            
            Assert.assertTrue(errors.isEmpty());

        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }
    
    @Test
    public void test_03() {
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream(JSON_FILE_03)) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource(JSON_SCHEMA_FILE);
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors);
            
            Assert.assertEquals(1, errors.size());

        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }
}
