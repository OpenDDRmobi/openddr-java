/*
 * Copyright (c) 2011-2016 OpenDDR LLC and others. All rights reserved.
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

/**
 * @author Werner Keil
 * @version 0.4
 */
abstract class DeviceHints {
    static enum WindowsVersion {
	// TODO Windows 10
	WIN_81("Windows NT 6.3", "Windows 8.1", "8.1"), //
	WIN_8("Windows NT 6.2", "Windows 8", "8.0"), //
	WIN_7("Windows NT 6.1", "Windows 7", "7.0"), //
	VISTA("Windows NT 6.0", "Windows Vista", "6.0"), //
	WIN_2003("Windows NT 5.2",
		"Windows Server 2003; Windows XP x64 Edition", "2003"), //
	WIN_XP("Windows NT 5.1", "Windows XP", "5.1"), //
	WIN_2000_SP1("Windows NT 5.01", "Windows 2000, Service Pack 1 (SP1)",
		"5.01"), //
	WIN_2000("Windows NT 5.0", "Windows 2000", "2000"), //
	WIN_NT_4("Windows NT 4.0", "Microsoft Windows NT 4.0", "4.0"), //
	WIN_ME("Windows 98; Win 9x 4.90",
		"Windows Millennium Edition (Windows Me)", "4.90"), //
	WIN_98("Windows 98", "Windows 98", "98"), //
	WIN_95("Windows 95", "Windows 95", "95"), //
	WIN_CE("Windows CE", "Windows CE", "CE"); // TODO version nr?

	private final String token;
	private final String description;
	private final String version;

	private WindowsVersion(String t, String d, String v) {
	    token = t;
	    description = d;
	    version = v;
	}

	String getDescription() {
	    return description;
	}

	String getVersion() {
	    return version;
	}

	static final WindowsVersion ofToken(String token) {
	    for (WindowsVersion version : values()) {
		if (version.token.equalsIgnoreCase(token))
		    return version;
	    }
	    return null;
	}
    }
}
