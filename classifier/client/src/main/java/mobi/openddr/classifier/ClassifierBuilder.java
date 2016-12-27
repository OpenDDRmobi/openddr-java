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
package mobi.openddr.classifier;

import java.io.IOException;

import org.apache.commons.lang3.builder.Builder;

import mobi.openddr.classifier.loader.LoaderOption;

/**
 * @author Werner Keil
 * @version 1.0
 */
public final class ClassifierBuilder implements Builder<Classifier>{
    // TODO could we change this to a ClassifierBuilder?
    private Classifier client = null;
//    private static volatile boolean initialized = false;

    private LoaderOption option = LoaderOption.JAR;

    public Classifier build() {
//	return build(DEFAULT, null);
	return client;
    }

    public ClassifierBuilder with(LoaderOption option) {
	return with(option, null);
    }

    public ClassifierBuilder with(LoaderOption option, String path) {
	this.option = option;
//	if (!initialized) {
	    synchronized (ClassifierBuilder.class) {
//		if (!initialized) {
		    client = new Classifier();
		    try {
			client.initDeviceData(this.option, path);
		    } catch (IOException ex) {
			throw new RuntimeException(ex);
		    }
//		    initialized = true;
//		}
	    }
//	}
//	return client;
	return this;
    }
/*
    public static void resetClient() {
	initialized = false;
    }*/
}
