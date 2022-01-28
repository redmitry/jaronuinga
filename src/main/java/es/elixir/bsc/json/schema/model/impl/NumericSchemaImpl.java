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

package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.model.JsonType;
import es.elixir.bsc.json.schema.model.NumericSchema;
import javax.json.JsonObject;
import es.elixir.bsc.json.schema.impl.JsonSubschemaParser;
import es.elixir.bsc.json.schema.model.JsonSchemaElement;

/**
 * @author Dmitry Repchevsky
 * @param <T>
 */

public abstract class NumericSchemaImpl<T extends Number> extends PrimitiveSchemaImpl
                                        implements NumericSchema<T> {
    protected T minimum;
    protected T maximum;

    protected Boolean exclusiveMinimum;
    protected Boolean exclusiveMaximum;
        
    @Override
    public T getMinimum() {
        return minimum;
    }
    
    @Override
    public void setMinimum(T minimum) {
        this.minimum = minimum;
    }
    
    @Override
    public T getMaximum() {
        return maximum;
    }
    
    @Override
    public void setMaximum(T max) {
        this.maximum = max;
    }

    @Override
    public Boolean getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    @Override
    public void setExclusiveMinimum(Boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    @Override
    public Boolean getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @Override
    public void setExclusiveMaximum(Boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    @Override
    public NumericSchemaImpl read(final JsonSubschemaParser parser, 
                                  final JsonSchemaLocator locator,
                                  final JsonSchemaElement parent,
                                  final String jsonPointer, 
                                  final JsonObject object, 
                                  final JsonType type) throws JsonSchemaException {

        super.read(parser, locator, parent, jsonPointer, object, type);
        
        if (object.getBoolean(EXCLUSIVE_MINIMUM, false)) {
            exclusiveMinimum = true;
        }
        
        if (object.getBoolean(EXCLUSIVE_MAXIMUM, false)) {
            exclusiveMaximum = true;
        }

        return this;
    }
}
