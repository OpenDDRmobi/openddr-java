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

public class DolfinBrowserBuilder extends LayoutEngineBrowserBuilder {

    private static final String VERSION_REGEXP = ".*?(?:(?:Dolfin/))([0-9\\.]+).*?";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.getCompleteUserAgent().contains("Dolfin")) {
            return true;
        }
        return false;
    }

    @Override
    protected Browser buildBrowser(UserAgent userAgent, String layoutEngine, String layoutEngineVersion, int hintedWidth, int hintedHeight) {
        String version = null;

        Matcher versionMatcher = versionPattern.matcher(userAgent.getCompleteUserAgent());
        if (!versionMatcher.matches()) {
            return null;

        } else {
            if (versionMatcher.group(1) != null) {
                version = versionMatcher.group(1);
            }
        }

        int confidence = 60;
        Browser identified = new Browser();

        identified.setVendor("Samsung");
        identified.setModel("Dolfin");

        identified.setVersion(version);
        String[] versionEl = version.split("\\.");

        if (versionEl.length > 0) {
            identified.setMajorRevision(versionEl[0]);
        }

        if (versionEl.length > 1) {
            identified.setMinorRevision(versionEl[1]);
            confidence += 10;
        }

        if (versionEl.length > 2) {
            identified.setMicroRevision(versionEl[2]);
        }

        if (versionEl.length > 3) {
            identified.setNanoRevision(versionEl[3]);
        }

        identified.setConfidence(confidence);

        if (layoutEngine != null) {
            identified.setLayoutEngine(layoutEngine);
            identified.setLayoutEngineVersion(layoutEngineVersion);
        }

        identified.setDisplayWidth(hintedWidth);
        identified.setDisplayHeight(hintedHeight);

        return identified;
    }
}
