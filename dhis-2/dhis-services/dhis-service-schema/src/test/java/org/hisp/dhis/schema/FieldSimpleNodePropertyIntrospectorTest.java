/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.node.annotation.NodeSimple;
import org.junit.Test;

class SimpleFields
{
    @NodeSimple( isAttribute = false )
    private String property;

    @NodeSimple( value = "renamedProperty", isAttribute = true )
    private String propertyToBeRenamed;

    @NodeSimple( isReadable = true, isWritable = false )
    private String readOnly;

    @NodeSimple( isReadable = false, isWritable = true )
    private boolean writeOnly;

    @NodeSimple( namespace = "http://ns.example.org" )
    private String propertyWithNamespace;

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }
}

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FieldSimpleNodePropertyIntrospectorTest extends AbstractNodePropertyIntrospectorTest
{

    public FieldSimpleNodePropertyIntrospectorTest()
    {
        super( SimpleFields.class );
    }

    @Test
    public void testAttribute()
    {
        assertFalse( propertyMap.get( "property" ).isAttribute() );
        assertTrue( propertyMap.get( "renamedProperty" ).isAttribute() );
    }

}