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
package mobi.openddr.simple.builder.os;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.BuiltObject;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

/**
 * 
 * @author Werner Keil
 * @version 1.0
 *
 */
public class BlackBerryOSBuilder implements Builder {

    private static final String VERSION_REGEXP = "(?:.*?[Bb]lack.?[Bb]erry(?:\\d+)/((\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?).*)";
    private Pattern versionPattern = Pattern.compile(VERSION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.containsBlackBerryOrRim()) {
            return true;
        }
        return false;
    }

    public BuiltObject build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();

        model.setVendor("BlackBerry");
        model.setModel("BlackBerry OS");
        model.setMajorRevision("1");

        Matcher versionMatcher = versionPattern.matcher(userAgent.getCompleteUserAgent());
        if (versionMatcher.find()) {
            if (versionMatcher.group(1) != null) {
                model.setConfidence(50);
                model.setVersion(versionMatcher.group(1));

                if (versionMatcher.group(2) != null) {
                    model.setMajorRevision(versionMatcher.group(2));
                    model.setConfidence(60);
                }

                if (versionMatcher.group(3) != null) {
                    model.setMinorRevision(versionMatcher.group(3));
                    model.setConfidence(70);
                }

                if (versionMatcher.group(4) != null) {
                    model.setMicroRevision(versionMatcher.group(4));
                    model.setConfidence(80);
                }

                if (versionMatcher.group(5) != null) {
                    model.setNanoRevision(versionMatcher.group(5));
                    model.setConfidence(90);
                }
            }
        }
        return model;
    }
}
