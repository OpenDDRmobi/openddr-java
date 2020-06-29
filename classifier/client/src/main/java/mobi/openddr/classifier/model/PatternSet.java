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
package mobi.openddr.classifier.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Werner Keil
 * @version 1.1
 */
public class PatternSet {
    private final List<Pattern> patterns;

    public PatternSet() {
        patterns = new ArrayList<Pattern>();
    }

    @Override
    public String toString() {
        List<String> orPatterns = new ArrayList<String>();
        for (Pattern pattern : patterns) {
            orPatterns.add(pattern.toString());
        }
        return JsonHelper.outputList(orPatterns, false);
    }

    /*
     * add the patterns together as an inner list (AND)
     */
    public void setAndPattern(List<String> patterns, String type) {
        this.patterns.add(new Pattern(patterns, type, 0));
    }

    /*
     * add each pattern on its own list (OR)
     */
    public void setOrPattern(List<String> patterns, String type) {
        for (String pattern : patterns) {
            setPattern(pattern, type);
        }
    }

    /*
     * add a single pattern (OR)
     */
    public void setPattern(String pattern, String type) {
        patterns.add(new Pattern(pattern, type, 0));
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    /*
     * does a pattern match
     */
    public Pattern isValid(Set<String> patternsToMatch) {
        Pattern winner = null;

        for (Pattern pattern : this.patterns) {
            if (!pattern.isValid(patternsToMatch)) {
                continue;
            }
            if (winner == null || pattern.getRank() > winner.getRank()) {
                winner = pattern;
            }
        }

        return winner;
    }
}
