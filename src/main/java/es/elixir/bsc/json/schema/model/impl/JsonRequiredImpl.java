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
import java.util.TreeSet;
import java.util.Collections;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
import es.elixir.bsc.json.schema.model.StringArray;

/**
 * @author Dmitry Repchevsky
 */

public class JsonRequiredImpl extends TreeSet<String>
                              implements StringArray {

    JsonRequiredImpl() {}

    JsonRequiredImpl(TreeSet<String> required) {
        super(required != null ? required : Collections.EMPTY_SET);
    }

    @Override
    public boolean remove(String name) {
        return super.remove(name);
    }

    @Override
    public boolean contains(String name) {
        return super.contains(name);
    }

    public JsonRequiredImpl read(JsonArray array) throws JsonSchemaException {
        for (JsonValue value : array) {
            final JsonString str = JsonSchemaUtil.check(value, JsonValue.ValueType.STRING);
            add(str.getString());
        }
        return this;
    }
}
