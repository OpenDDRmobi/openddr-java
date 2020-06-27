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

import java.util.ArrayList;
import java.util.List;

import mobi.openddr.simple.builder.device.DeviceBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DeviceBuilderHandler extends DefaultHandler {

    private Object BUILDER_DEVICE = "builder";
    private static final String ELEMENT_DEVICE = "device";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ELEMENT_LIST = "list";
    private static final String ELEMENT_VALUE = "value";
    private static final String ATTRIBUTE_DEVICE_ID = "id";
    private String character = "";
    private DeviceBuilder deviceBuilderInstance;
    private String deviceId = null;
    private List builderProperties = null;
    private boolean inList = false;
    private boolean inValue = false;
    private List<DeviceBuilder> builders;

    public DeviceBuilderHandler() {
        this.builders = new ArrayList<DeviceBuilder>();
    }

    public DeviceBuilderHandler(List builders) {
        this.builders = builders;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (BUILDER_DEVICE.equals(name)) {
            startBuilderElement(attributes);

        } else if (ELEMENT_DEVICE.equals(name)) {
            startDeviceElement(attributes);

        } else if (ELEMENT_PROPERTY.equals(name)) {
            startPropertyElement(attributes);

        } else if (ELEMENT_LIST.equals(name)) {
            startListElement(attributes);

        } else if (ELEMENT_VALUE.equals(name)) {
            startValueElement(attributes);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inList && inValue) {
            String s = new String(ch, start, length);
            character = character + s;
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (BUILDER_DEVICE.equals(name)) {
            endBuilderElement();

        } else if (ELEMENT_DEVICE.equals(name)) {
            endDeviceElement();

        } else if (ELEMENT_PROPERTY.equals(name)) {
            endPropertyElement();

        } else if (ELEMENT_LIST.equals(name)) {
            endListElement();

        } else if (ELEMENT_VALUE.equals(name)) {
            endValueElement();
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    private void startBuilderElement(Attributes attributes) {
        String builderClassName = attributes.getValue("class");
        try {
            Class deviceBuilderClass = Class.forName(builderClassName);
            deviceBuilderInstance = null;
            for (DeviceBuilder deviceBuilder : builders) {
                if (deviceBuilder.getClass().getName().equals(deviceBuilderClass.getName())) {
                    deviceBuilderInstance = deviceBuilder;
                    break;
                }
            }
            if (deviceBuilderInstance == null) {
                deviceBuilderInstance = (DeviceBuilder) deviceBuilderClass.newInstance();
                builders.add(deviceBuilderInstance);
            }

        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("Can not instantiate class: " + builderClassName + " described in device builder document");

        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Can not instantiate class: " + builderClassName + " described in device builder document due to constructor access restriction");

        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Can not find class: " + builderClassName + " described in device builder document");

        }
    }

    private void startDeviceElement(Attributes attributes) {
        deviceId = attributes.getValue(ATTRIBUTE_DEVICE_ID);
        builderProperties = new ArrayList();
    }

    private void startPropertyElement(Attributes attributes) {
    }

    private void startListElement(Attributes attributes) {
        inList = true;
    }

    private void startValueElement(Attributes attributes) {
        inValue = true;
    }

    private void endBuilderElement() {
        deviceBuilderInstance = null;
    }

    private void endDeviceElement() {
        deviceBuilderInstance.putDevice(deviceId, builderProperties);
        deviceId = null;
        builderProperties = null;
    }

    private void endPropertyElement() {
    }

    private void endListElement() {
        inList = false;
    }

    private void endValueElement() {
        builderProperties.add(character);
        character = "";
        inValue = false;
    }

    public DeviceBuilder[] getBuilders() {
        return builders.toArray(new DeviceBuilder[builders.size()]);
    }
}
