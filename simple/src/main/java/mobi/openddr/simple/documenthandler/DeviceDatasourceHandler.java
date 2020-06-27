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
package mobi.openddr.simple.documenthandler;

import java.util.HashMap;
import java.util.Map;

import mobi.openddr.simple.DDRService;
import mobi.openddr.simple.VocabularyHolder;
import mobi.openddr.simple.VocabularyService;
import mobi.openddr.simple.model.device.Device;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeviceDatasourceHandler extends DefaultHandler {

    private static final String PROPERTY_ID = "id";
    private static final String ELEMENT_DEVICE = "device";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ATTRIBUTE_DEVICE_ID = "id";
    private static final String ATTRIBUTE_DEVICE_PARENT_ID = "parentId";
    private static final String ATTRIBUTE_PROPERTY_NAME = "name";
    private static final String ATTRIBUTE_PROPERTY_VALUE = "value";
    private String propertyName = null;
    private String propertyValue = null;
    private Device device = null;
    private Map properties = null;
    private Map<String, Device> devices = null;
    private boolean patching = false;
    private VocabularyHolder vocabularyHolder = null;

    public DeviceDatasourceHandler() {
        this.devices = new HashMap<String, Device>();
    }

    public DeviceDatasourceHandler(Map devices) {
        this.devices = devices;
    }

    public DeviceDatasourceHandler(Map devices, VocabularyHolder vocabularyHolder) {
        this.devices = devices;
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
        if (ELEMENT_DEVICE.equals(name)) {
            startDeviceElement(attributes);

        } else if (ELEMENT_PROPERTY.equals(name)) {
            startPropertyElement(attributes);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (ELEMENT_DEVICE.equals(name)) {
            endDeviceElement();

        } else if (ELEMENT_PROPERTY.equals(name)) {
            endPropertyElement();
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    private void startDeviceElement(Attributes attributes) {
        device = new Device();
        device.setId(attributes.getValue(ATTRIBUTE_DEVICE_ID));
        if (attributes.getValue(ATTRIBUTE_DEVICE_PARENT_ID) != null) {
            device.setParentId(attributes.getValue(ATTRIBUTE_DEVICE_PARENT_ID));
        }
        properties = new HashMap();
    }

    private void startPropertyElement(Attributes attributes) {
        propertyName = attributes.getValue(ATTRIBUTE_PROPERTY_NAME);
        propertyValue = attributes.getValue(ATTRIBUTE_PROPERTY_VALUE);

        if (vocabularyHolder != null) {
            try {
                vocabularyHolder.existProperty(propertyName, DDRService.ASPECT_DEVICE, VocabularyService.ODDR_LIMITED_VOCABULARY_IRI);
                properties.put(propertyName.intern(), propertyValue);

            } catch (Exception ex) {
                //property non loaded
            }

        } else {
            properties.put(propertyName.intern(), propertyValue);
        }
    }

    private void endDeviceElement() {
        if (devices.containsKey(device.getId())) {
            if (patching) {
                devices.get(device.getId()).getPropertiesMap().putAll(properties);
                return;

            } else {
                //TODO: WARNING already present
            }
        }
        properties.put(PROPERTY_ID, device.getId());
        device.putPropertiesMap(properties);
        devices.put(device.getId(), device);
        device = null;
        properties = null;
    }

    private void endPropertyElement() {
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public void setPatching(boolean patching) {
        this.patching = patching;
    }
}
