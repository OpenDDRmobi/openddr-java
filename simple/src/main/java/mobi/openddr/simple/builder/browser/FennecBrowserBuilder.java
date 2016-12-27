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

public class FennecBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String FENNEC_VERSION_REGEXP = ".*Fennec/([0-9a-z\\.\\-]+)";
    private static final String FIREFOX_VERSION_REGEXP = ".*Firefox.([0-9a-z\\.b]+).*";
    private Pattern fennecVersionPattern = Pattern.compile(FENNEC_VERSION_REGEXP);
    private Pattern firefoxVersionPattern = Pattern.compile(FIREFOX_VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return (userAgent.getCompleteUserAgent().contains("Fennec") || (userAgent.getCompleteUserAgent().contains("Firefox") && userAgent.getCompleteUserAgent().contains("Mobile")));
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        if (!(userAgent.hasMozillaPattern())) {
            return null;
        }

        int confidence = 60;
        Browser identified = new Browser();

        identified.setVendor("Mozilla");
        identified.setModel("Firefox Mobile");
        identified.setVersion("-");
        identified.setMajorRevision("-");

        Matcher fennecMatcher = fennecVersionPattern.matcher(userAgent.getCompleteUserAgent());
        Matcher firefoxMatcher = firefoxVersionPattern.matcher(userAgent.getCompleteUserAgent());
        if (fennecMatcher.matches()) {
            if (fennecMatcher.group(1) != null) {
                identified.setVersion(fennecMatcher.group(1));
                String version[] = fennecMatcher.group(1).split("\\.");

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

        } else if (firefoxMatcher.matches()) {
            if (firefoxMatcher.group(1) != null) {
                identified.setVersion(firefoxMatcher.group(1));
                String version[] = firefoxMatcher.group(1).split("\\.");

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

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
            if (layoutEngine.equals(LayoutEngineBrowserBuilder.GECKO)) {
                confidence += 10;
            }
        }

        if (firefoxMatcher.matches()) {
            if (firefoxMatcher.group(1) != null) {
                identified.setReferenceBrowser("Firefox");
                identified.setReferenceBrowserVersion(firefoxMatcher.group(1));
                confidence += 10;
            }
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
