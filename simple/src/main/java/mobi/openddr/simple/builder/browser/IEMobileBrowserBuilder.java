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
package mobi.openddr.simple.builder.browser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public class IEMobileBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String VERSION_REGEXP = ".*[^MS]IEMobile.([0-9\\.]+).*?";
    private static final String MSIE_VERSION_REGEXP = ".*MSIE.([0-9\\.]+).*";
    private static final String MSIEMOBILE_VERSION_REGEXP = ".*MSIEMobile.([0-9\\.]+).*";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern msieVersionPattern = Pattern.compile(MSIE_VERSION_REGEXP);
    private Pattern msieMobileVersionPattern = Pattern.compile(MSIEMOBILE_VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return (userAgent.containsWindowsPhone());
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        if (!userAgent.containsWindowsPhone() || !(userAgent.getCompleteUserAgent().matches(".*Windows.?(?:(?:CE)|(?:Phone)).*"))) {
            return null;
        }

        int confidence = 40;
        Browser identified = new Browser();

        identified.setVendor("Microsoft");
        identified.setModel("IEMobile");

        if (userAgent.getCompleteUserAgent().contains("MSIEMobile")) {
            confidence += 10;
        }

        if (userAgent.hasMozillaPattern()) {
            confidence += 10;
        }

        Matcher versionMatcher = versionPattern.matcher(userAgent.getCompleteUserAgent());
        if (versionMatcher.matches()) {
            if (versionMatcher.group(1) != null) {
                identified.setVersion(versionMatcher.group(1));
                String version[] = versionMatcher.group(1).split("\\.");

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

        } else {
            //fallback version
            identified.setVersion("1.0");
            identified.setMajorRevision("1");
        }

        Matcher msieMatcher = msieVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (msieMatcher.matches()) {
            if (msieMatcher.group(1) != null) {
                identified.setReferenceBrowser("MSIE");
                identified.setReferenceBrowserVersion(msieMatcher.group(1));
                confidence += 10;
            }
        }

        Matcher msieMobileMatcher = msieMobileVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (msieMobileMatcher.matches()) {
            if (msieMobileMatcher.group(1) != null) {
                identified.setLayoutEngine("MSIEMobile");
                identified.setLayoutEngineVersion(msieMobileMatcher.group(1));
                confidence += 10;
            }
        }

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
            if (layoutEngine.equals(LayoutEngineBrowserBuilder.TRIDENT)) {
                confidence += 10;
            }
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
