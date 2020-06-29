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

import java.util.List;
import java.util.Map;

/**
 * A helper class for JSON strings
 * @author Werner Keil
 * @version 1.0
 *
 */
abstract class JsonHelper {

    public static String outputString(String s) {
        if (s == null) {
            return "null";
        }
        return "\"" + s.replace("\"", "\\\"").replace("\n", " ") + "\"";
    }

    public static String outputKeyValue(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(outputString(key));
        sb.append(':');
        sb.append(outputString(value));
        return sb.toString();
    }

    public static String outputKeyRValue(String key, Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append(outputString(key));
        sb.append(':');
        sb.append(value);
        return sb.toString();
    }
    
    public static String outputMap(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (sb.length() == 0) {
                sb.append('{');
            } else {
                sb.append(',');
            }
            sb.append(outputKeyValue(key, value));
        }
        sb.append('}');
        return sb.toString();
    }

    public static String outputList(List<String> list, boolean format) {
        StringBuilder sb = new StringBuilder();
        for (String value : list) {
            if (sb.length() == 0) {
                sb.append('[');
            } else {
                sb.append(',');
            }
            if (format) {
                sb.append(outputString(value));
            } else {
                sb.append(value);
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
