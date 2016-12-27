/*
 * Copyright (c) 2011-2016 OpenDDR LLC and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mobi.openddr.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValue;
import org.w3c.ddr.simple.PropertyValues;
import org.w3c.ddr.simple.Service;
import org.w3c.ddr.simple.exception.NameException;
import org.w3c.ddr.simple.exception.ValueException;

public class Util {
    
    static void assertProperties(String [] expected, PropertyValues actual, PropertyRef [] refs) throws NameException, ValueException {
        assertNotNull("Expecting non-null PropertyValues", actual);
        for(int i=0; i < refs.length; i++) {
            final PropertyValue pv = actual.getValue(refs[i]);
            assertNotNull("Expecting non-null PropertyValue for " 
                    + refs[i].getLocalPropertyName(), pv);
            if(expected[i] == null) {
                assertFalse("Expecting property to have exist==false: " + refs[i].getLocalPropertyName(), pv.exists());
            } else {
                assertTrue("Expecting property to exist:" + refs[i].getLocalPropertyName(), pv.exists());
                assertEquals("Expecting value " + expected[i] + " for " 
                        + refs[i].getLocalPropertyName(), expected[i], pv.getString());
            }
        }
    }
    
    static void assertProperties(Properties expected, PropertyValues actual, PropertyRef [] refs) throws NameException, ValueException {
        assertNotNull("Expecting non-null PropertyValues", actual);
        assertFalse("Expected properties should not be empty", expected.isEmpty());
        for(Object k : expected.keySet()) {
            final String name = k.toString();
            PropertyRef ref = null;
            for(PropertyRef pr : refs) {
                if(pr.getLocalPropertyName().equals(name)) {
                    ref = pr;
                    break;
                }
            }
            assertNotNull("ref '" + name + "' should be found", ref);
            final PropertyValue pv = actual.getValue(ref);
            assertTrue("Expecting property '" + name + "' to be found for current device", pv.exists());
            final String value = pv.getString();
            final String expectedValue = expected.getProperty(name);
            assertEquals("Expecting value " + expectedValue + " for " 
                    + name, expectedValue, value);
        }
    }
    
    static PropertyRef [] getPropertyRefs(Service s, String ... names) {
        final PropertyRef [] result = new PropertyRef[names.length];
        try {
            for(int i=0; i < names.length; i++) {
                result[i] = s.newPropertyRef(names[i]);
            }
        } catch(Exception e) {
            throw new RuntimeException("Exception while creating PropertyRefs", e);
        }
        return result;
    }
}
