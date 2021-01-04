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
package mobi.openddr.simple.test;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Properties;

import org.w3c.ddr.simple.Service;
import org.w3c.ddr.simple.ServiceFactory;

/** Provides a DDR Service instance for tests. 
 */
public class ServiceSetup {
    static Service getService(String resourcePath) {
        try {
            final Properties props = new Properties();
            final InputStream cfg = ServiceSetup.class.getClassLoader().getResourceAsStream(resourcePath);
            assertNotNull("Expecting " + resourcePath + " resource to be available", cfg);
            try {
                props.load(cfg);
            } finally {
                cfg.close();
            }
            final String vocabIRI = props.getProperty("oddr.vocabulary.device");
            final String serviceClass = props.getProperty("oddr.service.class");
            return ServiceFactory.newService(serviceClass, vocabIRI, props);
        } catch(Exception e) {
            throw new RuntimeException("Failed to initialize Service", e);
        }
    }
}
