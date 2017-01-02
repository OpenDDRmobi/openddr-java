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
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class SymbianMozillaSubBuilder implements Builder {

    private static final String VERSION_REGEXP = ".*Series.?60/(\\d+)(?:[\\.\\- ](\\d+))?(?:[\\.\\- ](\\d+))?.*";
    private static final String VERSION_EXTRA = ".*Symbian(?:OS)?/(.*)";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern versionExtraPattern = Pattern.compile(VERSION_EXTRA);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsSymbian()) {
            return true;
        }
        return false;
    }

    public OperatingSystem build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();
        model.setMajorRevision("1");
        model.setVendor("Nokia");
        model.setModel("Symbian OS");
        model.setConfidence(40);

        String[] splittedTokens = userAgent.getPatternElementsInside().split(";");
        for (String tokenElement : splittedTokens) {
            Matcher versionMatcher = versionPattern.matcher(tokenElement);
            if (versionMatcher.find()) {
                model.setDescription("Series60");
                if (model.getConfidence() > 40) {
                    model.setConfidence(100);

                } else {
                    model.setConfidence(90);
                }

                if (versionMatcher.group(1) != null) {
                    model.setMajorRevision(versionMatcher.group(1));
                }
                if (versionMatcher.group(2) != null) {
                    model.setMinorRevision(versionMatcher.group(2));
                }
                if (versionMatcher.group(3) != null) {
                    model.setMicroRevision(versionMatcher.group(3));
                }
            }

            Matcher versionExtraMatcher = versionExtraPattern.matcher(tokenElement);
            if (versionExtraMatcher.find()) {
                if (model.getConfidence() > 40) {
                    model.setConfidence(100);

                } else {
                    model.setConfidence(85);
                }

                if (versionExtraMatcher.group(1) != null) {
                    model.setVersion(versionExtraMatcher.group(1).trim());
                }
            }
            //TODO: inference VERSION_EXTRA/VERSION_REGEXP and vice-versa
        }
        return model;
    }
}
