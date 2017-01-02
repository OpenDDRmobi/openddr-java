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
package mobi.openddr.simple.documenthandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mobi.openddr.simple.DDRService;
import mobi.openddr.simple.VocabularyHolder;
import mobi.openddr.simple.VocabularyService;
import mobi.openddr.simple.model.browser.Browser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BrowserDatasourceHandler extends DefaultHandler {

    private static final String ELEMENT_BROWSER_DESCRIPTION = "browser";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ATTRIBUTE_BROWSER_ID = "id";
    private static final String ATTRIBUTE_PROPERTY_NAME = "name";
    private static final String ATTRIBUTE_PROPERTY_VALUE = "value";
    private String propertyName = null;
    private String propertyValue = null;
    private Browser browser = null;
    private String browserId = null;
    private Map properties = null;
    private Map<String, Browser> browsers = null;
    private VocabularyHolder vocabularyHolder = null;

    public BrowserDatasourceHandler() {
        this.browsers = new TreeMap<String, Browser>(new Comparator<String>() {

            public int compare(String keya, String keyb) {
                return keya.compareTo(keyb);
            }
        });
    }

    public BrowserDatasourceHandler(VocabularyHolder vocabularyHolder) {
        this.browsers = new TreeMap<String, Browser>(new Comparator<String>() {

            public int compare(String keya, String keyb) {
                return keya.compareTo(keyb);
            }
        });
        try {
            vocabularyHolder.existVocabulary(VocabularyService.ODDR_LIMITED_VOCABULARY_IRI);
            this.vocabularyHolder = vocabularyHolder;

        } catch (Exception ex) {
            vocabularyHolder = null;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (ELEMENT_BROWSER_DESCRIPTION.equals(name)) {
            startBrowserDescription(attributes);

        } else if (ELEMENT_PROPERTY.equals(name)) {
            startProperty(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (ELEMENT_BROWSER_DESCRIPTION.equals(name)) {
            endBrowserDescription();

        } else if (ELEMENT_PROPERTY.equals(name)) {
            endProperty();
        }
    }

    private void startBrowserDescription(Attributes attributes) {
        properties = new HashMap();
        browserId = attributes.getValue(ATTRIBUTE_BROWSER_ID);
        browser = new Browser(properties);
    }

    private void startProperty(Attributes attributes) {
        propertyName = attributes.getValue(ATTRIBUTE_PROPERTY_NAME);
        propertyValue = attributes.getValue(ATTRIBUTE_PROPERTY_VALUE);

        if (vocabularyHolder != null) {
            try {
                vocabularyHolder.existProperty(propertyName, DDRService.ASPECT_WEB_BROWSER, VocabularyService.ODDR_LIMITED_VOCABULARY_IRI);
                properties.put(propertyName.intern(), propertyValue);

            } catch (Exception ex) {
                //property non loaded
            }

        } else {
            properties.put(propertyName.intern(), propertyValue);
        }
    }

    private void endProperty() {
    }

    private void endBrowserDescription() {
        browsers.put(browserId, browser);
        properties = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    public Map<String, Browser> getBrowsers() {
        return browsers;
    }
}
