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
package mobi.openddr.simple.test;

import org.w3c.ddr.simple.Evidence;

/** Simple Evidence implementation, we use just User-Agent for now */ 
public class UserAgentEvidence implements Evidence {
    public static final String USER_AGENT = "User-Agent";
    private final String userAgent;
    
    public UserAgentEvidence(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean exists(String key) {
        return USER_AGENT.equalsIgnoreCase(key);
    }

    public String get(String key) {
        if(USER_AGENT.equalsIgnoreCase(key)) {
            return userAgent;
        }
        return null;
    }

    public void put(String key, String value) {
        throw new UnsupportedOperationException("This Evidence is immutable");
    }
}
