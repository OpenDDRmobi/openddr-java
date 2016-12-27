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

import static mobi.openddr.simple.test.DDRTestService.ODDR_SERVICE;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValues;

/** Test a simple DDR query */
@Ignore
public class TestDeviceDataQueries {
    
    public static final PropertyRef [] PROP_REFS = 
            Util.getPropertyRefs(
                    ODDR_SERVICE, 
                    "vendor", "model", "displayWidth", "displayHeight");
    
    @Test
    public void SamsungGtIsFound() throws Exception {
        final String [] expected = { "Samsung", "GT-B7610", "480", "800" };
        final String UA = "SAMSUNG-GT-B7610/1.0 Browser/Opera/9.5 Profile/MIDP-2.0 Configuration/CLDC-1.1 UNTRUSTED/1.0";
        
        final PropertyValues pvs = ODDR_SERVICE.getPropertyValues(new UserAgentEvidence(UA));
        Util.assertProperties(expected, pvs, PROP_REFS);
    }
    
    @Test
    public void iPhone5IsFound() throws Exception {
        final String [] expected = { "Apple", "iPhone", "320", "480" };
        final String UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
        
        final PropertyValues pvs = ODDR_SERVICE.getPropertyValues(new UserAgentEvidence(UA));
        Util.assertProperties(expected, pvs, PROP_REFS);
    }
}