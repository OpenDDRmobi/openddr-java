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
package mobi.openddr.simple.builder.browser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public class DefaultBrowserBuilder implements Builder {

    private Builder[] builders;
    private static DefaultBrowserBuilder instance;

    public static synchronized DefaultBrowserBuilder getInstance() {
        if (instance == null) {
            instance = new DefaultBrowserBuilder();
        }
        return instance;
    }

    private DefaultBrowserBuilder() {

        builders = new Builder[]{
                    new OperaMiniBrowserBuilder(),
                    new ChromeMobileBrowserBuilder(),
                    new FennecBrowserBuilder(),
                    new SafariMobileBrowserBuilder(), 
                    new SilkBrowserBuilder(),
                    new AndroidMobileBrowserBuilder(),
                    new NetFrontBrowserBuilder(),
                    new UPBrowserBuilder(),
                    new OpenWaveBrowserBuilder(),
                    new SEMCBrowserBuilder(),
                    new DolfinBrowserBuilder(),
                    new JasmineBrowserBuilder(),
                    new PolarisBrowserBuilder(),
                    new ObigoBrowserBuilder(),
                    new OperaBrowserBuilder(),
                    new IEMobileBrowserBuilder(),
                    new NokiaBrowserBuilder(),
                    new BlackBerryBrowserBuilder(),
                    new WebOsBrowserBuilder(),
                    new InternetExplorerBrowserBuilder(),
                    new ChromeBrowserBuilder(),
                    new FirefoxBrowserBuilder(),
                    new SafariBrowserBuilder(),
                    new KonquerorBrowserBuilder()};
    }

    public boolean canBuild(UserAgent userAgent) {
        for (Builder browserBuilder : builders) {
            if (browserBuilder.canBuild(userAgent)) {
                return true;
            }
        }
        return false;
    }

    public Browser build(UserAgent userAgent, int confidenceTreshold) {
        List<Browser> founds = new ArrayList<Browser>();
        Browser found = null;
        for (Builder builder : builders) {
            if (builder.canBuild(userAgent)) {
                Browser builded = (Browser) builder.build(userAgent, confidenceTreshold);
                if (builded != null) {
                    founds.add(builded);
                    if (builded.getConfidence() >= confidenceTreshold) {
                        found = builded;
                        break;
                    }
                }
            }
        }

        if (found != null) {
            return found;

        } else {
            if (founds.isEmpty()) {
                return null;
            }

            Collections.sort(founds, Collections.reverseOrder());
            return founds.get(0);
        }
    }
}
