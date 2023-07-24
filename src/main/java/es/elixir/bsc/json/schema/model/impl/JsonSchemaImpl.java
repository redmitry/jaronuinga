package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.JsonSchema;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.JsonType;
import java.net.URI;
import jakarta.json.JsonValue;

/**
 * @author Dmitry Repchevsky
 * 
 * @param <T> the type of the Json Schema (either 'JsonObject' or 'JsonValue' for TRUE/FALSE)
 */

public abstract class JsonSchemaImpl<T extends JsonValue> implements JsonSchema {
    
    private URI id;
    private JsonSchemaElement parent;
    private String jsonPointer;

    @Override
    public URI getId() {
        return id;
    }
    
    @Override
    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public JsonSchemaElement getParent() {
        return parent;
    }

    @Override
    public String getJsonPointer() {
        return jsonPointer;
    }

    public JsonSchemaImpl read(JsonSubschemaParser parser, 
                               JsonSchemaLocator locator,
                               JsonSchemaElement parent,
                               String jsonPointer,
                               T schema, 
                               JsonType type) throws JsonSchemaException {

        this.parent = parent;
        this.jsonPointer = jsonPointer.isEmpty() ? "/" : jsonPointer;
        
        id = locator.uri;
        
        return this;
    }
}
