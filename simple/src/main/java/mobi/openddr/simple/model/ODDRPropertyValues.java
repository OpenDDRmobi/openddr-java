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

import java.util.ArrayList;
import java.util.List;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValue;
import org.w3c.ddr.simple.PropertyValues;
import org.w3c.ddr.simple.exception.NameException;

public class ODDRPropertyValues implements PropertyValues {

    List<PropertyValue> properties;

    public ODDRPropertyValues() {
        this.properties = new ArrayList<PropertyValue>();
    }

    public void addProperty(PropertyValue v) {
        properties.add(v);
    }

    public PropertyValue[] getAll() {
	if (properties != null)
            return properties.toArray(new PropertyValue[properties.size()]);
	else
            return new PropertyValue[0];
    }

    public PropertyValue getValue(PropertyRef pr) throws NameException {
        for (PropertyValue propertyValue : properties) {
            if (propertyValue.getPropertyRef().equals(pr)) {
                return propertyValue;
            }
        }
        return null;
        //throw new NameException(NameException.PROPERTY_NOT_RECOGNIZED, new IllegalArgumentException());
    }
}
