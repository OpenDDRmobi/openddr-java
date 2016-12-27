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

public class OperaMiniBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String VERSION_REGEXP = ".*?Opera Mini/(?:att/)?v?((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?).*?";
    private static final String BUILD_REGEXP = ".*?Opera Mini/(?:att/)?v?.*?/(.*?);.*";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern buildPattern = Pattern.compile(BUILD_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.getCompleteUserAgent().contains("Opera Mini")) {
            return true;
        }
        return false;
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        Matcher versionMatcher = versionPattern.matcher(userAgent.getCompleteUserAgent());
        if (!versionMatcher.matches()) {
            return null;
        }

        int confidence = 60;
        Browser identified = new Browser();

        identified.setVendor("Opera");
        identified.setModel("Opera Mini");

        if (versionMatcher.group(1) != null) {
            identified.setVersion(versionMatcher.group(1));
        }
        if (versionMatcher.group(2) != null) {
            identified.setMajorRevision(versionMatcher.group(2));
        }
        if (versionMatcher.group(3) != null) {
            identified.setMinorRevision(versionMatcher.group(3));
        }
        if (versionMatcher.group(4) != null) {
            identified.setMicroRevision(versionMatcher.group(4));
        }
        if (versionMatcher.group(5) != null) {
            identified.setNanoRevision(versionMatcher.group(5));
        }

        if (userAgent.hasOperaPattern() && userAgent.getOperaVersion() != null) {
            identified.setReferenceBrowser("Opera");
            identified.setReferenceBrowserVersion(userAgent.getOperaVersion());
            confidence += 20;
        }

        Matcher buildMatcher = buildPattern.matcher(userAgent.getCompleteUserAgent());
        if (buildMatcher.matches()) {
            if (buildMatcher.group(1) != null) {
                identified.setBuild(buildMatcher.group(1));
                confidence += 10;
            }
        }

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
            if (layoutEngine.equals(LayoutEngineBrowserBuilder.PRESTO)) {
                confidence += 10;
            }
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);
        identified.setConfidence(confidence);

        return identified;
    }
}
