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

import mobi.openddr.simple.model.browser.Browser;
import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.os.OperatingSystem;

public class BufferedODDRHTTPEvidence extends ODDRHTTPEvidence {

    private Browser browserFound = null;
    private Device deviceFound = null;
    private OperatingSystem osFound = null;

    public synchronized Browser getBrowserFound() {
        return browserFound;
    }

    public synchronized void setBrowserFound(Browser browserFound) {
        this.browserFound = browserFound;
    }

    public synchronized Device getDeviceFound() {
        return deviceFound;
    }

    public synchronized void setDeviceFound(Device deviceFound) {
        this.deviceFound = deviceFound;
    }

    public synchronized OperatingSystem getOsFound() {
        return osFound;
    }

    public synchronized void setOsFound(OperatingSystem osFound) {
        this.osFound = osFound;
    }

    @Override
    public synchronized void put(String key, String value) {
        setOsFound(null);
        setBrowserFound(null);
        setDeviceFound(null);
        super.put(key, value);
    }
}
