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
package mobi.openddr.simple.model;

import org.w3c.ddr.simple.PropertyName;
import org.w3c.ddr.simple.PropertyRef;

public class ODDRPropertyRef implements PropertyRef {

    private final PropertyName pn;
    private final String aspectName;

    public ODDRPropertyRef(PropertyName pn, String string) {
        this.pn = pn;
        this.aspectName = string;
    }

    public String getLocalPropertyName() {
        return pn.getLocalPropertyName();
    }

    public String getAspectName() {
        return aspectName;
    }

    public String getNamespace() {
        return pn.getNamespace();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ODDRPropertyRef)) {
            return false;
        }
        ODDRPropertyRef oddr = (ODDRPropertyRef) o;
	return
	    aspectName != null && aspectName.equals(oddr.aspectName) &&
	    getLocalPropertyName() != null && getLocalPropertyName().equals(oddr.getLocalPropertyName()) &&
	    getNamespace() != null && getNamespace().equals(oddr.getNamespace());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.pn != null ? this.pn.hashCode() : 0);
        hash = 73 * hash + (this.aspectName != null ? this.aspectName.hashCode() : 0);
        return hash;
    }
}
