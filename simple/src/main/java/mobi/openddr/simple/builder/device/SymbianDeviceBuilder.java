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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.UserAgent;

public class SymbianDeviceBuilder extends OrderedTokenDeviceBuilder {

    private Map<String, Device> devices;

    public SymbianDeviceBuilder() {
        super();
    }

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsSymbian()) {
            return true;
        } else {
            return false;
        }
    }

    public Device build(UserAgent userAgent, int confidenceTreshold) {
        ArrayList<Device> foundDevices = new ArrayList<Device>();
        Iterator<String> it = orderedRules.keySet().iterator();
        while (it.hasNext()) {
            String token = it.next();
            Device d = elaborateSymbianDeviceWithToken(userAgent, token);
            if (d != null) {
                if (d.getConfidence() > confidenceTreshold) {
                    return d;

                } else {
                    if (d.getConfidence() > 0) {
                        foundDevices.add(d);
                    }
                }
            }
        }

        if (foundDevices.size() > 0) {
            Collections.sort(foundDevices, Collections.reverseOrder());
            return foundDevices.get(0);
        }
        return null;
    }

    public void putDevice(String device, List<String> initProperties) {
        orderedRules.put(initProperties.get(0), device);
    }

    private Device elaborateSymbianDeviceWithToken(UserAgent userAgent, String token) {
        String originalToken = token;

        if (userAgent.hasMozillaPattern() || userAgent.hasOperaPattern()) {
            int subtract = 0;
            String currentToken = token;

            String looseToken = token.replaceAll("[ _/-]", ".?");
            Pattern loosePattern = Pattern.compile(".*" + looseToken + ".*");

            if (!loosePattern.matcher(userAgent.getCompleteUserAgent()).matches()) {
                return null;
            }

            Pattern currentPattern = null;

            if (userAgent.hasOperaPattern()) {
                subtract += 10;
            }
            for (int i = 0; i <= 1; i++) {
                if (i == 1) {
                    currentToken = looseToken;
                }

                currentPattern = Pattern.compile(".*Series60.?(\\d+)\\.(\\d+).?" + currentToken + ".*");
                if (userAgent.getPatternElementsInside() != null && currentPattern.matcher(userAgent.getPatternElementsInside()).matches()) {// userAgent.getPatternElementsInside().matches(".*Series60.?(\\d+)\\.(\\d+).?" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(100 - subtract);
                        return retDevice;
                    }
                }

                currentPattern = Pattern.compile(".*" + currentToken);
                if (userAgent.getPatternElementsPre() != null && currentPattern.matcher(userAgent.getPatternElementsPre()).matches()) {//userAgent.getPatternElementsPre().matches(".*" + currentToken)) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(95 - subtract);
                        return retDevice;
                    }
                }

                if (userAgent.getPatternElementsInside() != null && currentPattern.matcher(userAgent.getPatternElementsInside()).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken)) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(90 - subtract);
                        return retDevice;
                    }
                }

                currentPattern = Pattern.compile(".*" + currentToken + ".?;.*");
                if (userAgent.getPatternElementsInside() != null && currentPattern.matcher(userAgent.getPatternElementsInside()).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken + ".?;.*")) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(90 - subtract);
                        return retDevice;
                    }
                }

                if (i == 1) {
                    currentPattern = loosePattern;
                } else {
                    currentPattern = Pattern.compile(".*" + currentToken + ".*");
                }

                if (userAgent.getPatternElementsInside() != null && currentPattern.matcher(userAgent.getPatternElementsInside()).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(80 - subtract);
                        return retDevice;
                    }
                }

                if (userAgent.getPatternElementsPre() != null && currentPattern.matcher(userAgent.getPatternElementsPre()).matches()) {//userAgent.getPatternElementsPre().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(80 - subtract);
                        return retDevice;
                    }
                }

                if (userAgent.getPatternElementsPost() != null && currentPattern.matcher(userAgent.getPatternElementsPost()).matches()) {//userAgent.getPatternElementsPost().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(60 - subtract);
                        return retDevice;
                    }
                }
                subtract += 20;
            }
        } else {
            final String ua = userAgent.getCompleteUserAgent().replaceAll("SN[0-9]*", "");

            int subtract = 0;
            String currentToken = token;

            String looseToken = token.replaceAll("[ _/-]", ".?");
            Pattern loosePattern = Pattern.compile(".*" + looseToken + ".*");

            if (!loosePattern.matcher(userAgent.getCompleteUserAgent()).matches()) {
                return null;
            }

            Pattern currentPattern = null;

            for (int i = 0; i <= 1; i++) {
                if (i == 1) {
                    currentToken = looseToken;
                }

                currentPattern = Pattern.compile(".*" + currentToken + ".*");
                if (currentPattern.matcher(ua).matches()) {
                    String deviceId = (String) orderedRules.get(originalToken);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(100 - subtract);
                        return retDevice;
                    }
                }
                subtract += 20;
            }
        }

        return null;
    }

    @Override
    protected void afterOderingCompleteInit(Map<String, Device> devices) {
        this.devices = devices;
        for (Object devIdObj : orderedRules.values()) {
            String devId = (String) devIdObj;
            if (!devices.containsKey(devId)) {
                throw new IllegalStateException("unable to find device with id: " + devId + "in devices");
            }
        }
    }
}
