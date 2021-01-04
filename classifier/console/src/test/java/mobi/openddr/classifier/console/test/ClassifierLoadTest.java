/*
 * Copyright (c) 2011-2021 OpenDDR LLC and others. All rights reserved.
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
package mobi.openddr.classifier.console.test;

import mobi.openddr.classifier.Classifier;
import mobi.openddr.classifier.loader.LoaderOption;

public class ClassifierLoadTest {
	private static final String DEFAULT_URL = "http://dl.bintray.com/openddr/ddr/1.34/";	    
	private static final int DEFAULT_ITERATIONS = 10;
	
    public static void main(String[] args) throws Exception {
    	int iterations = DEFAULT_ITERATIONS;
    	if (args.length>0) {
    		iterations = Integer.parseInt(args[0]);
    	}
    	for (int i=0; i<iterations; i++) {
    		init();
    		Thread.sleep(100);
    	}
    }
   
    private static synchronized void init() {
        long start = System.nanoTime();

        final Classifier classifier = Classifier.builder().with(LoaderOption.URL, DEFAULT_URL).build();
        long diff = (System.nanoTime() - start) / 1000;
        System.out.println("OpenDDR Classifier loaded " + classifier.getDeviceCount() + " devices and " + classifier.getPatternCount() + " patterns in " + diff + "ms");
    }
}
