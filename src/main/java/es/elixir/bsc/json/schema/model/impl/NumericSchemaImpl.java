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

package es.elixir.bsc.json.schema.model.impl;

import es.elixir.bsc.json.schema.JsonSchemaException;
import es.elixir.bsc.json.schema.JsonSchemaLocator;
import es.elixir.bsc.json.schema.JsonSchemaParser;
import es.elixir.bsc.json.schema.model.NumericSchema;
import es.elixir.bsc.json.schema.model.PrimitiveSchema;
import javax.json.JsonObject;

/**
 * @author Dmitry Repchevsky
 * @param <T>
 */

public abstract class NumericSchemaImpl<T extends Number> extends PrimitiveSchema
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
    public NumericSchemaImpl read(JsonSchemaParser parser, JsonSchemaLocator locator, String jsonPointer, JsonObject object) throws JsonSchemaException {
        super.read(parser, locator, jsonPointer, object);
        
        if (object.getBoolean(EXCLUSIVE_MINIMUM, false)) {
            exclusiveMinimum = true;
        }
        
        if (object.getBoolean(EXCLUSIVE_MAXIMUM, false)) {
            exclusiveMaximum = true;
        }

        return this;
    }
}
