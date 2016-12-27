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

public class BlackBerryBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String BLACKBERRY_VERSION_REGEXP = ".*(?:(?:Version)|(?:[Bb]lack.?[Bb]erry.?(?:[0-9a-z]+)))/([0-9\\.]+).*";//"(?:.*?Version.?([0-9\\.]+).*)|(?:.*?[Bb]lack.?[Bb]erry(?:\\d+)/([0-9\\.]+).*)";
    private static final String SAFARI_VERSION_REGEXP = ".*Safari/([0-9\\.]+).*";
    private Pattern blackberryVersionPattern = Pattern.compile(BLACKBERRY_VERSION_REGEXP);
    private Pattern safariVersionPattern = Pattern.compile(SAFARI_VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return userAgent.getCompleteUserAgent().contains("BlackBerry");
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {

        int confidence = 50;
        Browser identified = new Browser();

        identified.setVendor("RIM");
        identified.setModel("BlackBerry");
        identified.setVersion("-");
        identified.setMajorRevision("-");

        Matcher blackberryBrowserMatcher = blackberryVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (blackberryBrowserMatcher.matches()) {
            if (blackberryBrowserMatcher.group(1) != null) {
                String totalVersion = "";
                if (blackberryBrowserMatcher.group(1) != null) {
                    totalVersion = blackberryBrowserMatcher.group(1);
                } else if (blackberryBrowserMatcher.group(2) != null) {
                    totalVersion = blackberryBrowserMatcher.group(2);
                }
                if (totalVersion.length() > 0) {
                    identified.setVersion(totalVersion);
                    String version[] = totalVersion.split("\\.");

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
