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
package mobi.openddr.simple.builder.os.mozilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

/**
 * 
 * @author Werner Keil
 * @version 1.0
 *
 */
public class BlackBerryMozillaSubBuilder implements Builder {

    private static final String VERSION_REGEXP = "(?:.*?Version.?((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?).*)|(?:.*?[Bb]lack.?[Bb]erry(?:\\d+)/((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?).*)|(?:.*?RIM.?Tablet.?OS.?((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?).*)";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsBlackBerryOrRim()) {
            return true;
        }
        return false;
    }

    public OperatingSystem build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();

        String rebuilded = userAgent.getPatternElementsInside() + ";" + userAgent.getPatternElementsPost();

        String[] splittedTokens = rebuilded.split(";");
        for (String tokenElement : splittedTokens) {
            Matcher versionMatcher = versionPattern.matcher(tokenElement);
            if (versionMatcher.find()) {
                if (versionMatcher.group(11) != null) {
                    model.setVendor("BlackBerry");
                    model.setModel("BlackBerry OS");
                    model.setMajorRevision("1");
                    model.setConfidence(50);

                    if (versionMatcher.group(11) != null) {
                        model.setVersion(versionMatcher.group(11));
                    }

                    if (versionMatcher.group(12) != null) {
                        model.setMajorRevision(versionMatcher.group(12));
                        model.setConfidence(60);
                    }

                    if (versionMatcher.group(13) != null) {
                        model.setMinorRevision(versionMatcher.group(13));
                        model.setConfidence(70);
                    }

                    if (versionMatcher.group(14) != null) {
                        model.setMicroRevision(versionMatcher.group(14));
                        model.setConfidence(80);
                    }

                    if (versionMatcher.group(15) != null) {
                        model.setNanoRevision(versionMatcher.group(5));
                        model.setConfidence(90);
                    }
                    return model;

                } else if (versionMatcher.group(1) != null || versionMatcher.group(6) != null) {
                    model.setVendor("BlackBerry");
                    model.setModel("BlackBerry OS");
                    model.setMajorRevision("1");
                    model.setConfidence(40);

                    if (versionMatcher.group(1) != null) {
                        if (versionMatcher.group(6) != null) {
                            model.setConfidence(100);
                        } else {
                            model.setConfidence(80);
                        }

                    } else if (versionMatcher.group(6) != null) {
                        model.setConfidence(90);
                    }

                    if (versionMatcher.group(1) != null) {
                        model.setVersion(versionMatcher.group(1));

                    } else if (versionMatcher.group(6) != null) {
                        model.setVersion(versionMatcher.group(6));
                    }

                    if (versionMatcher.group(2) != null) {
                        model.setMajorRevision(versionMatcher.group(2));

                    } else if (versionMatcher.group(7) != null) {
                        model.setMajorRevision(versionMatcher.group(7));
                    }

                    if (versionMatcher.group(3) != null) {
                        model.setMinorRevision(versionMatcher.group(3));
                    } else if (versionMatcher.group(8) != null) {
                        model.setMinorRevision(versionMatcher.group(8));
                    }

                    if (versionMatcher.group(4) != null) {
                        model.setMicroRevision(versionMatcher.group(4));
                    } else if (versionMatcher.group(9) != null) {
                        model.setMicroRevision(versionMatcher.group(9));
                    }

                    if (versionMatcher.group(5) != null) {
                        model.setNanoRevision(versionMatcher.group(5));
                    } else if (versionMatcher.group(10) != null) {
                        model.setNanoRevision(versionMatcher.group(10));
                    }
                    return model;
                }
            }
        }
        return model;
    }
}
