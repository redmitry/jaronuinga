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

package es.elixir.bsc.json.schema.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitry Repchevsky
 */

public class JsonDateTimeFormatTest extends JsonTest {
    
    private final static String JSON_SCHEMA_FILE = "draft4/schemas/datetime.json";

    private final static String JSON_FILE_01 = "draft4/data/datetime_01.json";
    private final static String JSON_FILE_02 = "draft4/data/datetime_02.json";

    @Test
    public void test_01() {
        Assert.assertTrue(test(JSON_SCHEMA_FILE, JSON_FILE_01).isEmpty());
    }

    @Test
    public void test_02() {
        Assert.assertFalse(test(JSON_SCHEMA_FILE, JSON_FILE_02).isEmpty());
    }
}
