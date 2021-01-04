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
package mobi.openddr.simple.model;

import static mobi.openddr.simple.model.os.OperatingSystem.ANDROID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgent {
	
    public static final String MOZILLA_AND_OPERA_PATTERN = "(.*?)((?:Mozilla)|(?:Opera))[/ ](\\d+\\.\\d+).*?\\(((?:.*?)(?:.*?\\(.*?\\))*(?:.*?))\\)(.*)";
    public static final int INDEX_MOZILLA_PATTERN_GROUP_PRE = 1;
    public static final int INDEX_MOZILLA_PATTERN_GROUP_INSIDE = 4;
    public static final int INDEX_MOZILLA_PATTERN_GROUP_POST = 5;
    public static final int INDEX_MOZILLA_PATTERN_GROUP_MOZ_VER = 3;
    public static final int INDEX_OPERA_OR_MOZILLA = 2;
    private static Pattern mozillaPatternCompiled =
	Pattern.compile(MOZILLA_AND_OPERA_PATTERN);
    private static Pattern versionPatternCompiled =
	Pattern.compile(".*Version/(\\d+.\\d+).*");
    private String completeUserAgent;
    private boolean mozillaPattern;
    private boolean operaPattern;
    private String mozillaVersion;
    private String operaVersion;
    private boolean containsAndroid;
    private boolean containsBlackBerryOrRim;
    private boolean containsIOSDevices;
    private boolean containsMSIE;
    private boolean containsSymbian;
    private boolean containsWindowsPhone;
    private String[] patternElements;

    UserAgent(String userAgent) {
        if (userAgent == null) {
            throw new IllegalArgumentException("userAgent can not be null");
        }
        completeUserAgent = userAgent;

        Matcher result = mozillaPatternCompiled.matcher(userAgent);

        if (result.matches()) {
            patternElements = new String[]{
                        result.group(INDEX_MOZILLA_PATTERN_GROUP_PRE),
                        result.group(INDEX_MOZILLA_PATTERN_GROUP_INSIDE),
                        result.group(INDEX_MOZILLA_PATTERN_GROUP_POST)
                    };
            String version = result.group(INDEX_MOZILLA_PATTERN_GROUP_MOZ_VER);
            if (result.group(INDEX_OPERA_OR_MOZILLA).contains("Opera")) {
                mozillaPattern = false;
                operaPattern = true;
                operaVersion = version;

                if (operaVersion.equals("9.80") && patternElements[2] != null) {
                    Matcher result2 = versionPatternCompiled.matcher(patternElements[2]);

                    if (result2.matches()) {
                        operaVersion = result2.group(1);
                    }
                }

            } else {
                mozillaPattern = true;
                mozillaVersion = version;
            }

        } else {
            mozillaPattern = false;
            operaPattern = false;
            patternElements = new String[]{
                        null,
                        null,
                        null};
            mozillaVersion = null;
            operaVersion = null;
        }

        if (userAgent.contains(ANDROID)) {
            containsAndroid = true;

        } else {
            containsAndroid = false;
            if (userAgent.matches(".*(?!like).iPad.*") || userAgent.matches(".*(?!like).iPod.*") || userAgent.matches(".*(?!like).iPhone.*")) {
                containsIOSDevices = true;

            } else {
                containsIOSDevices = false;
                if (userAgent.matches(".*[Bb]lack.?[Bb]erry.*|.*RIM.?Tablet.?OS.*")) {
                    containsBlackBerryOrRim = true;

                } else {
                    containsBlackBerryOrRim = false;
                    if (userAgent.matches(".*Symbian.*|.*SymbOS.*|.*Series.?60.*")) {
                        containsSymbian = true;

                    } else {
                        containsSymbian = false;
                        if (userAgent.matches(".*Windows.?(?:(?:CE)|(?:Phone)|(?:NT)|(?:Mobile)).*")) {
                            containsWindowsPhone = true;

                        } else {
                            containsWindowsPhone = false;
                        }

                        if (userAgent.matches(".*MSIE.([0-9\\.b]+).*")) {
                            containsMSIE = true;

                        } else {
                            containsMSIE = false;
                        }
                    }
                }
            }
        }
    }

    public String getCompleteUserAgent() {
        return completeUserAgent;
    }

    public boolean containsAndroid() {
        return containsAndroid;
    }

    public boolean containsBlackBerryOrRim() {
        return containsBlackBerryOrRim;
    }

    public boolean containsIOSDevices() {
        return containsIOSDevices;
    }

    public boolean containsMSIE() {
        return containsMSIE;
    }

    public boolean containsSymbian() {
        return containsSymbian;
    }

    public boolean containsWindowsPhone() {
        return containsWindowsPhone;
    }

    public boolean hasMozillaPattern() {
        return mozillaPattern;
    }

    public boolean hasOperaPattern() {
        return operaPattern;
    }

    public String getPatternElementsPre() {
        return patternElements[0];
    }

    public String getPatternElementsInside() {
        return patternElements[1];
    }

    public String getPatternElementsPost() {
        return patternElements[2];
    }

    public String getMozillaVersion() {
        return mozillaVersion;
    }

    public String getOperaVersion() {
        return operaVersion;
    }
}
