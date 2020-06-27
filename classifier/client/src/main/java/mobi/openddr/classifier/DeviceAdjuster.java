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
package mobi.openddr.classifier;

import static mobi.openddr.classifier.model.UserAgent.ANDROID;
import static mobi.openddr.classifier.model.UserAgent.WINDOWS;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import mobi.openddr.classifier.model.DeviceType;
import mobi.openddr.classifier.model.UserAgent;

/**
 * @author Werner Keil
 * @version 1.2
 */
abstract class DeviceAdjuster {
	private static final Logger LOG = Logger.getLogger(DeviceAdjuster.class.getName());

	private static final String DEVICE_OS = "device_os";
	private static final String DEVICE_OS_VERSION = "device_os_version";
	// private static final String VENDOR = "vendor";
	private static final String LIKE_MAC = "like Mac OS X";

	private static final String CHROME = "Chrome";
	private static final String SAFARI = "Safari";

	private static final String CHROME_VERSION_REGEXP = "Chrome.([0-9a-z\\.b]+).*";
	private static final String SAFARI_REGEXP = ".*Safari/([0-9\\.]+).*?";

	private static final String BROWSER = "mobile_browser";
	private static final String BROWSER_VERSION = "mobile_browser_version";

	static final DeviceType adjustFromUserAgent(final DeviceType device, final UserAgent userAgent) {
		Map<String, String> attributes;
		if (device.isLocked()) {
			// clone map
			attributes = new HashMap<>();

			attributes.putAll(device.getAttributes());
		} else {
			attributes = device.getAttributes();
		}

		// OS
		String pattern = userAgent.getPatternElementsInside();
		if (pattern != null && pattern.contains(";")) {
			String[] parts = pattern.split(";");

			for (String part : parts) {
				if (part != null) {
					if (part.trim().startsWith(ANDROID)) {
						final String versionPart = part.trim().substring(ANDROID.length()).trim();
						final String versionExisting = attributes.get(DEVICE_OS_VERSION);
						if (!versionPart.equals(versionExisting)) {
							LOG.fine("Adjusting '" + versionExisting + "' to '" + versionPart + "'");
							attributes.put(DEVICE_OS_VERSION, versionPart);
							device.setAttributes(attributes);
						}
					}
					if (part.trim().endsWith(LIKE_MAC)) {
						final String versionCandidate = part.trim()
								.substring(0, part.trim().length() - LIKE_MAC.length()).trim();
						if (versionCandidate.contains("OS")) {
							final String versionPart = versionCandidate.substring(versionCandidate.indexOf("OS") + 2)
									.trim().replaceAll("_", ".");
							final String versionExisting = attributes.get(DEVICE_OS_VERSION);
							if (!versionPart.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + versionPart + "'");
								attributes.put(DEVICE_OS_VERSION, versionPart);
								device.setAttributes(attributes);
							}
						}
					}
					if (part.trim().startsWith(WINDOWS)) {
						final String versionCandidate = part.trim();
						DeviceHints.WindowsVersion version = DeviceHints.WindowsVersion.ofToken(versionCandidate);
						if (version != null) {
							final String osExisting = attributes.get(DEVICE_OS);
							LOG.fine("Adjusting '" + osExisting + "' to '" + version.getDescription() + "'");
							attributes.put(DEVICE_OS, version.getDescription());
							final String versionExisting = attributes.get(DEVICE_OS_VERSION);
							if (!version.getVersion().equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + version.getVersion() + "'");
								attributes.put(DEVICE_OS_VERSION, version.getVersion());
							}
							// final String vendorExisting =
							// attributes.get(VENDOR);
							// if (vendorExisting == null ||
							// vendorExisting.length()==0 ||
							// "-".equals(vendorExisting) ) {
							// LOG.finer("Desktop" +
							// attributes.get("is_desktop"));
							// if
							// (Boolean.parseBoolean(attributes.get("is_desktop")))
							// {
							// attributes.put(VENDOR, "Microsoft");
							// }
							// }
						}
					} /*
						 * else { String versionCandidate = part.trim();
						 * System.out.println(versionCandidate); }
						 */
				}
			}
		}

		// Browser
		pattern = userAgent.getPatternElementsPost();
		if (pattern != null) {
			if (pattern.contains(CHROME)) {
				String parts = pattern.substring(pattern.indexOf(CHROME));
				String version = parts.substring(CHROME.length() + 1, parts.indexOf(" "));
				// System.out.println(version);
				// Matcher chromeVersionMatcher =
				// chromeVersionPattern.matcher(pattern);
				// System.out.println(chromeVersionMatcher);
				final String versionExisting = attributes.get(BROWSER_VERSION);
				if (!version.equals(versionExisting)) {
					LOG.fine("Adjusting '" + versionExisting + "' to '" + version + "'");
					attributes.put(BROWSER_VERSION, version);
					attributes.put(BROWSER, CHROME);
				}
			}
			// Pattern safariPattern = Pattern.compile(SAFARI_REGEXP);

		}

		device.setAttributes(attributes);

		// logger.info("Device: " + device.getId() + " - " +
		// device.getPropertiesMap());
		return device;
	}
}
