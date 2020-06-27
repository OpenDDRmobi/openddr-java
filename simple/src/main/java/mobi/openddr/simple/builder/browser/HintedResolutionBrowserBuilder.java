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

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.browser.Browser;

public abstract class HintedResolutionBrowserBuilder implements Builder {

    private final String RESOLUTION_HINT_WXH_REGEXP = ".*([0-9][0-9][0-9]+)[*Xx]([0-9][0-9][0-9]+).*";
    private final String RESOLUTION_HINT_FWVGA_REGEXP = ".*FWVGA.*";
    private final String RESOLUTION_HINT_WVGA_REGEXP = ".*WVGA.*";
    private final String RESOLUTION_HINT_WXGA_REGEXP = ".*WXGA.*";
    private final String RESOLUTION_HINT_WQVGA_REGEXP = ".*WQVGA.*";
    private Pattern resolutionHintWxHPattern = Pattern.compile(RESOLUTION_HINT_WXH_REGEXP);
    private Pattern resolutionHintFWVGAPattern = Pattern.compile(RESOLUTION_HINT_FWVGA_REGEXP);
    private Pattern resolutionHintWVGAPattern = Pattern.compile(RESOLUTION_HINT_WVGA_REGEXP);
    private Pattern resolutionHintWXGAPattern = Pattern.compile(RESOLUTION_HINT_WXGA_REGEXP);
    private Pattern resolutionHintWQVGAPattern = Pattern.compile(RESOLUTION_HINT_WQVGA_REGEXP);

    public Browser build(UserAgent userAgent, int confidenceTreshold) {
        int hintedWidth = -1;
        int hintedHeight = -1;

        Matcher result = resolutionHintWxHPattern.matcher(userAgent.getCompleteUserAgent());

        if (result.matches()) {
            try {
                hintedWidth = Integer.parseInt(result.group(1));
                hintedHeight = Integer.parseInt(result.group(2));

            } catch (NumberFormatException x) {
                hintedWidth = -1;
                hintedHeight = -1;
            }

        } else if (userAgent.getCompleteUserAgent().contains("VGA") || userAgent.getCompleteUserAgent().contains("WXGA")) {
            result = resolutionHintFWVGAPattern.matcher(userAgent.getCompleteUserAgent());
            if (result.matches()) {
                hintedWidth = 480;
                hintedHeight = 854;

            } else {
                result = resolutionHintWVGAPattern.matcher(userAgent.getCompleteUserAgent());
                if (result.matches()) {
                    hintedWidth = 480;
                    hintedHeight = 800;

                } else {
                    result = resolutionHintWXGAPattern.matcher(userAgent.getCompleteUserAgent());
                    if (result.matches()) {
                        hintedWidth = 768;
                        hintedHeight = 1280;

                    } else {
                        result = resolutionHintWQVGAPattern.matcher(userAgent.getCompleteUserAgent());
                        if (result.matches()) {
                            hintedWidth = 240;
                            hintedHeight = 400;
                        }
                    }
                }
            }
        }
        return buildBrowser(userAgent, hintedWidth, hintedHeight);
    }

    protected abstract Browser buildBrowser(UserAgent userAgent, int hintedWidth, int hintedHeight);
}
