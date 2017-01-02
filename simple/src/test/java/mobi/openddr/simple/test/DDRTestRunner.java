/*
 * Copyright (c) 2011-2017 OpenDDR LLC and others. All rights reserved.
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

import java.util.Hashtable;

import org.w3c.ddr.simple.Evidence;

import test.DDRSimpleAPITester;

/**
 * 
 * @author Werner Keil
 *
 */
public class DDRTestRunner {

	public static void main(String[] args) throws Exception {
		final String ASPECT_DEVICE = "device";
		final String nexusUA = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
        final String iri = "http://www.w3.org/2008/01/ddr-core-vocabulary";
		final Evidence uaEvidence = new UserAgentEvidence(nexusUA);
		final DDRSimpleAPITester tester = new DDRSimpleAPITester(DDRTestService.ODDR_SERVICE, uaEvidence, 
				iri, ASPECT_DEVICE, "webBrowser", 
				13, 
				"version", "jpeg", ASPECT_DEVICE, ASPECT_DEVICE, "", "", "",
				1,
				"", "",
				false,
				"", "",
				new String[] {""}
				);
		final Hashtable<?, ?> report = tester.getReport();
		if (report.size() > 0) {
			System.out.println(report.size() + " entries found.");
			for (Object key : report.keySet()) {
				System.out.println("Key: " + key + "; " + "Value: " + report.get(key));
			}
		}
		
	}
}
