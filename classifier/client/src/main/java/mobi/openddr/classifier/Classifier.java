/*
 * Copyright (c) 2011-2018 OpenDDR LLC and others. All rights reserved.
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
package mobi.openddr.classifier;

import static mobi.openddr.classifier.DeviceAdjuster.adjustFromUserAgent;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.openddr.classifier.loader.LoaderOption;
import mobi.openddr.classifier.model.Device;
import mobi.openddr.classifier.model.DeviceType;
import mobi.openddr.classifier.model.UserAgent;
import mobi.openddr.classifier.parser.Pattern;
import mobi.openddr.classifier.loader.LoaderFactory;

/**
 * @author Werner Keil
 * @version 1.3.1
 */
public class Classifier {
	private static final Logger LOG = Logger.getLogger(Classifier.class.getName());
	private static final java.util.regex.Pattern TEXT_SPLIT_PATTERN = java.util.regex.Pattern
			.compile(" |-|_|/|\\\\|\\[|\\]|\\(|\\)|;");

	private static long initCount = 0;

	// indexes
	private Map<String, DeviceType> devices;
	private Map<String, List<DeviceType>> patterns;

	private final Device unknown;

	Classifier() {
		devices = null;
		patterns = null;
		Map<String, String> uAttributes = new HashMap<String, String>();
		uAttributes.put("id", Device.UNKNOWN_ID);
		uAttributes = Collections.unmodifiableMap(uAttributes);
		unknown = new Device(Device.UNKNOWN_ID, uAttributes);
	}

	public void initDeviceData(LoaderOption option) throws IOException {
		initDeviceData(option, null);
	}

	public synchronized void initDeviceData(LoaderOption option, String path) throws IOException {
		devices = LoaderFactory.getLoader(option, path).load();
		initCount++;

		if (initCount % 1000 == 0) {
			LOG.log(Level.WARNING, "Possible device data over-initialization detected");
		}

		if (devices == null) {
			patterns = null;
			return;
		}
		createIndex();
	}

	private void createIndex() {
		patterns = new HashMap<String, List<DeviceType>>(8000);

		for (DeviceType device : devices.values()) {
			for (Pattern pattern : device.getPatternSet().getPatterns()) {
				for (int i = 0; i < pattern.getPatternParts().size(); i++) {
					String part = pattern.getPatternParts().get(i);

					// duplicate
					if (patterns.get(part) != null) {
						if (i == (pattern.getPatternParts().size() - 1) && !patterns.get(part).contains(device)) {
							patterns.get(part).add(device);
						}
					} else {
						List<DeviceType> single = new ArrayList<DeviceType>();
						single.add(device);
						patterns.put(part, single);
					}
				}
			}
		}
	}

	public Map<String, String> classify(String text) {
		if (devices == null) {
			throw new RuntimeException("Uninitialized device index");
		}

		if (text == null) {
			return null;
		}

		Set<String> hitPatterns = new HashSet<String>();
		Set<DeviceType> hitDevices = new HashSet<DeviceType>();
		DeviceType winner = null;
		Pattern winnerPattern = null;
		LOG.log(Level.FINE, "classify: ''{0}''", text);
		List<String> parts = split(text);

		// generate ngrams upto size 4 TODO this was not working in some cases, took 4 out
		for (int i = 0; i < parts.size(); i++) {
			String partPattern = "";
			String currPart = "";
			for (int j = 0; (j + i) < parts.size(); j++) { // j < 4 &&
				currPart = parts.get(i + j);
				List<DeviceType> dlist = patterns.get(currPart);
				if (dlist != null) {
					// TODO make this and else part reusable
					hitPatterns.add(currPart);
					hitDevices.addAll(dlist);
					for (DeviceType device : dlist) {
						LOG.log(Level.FINER, "Hit found: ''{0}'' => id: ''{1}'' {2}",
								new Object[] { currPart, device.getId(), device.getPatternSet() });
						break;
					}
				} else {
					partPattern += currPart;
					dlist = patterns.get(partPattern);
					if (dlist != null) {
						hitPatterns.add(partPattern);
						hitDevices.addAll(dlist);
						for (DeviceType device : dlist) {
							LOG.log(Level.FINER, "Hit found: ''{0}'' => id: ''{1}'' {2}",
									new Object[] { partPattern, device.getId(), device.getPatternSet() });
						}
					}
				}
			}
		}

		// look for the strongest hit
		for (DeviceType device : hitDevices) {
			Pattern pattern = device.getPatternSet().isValid(hitPatterns);
			if (pattern == null) {
				continue;
			}

			LOG.log(Level.FINER, "Hit candidate: ''{0}'' => ({1},{2})",
					new Object[] { device.getId(), pattern.getType(), pattern.getRank() });

			if (winnerPattern == null || pattern.getRank() > winnerPattern.getRank()) {
				winner = device;
				winnerPattern = pattern;
			}
		}

		if (winner != null) {
			LOG.log(Level.FINE, "Result: {0}", winner);

			UserAgent userAgent = UserAgent.of(text);
			LOG.log(Level.FINE, "User Agent: {0}", userAgent);
			return adjustFromUserAgent(winner, userAgent).getAttributes();
		} else {
			return null;
		}
	}

	private static List<String> split(String text) {
		String[] parts = TEXT_SPLIT_PATTERN.split(text);
		List<String> nonemptyParts = new ArrayList<String>();
		for (String part : parts) {
			String normalizedPart = Pattern.normalize(part);
			if (normalizedPart != null && !normalizedPart.isEmpty())
				nonemptyParts.add(normalizedPart);
		}
		return nonemptyParts;
	}

	public Device classifyDevice(String text) {
		Map<String, String> m = classify(text);
		if (m == null) {
			return unknown;
		}
		return new Device(m.get("id"), m);
	}

	public int getDeviceCount() {
		if (devices == null) {
			return -1;
		}
		return devices.size();
	}

	public int getPatternCount() {
		if (patterns == null) {
			return -1;
		}
		return patterns.size();
	}

	public long getNodeCount() {
		if (patterns == null) {
			return -1;
		}
		long count = 0;
		for (List<DeviceType> pDevices : patterns.values()) {
			count += pDevices.size();
		}
		return count;
	}
	
	public static ClassifierBuilder builder() {
		return new ClassifierBuilder();
	}

	Map<String, DeviceType> getDevices() {
		return devices;
	}
}