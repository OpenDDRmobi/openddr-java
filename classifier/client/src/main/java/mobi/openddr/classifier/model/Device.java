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
package mobi.openddr.classifier.model;

import java.util.Map;

/**
 * @author Werner Keil
 * @version 2.0
 */
public class Device {
    public static final String UNKNOWN_ID = "unknown";

    private final String id;
    private final Map<String, String> properties;

    public Device(String id, Map<String, String> properties) {
    	this.id = id;
    	this.properties = properties;
    }

    @Override
    public String toString() {
		final StringBuilder sb = new StringBuilder();
	
		sb.append('{');
		sb.append(JsonHelper.outputKeyValue("id", id)).append(',');
		sb.append(JsonHelper.outputString("properties")).append(':')
			.append(JsonHelper.outputMap(properties));
		sb.append('}');
	
		return sb.toString();
    }

    public String getId() {
    	return id;
    }

    public String getProperty(String key) {
    	return properties.get(key);
    }

    public Map<String, String> getProperties() {
    	return properties;
    }
    
    /**
     * @deprecated use getProperty()
     */
    public String getAttribute(String key) {
    	return getProperty(key);
    }
    
    /**
     * @deprecated use getProperties()
     */
    public Map<String, String> getAttributes() {
    	return getProperties();
    }
}
