/*
 * Copyright (c) 2011-2020 OpenDDR LLC and others. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mobi.openddr.simple.model.device.Device;

public abstract class OrderedTokenDeviceBuilder implements DeviceBuilder {
	protected static final String DEVICE_OS_VERSION = "device_os_version";
	
    protected Map<String, Object> orderedRules;

    public OrderedTokenDeviceBuilder() {
        orderedRules = new LinkedHashMap<String, Object>();
    }

    abstract protected void afterOderingCompleteInit(Map<String, Device> devices);

    public final void completeInit(Map<String, Device> devices) {
        Map<String, Object> tmp = new LinkedHashMap<String, Object>();
        List<String> keys = new ArrayList<String>(orderedRules.keySet());
        Collections.sort(keys, new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        for (String string : keys) {
            tmp.put(string, orderedRules.get(string));
        }
        List<String> keysOrdered = new ArrayList<String>();

        orderedRules = new LinkedHashMap();

        while (keys.size() > 0) {
            boolean found = false;
            for (String k1 : keys) {
                for (String k2 : keys) {
                    if ((!k1.equals(k2)) && k2.matches(".*" + k1 + ".*")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    keysOrdered.add(k1);
                    keys.remove(k1);
                    break;
                }
            }
            if (!found) {
                continue;
            }
            int max = 0;
            int idx = -1;
            for (int i = 0; i < keys.size(); i++) {
                String string = keys.get(i);
                if (string.length() > max) {
                    max = string.length();
                    idx = i;
                }
            }
            if (idx >= 0) {
                keysOrdered.add(keys.get(idx));
                keys.remove(idx);
            }
        }
        for (String key : keysOrdered) {
            orderedRules.put(key, tmp.get(key));
            tmp.remove(key);
        }

        afterOderingCompleteInit(devices);
    }
}
