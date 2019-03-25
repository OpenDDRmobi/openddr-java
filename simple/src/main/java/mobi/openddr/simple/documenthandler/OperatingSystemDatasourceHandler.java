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
package mobi.openddr.simple.documenthandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mobi.openddr.simple.DDRService;
import mobi.openddr.simple.VocabularyHolder;
import mobi.openddr.simple.VocabularyService;
import mobi.openddr.simple.model.os.OperatingSystem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for Operating System aspect
 * @author Werner Keil
 *
 */
public class OperatingSystemDatasourceHandler extends DefaultHandler {

    private static final String ELEMENT_OPERATING_SYSTEM_DESCRIPTION = "operatingSystem";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ATTRIBUTE_BROWSER_ID = "id";
    private static final String ATTRIBUTE_PROPERTY_NAME = "name";
    private static final String ATTRIBUTE_PROPERTY_VALUE = "value";
    private String propertyName = null;
    private String propertyValue = null;
    private OperatingSystem operatingSystem = null;
    private String operatingSystemId = null;
    private Map properties = null;
    private Map<String, OperatingSystem> operatingSystems = null;
    private VocabularyHolder vocabularyHolder = null;

    public OperatingSystemDatasourceHandler() {
        this.operatingSystems = new TreeMap<String, OperatingSystem>(new Comparator<String>() {

            public int compare(String keya, String keyb) {
                return keya.compareTo(keyb);
            }
        });
    }

    public OperatingSystemDatasourceHandler(VocabularyHolder vocabularyHolder) {
        this.operatingSystems = new TreeMap<String, OperatingSystem>(new Comparator<String>() {

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
        if (ELEMENT_OPERATING_SYSTEM_DESCRIPTION.equals(name)) {
            startOperatingSystemDescription(attributes);

        } else if (ELEMENT_PROPERTY.equals(name)) {
            startProperty(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (ELEMENT_OPERATING_SYSTEM_DESCRIPTION.equals(name)) {
            endOperatingSystemDescription();

        } else if (ELEMENT_PROPERTY.equals(name)) {
            endProperty();
        }
    }

    private void startOperatingSystemDescription(Attributes attributes) {
        properties = new HashMap();
        operatingSystemId = attributes.getValue(ATTRIBUTE_BROWSER_ID);
        operatingSystem = new OperatingSystem(properties);
    }

    @SuppressWarnings("unchecked")
	private void startProperty(Attributes attributes) {
        propertyName = attributes.getValue(ATTRIBUTE_PROPERTY_NAME);
        propertyValue = attributes.getValue(ATTRIBUTE_PROPERTY_VALUE);

        if (vocabularyHolder != null) {
            try {
                vocabularyHolder.existProperty(propertyName, DDRService.ASPECT_OPERATING_SYSTEM, VocabularyService.ODDR_LIMITED_VOCABULARY_IRI);
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

    private void endOperatingSystemDescription() {
        operatingSystems.put(operatingSystemId, operatingSystem);
        properties = null;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    public Map<String, OperatingSystem> getOperatingSystems() {
        return operatingSystems;
    }
}
