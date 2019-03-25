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
package mobi.openddr.simple.model.browser;

import java.util.Map;
import mobi.openddr.simple.model.BuiltObject;

public class Browser extends BuiltObject implements Comparable {

    private String majorRevision = "0";
    private String minorRevision = "0";
    private String microRevision = "0";
    private String nanoRevision = "0";

    public Browser() {
        super();
    }

    public Browser(Map<String, String> properties) {
        super(properties);
    }

    public String getMajorRevision() {
        return majorRevision;
    }

    public void setMajorRevision(String majorRevision) {
        this.majorRevision = majorRevision;
    }

    public String getMicroRevision() {
        return microRevision;
    }

    public void setMicroRevision(String microRevision) {
        this.microRevision = microRevision;
    }

    public String getMinorRevision() {
        return minorRevision;
    }

    public void setMinorRevision(String minorRevision) {
        this.minorRevision = minorRevision;
    }

    public String getNanoRevision() {
        return nanoRevision;
    }

    public void setNanoRevision(String nanoRevision) {
        this.nanoRevision = nanoRevision;
    }

    public String getId() {
        if (getModel() == null || getVendor() == null) {
            return null;
        }
        String id = getVendor() + "." + getModel() + "." + getMajorRevision() + "." + getMinorRevision() + "." + getMicroRevision() + "." + getNanoRevision();
        return id;
    }

    //GETTERS
    //utility getter for core ddr properties
    public String getCookieSupport() {
        return get("cookieSupport");
    }

    public int getDisplayHeight() {
        try {
            return Integer.parseInt(get("displayHeight"));

        } catch (Exception x) {
            return -1;
        }
    }

    public int getDisplayWidth() {
        try {
            return Integer.parseInt(get("displayWidth"));

        } catch (Exception x) {
            return -1;
        }
    }

    public String getImageFormatSupport() {
        return get("imageFormatSupport");
    }

    public String getInputModeSupport() {
        return get("inputModeSupport");
    }

    public String getMarkupSupport() {
        return get("markupSupport");
    }

    public String getModel() {
        return get("model");
    }

    public String getScriptSupport() {
        return get("scriptSupport");
    }

    public String getStylesheetSupport() {
        return get("stylesheetSupport");
    }

    public String getVendor() {
        return get("vendor");
    }

    public String getVersion() {
        return get("version");
    }

    //utility getter for significant oddr browser properties
    public String getRenderer() {
        return get("layoutEngine");
    }

    public String getRendererVersion() {
        return get("layoutEngineVersion");
    }

    public String getClaimedReference() {
        return get("referencedBrowser");
    }

    public String getClaimedReferenceVersion() {
        return get("referencedBrowserVersion");
    }

    public String getBuild() {
        return get("build");
    }

    //SETTERS
    //utility setter for core ddr properties
    public void setCookieSupport(String cookieSupport) {
        putProperty("cookieSupport", cookieSupport);
    }

    public void setDisplayHeight(int displayHeight) {
        putProperty("displayHeight", Integer.toString(displayHeight));
    }

    public void setDisplayWidth(int displayWidth) {
        putProperty("displayWidth", Integer.toString(displayWidth));
    }

    public void setImageFormatSupport(String imageFormatSupport) {
        putProperty("imageFormatSupport", imageFormatSupport);
    }

    public void setInputModeSupport(String inputModeSupport) {
        putProperty("inputModeSupport", inputModeSupport);
    }

    public void setMarkupSupport(String markupSupport) {
        putProperty("markupSupport", markupSupport);
    }

    public void setModel(String model) {
        putProperty("model", model);
    }

    public void setScriptSupport(String scriptSupport) {
        putProperty("scriptSupport", scriptSupport);
    }

    public void setStylesheetSupport(String stylesheetSupport) {
        putProperty("stylesheetSupport", stylesheetSupport);
    }

    public void setVendor(String vendor) {
        putProperty("vendor", vendor);
    }

    public void setVersion(String version) {
        putProperty("version", version);
    }

    //utility setter for significant oddr browser properties
    public void setLayoutEngine(String layoutEngine) {
        putProperty("layoutEngine", layoutEngine);
    }

    public void setLayoutEngineVersion(String layoutEngineVersion) {
        putProperty("layoutEngineVersion", layoutEngineVersion);
    }

    public void setReferenceBrowser(String referenceBrowser) {
        putProperty("referenceBrowser", referenceBrowser);
    }

    public void setReferenceBrowserVersion(String referenceBrowserVersion) {
        putProperty("referenceBrowserVersion", referenceBrowserVersion);
    }

    public void setBuild(String build) {
        putProperty("build", build);
    }

    //Comparable
    public int compareTo(Object o) {
        if (o == null || !(o instanceof Browser)) {
            return Integer.MAX_VALUE;
        }

        Browser bd = (Browser) o;
        return this.getConfidence() - bd.getConfidence();
    }

    // Cloneable
    public Object clone() {
        Browser b = new Browser();
        b.setMajorRevision(getMajorRevision());
        b.setMinorRevision(getMinorRevision());
        b.setMicroRevision(getMicroRevision());
        b.setNanoRevision(getNanoRevision());
        b.setConfidence(getConfidence());
        b.putPropertiesMap(getPropertiesMap());
        return b;
    }

    //Utility
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVendor());
        sb.append(" ");
        sb.append(getModel());

        sb.append("(");
        if (getRenderer() != null && getRenderer().length() > 0) {
            sb.append(" ");
            sb.append(getRenderer());
            sb.append(" ");
            sb.append(getRendererVersion());
            sb.append(" ");
        }
        if (getClaimedReference() != null && getClaimedReference().length() > 0) {
            sb.append(" ");
            sb.append(getClaimedReference());
            sb.append(" ");
            sb.append(getClaimedReferenceVersion());
            sb.append(" ");
        }
        sb.append(getVersion()).append(")");

        sb.append(" [").append(getMajorRevision()).append(".").append(getMinorRevision()).append(".").append(getMicroRevision()).append(".").append(getNanoRevision()).append("]");
        if (getBuild() != null) {
            sb.append(" - ").append(getBuild());
        }
        if (getDisplayWidth() > 0 && getDisplayHeight() > 0) {
            sb.append("<");
            sb.append(getDisplayWidth());
            sb.append("x");
            sb.append(getDisplayHeight());
            sb.append(">");
        }
        sb.append(" ").append(getConfidence()).append("%");
        return new String(sb);
    }
}
