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
package mobi.openddr.simple.builder.os.mozilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.model.BuiltObject;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class LinuxMozillaSubBuilder implements Builder {

    private static final String DESCRIPTION_REGEXP = ".*(X11;)?.*?Linux[^;]?([^;]*)?;.*";
    private Pattern descriptionPattern = Pattern.compile(DESCRIPTION_REGEXP);

    public boolean canBuild(UserAgent userAgent) {
        return userAgent.getCompleteUserAgent().contains("Linux") && !userAgent.getCompleteUserAgent().contains(OperatingSystem.ANDROID);
    }

    public BuiltObject build(UserAgent userAgent, int confidenceTreshold) {
        OperatingSystem model = new OperatingSystem();
        model.setMajorRevision("-");
        model.setVendor("-");
        model.setModel("Linux");

        int confidence = 60;

        Matcher descriptionMatcher = descriptionPattern.matcher(userAgent.getPatternElementsInside());
        if (descriptionMatcher.find()) {
            if (descriptionMatcher.group(1) != null) {
                confidence += 10;
            }
            if (descriptionMatcher.group(2) != null) {
                model.setDescription(descriptionMatcher.group(2));
                confidence += 10;
            }
        }

        model.setConfidence(confidence);

        return model;
    }
}
