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
package mobi.openddr.classifier.loader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import mobi.openddr.classifier.loader.Resource;

public class FileResource implements Resource {

    private static final String DEFAULT_PATH = "";

    private final String path;

    public FileResource(String path) {
        if (path == null) {
            this.path = DEFAULT_PATH;
        } else if (path.isEmpty()) {
            this.path = "";
        } else {
            this.path = path + File.separatorChar;
        }
    }

    @Override
    public InputStream getResource(String file) throws IOException {
        String rpath = path + file;
        return new FileInputStream(rpath);
    }

}
