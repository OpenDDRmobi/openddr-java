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
package mobi.openddr.classifier.loader.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import mobi.openddr.classifier.loader.Resource;

public class JarResource implements Resource {

    private static final String DEFAULT_PATH = "/devicedata/";

    private final String path;

    public JarResource(String path) {
        if (path == null) {
            this.path = DEFAULT_PATH;
        } else {
            this.path = path;
        }
    }

    @Override
    public InputStream getResource(String file) throws IOException {
        String rpath = path + file;

        InputStream in = JarResource.class.getResourceAsStream(rpath);

        if (in == null) {
            throw new FileNotFoundException("Jar resource not found: " + path);
        }

        return in;
    }
}
