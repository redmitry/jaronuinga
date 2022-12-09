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
 * *****************************************************************************
 */

package es.elixir.bsc.json.schema.org.tests;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaReader;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.impl.DefaultJsonSchemaLocator;
import es.elixir.bsc.json.schema.model.JsonSchema;
import es.elixir.bsc.json.schema.model.JsonTest;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.Assert;

/**
 * @author Dmitry Repchevsky
 */

public class JsonSchemaOrgTest {

    public void test(String file) {
        
        URL url = JsonTest.class.getClassLoader().getResource(file);
        
        try (InputStream in = url.openStream();
             JsonParser parser = Json.createParser(in)) {
            
            final StringBuilder out = new StringBuilder("");
            
            DefaultJsonSchemaLocator locator = new DefaultJsonSchemaLocator(url.toURI());
            if (parser.hasNext() && parser.next() == JsonParser.Event.START_ARRAY) {
                Stream<JsonValue> stream = parser.getArrayStream();
                Iterator<JsonValue> iter = stream.iterator();
                while (iter.hasNext()) {
                    JsonObject obj = iter.next().asJsonObject();
                    JsonObject sch = obj.getJsonObject("schema");
                    locator.setSchema(sch);
                    JsonSchema schema = JsonSchemaReader.getReader().read(locator);
                    JsonArray tests = obj.getJsonArray("tests");
                    for (int i = 0; i < tests.size(); i++) {
                        JsonObject test = tests.getJsonObject(i);
                        JsonValue data = test.get("data");
                        boolean valid = test.getBoolean("valid");
                        
                        List<ValidationError> errors = new ArrayList<>();
                        schema.validate(data, errors);
                        
                        if (valid != errors.isEmpty()) {
                            out.append(String.format("%s %s\n", obj.getString("description", ""), 
                                test.getString("description", "")));
                        }
                    }
                }
                if (out.length() > 0) {
                    Assert.fail(out.toString());
                }
            }
        } catch (IOException | JsonSchemaException | URISyntaxException ex) {
            Logger.getLogger(JsonSchemaOrgTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }

}
