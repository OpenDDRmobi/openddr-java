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
package mobi.openddr.simple.test;

import org.w3c.ddr.simple.Service;

/** Provides a DDR Service singleton
 *  that uses actual device data. 
 */
public class DDRTestService {
    public static final Service ODDR_SERVICE;
    
    static {
        ODDR_SERVICE = ServiceSetup.getService("config/openddr.properties");
    }
    
    /** Do not instantiate, use ODDR_SERVICE instead */
    private DDRTestService() {
    }
}
