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
package mobi.openddr.simple.builder.os.mozilla;

import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class WinCEMozillaSubBuilder implements Builder {

    private static final String VERSION_REGEXP = ".*Windows.?CE.?((\\d+)?\\.?(\\d+)?\\.?(\\d+)?).*";
    private static final String VERSION_MSIE_IEMOBILE = "(?:.*(?:MSIE).?(\\d+)\\.(\\d+).*)|(?:.*IEMobile.?(\\d+)\\.(\\d+).*)";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);
    private Pattern versionMsiePattern = Pattern.compile(VERSION_MSIE_IEMOBILE);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsWindowsPhone()) {
            if (userAgent.getPatternElementsInside().matches(".*Windows.?CE.*")) {
                return true;
            }
        }
        return false;
    }

    public OperatingSystem build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();
        model.setMajorRevision("1");
        model.setVendor("Microsoft");
        model.setModel("Windows Phone");
        model.setConfidence(40);

        String[] splittedTokens = userAgent.getPatternElementsInside().split(";");
        for (String tokenElement : splittedTokens) {
            Matcher versionMatcher = versionPattern.matcher(tokenElement);
            if (versionMatcher.find()) {
                if (model.getConfidence() > 40) {
                    model.setConfidence(95);

                } else {
                    model.setConfidence(85);
                }

                if (versionMatcher.group(1) != null) {
                    model.setDescription(versionMatcher.group(1));
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

            Matcher versionMsieMatcher = versionMsiePattern.matcher(tokenElement);
            if (versionMsieMatcher.find()) {
                String str = model.getVersion();
                if (str == null || str.length() < 7) {
                    str = "0.0.0.0";
                }
                String[] subV = str.split("\\.");
                int count = 0;
                for (int idx = 1; idx <= versionMsieMatcher.groupCount(); idx++) {
                    if ((idx >= 1) && (idx <= 4) && versionMsieMatcher.group(idx) != null) {
                        subV[idx - 1] = versionMsieMatcher.group(idx);
                        count++;
                    }
                }
                model.setVersion(subV[0] + "." + subV[1] + "." + subV[2] + "." + subV[3]);

                if (model.getConfidence() > 40) {
                    model.setConfidence(95);

                } else {
                    model.setConfidence(count * 18);
                }
            }
        }
        setWinCeVersion(model);
        return model;
    }

    private void setWinCeVersion(OperatingSystem model) {
        //TODO: to be refined
        String osV = model.getVersion();
        if (osV == null) {
            return;

        } else if (!model.getMajorRevision().equals("1")) {
            return;
        }

        if (osV.matches(".*(\\d+).(\\d+).(\\d+).(\\d+).*")) {
            Scanner s = new Scanner(osV);
            s.findInLine(".*(\\d+).(\\d+).(\\d+).(\\d+).*");
            MatchResult result = s.match();
            if (result.group(1).equals("4")) {
                model.setMajorRevision("5");

            } else if (result.group(1).equals("6")) {
                model.setMajorRevision("6");

                if (result.group(3).equals("7")) {
                    model.setMinorRevision("1");
                }
            }
        }
    }
}
