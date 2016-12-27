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
package mobi.openddr.simple.builder.device;

import static mobi.openddr.simple.model.os.OperatingSystem.ANDROID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.UserAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Werner Keil
 *
 */
public class AndroidDeviceBuilder extends OrderedTokenDeviceBuilder {

    private static final String BUILD_HASH_REGEXP = ".*Build/([^ \\)\\(]*).*";
    private Pattern buildHashPattern = Pattern.compile(BUILD_HASH_REGEXP);
    private Map<String, Device> devices;
//    private static final String[] FUZZY_TOKEN_REGEXPS = { "SM-T5\\d\\d" };
    protected static final Logger logger = Logger.getLogger(AndroidDeviceBuilder.class.getName());
    
    public AndroidDeviceBuilder() {
        super();
        logger.log(Level.FINE, "Constructor");
    }

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsAndroid()) {
            return true;
        } else {
            return false;
        }
    }

    public Device build(UserAgent userAgent, int confidenceTreshold) {
    	logger.fine("Building");
    	logger.log(Level.FINE, "UA: " + userAgent.getCompleteUserAgent() + " :: A=" + userAgent.containsAndroid());
        final List<Device> foundDevices = new ArrayList<Device>();
        Iterator<String> it = orderedRules.keySet().iterator();
        while (it.hasNext()) {
            String token = it.next();
//            logger.info("T: " + token);
            Device d = elaborateAndroidDeviceWithToken(userAgent, token);
            if (d != null) {
            	logger.log(Level.FINER, "Token: " + token);
                if (d.getConfidence() > confidenceTreshold) {
                    return fixFromUserAgent(d, userAgent);
                } else {
                    if (d.getConfidence() > 0) {
                        foundDevices.add(d);
                    }
                }
            }
        }
        if (foundDevices.size() > 0) {
            Collections.sort(foundDevices, Collections.reverseOrder());
            return fixFromUserAgent(foundDevices.get(0), userAgent);
        } /* else {
            // should work via regex in BuilderDataSource.xml
        	if (confidenceTreshold <= ODDR_DEFAULT_THRESHOLD) {
        	// we only do this if confidence is at or below default threshold (otherwise a "closer" result than this can offer is expected)
	        	it = orderedRules.keySet().iterator(); // need it again for "Fuzzy" patterns
	        	while (it.hasNext()) {
	        		String token = it.next();
	        		Device d = fuzzyAndroidDeviceWithToken(userAgent, token);
	        		if (d != null) {
	        			logger.debug("Got: " + token);
	        			return fixFromUserAgent(d, userAgent);
	        		}
	        	}
        	}
        } */
        return null;
    }

    public void putDevice(String deviceID, List<String> initProperties) {
        orderedRules.put(initProperties.get(0), deviceID);
    }

    private Device elaborateAndroidDeviceWithToken(final UserAgent userAgent, String token) {
        if (userAgent.hasMozillaPattern() || userAgent.hasOperaPattern()) {
            int subtract = 0;
            String currentToken = token;

            String looseToken = token.replaceAll("[ _/-]", ".?");
//logger.debug("Loose Token: " + looseToken);
            Pattern loosePattern = Pattern.compile("(?i).*" + looseToken + ".*");
//logger.debug("Loose Pattern: " + loosePattern);
            if (!loosePattern.matcher(userAgent.getCompleteUserAgent().replaceAll(ANDROID, "")).matches()) {
                return null;
            }

            String patternElementInsideClean = cleanPatternElementInside(userAgent.getPatternElementsInside());
//logger.debug("Pattern: " + patternElementInsideClean);
            Pattern currentPattern = null;

            for (int i = 0; i <= 1; i++) {
                if (i == 1) {
                    currentToken = looseToken;
                }

                currentPattern = Pattern.compile("(?i).*" + currentToken + ".?Build/.*");
//logger.debug("Pattern: " + currentPattern);
                if (patternElementInsideClean != null && currentPattern.matcher(patternElementInsideClean).matches()) {//&& userAgent.getPatternElementsInside().matches(".*" + currentToken + ".?Build/.*")) {
                    String deviceId = (String) orderedRules.get(token);
//logger.debug("DID: " + deviceId);
                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(100 - subtract);
                        return retDevice;
                    }
                }

                currentPattern = Pattern.compile("(?i).*" + currentToken);
                if (userAgent.getPatternElementsPre() != null && currentPattern.matcher(userAgent.getPatternElementsPre()).matches()) {//userAgent.getPatternElementsPre().matches(".*" + currentToken)) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(95 - subtract);
                        return retDevice;
                    }
                }

                if (patternElementInsideClean != null && currentPattern.matcher(patternElementInsideClean).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken)) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(90 - subtract);
                        return retDevice;
                    }
                }

                currentPattern = Pattern.compile("(?i).*" + currentToken + ".?;.*");
                if (patternElementInsideClean != null && currentPattern.matcher(patternElementInsideClean).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken + ".?;.*")) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(90 - subtract);
                        return retDevice;
                    }
                }

                if (i == 1) {
                    currentPattern = loosePattern;
                } else {
                    currentPattern = Pattern.compile("(?i).*" + currentToken + ".*");
                }
                if (patternElementInsideClean != null && currentPattern.matcher(patternElementInsideClean).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(80 - subtract);
                        return retDevice;
                    }
                }
                if (userAgent.getPatternElementsPre() != null && currentPattern.matcher(userAgent.getPatternElementsPre()).matches()) {//userAgent.getPatternElementsPre().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(80 - subtract);
                        return retDevice;
                    }
                }
                if (userAgent.getPatternElementsPost() != null && currentPattern.matcher(userAgent.getPatternElementsPost()).matches()) {//userAgent.getPatternElementsPost().matches(".*" + currentToken + ".*")) {
                    String deviceId = (String) orderedRules.get(token);

                    if (devices.containsKey(deviceId)) {
                        Device retDevice = (Device) devices.get(deviceId).clone();
                        retDevice.setConfidence(60 - subtract);
                        return retDevice;
                    }
                }
                if (i == 1) {
                    if (userAgent.getPatternElementsInside() != null && currentPattern.matcher(userAgent.getPatternElementsInside()).matches()) {//userAgent.getPatternElementsInside().matches(".*" + currentToken + ".*")) {
                        String deviceId = (String) orderedRules.get(token);

                        if (devices.containsKey(deviceId)) {
                            Device retDevice = (Device) devices.get(deviceId).clone();
                            retDevice.setConfidence(40);
                            return retDevice;
                        }
                    }
                }
                subtract += 20;
            }
        }

        return null;
    }
    
    private Device fixFromUserAgent(final Device device, final UserAgent userAgent) {
//    	if (device.containsProperty(DEVICE_OS_VERSION)) {
    		String pattern = userAgent.getPatternElementsInside();
    		String[] parts = pattern.split(";");
    		for (String part : parts) {
    			if (part.trim().startsWith(ANDROID)) {
    				final String versionPart = part.trim().substring(ANDROID.length()).trim();
    				final String versionExisting = device.get(DEVICE_OS_VERSION);
    				if (!versionPart.equals(versionExisting)) {
    					logger.fine("Fixing '" + versionExisting +"' to '" + versionPart + "'" );
    					device.putProperty(DEVICE_OS_VERSION, versionPart);
    				}
    			}
    		}
//    	}
//    	logger.info("Device: " + device.getId() + " - " + device.getPropertiesMap());
    	return device;
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

    private String cleanPatternElementInside(String patternElementsInside) {
        String patternElementInsideClean = patternElementsInside;

        Matcher buildHashMatcher = buildHashPattern.matcher(patternElementInsideClean);

        if (buildHashMatcher.find()) {
            String build = buildHashMatcher.group(1);
	    patternElementInsideClean = patternElementInsideClean.replaceAll("Build/" + Pattern.quote(build), "Build/");

        }
        patternElementInsideClean = patternElementInsideClean.replaceAll(ANDROID, "");

        return patternElementInsideClean;
    }
}
