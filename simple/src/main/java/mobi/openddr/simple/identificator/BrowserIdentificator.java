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
package mobi.openddr.simple.identificator;

import java.util.Map;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.UserAgentFactory;
import mobi.openddr.simple.model.browser.Browser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.ddr.simple.Evidence;

/**
 * 
 * @author Werner Keil
 *
 */
public class BrowserIdentificator implements Identificator {

    private Builder[] builders;
    private Map<String, Browser> browserCapabilities;
    protected static final Logger logger = Logger.getLogger(BrowserIdentificator.class.getName());

    public BrowserIdentificator(Builder[] builders, Map<String, Browser> browserCapabilities) {
        this.builders = builders;
        this.browserCapabilities = browserCapabilities;
    }

    public Browser get(String userAgent, int confidenceTreshold) {
        return get(UserAgentFactory.newUserAgent(userAgent), confidenceTreshold);
    }

    //XXX to be refined, this should NOT be the main entry point, we should use a set of evidence derivation
    public Browser get(Evidence evdnc, int threshold) {
        UserAgent ua = UserAgentFactory.newBrowserUserAgent(evdnc);

        if (ua != null) {
            return get(ua, threshold);
        }

        return null;
    }

    public Browser get(UserAgent userAgent, int confidenceTreshold) {
        for (Builder builder : builders) {
            if (builder.canBuild(userAgent)) {
                Browser browser = (Browser) builder.build(userAgent, confidenceTreshold);
                if (browser != null) {
                    if (browserCapabilities != null) {
                        String bestID = getClosestKnownBrowserID(browser.getId());
                        if (bestID != null) {
                            browser.putPropertiesMap(browserCapabilities.get(bestID).getPropertiesMap());
                            if (!bestID.equals(browser.getId())) {
                                browser.setConfidence(browser.getConfidence() - 15);
                            }
                        }
                    }
                    return browser;
                }
            }
        }

        return null;
    }

    private String getClosestKnownBrowserID(String actualBrowserID) {
        if (actualBrowserID == null) {
            return null;
        }

        int idx = actualBrowserID.indexOf(".");

        if (idx < 0) {
            logger.log(Level.WARNING, "SHOULD NOT BE HERE, PLEASE CHECK BROWSER DOCUMENT(1)");
            logger.log(Level.FINE, actualBrowserID);
            return null;

        } else {
            idx++;
        }
        idx = actualBrowserID.indexOf(".", idx);

        if (idx < 0) {
        	logger.log(Level.WARNING, "SHOULD NOT BE HERE, PLEASE CHECK BROWSER DOCUMENT(2)" + idx);
            logger.log(Level.FINE, actualBrowserID);
            return null;

        } else {
            idx++;
        }

        String bestID = null;
        for (String listBrowserID : browserCapabilities.keySet()) {
            if (listBrowserID.equals(actualBrowserID)) {
                return actualBrowserID;
            }

            if (listBrowserID.length() > idx && listBrowserID.substring(0, idx).equals(actualBrowserID.substring(0, idx))) {
                if (listBrowserID.compareTo(actualBrowserID) <= 0) {
                    bestID = listBrowserID;
                }
            }
        }

        return bestID;
    }

    public void completeInit() {
        //does nothing
    }
}
