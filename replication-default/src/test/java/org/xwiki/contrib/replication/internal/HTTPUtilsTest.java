/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.replication.internal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Validate {@link HTTPUtils}.
 * 
 * @version $Id$
 */
class HTTPUtilsTest
{
    @Test
    void toList()
    {
        assertEquals(Arrays.asList(""), HTTPUtils.toList(""));
        assertEquals(Arrays.asList("e1"), HTTPUtils.toList("e1"));
        assertEquals(Arrays.asList("e1", ""), HTTPUtils.toList("e1|"));
        assertEquals(Arrays.asList("", "e1"), HTTPUtils.toList("|e1"));
        assertEquals(Arrays.asList("", ""), HTTPUtils.toList("|"));
        assertEquals(Arrays.asList("", "", "e1"), HTTPUtils.toList("||e1"));
    }

    @Test
    void toStr()
    {
        assertEquals("", HTTPUtils.toString(Arrays.asList("")));
        assertEquals("e1", HTTPUtils.toString(Arrays.asList("e1")));
        assertEquals("e1|", HTTPUtils.toString(Arrays.asList("e1", "")));
        assertEquals("|e1", HTTPUtils.toString(Arrays.asList("", "e1")));
        assertEquals("|", HTTPUtils.toString(Arrays.asList("", "")));
        assertEquals("||e1", HTTPUtils.toString(Arrays.asList("", "", "e1")));
    }
}
