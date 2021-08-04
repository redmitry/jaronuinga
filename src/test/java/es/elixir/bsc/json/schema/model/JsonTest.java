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
import org.junit.Assert;

/**
 * @author Dmitry Repchevsky
 */

public class JsonTest {

    public List<ValidationError> test(String fschema, String file) {

        List<ValidationError> errors = new ArrayList<>();
        
        try (InputStream in = JsonAnyOfTest.class.getClassLoader().getResourceAsStream(file)) {
            
            URL url = JsonAnyOfTest.class.getClassLoader().getResource(fschema);
            
            JsonSchema schema = JsonSchemaReader.getReader().read(url);
            JsonStructure json = Json.createReader(in).read();
            
            schema.validate(json, errors);
        } catch (IOException | JsonSchemaException ex) {
            Logger.getLogger(JsonAnyOfTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
        
        return errors;
    }
}
