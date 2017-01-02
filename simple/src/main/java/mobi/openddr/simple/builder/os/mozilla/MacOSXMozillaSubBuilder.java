/*
 * Copyright (c) 2011-2017 OpenDDR LLC and others. All rights reserved.
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
package mobi.openddr.simple.builder.os.mozilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.BuiltObject;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class MacOSXMozillaSubBuilder implements Builder {

    private static final String VERSION_REGEXP = ".*(?:(?:Intel)|(?:PPC)).?Mac OS X.?((\\d+)[_\\.](\\d+)(?:[_\\.](\\d+))?).*";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return userAgent.getCompleteUserAgent().contains("Macintosh");
    }

    public BuiltObject build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();
        model.setMajorRevision("-");
        model.setVendor("Apple");
        model.setModel("Mac OS X");

        int confidence = 60;

        Matcher versionMatcher = versionPattern.matcher(userAgent.getPatternElementsInside());
        if (versionMatcher.find()) {
            model.setConfidence(80);

            if (versionMatcher.group(1) != null) {
                model.setVersion(versionMatcher.group(1));
            }
            if (versionMatcher.group(2) != null) {
                model.setMajorRevision(versionMatcher.group(2));
            }
            if (versionMatcher.group(3) != null) {
                model.setMinorRevision(versionMatcher.group(3));
            }
            if (versionMatcher.group(4) != null) {
                model.setMicroRevision(versionMatcher.group(4));
            }
        }

        model.setConfidence(confidence);

        return model;
    }
}
