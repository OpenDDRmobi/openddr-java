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
package mobi.openddr.simple.model.vocabulary;

public class VocabularyProperty {

    private String[] aspects = null;
    private String defaultAspect = null;
    private String expr = null;
    private String name = null;
    private String type = null;

    public String[] getAspects() {
        return aspects;
    }

    public void setAspects(String[] aspects) {
        this.aspects = aspects;
    }

    public String getDefaultAspect() {
        return defaultAspect;
    }

    public void setDefaultAspect(String defaultAspect) {
        this.defaultAspect = defaultAspect;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
