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
package mobi.openddr.simple.builder.device;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.UserAgent;

public class IOSDeviceBuilder implements DeviceBuilder {

    private final Map<String, String> iOSDevices;
    private Map<String, Device> devices;

    public IOSDeviceBuilder() {
        iOSDevices = new LinkedHashMap<String, String>();
    }

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsIOSDevices() && (!userAgent.containsAndroid()) && (!userAgent.containsWindowsPhone())) {
            return true;

        } else {
            return false;
        }
    }

    public Device build(UserAgent userAgent, int confidenceTreshold) {
        Iterator<String> it = iOSDevices.keySet().iterator();
        while (it.hasNext()) {
            String token = (String) it.next();
            if (userAgent.getCompleteUserAgent().matches(".*" + token + ".*")) {
                String iosDeviceID = iOSDevices.get(token);
                if (iosDeviceID != null) {
                    Device retDevice = (Device) devices.get(iosDeviceID).clone();
                    retDevice.setConfidence(90);
                    return retDevice;
                }
            }
        }
        return null;
    }

    public void putDevice(String device, List<String> initProperties) {
        iOSDevices.put(initProperties.get(0), device);
    }

    public void completeInit(Map<String, Device> devices) {
        String global = "iPhone";
        if (iOSDevices.containsKey(global)) {
            String iphone = iOSDevices.get(global);
            iOSDevices.remove(global);
            iOSDevices.put(global, iphone);
        }

        this.devices = devices;

        for (String deviceID : iOSDevices.values()) {
            if (!devices.containsKey(deviceID)) {
                throw new IllegalStateException("unable to find device with id: " + deviceID + "in devices");
            }
        }
    }
}
