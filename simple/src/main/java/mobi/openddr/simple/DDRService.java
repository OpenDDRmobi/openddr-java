/*
 * Copyright (c) 2011-2017 OpenDDR LLC and others. All rights reserved.
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
package mobi.openddr.simple;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import mobi.openddr.simple.model.ODDRPropertyValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mobi.openddr.simple.builder.Builder;
import mobi.openddr.simple.builder.browser.DefaultBrowserBuilder;
import mobi.openddr.simple.builder.device.DeviceBuilder;
import mobi.openddr.simple.builder.os.DefaultOSBuilder;
import mobi.openddr.simple.documenthandler.BrowserDatasourceHandler;
import mobi.openddr.simple.documenthandler.DeviceBuilderHandler;
import mobi.openddr.simple.documenthandler.DeviceDatasourceHandler;
import mobi.openddr.simple.documenthandler.OperatingSystemDatasourceHandler;
import mobi.openddr.simple.identificator.BrowserIdentificator;
import mobi.openddr.simple.identificator.DeviceIdentificator;
import mobi.openddr.simple.identificator.OSIdentificator;
import mobi.openddr.simple.model.BufferedODDRHTTPEvidence;
import mobi.openddr.simple.model.device.Device;
import mobi.openddr.simple.model.ODDRHTTPEvidence;
import mobi.openddr.simple.model.ODDRPropertyName;
import mobi.openddr.simple.model.ODDRPropertyRef;
import mobi.openddr.simple.model.ODDRPropertyValues;
import mobi.openddr.simple.model.UserAgent;
import mobi.openddr.simple.model.UserAgentFactory;
import mobi.openddr.simple.model.browser.Browser;
import mobi.openddr.simple.model.os.OperatingSystem;
import mobi.openddr.simple.model.vocabulary.Vocabulary;
import mobi.openddr.simple.model.vocabulary.VocabularyProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.ddr.simple.Evidence;
import org.w3c.ddr.simple.PropertyName;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValue;
import org.w3c.ddr.simple.PropertyValues;
import org.w3c.ddr.simple.Service;
import org.w3c.ddr.simple.exception.InitializationException;
import org.w3c.ddr.simple.exception.NameException;
import org.xml.sax.SAXException;

/**
 * 
 * @author Werner Keil
 *
 */
public class DDRService implements Service {

    public static final String ASPECT_DEVICE = "device";
    public static final String ASPECT_WEB_BROWSER = "webBrowser";
    public static final String ASPECT_OPERATING_SYSTEM = "operatingSystem";
    public static final String ASPECT_GROUP = "group";
    public static final String ODDR_UA_DEVICE_BUILDER_PATH_PROP = "oddr.ua.device.builder.path";
    public static final String ODDR_UA_DEVICE_DATASOURCE_PATH_PROP = "oddr.ua.device.datasource.path";
    public static final String ODDR_UA_DEVICE_BUILDER_PATCH_PATHS_PROP = "oddr.ua.device.builder.patch.paths";
    public static final String ODDR_UA_DEVICE_DATASOURCE_PATCH_PATHS_PROP = "oddr.ua.device.datasource.patch.paths";
    public static final String ODDR_UA_BROWSER_DATASOURCE_PATH_PROP = "oddr.ua.browser.datasource.path";
    public static final String ODDR_UA_OPERATINGSYSTEM_DATASOURCE_PATH_PROP = "oddr.ua.operatingSystem.datasource.path";
    public static final String ODDR_UA_DEVICE_BUILDER_STREAM_PROP = "oddr.ua.device.builder.stream";
    public static final String ODDR_UA_DEVICE_DATASOURCE_STREAM_PROP = "oddr.ua.device.datasource.stream";
    public static final String ODDR_UA_DEVICE_BUILDER_PATCH_STREAMS_PROP = "oddr.ua.device.builder.patch.streams";
    public static final String ODDR_UA_DEVICE_DATASOURCE_PATCH_STREAMS_PROP = "oddr.ua.device.datasource.patch.streams";
    public static final String ODDR_UA_BROWSER_DATASOURCE_STREAM_PROP = "oddr.ua.browser.datasource.stream";
    public static final String ODDR_UA_OPERATINGSYSTEM_DATASOURCE_STREAM_PROP = "oddr.ua.operatingSystem.datasource.stream";
    public static final String ODDR_THRESHOLD_PROP = "oddr.threshold";
    public static final String ODDR_VOCABULARY_IRI = "oddr.vocabulary.device";
    private static final String ODDR_API_VERSION = "1.1.0-SNAPSHOT";
    private static final String ODDR_DATA_VERSION = "1.0.4-SNAPSHOT"; // TODO add actual discovery from XML
    public static final int ODDR_DEFAULT_THRESHOLD = 70;
    private String defaultVocabularyIRI = null;
    private DeviceIdentificator deviceIdentificator = null;
    private BrowserIdentificator browserIdentificator = null;
    private OSIdentificator osIdentificator = null;
    private VocabularyHolder vocabularyHolder = null;
    private int threshold = ODDR_DEFAULT_THRESHOLD;
    private static final String GROUP_REGEXPR = "\\$([^ ]+)";
    private Pattern groupRegexprPattern = Pattern.compile(GROUP_REGEXPR);
    protected final Logger logger = Logger.getLogger(getClass().getName());

    public void initialize(String defaultVocabularyIRI, Properties prprts) throws NameException, InitializationException {
        if (defaultVocabularyIRI == null || defaultVocabularyIRI.trim().length() == 0) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new NullPointerException("defaultVocabularyIRI can not be null"));
        }

        /*Initializing VocabularyHolder*/
        VocabularyService oddrVocabularyService = new VocabularyService();
        oddrVocabularyService.initialize(prprts);

        vocabularyHolder = oddrVocabularyService.getVocabularyHolder();
        vocabularyHolder.existVocabulary(defaultVocabularyIRI);

        String oddrUaDeviceBuilderPath = prprts.getProperty(ODDR_UA_DEVICE_BUILDER_PATH_PROP);
        String oddrUaDeviceDatasourcePath = prprts.getProperty(ODDR_UA_DEVICE_DATASOURCE_PATH_PROP);
        String oddrUaDeviceBuilderPatchPaths = prprts.getProperty(ODDR_UA_DEVICE_BUILDER_PATCH_PATHS_PROP);
        String oddrUaDeviceDatasourcePatchPaths = prprts.getProperty(ODDR_UA_DEVICE_DATASOURCE_PATCH_PATHS_PROP);
        String oddrUaBrowserDatasourcePaths = prprts.getProperty(ODDR_UA_BROWSER_DATASOURCE_PATH_PROP);
        String oddrUaOperatingSystemDatasourcePaths = prprts.getProperty(ODDR_UA_OPERATINGSYSTEM_DATASOURCE_PATH_PROP);

        InputStream oddrUaDeviceBuilderStream = null;
        InputStream oddrUaDeviceDatasourceStream = null;
        InputStream[] oddrUaDeviceBuilderPatchStreams = null;
        InputStream[] oddrUaDeviceDatasourcePatchStreams = null;
        InputStream oddrUaBrowserDatasourceStream = null;
        InputStream oddrUaOperatingSystemDatasourceStream = null;

        try {
            oddrUaDeviceBuilderStream = (InputStream) prprts.get(ODDR_UA_DEVICE_BUILDER_STREAM_PROP);
        } catch (Exception ex) {
            oddrUaDeviceBuilderStream = null;
        }
        try {
            oddrUaDeviceDatasourceStream = (InputStream) prprts.get(ODDR_UA_DEVICE_DATASOURCE_STREAM_PROP);
        } catch (Exception ex) {
            oddrUaDeviceDatasourceStream = null;
        }
        try {
            oddrUaDeviceBuilderPatchStreams = (InputStream[]) prprts.get(ODDR_UA_DEVICE_BUILDER_PATCH_STREAMS_PROP);
        } catch (Exception ex) {
            oddrUaDeviceBuilderPatchStreams = null;
        }
        try {
            oddrUaDeviceDatasourcePatchStreams = (InputStream[]) prprts.get(ODDR_UA_DEVICE_DATASOURCE_PATCH_STREAMS_PROP);
        } catch (Exception ex) {
            oddrUaDeviceDatasourcePatchStreams = null;
        }
        try {
            oddrUaBrowserDatasourceStream = (InputStream) prprts.get(ODDR_UA_BROWSER_DATASOURCE_STREAM_PROP);
        } catch (Exception ex) {
            oddrUaBrowserDatasourceStream = null;
        }
        try {
            oddrUaOperatingSystemDatasourceStream = (InputStream) prprts.get(ODDR_UA_OPERATINGSYSTEM_DATASOURCE_STREAM_PROP);
        } catch (Exception ex) {
            oddrUaOperatingSystemDatasourceStream = null;
        }

        String oddrThreshold = prprts.getProperty(ODDR_THRESHOLD_PROP);

        if ((oddrUaDeviceBuilderPath == null || oddrUaDeviceBuilderPath.trim().length() == 0) && oddrUaDeviceBuilderStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + ODDR_UA_DEVICE_BUILDER_PATH_PROP));
        }

        if ((oddrUaDeviceDatasourcePath == null || oddrUaDeviceDatasourcePath.trim().length() == 0) && oddrUaDeviceDatasourceStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP));
        }

        String oddrUaDeviceBuilderPatchPathArray[] = null;

        if (oddrUaDeviceBuilderPatchPaths != null && oddrUaDeviceBuilderPatchPaths.trim().length() != 0) {
            oddrUaDeviceBuilderPatchPathArray = oddrUaDeviceBuilderPatchPaths.split(",");
        } else {
            oddrUaDeviceBuilderPatchPathArray = new String[0];
        }

        String ooddrUaDeviceDatasourcePatchPathArray[] = null;

        if (oddrUaDeviceDatasourcePatchPaths != null && oddrUaDeviceDatasourcePatchPaths.trim().length() != 0) {
            ooddrUaDeviceDatasourcePatchPathArray = oddrUaDeviceDatasourcePatchPaths.split(",");
        } else {
            ooddrUaDeviceDatasourcePatchPathArray = new String[0];
        }

//        if (oddrUaDeviceBuilderPatchStreams == null) {
//            oddrUaDeviceBuilderPatchStreams = new InputStream[0];
//        }
//
//        if (oddrUaDeviceDatasourcePatchStreams == null) {
//            oddrUaDeviceDatasourcePatchStreams = new InputStream[0];
//        }

        if ((oddrUaBrowserDatasourcePaths == null || oddrUaBrowserDatasourcePaths.trim().length() == 0) && oddrUaBrowserDatasourceStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + ODDR_UA_BROWSER_DATASOURCE_PATH_PROP));
        }

        if ((oddrUaOperatingSystemDatasourcePaths == null || oddrUaOperatingSystemDatasourcePaths.trim().length() == 0) && oddrUaOperatingSystemDatasourceStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + ODDR_UA_OPERATINGSYSTEM_DATASOURCE_PATH_PROP));
        }

        if (oddrThreshold == null || oddrThreshold.trim().length() == 0) {
            this.threshold = ODDR_DEFAULT_THRESHOLD;
        } else {
            try {
                this.threshold = Integer.parseInt(oddrThreshold);
                if (this.threshold <= 0) {
                    this.threshold = ODDR_DEFAULT_THRESHOLD;
                }
            } catch (NumberFormatException x) {
                this.threshold = ODDR_DEFAULT_THRESHOLD;
            }
        }

        Map<String, Device> devices = new HashMap<String, Device>();
        DeviceDatasourceHandler deviceDatasourceHandler = new DeviceDatasourceHandler(devices, vocabularyHolder);

        InputStream stream = null;
        SAXParser parser = null;

        try {
            if (oddrUaDeviceDatasourceStream != null) {
                stream = oddrUaDeviceDatasourceStream;
            } else {
                stream = new FileInputStream(new File(oddrUaDeviceDatasourcePath));
            }

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " " + oddrUaDeviceDatasourcePath));
        }

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, deviceDatasourceHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + oddrUaDeviceDatasourcePath));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " :" + oddrUaDeviceDatasourcePath));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        deviceDatasourceHandler.setPatching(true);

        if (oddrUaDeviceDatasourcePatchStreams != null) {
            for (int i = 0; i < oddrUaDeviceDatasourcePatchStreams.length; i++) {
                stream = oddrUaDeviceDatasourcePatchStreams[i];

                try {
                    parser = SAXParserFactory.newInstance().newSAXParser();

                } catch (ParserConfigurationException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

                } catch (SAXException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
                }

                try {
                    parser.parse(stream, deviceDatasourceHandler);

                } catch (Exception ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse DeviceDatasource input stream " + i));
                }

                try {
                    stream.close();

                } catch (IOException ex) {
                    logger.log(Level.WARNING, "", ex);
                }
            }
        } else {
            for (int i = 0; i < ooddrUaDeviceDatasourcePatchPathArray.length; i++) {
                try {
                    stream = new FileInputStream(new File(ooddrUaDeviceDatasourcePatchPathArray[i]));

                } catch (IOException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " " + ooddrUaDeviceDatasourcePatchPathArray[i]));
                }

                try {
                    parser = SAXParserFactory.newInstance().newSAXParser();

                } catch (ParserConfigurationException ex) {
                    try {
                        stream.close();
                    } catch (IOException ie) {
                        logger.log(Level.WARNING, "", ie);
                    }
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

                } catch (SAXException ex) {
                    try {
                        stream.close();
                    } catch (IOException ie) {
                        logger.log(Level.WARNING, "", ie);
                    }
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
                }

                try {
                    parser.parse(stream, deviceDatasourceHandler);
                } catch (SAXException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + ooddrUaDeviceDatasourcePatchPathArray[i]));

                } catch (IOException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " :" + ooddrUaDeviceDatasourcePatchPathArray[i]));
                }

                try {
                    stream.close();

                } catch (IOException ex) {
                    logger.log(Level.WARNING, "", ex);
                }
            }

        }

        List<DeviceBuilder> builders = new ArrayList<DeviceBuilder>();
        DeviceBuilderHandler deviceBuilderHandler = new DeviceBuilderHandler(builders);

        try {
            if (oddrUaDeviceBuilderStream != null) {
                stream = oddrUaDeviceBuilderStream;
            } else {
                stream = new FileInputStream(new File(oddrUaDeviceBuilderPath));
            }

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_DEVICE_BUILDER_PATH_PROP + " " + oddrUaDeviceBuilderPath));
        }

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, deviceBuilderHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + oddrUaDeviceBuilderPath));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " :" + oddrUaDeviceBuilderPath));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        if (oddrUaDeviceBuilderPatchStreams != null) {
            for (int i = 0; i < oddrUaDeviceBuilderPatchStreams.length; i++) {
                stream = oddrUaDeviceBuilderPatchStreams[i];

                try {
                    parser = SAXParserFactory.newInstance().newSAXParser();

                } catch (ParserConfigurationException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

                } catch (SAXException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
                }

                try {
                    parser.parse(stream, deviceBuilderHandler);

                } catch (Exception ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse DeviceBuilder input stream " + i));
                }

                try {
                    stream.close();

                } catch (IOException ex) {
                    logger.log(Level.WARNING, "", ex);
                }
            }

        } else {
            for (int i = 0; i < oddrUaDeviceBuilderPatchPathArray.length; i++) {
                try {
                    stream = new FileInputStream(new File(oddrUaDeviceBuilderPatchPathArray[i]));

                } catch (IOException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_DEVICE_BUILDER_PATCH_PATHS_PROP + " " + oddrUaDeviceBuilderPatchPathArray[i]));
                }

                try {
                    parser = SAXParserFactory.newInstance().newSAXParser();

                } catch (ParserConfigurationException ex) {
                    try {
                        stream.close();
                    } catch (IOException ie) {
                        logger.log(Level.WARNING, "", ie);
                    }
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

                } catch (SAXException ex) {
                    try {
                        stream.close();
                    } catch (IOException ie) {
                        logger.log(Level.WARNING, "", ie);
                    }
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
                }

                try {
                    parser.parse(stream, deviceBuilderHandler);

                } catch (SAXException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + oddrUaDeviceBuilderPatchPathArray[i]));
                } catch (IOException ex) {
                    throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_DEVICE_DATASOURCE_PATH_PROP + " :" + oddrUaDeviceBuilderPatchPathArray[i]));
                } 
                try {
                    stream.close();
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "", ex);
                }
            }
        }

        BrowserDatasourceHandler browserDatasourceHandler = new BrowserDatasourceHandler(vocabularyHolder);

        try {
            if (oddrUaBrowserDatasourceStream != null) {
                stream = oddrUaBrowserDatasourceStream;
            } else {
                stream = new FileInputStream(new File(oddrUaBrowserDatasourcePaths));
            }

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_BROWSER_DATASOURCE_PATH_PROP + " " + oddrUaBrowserDatasourcePaths));
        }

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, browserDatasourceHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + oddrUaBrowserDatasourcePaths));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_BROWSER_DATASOURCE_PATH_PROP + " :" + oddrUaBrowserDatasourcePaths));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }

        OperatingSystemDatasourceHandler operatingSystemDatasourceHandler = new OperatingSystemDatasourceHandler(vocabularyHolder);

        try {
            if (oddrUaOperatingSystemDatasourceStream != null) {
                stream = oddrUaOperatingSystemDatasourceStream;
            } else {
                stream = new FileInputStream(new File(oddrUaOperatingSystemDatasourcePaths));
            }
        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + ODDR_UA_OPERATINGSYSTEM_DATASOURCE_PATH_PROP + " " + oddrUaOperatingSystemDatasourcePaths));
        }

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, operatingSystemDatasourceHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + oddrUaOperatingSystemDatasourcePaths));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + ODDR_UA_OPERATINGSYSTEM_DATASOURCE_PATH_PROP + " :" + oddrUaOperatingSystemDatasourcePaths));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "", ex);
        }
        
        deviceIdentificator = new DeviceIdentificator(deviceBuilderHandler.getBuilders(), deviceDatasourceHandler.getDevices());
        deviceIdentificator.completeInit();

        browserIdentificator = new BrowserIdentificator(new Builder[]{DefaultBrowserBuilder.getInstance()}, browserDatasourceHandler.getBrowsers());
        browserIdentificator.completeInit();

        osIdentificator = new OSIdentificator(new Builder[]{DefaultOSBuilder.getInstance()}, operatingSystemDatasourceHandler.getOperatingSystems());
        osIdentificator.completeInit();

        deviceDatasourceHandler = null;
        deviceBuilderHandler = null;
        browserDatasourceHandler = null;
        operatingSystemDatasourceHandler = null;

        this.defaultVocabularyIRI = defaultVocabularyIRI;

        parser = null;

        oddrVocabularyService = null;

        return;
    }

    public String getDataVersion() {
        return ODDR_DATA_VERSION;
    }

	@Override
	public String getImplementationVersion() {
		return ODDR_API_VERSION;
	}
    
    public PropertyRef[] listPropertyRefs() {
        List<PropertyRef> propertyRefsList = new ArrayList<PropertyRef>();
        Map<String, Vocabulary> vocabularies = vocabularyHolder.getVocabularies();
        Set<String> vocabularyKeys = vocabularies.keySet();

        for (String vocabularyKey : vocabularyKeys) {
            Vocabulary vocabulary = vocabularies.get(vocabularyKey);
            Map<String, VocabularyProperty> properties = vocabulary.getProperties();
            Set<String> propertyKeys = properties.keySet();
            for (String propertyKey : propertyKeys) {
                PropertyName propertyName = new ODDRPropertyName(propertyKey, vocabularyKey);
                for (int i = 0; i < properties.get(propertyKey).getAspects().length; i++) {
                    PropertyRef propertyRef = new ODDRPropertyRef(propertyName, properties.get(propertyKey).getAspects()[i]);
                    propertyRefsList.add(propertyRef);
                }
            }
        }

        PropertyRef[] propertyRefs = new PropertyRef[propertyRefsList.size()];
        propertyRefs = propertyRefsList.toArray(propertyRefs);

        return propertyRefs;
    }

    public PropertyValue getPropertyValue(Evidence evdnc, PropertyRef pr) throws NameException {
        return getPropertyValues(evdnc, new PropertyRef[]{pr}).getValue(pr);
    }

    public PropertyValue getPropertyValue(Evidence evdnc, String localPropertyName, String localAspectName, String vocabularyIRI) throws NameException {
        return getPropertyValue(evdnc, newPropertyRef(newPropertyName(localPropertyName, vocabularyIRI), localAspectName));
    }

    public PropertyValue getPropertyValue(Evidence evdnc, PropertyName pn) throws NameException {
        return getPropertyValue(evdnc, newPropertyRef(pn));
    }

    public PropertyValue getPropertyValue(Evidence evdnc, String localPropertyName) throws NameException {
        return getPropertyValue(evdnc, newPropertyName(localPropertyName));
    }

    public PropertyValues getPropertyValues(Evidence evdnc) {
        Device deviceFound = null;
        Browser browserFound = null;
        OperatingSystem osFound = null;
        boolean deviceIdentified = false;
        boolean browserIdentified = false;
        boolean osIdentified = false;
        UserAgent deviceUA = null;
        UserAgent browserUA = null;

        JexlEngine jexl = new JexlEngine();
        ODDRPropertyValues ret = new ODDRPropertyValues();
        Map<String, Vocabulary> vocabularies = vocabularyHolder.getVocabularies();
        Set<String> vocabularyKeys = vocabularies.keySet();

        for (String vocabularyKey : vocabularyKeys) {
            Vocabulary vocabulary = vocabularies.get(vocabularyKey);
            Map<String, VocabularyProperty> properties = vocabulary.getProperties();
            Set<String> propertyKeys = properties.keySet();
            for (String propertyKey : propertyKeys) {
                PropertyName propertyName = new ODDRPropertyName(propertyKey, vocabularyKey);
                for (int i = 0; i < properties.get(propertyKey).getAspects().length; i++) {
                    PropertyRef propertyRef = new ODDRPropertyRef(propertyName, properties.get(propertyKey).getAspects()[i]);
                    if (ASPECT_DEVICE.equals(propertyRef.getAspectName())) {
                        if (!deviceIdentified) {
                            if (deviceUA == null) {
                                deviceUA = UserAgentFactory.newDeviceUserAgent(evdnc);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                deviceFound = ((BufferedODDRHTTPEvidence) evdnc).getDeviceFound();
                            }
                            if (deviceFound == null) {
                                deviceFound = deviceIdentificator.get(deviceUA, this.threshold);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                ((BufferedODDRHTTPEvidence) evdnc).setDeviceFound(deviceFound);
                            }
                            deviceIdentified = true;
                        }
                        String property = null;
                        if (deviceFound != null) {
                            property = deviceFound.get(propertyRef.getLocalPropertyName());
                            ret.addProperty(new ODDRPropertyValue(property, properties.get(propertyKey).getType(), propertyRef));

                        } else {
                            ret.addProperty(new ODDRPropertyValue(null, properties.get(propertyKey).getType(), propertyRef));
                        }
                        continue;

                    } else if (ASPECT_WEB_BROWSER.equals(propertyRef.getAspectName())) {
                        if (!browserIdentified) {
                            if (browserUA == null) {
                                browserUA = UserAgentFactory.newBrowserUserAgent(evdnc);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                browserFound = ((BufferedODDRHTTPEvidence) evdnc).getBrowserFound();
                            }
                            if (browserFound == null) {
                                browserFound = browserIdentificator.get(browserUA, this.threshold);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                ((BufferedODDRHTTPEvidence) evdnc).setBrowserFound(browserFound);
                            }

                            browserIdentified = true;
                        }
                        String property = null;
                        if (browserFound != null) {
                            property = browserFound.get(propertyRef.getLocalPropertyName());
                            ret.addProperty(new ODDRPropertyValue(property, properties.get(propertyKey).getType(), propertyRef));

                        } else {
                            ret.addProperty(new ODDRPropertyValue(null, properties.get(propertyKey).getType(), propertyRef));
                        }
                        continue;

                    } else if (ASPECT_OPERATING_SYSTEM.equals(propertyRef.getAspectName())) {
                        if (!osIdentified) {
                            if (deviceUA == null) {
                                deviceUA = UserAgentFactory.newDeviceUserAgent(evdnc);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                osFound = ((BufferedODDRHTTPEvidence) evdnc).getOsFound();
                            }
                            if (osFound == null) {
                                osFound = osIdentificator.get(deviceUA, this.threshold);
                            }
                            if (evdnc instanceof BufferedODDRHTTPEvidence) {
                                ((BufferedODDRHTTPEvidence) evdnc).setOsFound(osFound);
                            }

                            osIdentified = true;
                        }
                        String property = null;
                        if (osFound != null) {
                            property = osFound.get(propertyRef.getLocalPropertyName());
                            ret.addProperty(new ODDRPropertyValue(property, properties.get(propertyKey).getType(), propertyRef));

                        } else {
                            ret.addProperty(new ODDRPropertyValue(null, properties.get(propertyKey).getType(), propertyRef));
                        }
                        continue;

                    } else if (ASPECT_GROUP.equals(propertyRef.getAspectName())) {
                        try {
                            String jexlExp = properties.get(propertyKey).getExpr();
                            Matcher m = groupRegexprPattern.matcher(jexlExp);
                            while (m.find()) {
                                String id = m.group(1);
                                String propertyValueString = null;
                                PropertyValue propertyValue = getPropertyValue(evdnc, vocabulary.getVocabularyVariables().get(id).getName(), vocabulary.getVocabularyVariables().get(id).getAspect(), vocabulary.getVocabularyVariables().get(id).getVocabulary());
                                propertyValueString = (propertyValue.exists() ? propertyValue.getString() : "-");
                                String toReplace = "$" + id;
                                jexlExp = jexlExp.replaceAll(Matcher.quoteReplacement(toReplace), "'" + propertyValueString + "'");
                            }
                            Expression e = jexl.createExpression(jexlExp);
                            JexlContext jc = new MapContext();
                            Object o = e.evaluate(jc);
                            ret.addProperty(new ODDRPropertyValue(o.toString(), properties.get(propertyKey).getType(), propertyRef));

                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }

        return ret;
    }

    public PropertyValues getPropertyValues(Evidence evdnc, PropertyRef[] prs) throws NameException {
        Device deviceFound = null;
        Browser browserFound = null;
        OperatingSystem osFound = null;
        boolean deviceIdentified = false;
        boolean browserIdentified = false;
        boolean osIdentified = false;
        UserAgent deviceUA = null;
        UserAgent browserUA = null;

        JexlEngine jexl = new JexlEngine();
        ODDRPropertyValues ret = new ODDRPropertyValues();
        Map<String, Vocabulary> vocabularies = vocabularyHolder.getVocabularies();

        for (PropertyRef propertyRef : prs) {
            VocabularyProperty vocabularyProperty = vocabularyHolder.existProperty(propertyRef.getLocalPropertyName(), propertyRef.getAspectName(), propertyRef.getNamespace());
            Vocabulary vocabulary = vocabularies.get(propertyRef.getNamespace());
            if (ASPECT_DEVICE.equals(propertyRef.getAspectName())) {
                if (!deviceIdentified) {
                    if (deviceUA == null) {
                        deviceUA = UserAgentFactory.newDeviceUserAgent(evdnc);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        deviceFound = ((BufferedODDRHTTPEvidence) evdnc).getDeviceFound();
                    }
                    if (deviceFound == null) {
                        deviceFound = deviceIdentificator.get(deviceUA, this.threshold);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        ((BufferedODDRHTTPEvidence) evdnc).setDeviceFound(deviceFound);
                    }

                    deviceIdentified = true;
                }
                String property = null;

                if (deviceFound != null) {
                    property = deviceFound.get(propertyRef.getLocalPropertyName());
                    ret.addProperty(new ODDRPropertyValue(property, vocabularyProperty.getType(), propertyRef));

                } else {
                    ret.addProperty(new ODDRPropertyValue(null, vocabularyProperty.getType(), propertyRef));
                }
                continue;

            } else if (ASPECT_WEB_BROWSER.equals(propertyRef.getAspectName())) {
                //TODO: evaluate ua-pixels header in evidence
                if (!browserIdentified) {
                    if (browserUA == null) {
                        browserUA = UserAgentFactory.newBrowserUserAgent(evdnc);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        browserFound = ((BufferedODDRHTTPEvidence) evdnc).getBrowserFound();
                    }
                    if (browserFound == null) {
                        browserFound = browserIdentificator.get(browserUA, this.threshold);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        ((BufferedODDRHTTPEvidence) evdnc).setBrowserFound(browserFound);
                    }
                    browserIdentified = true;
                }
                String property = null;
                if (browserFound != null) {
                    property = browserFound.get(propertyRef.getLocalPropertyName());
                    ret.addProperty(new ODDRPropertyValue(property, vocabularyProperty.getType(), propertyRef));

                } else {
                    ret.addProperty(new ODDRPropertyValue(null, vocabularyProperty.getType(), propertyRef));
                }
                continue;

            } else if (ASPECT_OPERATING_SYSTEM.equals(propertyRef.getAspectName())) {
                //TODO: evaluate ua-os header in evidence
                if (!osIdentified) {
                    if (deviceUA == null) {
                        deviceUA = UserAgentFactory.newDeviceUserAgent(evdnc);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        osFound = ((BufferedODDRHTTPEvidence) evdnc).getOsFound();
                    }
                    if (osFound == null) {
                        osFound = osIdentificator.get(deviceUA, this.threshold);
                    }
                    if (evdnc instanceof BufferedODDRHTTPEvidence) {
                        ((BufferedODDRHTTPEvidence) evdnc).setOsFound(osFound);
                    }
                    osIdentified = true;
                }
                String property = null;
                if (osFound != null) {
                    property = osFound.get(propertyRef.getLocalPropertyName());
                    ret.addProperty(new ODDRPropertyValue(property, vocabularyProperty.getType(), propertyRef));

                } else {
                    ret.addProperty(new ODDRPropertyValue(null, vocabularyProperty.getType(), propertyRef));
                }
                continue;

            } else if (ASPECT_GROUP.equals(propertyRef.getAspectName())) {
                try {
                    String jexlExp = vocabularyProperty.getExpr();
                    Matcher m = groupRegexprPattern.matcher(jexlExp);
                    while (m.find()) {
                        String id = m.group(1);
                        String propertyValueString = null;
                        PropertyValue propertyValue = getPropertyValue(evdnc, vocabulary.getVocabularyVariables().get(id).getName(), vocabulary.getVocabularyVariables().get(id).getAspect(), vocabulary.getVocabularyVariables().get(id).getVocabulary());
                        propertyValueString = (propertyValue.exists() ? propertyValue.getString() : "-");
                        String toReplace = "$" + id;
                        jexlExp = jexlExp.replaceAll(Matcher.quoteReplacement(toReplace), "'" + propertyValueString + "'");
                    }
                    Expression e = jexl.createExpression(jexlExp);
                    JexlContext jc = new MapContext();
                    Object o = e.evaluate(jc);
                    ret.addProperty(new ODDRPropertyValue(o.toString(), vocabularyProperty.getType(), propertyRef));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return ret;
    }

    public PropertyValues getPropertyValues(Evidence evdnc, String localAspectName) throws NameException {
        return getPropertyValues(evdnc, localAspectName, defaultVocabularyIRI);
    }

    public PropertyValues getPropertyValues(Evidence evdnc, String localAspectName, String vocabularyIRI) throws NameException {
        VocabularyProperty vocabularyProperty = vocabularyHolder.existProperty(localAspectName, null, vocabularyIRI);

        PropertyName propertyName = new ODDRPropertyName(localAspectName, vocabularyIRI);
        PropertyRef propertyRef = new ODDRPropertyRef(propertyName, vocabularyProperty.getDefaultAspect());

        return getPropertyValues(evdnc, new PropertyRef[]{propertyRef});
    }

    public PropertyName newPropertyName(String localPropertyName, String vocabularyIRI) throws NameException {
        vocabularyHolder.existProperty(localPropertyName, null, vocabularyIRI);
        return new ODDRPropertyName(localPropertyName, vocabularyIRI);
    }

    public PropertyName newPropertyName(String localPropertyName) throws NameException {
        return newPropertyName(localPropertyName, defaultVocabularyIRI);
    }

    public PropertyRef newPropertyRef(PropertyName pn, String localAspectName) throws NameException {
        vocabularyHolder.existProperty(pn.getLocalPropertyName(), localAspectName, pn.getNamespace());
        return new ODDRPropertyRef(pn, localAspectName);
    }

    public PropertyRef newPropertyRef(PropertyName pn) throws NameException {
        VocabularyProperty vocabularyProperty = vocabularyHolder.existProperty(pn.getLocalPropertyName(), null, pn.getNamespace());
        return newPropertyRef(pn, vocabularyProperty.getDefaultAspect());
    }

    public PropertyRef newPropertyRef(String localPropertyName) throws NameException {
        return newPropertyRef(newPropertyName(localPropertyName));
    }

    public Evidence newHTTPEvidence() {
        return new ODDRHTTPEvidence();
    }

    public Evidence newHTTPEvidence(Map<String, String> map) {
        return new ODDRHTTPEvidence(map);
    }
}
