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
package mobi.openddr.classifier.parser;

import java.io.IOException;
import java.io.Reader;

public class XMLParser {

    private final Reader in;

    private char pre;

    public XMLParser(Reader in) {
        this.in = in;
        pre = 0;
    }

    public String getNextTag() throws IOException {
        StringBuilder ret = new StringBuilder();

        int i;
        boolean start = false;

        if (pre == '<') {
            ret.append(pre);
            pre = 0;
            start = true;
        }

        while ((i = in.read()) != -1) {
            char c = (char) i;
            if (c == '<') {
                start = true;
                ret.append(c);
            } else if (start) {
                ret.append(c);
            }
            
            if(c == '>') {
                if(ret.lastIndexOf("<!--") == 0 && (ret.lastIndexOf("-->") + 3) != ret.length()) {
                    continue;
                }
                break;
            }
        }
        
        if(ret.lastIndexOf("<!--") == 0) {
            if((ret.lastIndexOf("-->") + 3) == ret.length()) {
                return getNextTag();
            } else {
                return "";
            }
        }

        return ret.toString();
    }

    public String getTagValue() throws IOException {
        StringBuilder ret = new StringBuilder();

        int i;

        while ((i = in.read()) != -1) {
            char c = (char) i;
            if (c == '<') {
                pre = '<';
                break;
            } else {
                ret.append(c);
            }
        }

        return parseEntities(ret.toString().trim());
    }

    public static String getAttribute(String tag, String name) {
        int retpos = tag.toLowerCase().indexOf(name.toLowerCase() + "=");

        if (retpos == -1) {
            return "";
        }

        String ret = tag.substring(retpos + name.length() + 1);

        if (ret.startsWith("\"")) {
            ret = ret.substring(1);
            int endpos = ret.indexOf("\"");

            if (endpos == -1) {
                return "";
            }

            ret = ret.substring(0, endpos);
        } else {
            int endpos = ret.indexOf(" ");

            if (endpos == -1) {
                return "";
            }

            ret = ret.substring(0, endpos);
        }

        return parseEntities(ret);
    }
    
    private static String parseEntities(String s) {
        return s.replace("&quot;", "\"").replace("&amp;", "&")
                .replace("&apos;", "'").replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}
