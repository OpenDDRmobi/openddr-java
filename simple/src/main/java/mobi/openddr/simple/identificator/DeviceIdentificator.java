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
package mobi.openddr.simple.identificator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.openddr.simple.builder.device.DeviceBuilder;
import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.UserAgentFactory;
import org.w3c.ddr.simple.Evidence;

public class DeviceIdentificator implements Identificator {

    private final DeviceBuilder[] builders;
    private final Map<String, Device> devices;

    public DeviceIdentificator(DeviceBuilder[] builders, Map<String, Device> devices) {
        this.builders = builders;
        this.devices = devices;
    }

    public Device get(String userAgent, int confidenceTreshold) {
        return get(UserAgentFactory.newUserAgent(userAgent), confidenceTreshold);
    }

    //XXX to be refined, this should NOT be the main entry point, we should use a set of evidence derivation
    public Device get(Evidence evdnc, int threshold) {
        UserAgent ua = UserAgentFactory.newDeviceUserAgent(evdnc);
        if (ua != null) {
            return get(ua, threshold);
        }
        return null;
    }

    public Device get(UserAgent userAgent, int confidenceTreshold) {
        final List<Device> foundDevices = new ArrayList<Device>();
        Device foundDevice = null;
        for (DeviceBuilder deviceBuilder : builders) {
            if (deviceBuilder.canBuild(userAgent)) {
                Device device = (Device) deviceBuilder.build(userAgent, confidenceTreshold);
                if (device != null) {
                    String parentId = device.getParentId();
                    Device parentDevice = null;
                    Set propertiesSet = null;
                    Iterator it = null;
                    while (!"root".equals(parentId)) {
                        parentDevice = (Device) devices.get(parentId);
                        propertiesSet = parentDevice.getPropertiesMap().entrySet();
                        it = propertiesSet.iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry) it.next();
                            if (!device.containsProperty((String) entry.getKey())) {
                                device.putProperty((String) entry.getKey(), (String) entry.getValue());
                            }
                        }
                        parentId = parentDevice.getParentId();
                    }
                    foundDevices.add(device);
                    if (device.getConfidence() >= confidenceTreshold) {
                        foundDevice = device;
                        break;
                    }
                }
            }
        }

        if (foundDevice != null) {
            return foundDevice;

        } else {
            if (foundDevices.isEmpty()) {
                return null;
            }

            Collections.sort(foundDevices, Collections.reverseOrder());
            return foundDevices.get(0);
        }
    }

    public void completeInit() {
        for (DeviceBuilder deviceBuilder : builders) {
            deviceBuilder.completeInit(devices);
        }
    }
}
