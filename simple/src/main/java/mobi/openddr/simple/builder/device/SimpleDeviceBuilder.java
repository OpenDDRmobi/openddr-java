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
package mobi.openddr.simple.builder.device;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.BuiltObject;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.device.Device;

public class SimpleDeviceBuilder implements DeviceBuilder {

    private LinkedHashMap<String, String> simpleTokenMap;
    private Map<String, Device> devices;

    public SimpleDeviceBuilder() {
        simpleTokenMap = new LinkedHashMap<String, String>();
    }

    public void putDevice(String deviceId, List<String> initProperties) {

        for (String token : initProperties) {
            simpleTokenMap.put(token, deviceId);
        }
    }

    public void completeInit(Map<String, Device> devices) {
        this.devices = devices;

        for (String deviceID : simpleTokenMap.values()) {
            if (!devices.containsKey(deviceID)) {
                throw new IllegalStateException("unable to find device with id: " + deviceID + "in devices");
            }
        }
    }

    public boolean canBuild(UserAgent userAgent) {
        for (String token : simpleTokenMap.keySet()) {
            if (userAgent.getCompleteUserAgent().matches("(?i).*" + Pattern.quote(token) + ".*")) {
                return true;
            }
        }
        return false;
    }

    public BuiltObject build(UserAgent userAgent, int confidenceTreshold) {
        Iterator it = simpleTokenMap.keySet().iterator();
        while (it.hasNext()) {
            String token = (String) it.next();
            if (userAgent.getCompleteUserAgent().matches("(?i).*" + Pattern.quote(token) + ".*")) {
                String desktopDeviceId = simpleTokenMap.get(token);
                if (desktopDeviceId != null) {
                    Device device = devices.get(desktopDeviceId);
                    return device;
                }
            }
        }
        return null;
    }
}
