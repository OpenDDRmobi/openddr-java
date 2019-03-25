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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public abstract class LayoutEngineBrowserBuilder extends HintedResolutionBrowserBuilder {

    static final String APPLEWEBKIT = "AppleWebKit";
    static final String PRESTO = "Presto";
    static final String GECKO = "Gecko";
    static final String TRIDENT = "Trident";
    static final String KHML = "KHTML";
    private final String WEBKIT_VERSION_REGEXP = ".*AppleWebKit/([0-9\\.]+).*?";
    private final String PRESTO_VERSION_REGEXP = ".*Presto/([0-9\\.]+).*?";
    private final String GECKO_VERSION_REGEXP = ".*Gecko/([0-9\\.]+).*?";
    private final String TRIDENT_VERSION_REGEXP = ".*Trident/([0-9\\.]+).*?";
    private final String KHTML_VERSION_REGEXP = ".*KHTML/([0-9\\.]+).*?";
    private Pattern webkitVersionPattern = Pattern.compile(WEBKIT_VERSION_REGEXP);
    private Pattern prestoVersionPattern = Pattern.compile(PRESTO_VERSION_REGEXP);
    private Pattern geckoVersionPattern = Pattern.compile(GECKO_VERSION_REGEXP);
    private Pattern tridentVersionPattern = Pattern.compile(TRIDENT_VERSION_REGEXP);
    private Pattern khtmlVersionPattern = Pattern.compile(KHTML_VERSION_REGEXP);

    @Override
    protected Browser buildBrowser(UserAgent userAgent, int hintedWidth, int hintedHeight) {
        String layoutEngine = null;
        String layoutEngineVersion = null;
        Matcher result = webkitVersionPattern.matcher(userAgent.getCompleteUserAgent());

        if (result.matches()) {
            layoutEngine = APPLEWEBKIT;
            layoutEngineVersion = result.group(1);

        } else {
            result = prestoVersionPattern.matcher(userAgent.getCompleteUserAgent());
            if (result.matches()) {
                layoutEngine = "Presto";
                layoutEngineVersion = result.group(1);

            } else {
                result = geckoVersionPattern.matcher(userAgent.getCompleteUserAgent());
                if (result.matches()) {
                    layoutEngine = "Gecko";
                    layoutEngineVersion = result.group(1);

                } else {
                    result = tridentVersionPattern.matcher(userAgent.getCompleteUserAgent());
                    if (result.matches()) {
                        layoutEngine = "Trident";
                        layoutEngineVersion = result.group(1);

                    } else {
                        result = khtmlVersionPattern.matcher(userAgent.getCompleteUserAgent());
                        if (result.matches()) {
                            layoutEngine = "KHTML";
                            layoutEngineVersion = result.group(1);
                        }
                    }
                }
            }
        }
        return buildBrowser(userAgent, layoutEngine, layoutEngineVersion, hintedWidth, hintedHeight);
    }

    protected abstract Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight);
}
