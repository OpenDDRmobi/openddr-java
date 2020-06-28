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
package mobi.openddr.classifier.loader.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import mobi.openddr.classifier.loader.Loader;
import mobi.openddr.classifier.loader.Resource;
import mobi.openddr.classifier.model.DeviceType;
import mobi.openddr.classifier.parser.Pattern;
import mobi.openddr.classifier.parser.XMLParser;

/**
 * 
 * @author Werner Keil
 *
 */
public class DDRLoader2 implements Loader {

	private final static Logger LOG = Logger.getLogger(DDRLoader2.class.getName());

	private static final String DEVICE_DATA = "DeviceDataSource.xml";
	private static final String DEVICE_DATA_PATCH = "DeviceDataSourcePatch.xml";
	private static final String BUILDER_DATA = "BuilderDataSource.xml";
	private static final String BUILDER_DATA_PATCH = "BuilderDataSourcePatch.xml";

	private final Map<String, DeviceType> devices;
	private final Resource resourceLoader;

	public DDRLoader2(Resource resourceLoader) {
		devices = new HashMap<>(5000);
		this.resourceLoader = resourceLoader;
	}

	@Override
	public Map<String, DeviceType> load() throws IOException {
		long start = System.currentTimeMillis();

		BufferedReader ddin = new BufferedReader(
				new InputStreamReader(resourceLoader.getResource(DEVICE_DATA), "UTF-8"));
		long diff = System.currentTimeMillis() - start;
		try {
			loadDeviceData(ddin);
			ddin.close();

			LOG.log(Level.FINE, "Loaded " + DEVICE_DATA + " in {0}ms", diff);
		} catch (XMLStreamException xe) {
			LOG.log(Level.SEVERE, "Error", xe);
		}
		try {
			start = System.currentTimeMillis();

			final BufferedReader ddpin = new BufferedReader(
					new InputStreamReader(resourceLoader.getResource(DEVICE_DATA_PATCH), "UTF-8"));
			loadDeviceData(ddpin);
			ddpin.close();

			diff = System.currentTimeMillis() - start;
			LOG.log(Level.FINE, "Loaded " + DEVICE_DATA_PATCH + " in {0}ms", diff);
		} catch (FileNotFoundException | XMLStreamException ex) {
			LOG.log(Level.WARNING, "File not found " + DEVICE_DATA_PATCH + ": {0}", ex.toString());
		}

		setParentAttributes();
		start = System.currentTimeMillis();

		BufferedReader bin = new BufferedReader(
				new InputStreamReader(resourceLoader.getResource(BUILDER_DATA), "UTF-8"));
		loadDevicePatterns(bin);
		bin.close();

		diff = System.currentTimeMillis() - start;
		LOG.log(Level.FINE, "Loaded " + BUILDER_DATA + " in {0}ms", diff);

		try {
			start = System.currentTimeMillis();

			final BufferedReader bpin = new BufferedReader(
					new InputStreamReader(resourceLoader.getResource(BUILDER_DATA_PATCH), "UTF-8"));
			loadDevicePatterns(bpin);
			bpin.close();

			diff = System.currentTimeMillis() - start;
			LOG.log(Level.FINE, "Loaded " + BUILDER_DATA_PATCH + " in {0}ms", diff);
		} catch (FileNotFoundException ex) {
			LOG.log(Level.WARNING, "File not found " + BUILDER_DATA_PATCH + ": {0}", ex.toString());
		}

		return getDevices();
	}

	/*
	 * loads device data from an InputStreamReader
	 */
	private void loadDeviceData(Reader in) throws XMLStreamException, IOException {
		String tag;
		DeviceType device = new DeviceType();
		Map<String, String> attributes = new HashMap<>();
		final QName ID = new QName("id");
		final QName PARENT_ID = new QName("parentId");
		final QName NAME = new QName("name");
		final QName VALUE = new QName("value");
		
		XMLInputFactory xmlInFact = XMLInputFactory.newInstance();
	    //XMLStreamReader reader = xmlInFact.createXMLStreamReader(in);
		XMLEventReader reader = xmlInFact.createXMLEventReader(in);
	    while(reader.hasNext()) {

	           //reader.next(); // do something here
	           XMLEvent event = reader.nextEvent();
	           
	           tag = event.toString();
	           LOG.log(Level.FINEST, "Tag: {}", tag);
	           //System.out.println(tag);
//	           if (e.isCharacters()) {
//	        	   tag = e.asCharacters().getData();
//	           } else {
//	        	   tag = "";
//	           }


		//while (!(tag = parser.getNextTag()).isEmpty()) {
			// new device found
			if (tag.startsWith("<device ")) {
				//device.setId(e. XMLParser.getAttribute(tag, "id"));
				final StartElement startElement = event.asStartElement();
                
				device.setId(startElement.getAttributeByName(ID).getValue());
				device.setParentId(startElement.getAttributeByName(PARENT_ID).getValue());
				//device.setParentId(XMLParser.getAttribute(tag, "parentId"));
			} else if (tag.equals("</device>")) {

				// add the device
				if (device.getId() != null && !device.getId().isEmpty()) {
					attributes.put("id", device.getId());
					device.setAttributes(attributes);
					devices.put(device.getId(), device);
				}

				// reset the device
				device = new DeviceType();
				attributes = new HashMap<>();
			} else if (tag.startsWith("<property ")) {
				// add the property to the device
				final StartElement startElement = event.asStartElement();
				if (null != startElement) {
					String key = startElement.getAttributeByName(NAME).getValue();
					
					String value = ""; 
					if (startElement.getAttributeByName(VALUE) != null) {
						value = startElement.getAttributeByName(VALUE).getValue(); 					
					}
					//String value = "";
					
					if ("types".equals(key)) {
						System.out.println(value);
					}
					
					attributes.put(key, value);
				}
			}
		}
	}

	/*
	 * loads patterns from an InputStreamReader
	 */
	private void loadDevicePatterns(Reader in) throws IOException {
		XMLParser parser = new XMLParser(in);
		String tag;
		String builder = "";
		String type = "";
		DeviceType device = null;
		String id = "";
		List<String> patterns = new ArrayList<>();

		while (!(tag = parser.getNextTag()).isEmpty()) {
			// new builder found
			if (tag.startsWith("<builder ")) {
				builder = XMLParser.getAttribute(tag, "class");

				if (builder.lastIndexOf(".") >= 0) {
					builder = builder.substring(builder.lastIndexOf(".") + 1);
				}

				type = "weak";
				if (builder.equals("SimpleDeviceBuilder")) {
					type = "simple";
				}
			} else if (tag.startsWith("<device ")) {
				// new device found
				id = XMLParser.getAttribute(tag, "id");
				device = devices.get(id);
			} else if (tag.equals("</device>")) {
				// add the device
				if (device != null) {
					// TwoStep is an AND pattern, also index the unigram
					if (builder.equals("TwoStepDeviceBuilder")) {
						device.getPatternSet().setAndPattern(patterns, type);

						String unigram = "";

						for (String pattern : patterns) {
							if (pattern.contains(unigram)) {
								unigram = pattern;
							} else {
								unigram += pattern;
							}
						}

						device.getPatternSet().setPattern(unigram, type);
					} else {
						device.getPatternSet().setOrPattern(patterns, type);
					}
				} else {
					LOG.log(Level.FINE, "ERROR: device not found: ''{0}''", id);
					// TODO should we use WARN here?
				}

				// reset the device
				device = null;
				id = "";
				patterns = new ArrayList<String>();
			} else if (tag.equals("<value>")) {
				// add the pattern to the device
				String pattern = Pattern.normalize(parser.getTagValue());

				if (pattern.isEmpty()) {
					continue;
				}

				patterns.add(pattern);
			}
		}
	}

	/**
	 * Sets attributes from parents
	 */
	private void setParentAttributes() {
		for (DeviceType device : devices.values()) {
			mergeParent(device);
			device.lockAttributes();
		}
	}

	private void mergeParent(DeviceType device) {
		String parentId = device.getParentId();

		if (parentId == null) {
			return;
		}

		DeviceType parent = devices.get(parentId);

		if (parent == null) {
			return;
		}

		mergeParent(parent);

		for (String key : parent.getAttributes().keySet()) {
			String value = parent.getAttributes().get(key);

			if (!device.getAttributes().containsKey(key)) {
				device.getAttributes().put(key, value);
			}
		}
	}

	/**
	 * @return the devices
	 */
	private Map<String, DeviceType> getDevices() {
		return devices;
	}
}
