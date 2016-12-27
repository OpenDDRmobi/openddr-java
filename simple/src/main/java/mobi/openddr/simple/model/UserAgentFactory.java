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

import java.util.Map;
import org.w3c.ddr.simple.Evidence;

public class UserAgentFactory {

    public static UserAgent newBrowserUserAgent(Evidence evidence) {
        return newUserAgent(evidence.get("user-agent"));
    }

    public static UserAgent newBrowserUserAgent(Map<String, String> headers) {
        return newBrowserUserAgent(new ODDRHTTPEvidence(headers));
    }

    public static UserAgent newDeviceUserAgent(Evidence evidence) {
        String ua = evidence.get("x-device-user-agent");
        if (ua == null || ua.trim().length() < 2) {
            ua = evidence.get("user-agent");
        }
        return newUserAgent(ua);
    }

    public static UserAgent newDeviceUserAgent(Map<String, String> headers) {
        return newDeviceUserAgent(new ODDRHTTPEvidence(headers));
    }

    public static UserAgent newUserAgent(String realUserAgent) {
        return new UserAgent(realUserAgent);
    }
}
