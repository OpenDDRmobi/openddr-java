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
package mobi.openddr.classifier.loader;

import mobi.openddr.classifier.loader.impl.DDRLoader;
import mobi.openddr.classifier.loader.impl.DDRLoader2;
import mobi.openddr.classifier.loader.impl.FileResource;
import mobi.openddr.classifier.loader.impl.JarResource;
import mobi.openddr.classifier.loader.impl.NoopLoader;
import mobi.openddr.classifier.loader.impl.URLResource;
import mobi.openddr.classifier.loader.impl.UninitializedLoader;

/**
 * @author Werner Keil
 * @version 1.3
 */
public abstract class LoaderFactory {

    public static Loader getLoader(final LoaderOption option,
	    final String path) {
	switch (option) {
	case JAR: {
	    return new DDRLoader(new JarResource(path));
	}
	case FOLDER: {
	    return new DDRLoader2(new FileResource(path));
	}
	case URL: {
	    return new DDRLoader(new URLResource(path));
	}
	case NOOP: {
	    return new NoopLoader();
	}
	default:
	    return new UninitializedLoader();
	}
    }
}