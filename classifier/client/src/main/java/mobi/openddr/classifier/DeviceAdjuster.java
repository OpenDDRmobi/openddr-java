/*
 * Copyright (c) 2011-2021 OpenDDR LLC and others. All rights reserved.
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
import static mobi.openddr.classifier.model.UserAgent.MAC;
import static mobi.openddr.classifier.model.UserAgent.WINDOWS;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.openddr.classifier.model.DeviceType;
import mobi.openddr.classifier.model.UserAgent;

/**
 * @author Werner Keil
 * @version 2.0
 */
abstract class DeviceAdjuster {
	private static final Logger LOG = Logger.getLogger(DeviceAdjuster.class.getName());

	private static final String DEVICE_VENDOR = "vendor";
	private static final String DEVICE_MODEL = "model";
	private static final String DEVICE_OS = "device_os";
	private static final String DEVICE_OS_VERSION = "device_os_version";
	
	private static final String LIKE_MAC_OS_X = "like Mac OS X";
	private static final String MAC_OS_X = "Mac OS X";
	
	private static final String BROWSER_CHROME = "Chrome";
	private static final String BROWSER_SAFARI = "Safari";
	private static final String BROWSER_EDGE = "Edg";
	private static final String BROWSER_OPERA = "OPR";
	private static final String BROWSER_NINTENDO = "NintendoBrowser";

	//private static final String CHROME_VERSION_REGEXP = "Chrome.([0-9a-z\\.b]+).*";
	private static final String SAFARI_REGEXP = ".*Safari/([0-9\\.]+).*?";

	private static final String BROWSER = "browser";
	private static final String BROWSER_VERSION = "browser_version";
	
	private static final String MOBILE_BROWSER = "mobile_browser";
	private static final String MOBILE_BROWSER_VERSION = "mobile_browser_version";
	
	private static final String VENDOR_APPLE = "Apple";	
	
	static final DeviceType adjustFromUserAgent(final DeviceType device, final UserAgent userAgent) {
		boolean isApple = false;
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
							//device.setAttributes(attributes);
						}
					}
					
					if (part.trim().endsWith(LIKE_MAC_OS_X)) {
						final String versionCandidate = part.trim()
								.substring(0, part.trim().length() - LIKE_MAC_OS_X.length()).trim();
						if (versionCandidate.contains("OS")) {
							final int versionCutoff = versionCandidate.indexOf("OS") + 2;
							final String versionPart = (versionCandidate.length() > versionCutoff ?
									versionCandidate.substring(versionCutoff) : versionCandidate)							
									.trim().replaceAll("_", ".");
							final String versionExisting = attributes.get(DEVICE_OS_VERSION);
							if (!versionPart.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + versionPart + "'");
								attributes.put(DEVICE_OS_VERSION, versionPart);
								//device.setAttributes(attributes);
							}
						}
					}
					else if (part.trim().contains(MAC_OS_X)) {
						final String versionCandidate = part.trim();						
						if (versionCandidate.contains("OS")) {
							final int versionCutoff = versionCandidate.indexOf("OS") + 4;
							final String versionPart = (versionCandidate.length() > versionCutoff ?
									versionCandidate.substring(versionCutoff) : versionCandidate)
									.trim().replaceAll("_", ".");
							final String osPart = versionCandidate.substring(0, versionCutoff);
							final String versionExisting = attributes.get(DEVICE_OS_VERSION);
							boolean attribChanged = false;
							if (!versionPart.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + versionPart + "'");
								attributes.put(DEVICE_OS_VERSION, versionPart);
								attribChanged = true;
							}
							final String osExisting = attributes.get(DEVICE_OS);
							if (!osPart.equals(osExisting)) {
								LOG.fine("Adjusting '" + osExisting + "' to '" + osPart + "'");
								attributes.put(DEVICE_OS, osPart);
								attribChanged = true;
							}
							if (attribChanged) {
								device.setAttributes(attributes);
							}
						}
					}					
					if (part.trim().startsWith(MAC)) {
						final String versionCandidate = part.trim();						
						if (versionCandidate.contains("OS")) {
							final int versionCutoff = versionCandidate.indexOf("OS") + 2;
							final String versionPart = (versionCandidate.length() > versionCutoff ? 
									versionCandidate.substring(versionCutoff) : versionCandidate)
									.trim().replaceAll("_", ".");														
							final String versionExisting = attributes.get(DEVICE_OS_VERSION);
							if (!versionPart.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + versionPart + "'");
								attributes.put(DEVICE_OS_VERSION, versionPart);
								//device.setAttributes(attributes);
							}
						} else {							
							final String modelExisting = attributes.get(DEVICE_MODEL);
							final String model = versionCandidate;
							if (modelExisting != null) {
								if (!modelExisting.equals(model)) {
									LOG.fine("Adjusting '" + modelExisting + "' to '" + model + "'");
									attributes.put(DEVICE_MODEL, model);
									attributes.put(DEVICE_VENDOR, VENDOR_APPLE);
									isApple = true;
								} else {
									LOG.fine("Setting '" + DEVICE_MODEL + "' to '" + model + "'");
									attributes.put(DEVICE_MODEL, model);
									attributes.put(DEVICE_VENDOR, VENDOR_APPLE);
									isApple = true;
								}
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
							// attributes.get(DEVICE_VENDOR);
							// if (vendorExisting == null ||
							// vendorExisting.length()==0 ||
							// "-".equals(vendorExisting) ) {
							// LOG.finer("Desktop" +
							// attributes.get("is_desktop"));
							// if
							// (Boolean.parseBoolean(attributes.get("is_desktop")))
							// {
							// attributes.put(DEVICE_VENDOR, "Microsoft");
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
			if (isApple) {
				if (pattern.contains(BROWSER_SAFARI)) {
					final Pattern safariRegexPattern = Pattern.compile(SAFARI_REGEXP);
					//LOG.fine("Adjusting '" + safariRegexPattern + "' to '" + safariRegexPattern + "'");
					final Matcher safariMatcher = safariRegexPattern.matcher(pattern);
					if (safariMatcher.matches()) {
						final String version = safariMatcher.group(1);					
						if (attributes.containsKey(BROWSER_VERSION)) {
							final String versionExisting = attributes.get(BROWSER_VERSION);
							if (!version.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + version + "'");
								attributes.put(BROWSER_VERSION, version);
								attributes.put(BROWSER, BROWSER_SAFARI);
							}
						} else if (attributes.containsKey(MOBILE_BROWSER_VERSION)) { // fallback
							final String versionExisting = attributes.get(MOBILE_BROWSER_VERSION);
							if (!version.equals(versionExisting)) {
								LOG.fine("Adjusting '" + versionExisting + "' to '" + version + "'");
								attributes.put(MOBILE_BROWSER_VERSION, version);
								attributes.put(MOBILE_BROWSER, BROWSER_SAFARI);
							}
						}
					}
				}
			} else	if (pattern.contains(BROWSER_CHROME)) {
				final String parts = pattern.substring(pattern.indexOf(BROWSER_CHROME));
				final String version = parts.substring(BROWSER_CHROME.length() + 1, parts.indexOf(" ") == -1 ? parts.length() : parts.indexOf(" "));
				// System.out.println(version);
				// Matcher chromeVersionMatcher =
				// chromeVersionPattern.matcher(pattern);
				// System.out.println(chromeVersionMatcher);
				if (attributes.containsKey(BROWSER_VERSION)) {
					final String versionExisting = attributes.get(BROWSER_VERSION);
					if (!version.equals(versionExisting)) {
						LOG.fine("Adjusting '" + versionExisting + "' to '" + version + "'");
						attributes.put(BROWSER_VERSION, version);
						attributes.put(BROWSER, BROWSER_CHROME);
					}
				} else if (attributes.containsKey(MOBILE_BROWSER_VERSION)) { // fallback
					final String versionExisting = attributes.get(MOBILE_BROWSER_VERSION);
					if (!version.equals(versionExisting)) {
						LOG.fine("Adjusting '" + versionExisting + "' to '" + version + "'");
						attributes.put(MOBILE_BROWSER_VERSION, version);
						attributes.put(MOBILE_BROWSER, BROWSER_CHROME);
					}
				}
			}
		} else	if (pattern.contains(BROWSER_EDGE)) {
			
		} else	if (pattern.contains(BROWSER_OPERA)) {
			
		}

		device.setAttributes(attributes);

		// logger.info("Device: " + device.getId() + " - " +
		// device.getPropertiesMap());
		return device;
	}
}
