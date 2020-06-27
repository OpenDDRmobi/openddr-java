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
package mobi.openddr.simple.model;

import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValue;
import org.w3c.ddr.simple.exception.ValueException;

public class ODDRPropertyValue implements PropertyValue {

    private static final String TYPE_BOOLEAN = "xs:boolean";
    private static final String TYPE_DOUBLE = "xs:double";
    private static final String TYPE_ENUMERATION = "xs:enumeration";
    private static final String TYPE_FLOAT = "xs:float";
    private static final String TYPE_INT = "xs:integer";
    private static final String TYPE_NON_NEGATIVE_INTEGER = "xs:nonNegativeInteger";
    private static final String TYPE_LONG = "xs:long";
    private final String value;
    private final String type;
    private final PropertyRef propertyRef;

    public ODDRPropertyValue(String value, String type, PropertyRef propertyRef) {
        this.value = (value == null ? value : value.trim());
        this.type = type;
        this.propertyRef = propertyRef;
    }
    
    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
        .append(propertyRef.getLocalPropertyName())
        .append("=")
        .append(value)
        .toString();
    }

    public double getDouble() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }

        if (type.equals(TYPE_DOUBLE) || type.equals(TYPE_FLOAT)) {
            try {
                return Double.parseDouble(value);

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_DOUBLE + " value");
    }

    public long getLong() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }
        if (type.equals(TYPE_LONG) || type.equals(TYPE_INT) || type.equals(TYPE_NON_NEGATIVE_INTEGER)) {
            try {
                return Long.parseLong(value);

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_LONG + " value");
    }

    public boolean getBoolean() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }
        if (type.equals(TYPE_BOOLEAN)) {
            try {
                return Boolean.parseBoolean(value);

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_BOOLEAN + " value");
    }

    public int getInteger() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }

        if (type.equals(TYPE_INT)) {
            try {
                return Integer.parseInt(value);

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }

        if (type.equals(TYPE_NON_NEGATIVE_INTEGER)) {
            try {
                Integer integer = Integer.parseInt(value);

                if (integer >= 0) {
                    return Integer.parseInt(value);
                }

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_INT + " value");
    }

    public String[] getEnumeration() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }

        if (type.equals(TYPE_ENUMERATION)) {
            try {
                String[] splitted = value.split(",");
                for (int i = 0; i < splitted.length; i++) {
                    splitted[i] = splitted[i].trim();
                }

                return splitted;

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_ENUMERATION + " value");
    }

    public float getFloat() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }

        if (type.equals(TYPE_FLOAT)) {
            try {
                return Float.parseFloat(value);

            } catch (NumberFormatException ex) {
                throw new ValueException(ValueException.INCOMPATIBLE_TYPES, ex);
            }
        }
        throw new ValueException(ValueException.INCOMPATIBLE_TYPES, "Not " + TYPE_FLOAT + " value");
    }

    public PropertyRef getPropertyRef() {
        return propertyRef;
    }

    public String getString() throws ValueException {
        if (!exists()) {
            throw new ValueException(ValueException.NOT_KNOWN, type);
        }
        return value;
    }

    public boolean exists() {
        if (value != null && value.length() > 0 && !"-".equals(value)) {
            return true;
        }
        return false;
    }
}
