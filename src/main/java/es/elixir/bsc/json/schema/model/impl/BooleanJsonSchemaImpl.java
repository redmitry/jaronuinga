package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ParsingError;
import es.elixir.bsc.json.schema.ParsingMessage;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationException;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.BooleanJsonSchema;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;
import es.elixir.bsc.json.schema.model.JsonType;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import java.util.List;

/**
 * @author Dmitry Repchevsky
 */

public class BooleanJsonSchemaImpl extends JsonSchemaImpl<JsonValue>
        implements BooleanJsonSchema {

    private boolean evaluation;
    
    @Override
    public BooleanJsonSchemaImpl read(final JsonSubschemaParser parser, 
                                      final JsonSchemaLocator locator,
                                      final JsonSchemaElement parent,
                                      final String jsonPointer, 
                                      final JsonValue schema,
                                      final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, parent, jsonPointer, schema, type);

        if (schema.getValueType() != ValueType.TRUE &&
            schema.getValueType() != ValueType.FALSE) {
            throw new JsonSchemaException(new ParsingError(ParsingMessage.SCHEMA_OBJECT_ERROR, 
                   new Object[] {schema.getValueType()}));
        }
        
        evaluation = schema.getValueType() == ValueType.TRUE;
        
        return this;
    }
    
    @Override
    public boolean validate(String jsonPointer, JsonValue value, JsonValue parent, List<String> evaluated, List<ValidationError> errors, JsonSchemaValidationCallback<JsonValue> callback) throws ValidationException {
        return evaluation;
    }
}
