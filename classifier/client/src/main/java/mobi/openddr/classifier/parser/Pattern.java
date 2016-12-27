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
package mobi.openddr.classifier.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Werner Keil
 * @version 1.2
 */
public class Pattern {
    private final List<String> pattern;
    private final String type;
    private final int rank;
    private final int boost;

    Pattern(String pattern, String type, int boost) {
        this(Arrays.asList(pattern), type, boost);
    }

    Pattern(List<String> pattern, String type, int boost) {
        this.pattern = pattern;
        this.type = type;
        this.boost = boost;
        rank = genRank();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');
        sb.append(JsonParser.outputKeyValue("type", type)).append(',');
        sb.append(JsonParser.outputKeyRValue("rank", rank)).append(',');
        sb.append(JsonParser.outputKeyRValue("boost", boost)).append(',');
        sb.append(JsonParser.outputString("pattern")).append(':').append(JsonParser.outputList(pattern, true));
        sb.append('}');

        return sb.toString();
    }

    private int genRank() {
        int r = 0;
        
        if ("weak".equals(type)) {
            r += 1000;
        }

        r += pattern.size() * 100;

        for (String part : pattern) {
            r += part.length();
        }
        return r;
    }
    
    public int getRank() {
        return rank + boost;
    }

    public String getType() {
        return type;
    }

    public List<String> getPatternParts() {
        return pattern;
    }

    boolean isValid(Set<String> patternsToMatch) {
        for (String part : pattern) {
            if (!patternsToMatch.contains(part)) {
                return false;
            }
        }
        return true;
    }

    /*
     * normalizes a pattern
     */
    public static String normalize(String p) {
        if (p == null) {
            return p;
        }

        p = p.toLowerCase().trim();
        p = p.replaceAll("\\[bb\\]", "b");
        final StringBuilder ret = new StringBuilder();

        for (int i = 0; i < p.length(); i++) {
            Character c = p.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                ret.append(c);
            }
        }

        return ret.toString();
    }
}
