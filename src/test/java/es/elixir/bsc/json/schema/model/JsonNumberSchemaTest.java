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
import es.elixir.bsc.json.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
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

public class JsonNumberSchemaTest {
    
    @Test
    public void test_01() {
        
        try (InputStream in = JsonNumberSchemaTest.class.getClassLoader().getResourceAsStream("draft4/data/number_01.json")) {
            
            URL url = JsonNumberSchemaTest.class.getClassLoader().getResource("draft4/schemas/number.json");
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors);
            
            Assert.assertFalse("no validation errors found", errors.isEmpty());
            Assert.assertTrue("too many validation errors", errors.size() == 1);
            Assert.assertEquals("wrong validation error code", ValidationMessage.NUMBER_MIN_CONSTRAINT.CODE, errors.get(0).code);
            
        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonNumberSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    @Test
    public void test_02() {
        
        try (InputStream in = JsonNumberSchemaTest.class.getClassLoader().getResourceAsStream("draft4/data/number_02.json")) {
            
            URL url = JsonNumberSchemaTest.class.getClassLoader().getResource("draft4/schemas/number.json");
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors, null);
            
            Assert.assertTrue("no validation errors found", errors.isEmpty());            
        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonNumberSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Test
    public void test_03() {
        
        try (InputStream in = JsonNumberSchemaTest.class.getClassLoader().getResourceAsStream("draft4/data/number_03.json")) {
            
            URL url = JsonNumberSchemaTest.class.getClassLoader().getResource("draft4/schemas/number.json");
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            List<ValidationError> errors = new ArrayList<>();
            schema.validate(json, errors, null);
            
            Assert.assertFalse("no validation errors found", errors.isEmpty());
            Assert.assertTrue("too many validation errors", errors.size() == 1);
            Assert.assertEquals("wrong validation error code", ValidationMessage.NUMBER_MAX_CONSTRAINT.CODE, errors.get(0).code);
            
        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonNumberSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
