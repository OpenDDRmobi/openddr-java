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
package mobi.openddr.simple.model.os;

import java.util.Map;

import mobi.openddr.simple.model.BuiltObject;

public class OperatingSystem extends BuiltObject implements Comparable {
	public static final String ANDROID = "Android";
	
    private String majorRevision = "0";
    private String minorRevision = "0";
    private String microRevision = "0";
    private String nanoRevision = "0";

    public OperatingSystem() {
        super();
    }

    public OperatingSystem(Map<String, String> properties) {
        super(properties);
    }

    //version id getter and setters
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
    //utility getter for significant oddr OS properties
    public String getModel() {
        return get("model");
    }

    public String getVendor() {
        return get("vendor");
    }

    public String getVersion() {
        return get("version");
    }

    public String getBuild() {
        return get("build");
    }

    public String getDescription() {
        return get("description");
    }

    //SETTERS
    //utility setter for significant oddr OS properties
    public void setModel(String model) {
        putProperty("model", model);
    }

    public void setVendor(String vendor) {
        putProperty("vendor", vendor);
    }

    public void setVersion(String version) {
        putProperty("version", version);
    }

    public void setBuild(String build) {
        putProperty("build", build);
    }

    public void setDescription(String description) {
        putProperty("description", description);
    }

    //Comparable
    public int compareTo(Object o) {
        if (o == null || !(o instanceof OperatingSystem)) {
            return Integer.MAX_VALUE;
        }

        OperatingSystem bd = (OperatingSystem) o;
        return this.getConfidence() - bd.getConfidence();
    }

    // Cloneable
    @Override
    public Object clone() {
        OperatingSystem os = new OperatingSystem();
        os.setMajorRevision(getMajorRevision());
        os.setMinorRevision(getMinorRevision());
        os.setMicroRevision(getMicroRevision());
        os.setNanoRevision(getNanoRevision());
        os.setConfidence(getConfidence());
        os.putPropertiesMap(getPropertiesMap());
        return os;
    }

    //Utility
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVendor());
        sb.append(" ");
        sb.append(getModel());

        if (getDescription() != null && getDescription().length() > 0) {
            sb.append("(");
            sb.append(getDescription());
            sb.append(getVersion()).append(")");
        }

        sb.append(" [").append(getMajorRevision()).append(".").append(getMinorRevision()).append(".").append(getMicroRevision()).append(".").append(getNanoRevision()).append("]");
        if (getBuild() != null) {
            sb.append(" - ").append(getBuild());
        }
        sb.append(" ").append(getConfidence()).append("%");
        return new String(sb);
    }
}
