/*
 * Copyright (c) 2011-2020 OpenDDR LLC and others. All rights reserved.
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

import static mobi.openddr.simple.test.TestDataService.TEST_DATA_SERVICE;

import org.junit.Test;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValues;

/** Test a simple DDR query */
public class TestSimpleQueries {
    
    public static final PropertyRef [] PROP_REFS = 
            Util.getPropertyRefs(
                    TEST_DATA_SERVICE, 
                    "vendor", "model", "displayWidth", "displayHeight", "playback_3gpp");
    
    @Test
    public void nexusOneIsFound() throws Exception {
        final String [] expected = { "OpenDDR", "Nexus One", "245", "324", "true" };
        final String nexusUA = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
        
        final PropertyValues pvs = TEST_DATA_SERVICE.getPropertyValues(new UserAgentEvidence(nexusUA));
        Util.assertProperties(expected, pvs, PROP_REFS);
    }
    
    @Test
    public void fooIsNotFound() throws Exception {
        // We still get all PropertyValue for a device that's 
        // not found, but they're all non-existing, not ideal IMO,
        // but maybe there's a good reason.
        final String [] expected = new String[PROP_REFS.length];
        
        final PropertyValues pvs = TEST_DATA_SERVICE.getPropertyValues(new UserAgentEvidence("foo"));
        Util.assertProperties(expected, pvs, PROP_REFS);
    }
}