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
package mobi.openddr.simple.builder.browser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public class ObigoBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String VERSION_REGEXP = ".*?(?:(?:ObigoInternetBrowser/)|(?:Obigo Browser )|(?:[Oo]bigo[- ][Bb]rowser/))([0-9A-Z\\.]+).*?";
    private static final String VERSION_REGEXP2 = ".*?(?:(?:Browser/Obigo)|(?:OBIGO[/_-])|(?:Obigo[-/ ]))([0-9A-Z\\.]+).*?";
    private static final String VERSION_REGEXP3 = ".*?(?:(?:Obigo[Il]nternetBrowser/)|(?:Obigo Browser )|(?:[Oo]bigo[- ][Bb]rowser/))([0-9A-Zacqv\\.]+).*?";
    private static final String VERSION_REGEXP4 = ".*?(?:(?:[Bb]rowser/[Oo]bigo)|(?:OBIGO[/_-])|(?:Obigo[-/ ]))([0-9A-Zacqv\\.]+).*?";
    private static final String VERSION_REGEXP5 = ".*?(?:(?:[Tt]eleca Q))([0-9A-Zacqv\\.]+).*?";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern versionPattern2 = Pattern.compile(VERSION_REGEXP2);
    private Pattern versionPattern3 = Pattern.compile(VERSION_REGEXP3);
    private Pattern versionPattern4 = Pattern.compile(VERSION_REGEXP4);
    private Pattern versionPattern5 = Pattern.compile(VERSION_REGEXP5);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.getCompleteUserAgent().matches("((?i).*obigo.*)|((?i).*teleca.*)")) {
            return true;
        }
        return false;
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        String version = null;

        int confidence = 60;
        Browser identified = new Browser();
        identified.setVendor("Obigo");
        identified.setModel("Obigo Browser");

        Matcher versionMatcher = versionPattern.matcher(userAgent.getCompleteUserAgent());
        if (!versionMatcher.matches()) {
            version = null;

        } else {
            if (versionMatcher.group(1) != null) {
                version = versionMatcher.group(1);
            }
        }

        if (version == null) {
            Matcher versionMatcher2 = versionPattern2.matcher(userAgent.getCompleteUserAgent());
            if (!versionMatcher2.matches()) {
                version = null;

            } else {
                if (versionMatcher2.group(1) != null) {
                    version = versionMatcher2.group(1);
                }
            }
        }

        if (version == null) {
            Matcher versionMatcher3 = versionPattern3.matcher(userAgent.getCompleteUserAgent());
            if (!versionMatcher3.matches()) {
                version = null;

            } else {
                if (versionMatcher3.group(1) != null) {
                    version = versionMatcher3.group(1);
                }
            }
        }

        if (version == null) {
            Matcher versionMatcher4 = versionPattern4.matcher(userAgent.getCompleteUserAgent());
            if (!versionMatcher4.matches()) {
                version = null;

            } else {
                if (versionMatcher4.group(1) != null) {
                    version = versionMatcher4.group(1);
                }
            }
        }

        if (version == null) {
            Matcher versionMatcher5 = versionPattern5.matcher(userAgent.getCompleteUserAgent());
            if (!versionMatcher5.matches()) {
                version = null;

            } else {
                if (versionMatcher5.group(1) != null) {
                    version = versionMatcher5.group(1);
                    identified.setModel("Teleca-Obigo");
                }
            }
        }

        if (version == null) {
            return null;
        }

        identified.setVersion(version);

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
