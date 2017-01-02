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
package mobi.openddr.simple.model;

import java.util.HashMap;
import java.util.Map;
import org.w3c.ddr.simple.Evidence;

public class ODDRHTTPEvidence implements Evidence {

    Map<String, String> headers;

    public ODDRHTTPEvidence() {
        headers = new HashMap<String, String>();
    }

    public ODDRHTTPEvidence(Map<String, String> map) {
        headers = new HashMap<String, String>();
        headers.putAll(map);
    }

    public boolean exists(String string) {
        if (string == null) {
            return false;
        }
        return headers.containsKey(string.toLowerCase());
    }

    public String get(String key) {
        return headers.get(key.toLowerCase());
    }

    /**
     *
     * @param key case insensitive
     * @param value case sensitive
     */
    public void put(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }
}
