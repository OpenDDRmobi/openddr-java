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

public class OperaBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String OPERAMINI_VERSION_REGEXP = "Opera Mobi/(.*)";
    private static final String OPERA_VERSION_REGEXP = ".* Opera ([0-9\\.]+).*";
    private Pattern operaMiniVersionPattern = Pattern.compile(OPERAMINI_VERSION_REGEXP);
    private Pattern operaVersionPattern = Pattern.compile(OPERA_VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return ((userAgent.hasOperaPattern() || userAgent.getCompleteUserAgent().matches(".*" + " Opera [0-9\\.]+" + ".*")) && (!userAgent.getCompleteUserAgent().contains("Opera Mini")));
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        if ((!userAgent.hasOperaPattern() || userAgent.getOperaVersion() == null || userAgent.getOperaVersion().length() == 0) && (!userAgent.getCompleteUserAgent().matches(".*" + " Opera [0-9\\.]+" + ".*"))) {
            return null;
        }

        int confidence = 60;
        Browser identified = new Browser();

        identified.setVendor("Opera");
        if (userAgent.getCompleteUserAgent().contains("Mobi")) {
            identified.setModel("Opera Mobile");
            confidence += 10;

        } else if (userAgent.getCompleteUserAgent().contains("Tablet")) {
            identified.setModel("Opera Tablet");

        } else {
            identified.setModel("Opera");
        }

        if (userAgent.getOperaVersion() != null) {
            identified.setVersion(userAgent.getOperaVersion());
        } else {
            Matcher operaMatcher = operaVersionPattern.matcher(userAgent.getCompleteUserAgent());
            if (operaMatcher.matches()) {
                if (operaMatcher.group(1) != null) {
                    identified.setVersion(operaMatcher.group(1));
                }
            }
        }

        String version[] = identified.getVersion().split("\\.");

        if (version.length > 0) {
            identified.setMajorRevision(version[0]);
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

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
            if (layoutEngine.equals(LayoutEngineBrowserBuilder.PRESTO)) {
                confidence += 10;
            }
        }

        if (userAgent.getPatternElementsInside() != null) {
            String inside[] = userAgent.getPatternElementsInside().split(";");
            for (String token : inside) {
                String element = token.trim();
                Matcher miniMatcher = operaMiniVersionPattern.matcher(element);
                if (miniMatcher.matches()) {
                    if (miniMatcher.group(1) != null) {
                        identified.setReferenceBrowser("Opera Mobi");
                        identified.setReferenceBrowserVersion(miniMatcher.group(1));
                        confidence += 10;
                        break;
                    }
                }
            }
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
