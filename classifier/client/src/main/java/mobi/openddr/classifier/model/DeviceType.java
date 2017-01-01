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
package mobi.openddr.classifier.model;

import java.util.Collections;
import java.util.Map;

import mobi.openddr.classifier.parser.JsonParser;
import mobi.openddr.classifier.parser.PatternSet;

/**
 * @author Werner Keil
 * @version 1.1
 */
public class DeviceType {

    private String id;
    private String parentId;
    private final PatternSet pattern;
    private Map<String, String> attributes;
    private boolean locked;

    public DeviceType() {
	pattern = new PatternSet();
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append('{');
	sb.append(JsonParser.outputKeyValue("id", id)).append(',');
	sb.append(JsonParser.outputKeyValue("parentId", parentId)).append(',');
	sb.append(JsonParser.outputString("pattern")).append(':')
		.append(pattern.toString()).append(',');
	sb.append(JsonParser.outputString("attributes")).append(':')
		.append(JsonParser.outputMap(attributes));
	sb.append('}');

	return sb.toString();
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getParentId() {
	return parentId;
    }

    public void setParentId(String parentId) {
	this.parentId = parentId;
    }

    public PatternSet getPatternSet() {
	return pattern;
    }

    public Map<String, String> getAttributes() {
	return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
	this.attributes = attributes;
	this.locked = false;
    }

    public void lockAttributes() {
	attributes = Collections.unmodifiableMap(attributes);
	locked = true;
    }

    public boolean isLocked() {
	return locked;
    }
}
