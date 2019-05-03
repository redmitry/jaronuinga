package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.JsonSchemaValidationCallback;
import es.elixir.bsc.json.schema.ValidationError;
import es.elixir.bsc.json.schema.ValidationMessage;
import es.elixir.bsc.json.schema.model.JsonEnum;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;

/**
 * @author Dmitry Repchevsky
 */

public class JsonEnumImpl extends PrimitiveSchema implements JsonEnum {

    private JsonArray values;

    @Override
    public JsonArray getValues() {
        return values;
    }

    @Override
    public void setValues(JsonArray values) {
        this.values = values;
    }
    
    @Override
    public JsonEnumImpl read(final JsonSubschemaParser parser, 
                             final JsonSchemaLocator locator, 
                             final String jsonPointer, 
                             final JsonObject object,
                             final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, jsonPointer, object, type);
        
        values = JsonSchemaUtil.check(object.get(ENUM), JsonValue.ValueType.ARRAY);
        
        return this;
    }
    
    @Override
    public void validate(JsonValue value, List<ValidationError> errors, JsonSchemaValidationCallback callback) {
        
        if (value.getValueType() == JsonValue.ValueType.ARRAY || 
            value.getValueType() == JsonValue.ValueType.OBJECT) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ENUM_INVALID_VALUE_TYPE, value.getValueType().name()));
        } else if (values == null || !values.contains(value)) {
            errors.add(new ValidationError(getId(), getJsonPointer(),
                    ValidationMessage.ENUM_INVALID_VALUE, value.toString(), values.toString()));
         
        }
    }
}
