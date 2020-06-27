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
package mobi.openddr.simple.builder.browser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public class NokiaBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String NOKIA_BROWSER_VERSION_REGEXP = ".*(?:(?:BrowserNG)|(?:NokiaBrowser))/([0-9\\.]+).*";
    private static final String SAFARI_VERSION_REGEXP = ".*Safari/([0-9\\.]+).*";
    private Pattern nokiaBrowserVersionPattern = Pattern.compile(NOKIA_BROWSER_VERSION_REGEXP);
    private Pattern safariVersionPattern = Pattern.compile(SAFARI_VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return (userAgent.getCompleteUserAgent().contains("Nokia") || userAgent.getCompleteUserAgent().contains("NokiaBrowser") || userAgent.getCompleteUserAgent().contains("BrowserNG") || userAgent.getCompleteUserAgent().contains("Series60"));
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        if (!(userAgent.hasMozillaPattern() || userAgent.getCompleteUserAgent().contains("SymbianOS") || userAgent.getCompleteUserAgent().contains("Symbian/3") || userAgent.getCompleteUserAgent().contains("Nokia"))) {
            return null;
        }

        int confidence = 50;
        Browser identified = new Browser();

        identified.setVendor("Nokia");
        identified.setModel("Nokia Browser");
        identified.setVersion("-");
        identified.setMajorRevision("-");

        Matcher nokiaBrowserMatcher = nokiaBrowserVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (nokiaBrowserMatcher.matches()) {
            if (nokiaBrowserMatcher.group(1) != null) {
                identified.setVersion(nokiaBrowserMatcher.group(1));
                String version[] = nokiaBrowserMatcher.group(1).split("\\.");

                if (version.length > 0) {
                    identified.setMajorRevision(version[0]);
                    if (identified.getMajorRevision().length() == 0) {
                        identified.setMajorRevision("1");
                    }
                }

                if (version.length > 1) {
                    identified.setMinorRevision(version[1]);
                    confidence += 10;
                }

                if (version.length > 2) {
                    identified.setMicroRevision(version[2]);
                }

                if (version.length > 3) {
                    identified.setNanoRevision(version[3]);
                }
            }

        }

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
            if (layoutEngine.equals(LayoutEngineBrowserBuilder.APPLEWEBKIT)) {
                confidence += 10;
            }
        }

        Matcher safariMatcher = safariVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (safariMatcher.matches()) {
            if (safariMatcher.group(1) != null) {
                identified.setReferenceBrowser("Safari");
                identified.setReferenceBrowserVersion(safariMatcher.group(1));
                confidence += 10;
            }
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
