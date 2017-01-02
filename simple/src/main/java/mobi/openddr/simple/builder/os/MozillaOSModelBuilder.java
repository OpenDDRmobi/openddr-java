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
package mobi.openddr.simple.builder.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.builder.os.mozilla.AndroidMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.BadaMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.BlackBerryMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.BrewMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.IOSMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.LinuxMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.MacOSXMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.SymbianMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.WebOSMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.WinCEMozillaSubBuilder;
import mobi.openddr.simple.builder.os.mozilla.WinPhoneMozillaSubBuilder;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.os.OperatingSystem;

public class MozillaOSModelBuilder implements Builder {

    private Builder[] builders = {
        new IOSMozillaSubBuilder(),
        new AndroidMozillaSubBuilder(),
        new WinPhoneMozillaSubBuilder(),
        new BlackBerryMozillaSubBuilder(),
        new SymbianMozillaSubBuilder(),
        new WinCEMozillaSubBuilder(),
        new BadaMozillaSubBuilder(),
        new BrewMozillaSubBuilder(),
        new WebOSMozillaSubBuilder(),
        new LinuxMozillaSubBuilder(),
        new MacOSXMozillaSubBuilder()
    };

    public boolean canBuild(UserAgent userAgent) {
        if (userAgent.hasMozillaPattern()) {
            return true;
        }
        return false;
    }

    public OperatingSystem build(UserAgent userAgent, int confidenceTreshold) {
        List<OperatingSystem> founds = new ArrayList<OperatingSystem>();
        OperatingSystem found = null;
        for (Builder builder : builders) {
            if (builder.canBuild(userAgent)) {
                OperatingSystem builded = (OperatingSystem) builder.build(userAgent, confidenceTreshold);
                if (builded != null) {
                    founds.add(builded);
                    if (builded.getConfidence() >= confidenceTreshold) {
                        found = builded;
                        break;
                    }
                }
            }
        }

        if (found != null) {
            return found;

        } else {
            if (founds.isEmpty()) {
                return null;
            }

            Collections.sort(founds, Collections.reverseOrder());
            return founds.get(0);
        }
    }
}
