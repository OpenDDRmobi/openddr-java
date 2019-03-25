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
package mobi.openddr.simple.identificator;

import java.util.Map;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.UserAgentFactory;
import mobi.openddr.simple.model.os.OperatingSystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.ddr.simple.Evidence;

/**
 * 
 * @author Werner Keil
 *
 */
public class OSIdentificator implements Identificator {

    private Builder[] builders;
    private Map<String, OperatingSystem> operatingSystemCapabilities;
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public OSIdentificator(Builder[] builders, Map<String, OperatingSystem> operatingSystemCapabilities) {
        this.builders = builders;
        this.operatingSystemCapabilities = operatingSystemCapabilities;
    }

    public OperatingSystem get(String userAgent, int confidenceTreshold) {
        return get(UserAgentFactory.newUserAgent(userAgent), confidenceTreshold);
    }

    //XXX to be refined, this should NOT be the main entry point, we should use a set of evidence derivation
    public OperatingSystem get(Evidence evdnc, int threshold) {
        UserAgent ua = UserAgentFactory.newDeviceUserAgent(evdnc);
        if (ua != null) {
            return get(ua, threshold);
        }
        return null;
    }

    public OperatingSystem get(UserAgent userAgent, int confidenceTreshold) {
        for (Builder builder : builders) {
            if (builder.canBuild(userAgent)) {
                OperatingSystem os = (OperatingSystem) builder.build(userAgent, confidenceTreshold);
                if (os != null) {
                    if (operatingSystemCapabilities != null) {
                        String bestID = getClosestKnownBrowserID(os.getId());
                        if (bestID != null) {
                            os.putPropertiesMap(operatingSystemCapabilities.get(bestID).getPropertiesMap());
                            if (!bestID.equals(os.getId())) {
                                os.setConfidence(os.getConfidence() - 15);
                            }
                        }
                    }
                    return os;
                }
            }
        }
        return null;
    }

    private String getClosestKnownBrowserID(String actualOperatingSystemID) {
        if (actualOperatingSystemID == null) {
            return null;
        }

        int idx = actualOperatingSystemID.indexOf(".");

        if (idx < 0) {
            logger.log(Level.WARNING, "SHOULD NOT BE HERE, PLEASE CHECK BROWSER DOCUMENT(1)");
            logger.log(Level.FINER, actualOperatingSystemID);
            return null;

        } else {
            idx++;
        }
        idx = actualOperatingSystemID.indexOf(".", idx);

        if (idx < 0) {
            logger.log(Level.WARNING, "SHOULD NOT BE HERE, PLEASE CHECK BROWSER DOCUMENT(2)" + idx);
            logger.log(Level.FINER, actualOperatingSystemID);
            return null;

        } else {
            idx++;
        }

        String bestID = null;
        for (String listOperatingSystemID : operatingSystemCapabilities.keySet()) {
            if (listOperatingSystemID.equals(actualOperatingSystemID)) {
                return actualOperatingSystemID;
            }

            if (listOperatingSystemID.length() > idx && listOperatingSystemID.substring(0, idx).equals(actualOperatingSystemID.substring(0, idx))) {
                if (listOperatingSystemID.compareTo(actualOperatingSystemID) <= 0) {
                    bestID = listOperatingSystemID;
                }
            }
        }

        return bestID;
    }

    public void completeInit() {
        //does nothing
    }
}
