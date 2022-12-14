/*
 * Copyright (c) 2004-2004-2021, University of Oslo
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
package org.hisp.dhis.dxf2.metadata.objectbundle.validation;

import static org.hisp.dhis.dxf2.metadata.objectbundle.validation.ValidationUtils.addObjectReports;
import static org.hisp.dhis.feedback.ErrorCode.E5005;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata.objectbundle.ObjectBundle;
import org.hisp.dhis.feedback.ErrorReport;
import org.hisp.dhis.feedback.TypeReport;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.schema.Schema;

public class UniqueMultiPropertiesCheck implements ValidationCheck
{
    @Override
    public TypeReport check( ObjectBundle bundle, Class<? extends IdentifiableObject> klass,
        List<IdentifiableObject> persistedObjects, List<IdentifiableObject> nonPersistedObjects,
        ImportStrategy importStrategy, ValidationContext context )
    {
        TypeReport typeReport = new TypeReport( klass );

        Schema schema = context.getSchemaService().getSchema( klass );

        Map<List<String>, List<IdentifiableObject>> propertyValuesToObjects = new HashMap<>();

        for ( IdentifiableObject identifiableObject : nonPersistedObjects )
        {
            for ( Map.Entry<Collection<String>, Collection<Function<IdentifiableObject, String>>> entry : schema
                .getUniqueMultiPropertiesExctractors().entrySet() )
            {
                List<String> propertyValues = entry.getValue().stream()
                    .map( valueExtractor -> valueExtractor.apply( identifiableObject ) )
                    .collect( Collectors.toList() );

                if ( !propertyValuesToObjects.containsKey( propertyValues ) )
                {
                    propertyValuesToObjects.put( propertyValues, new ArrayList<>() );
                }

                propertyValuesToObjects.get( propertyValues ).add( identifiableObject );
            }
        }

        propertyValuesToObjects.entrySet()
            .forEach(
                pValuesObjectsEntry -> addErrorIfNecessary( context, bundle, klass, typeReport, pValuesObjectsEntry ) );

        return typeReport;
    }

    private void addErrorIfNecessary( ValidationContext context, ObjectBundle bundle,
        Class<? extends IdentifiableObject> klass, TypeReport typeReport,
        Map.Entry<List<String>, List<IdentifiableObject>> pValuesObjectsEntry )
    {
        if ( pValuesObjectsEntry.getValue().size() > 1 )
        {
            pValuesObjectsEntry.getValue()
                .forEach( identifiableObject -> addErrorToTypeReport(
                    typeReport,
                    klass,
                    identifiableObject,
                    pValuesObjectsEntry,
                    bundle,
                    context ) );
        }
    }

    private void addErrorToTypeReport( TypeReport typeReport, Class<? extends IdentifiableObject> klass,
        IdentifiableObject identifiableObject, Map.Entry<List<String>, List<IdentifiableObject>> pValuesObjectsEntry,
        ObjectBundle bundle, ValidationContext context )
    {
        addObjectReports( toErrorReports( klass, pValuesObjectsEntry ), typeReport, identifiableObject, bundle );
        context.markForRemoval( identifiableObject );
    }

    private List<ErrorReport> toErrorReports( Class<? extends IdentifiableObject> klass,
        Map.Entry<List<String>, List<IdentifiableObject>> entry )
    {
        return Collections.singletonList(
            new ErrorReport(
                klass,
                E5005,
                String.join( ", ", entry.getKey() ),
                entry.getValue().stream()
                    .map( IdentifiableObject::getUid )
                    .collect( Collectors.joining( ", " ) ) ) );
    }
}
