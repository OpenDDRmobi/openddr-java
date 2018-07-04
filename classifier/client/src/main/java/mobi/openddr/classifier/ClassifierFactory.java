/*
 * Copyright (c) 2011-2018 OpenDDR LLC and others. All rights reserved.
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
import mobi.openddr.classifier.loader.LoaderOption;

/**
 * @author Werner Keil
 * @version 1.2
 * @deprecated Use ClassifierBuilder?
 */
public final class ClassifierFactory {
    private static Classifier client = null;
    private static volatile boolean initialized = false;

    private static final LoaderOption DEFAULT = LoaderOption.JAR;

    private ClassifierFactory() {
    }

    public static Classifier getClient() {
	return getClient(DEFAULT, null);
    }

    public static Classifier getClient(LoaderOption option) {
	return getClient(option, null);
    }

    public static Classifier getClient(LoaderOption option, String path) {
	if (!initialized) {
	    synchronized (ClassifierFactory.class) {
		if (!initialized) {
		    client = new Classifier();
		    try {
			client.initDeviceData(option, path);
		    } catch (IOException ex) {
			throw new RuntimeException(ex);
		    }
		    initialized = true;
		}
	    }
	}
	return client;
    }

    public static void resetClient() {
	initialized = false;
    }
}
