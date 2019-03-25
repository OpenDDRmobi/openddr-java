/*
 * Copyright (c) 2011-2019 OpenDDR LLC and others. All rights reserved.
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
import static org.junit.Assert.assertNotNull;
import static mobi.openddr.simple.test.DDRTestService.ODDR_SERVICE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValues;

/** Run tests defined by the test-data module */
public class TestDataFileEntries {
    
    public static final String TEST_FILE_PROP = "oddr.test.data.file";
    public static final String DMAP_EXPECT = "DMAP_EXPECT";
    
    // TODO this should contain all property names that are
    // present in the test file
    public static final PropertyRef [] PROP_REFS = 
            Util.getPropertyRefs(
                    ODDR_SERVICE, 
                    "vendor", "model", "displayWidth", "displayHeight");
    
    static private class TestEntry {
        String userAgent;
        Properties expected;
    };
    
    private final File getDataFile() throws IOException {
        final String filename = System.getProperty(TEST_FILE_PROP);
        assertNotNull("Expecting non-null system property " + TEST_FILE_PROP, filename);
        final File f = new File(filename);
        if(!f.canRead()) {
            throw new IOException("File not found:" 
                    + f.getAbsolutePath() 
                    + " (defined by system property " + TEST_FILE_PROP + ")");
        }
        return f;
    }

    private TestEntry parseLine(String line) {
        line = line.trim();
        if(line.startsWith("#") || line.length() == 0) {
            return null;
        }
        
        final TestEntry result = new TestEntry();
        // Parse test line which is in a format like
        // UA DMAP_EXPECT vendor:foo model:bar
        // where UA is the User-Agent
        // DMAP_EXPECT is a constant separator
        // and the test is name/value pairs of properties
        final String [] mainParts = line.split(DMAP_EXPECT);
        assertEquals("Expecting " + DMAP_EXPECT + " separator", 2, mainParts.length);
        result.userAgent = mainParts[0].trim();
        result.expected = new Properties();
        final String [] kvParts = mainParts[1].split(" ");
        for(String part : kvParts) {
            part = part.trim();
            if(part.length() == 0) {
                continue;
            }
            String [] kvPair = part.split(":");
            assertEquals("Expecting 2 parts in kvPair '" + part + "'", 2, kvPair.length);
            result.expected.put(kvPair[0].trim(), kvPair[1].trim());
        }
        
        return result;
    }
    
    @Test
    @Ignore
    public void testDataFileEntries() throws Exception {
        String line = null;
        final BufferedReader r = new BufferedReader(new FileReader(getDataFile()));
        try {
            while( (line = r.readLine()) != null) {
                final TestEntry te = parseLine(line);
                if(te == null) {
                    continue;
                }
                final PropertyValues pvs = ODDR_SERVICE.getPropertyValues(new UserAgentEvidence(te.userAgent));
                Util.assertProperties(te.expected, pvs, PROP_REFS);
            }
        } finally {
            r.close();
        }
   }
}