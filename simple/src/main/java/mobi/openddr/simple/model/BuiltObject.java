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
package mobi.openddr.simple.model;

import java.util.HashMap;
import java.util.Map;

public class BuiltObject {

    protected int confidence;
    protected final Map<String, String> properties;

    public BuiltObject() {
        this.properties = new HashMap<String, String>();
        this.confidence = 0;
    }

    public BuiltObject(int confidence, Map<String, String> properties) {
        this.confidence = confidence;
        this.properties = properties;
    }

    public BuiltObject(Map<String, String> properties) {
        this.confidence = 0;
        this.properties = properties;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String get(String property) {
        if (properties.containsKey(property)) {
            return properties.get(property);
        }
        return null;
    }

    public void putProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public void putPropertiesMap(Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    public Map<String, String> getPropertiesMap() {
        return properties;
    }
}
