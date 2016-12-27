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
package mobi.openddr.classifier.loader.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import mobi.openddr.classifier.loader.Loader;
import mobi.openddr.classifier.model.DeviceType;

public class NoopLoader implements Loader {

    @Override
    public Map<String, DeviceType> load() throws IOException {
        return Collections.emptyMap();
    }
}
