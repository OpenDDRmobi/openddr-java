/*
 * Copyright (c) 2011-2021 OpenDDR LLC and others. All rights reserved.
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
package mobi.openddr.simple.model.device;

import mobi.openddr.simple.model.BuiltObject;

public class Device extends BuiltObject implements Comparable, Cloneable {

    private String id;
    private String parentId = "root";

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

    public int compareTo(Object o) {
        if (o == null || !(o instanceof Device)) {
            return Integer.MAX_VALUE;
        }

        Device bd = (Device) o;
        return this.getConfidence() - bd.getConfidence();
    }

    public Object clone() {
        Device d = new Device();
        d.setId(id);
        d.setParentId(parentId);
        d.setConfidence(getConfidence());
        d.putPropertiesMap(getPropertiesMap());
        return d;
    }

    public boolean containsProperty(String propertyName) {
	return
	    properties != null &&
	    properties.containsKey(propertyName);
    }
}
