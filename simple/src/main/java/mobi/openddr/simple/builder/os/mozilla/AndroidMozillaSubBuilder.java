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
package mobi.openddr.simple.builder.os.mozilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class AndroidMozillaSubBuilder implements Builder {

    private static final String VERSION_REGEXP = ".*?Android.?((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(.*))";
    private static final String BUILD_HASH_REGEXP = ".*Build/(.*)(?:[ \\)])?";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern buildHashPattern = Pattern.compile(BUILD_HASH_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsAndroid()) {
            return true;
        }
        return false;
    }

    public OperatingSystem build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();
        model.setMajorRevision("1");
        model.setVendor("Google");
        model.setModel(OperatingSystem.ANDROID);
        model.setConfidence(40);

        String[] splittedTokens = userAgent.getPatternElementsInside().split(";");
        for (String tokenElement : splittedTokens) {
            Matcher versionMatcher = versionPattern.matcher(tokenElement);
            if (versionMatcher.find()) {
                if (model.getConfidence() > 40) {
                    model.setConfidence(100);

                } else {
                    model.setConfidence(90);
                }

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
                if (versionMatcher.group(5) != null) {
                    model.setNanoRevision(versionMatcher.group(5));
                }
                if (versionMatcher.group(6) != null) {
                    model.setDescription(versionMatcher.group(6));
                }
            }

            Matcher buildHashMatcher = buildHashPattern.matcher(tokenElement);

            if (buildHashMatcher.find()) {
                if (model.getConfidence() > 40) {
                    model.setConfidence(100);

                } else {
                    model.setConfidence(45);
                }

                if (buildHashMatcher.group(1) != null) {
                    model.setBuild(buildHashMatcher.group(1));
                }
            }
        }
        return model;
    }
}
