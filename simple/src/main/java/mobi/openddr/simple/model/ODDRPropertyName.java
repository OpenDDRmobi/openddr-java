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
package mobi.openddr.simple.model;

import org.w3c.ddr.simple.PropertyName;

public class ODDRPropertyName implements PropertyName {

    private String localPropertyName;
    private String namespace;

    public ODDRPropertyName(String localPropertyName, String namespace) {
        this.localPropertyName = localPropertyName;
        this.namespace = namespace;
    }

    public String getLocalPropertyName() {
        return this.localPropertyName;
    }

    public String getNamespace() {
        return this.namespace;
    }
}
