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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mobi.openddr.simple.documenthandler.VocabularyHandler;
import mobi.openddr.simple.model.vocabulary.Vocabulary;
import org.w3c.ddr.simple.exception.InitializationException;
import org.xml.sax.SAXException;

/**
 * 
 * @author Werner Keil
 *
 */
public class VocabularyService {

    public static final String DDR_CORE_VOCABULARY_PATH_PROP = "ddr.vocabulary.core.path";
    public static final String ODDR_VOCABULARY_PATH_PROP = "oddr.vocabulary.path";
    public static final String ODDR_LIMITED_VOCABULARY_PATH_PROP = "oddr.limited.vocabulary.path";
    public static final String DDR_CORE_VOCABULARY_STREAM_PROP = "ddr.vocabulary.core.stream";
    public static final String ODDR_VOCABULARY_STREAM_PROP = "oddr.vocabulary.stream";
    public static final String ODDR_LIMITED_VOCABULARY_STREAM_PROP = "oddr.limited.vocabulary.stream";
    public static final String ODDR_LIMITED_VOCABULARY_IRI = "limitedVocabulary";
    private VocabularyHolder vocabularyHolder = null;

    public void initialize(Properties props) throws InitializationException {
        Map<String, Vocabulary> vocabularies = new HashMap<String, Vocabulary>();

        String ddrCoreVocabularyPath = props.getProperty(DDR_CORE_VOCABULARY_PATH_PROP);
        String oddrVocabularyPath = props.getProperty(ODDR_VOCABULARY_PATH_PROP);

        InputStream ddrCoreVocabulayStream = null;
        InputStream[] oddrVocabularyStream = null;
        try {
            ddrCoreVocabulayStream = (InputStream) props.get(DDR_CORE_VOCABULARY_STREAM_PROP);
        } catch (Exception ex) {
            ddrCoreVocabulayStream = null;
        }
        try {
            oddrVocabularyStream = (InputStream[]) props.get(ODDR_VOCABULARY_STREAM_PROP);
        } catch (Exception ex) {
            oddrVocabularyStream = null;
        }

        if ((ddrCoreVocabularyPath == null || ddrCoreVocabularyPath.trim().length() == 0) && ddrCoreVocabulayStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + DDR_CORE_VOCABULARY_PATH_PROP));
        }

        if ((oddrVocabularyPath == null || oddrVocabularyPath.trim().length() == 0) && oddrVocabularyStream == null) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not find property " + ODDR_VOCABULARY_PATH_PROP));
        }

        VocabularyHandler vocabularyHandler = new VocabularyHandler();
        Vocabulary vocabulary = null;

        if (ddrCoreVocabulayStream != null) {
            parseVocabularyFromStream(vocabularyHandler, DDR_CORE_VOCABULARY_STREAM_PROP, ddrCoreVocabulayStream);
        } else {
            parseVocabularyFromPath(vocabularyHandler, DDR_CORE_VOCABULARY_PATH_PROP, ddrCoreVocabularyPath);
        }
        vocabulary = vocabularyHandler.getVocabulary();
        vocabularies.put(vocabulary.getVocabularyIRI(), vocabulary);

        if (oddrVocabularyStream != null) {
            for (InputStream stream : oddrVocabularyStream) {
                vocabularyHandler = new VocabularyHandler();
                parseVocabularyFromStream(vocabularyHandler, ODDR_VOCABULARY_STREAM_PROP, stream);
                vocabulary = vocabularyHandler.getVocabulary();
                vocabularies.put(vocabulary.getVocabularyIRI(), vocabulary);
            }
        } else {
            String[] oddrVocabularyPaths = oddrVocabularyPath.split(",");
            for (int i = 0; i < oddrVocabularyPaths.length; i++) {
                oddrVocabularyPaths[i] = oddrVocabularyPaths[i].trim();
            }
            for (String oddVocabularyString : oddrVocabularyPaths) {
                vocabularyHandler = new VocabularyHandler();
                parseVocabularyFromPath(vocabularyHandler, ODDR_VOCABULARY_PATH_PROP, oddVocabularyString);
                vocabulary = vocabularyHandler.getVocabulary();
                vocabularies.put(vocabulary.getVocabularyIRI(), vocabulary);
            }
        }

        String oddrLimitedVocabularyPath = props.getProperty(ODDR_LIMITED_VOCABULARY_PATH_PROP);
        InputStream oddrLimitedVocabularyStream = (InputStream)props.get(ODDR_LIMITED_VOCABULARY_STREAM_PROP);

        if (oddrLimitedVocabularyStream != null) {
            parseVocabularyFromStream(vocabularyHandler, ODDR_LIMITED_VOCABULARY_STREAM_PROP, oddrLimitedVocabularyStream);

        } else {
           if (oddrLimitedVocabularyPath != null && oddrLimitedVocabularyPath.trim().length() != 0) {
                vocabularyHandler = new VocabularyHandler();
                parseVocabularyFromPath(vocabularyHandler, ODDR_LIMITED_VOCABULARY_PATH_PROP, oddrLimitedVocabularyPath);
            }
        }
        vocabulary = vocabularyHandler.getVocabulary();
        vocabularies.put(ODDR_LIMITED_VOCABULARY_IRI, vocabulary);

        vocabularyHolder = new VocabularyHolder(vocabularies);

        vocabularyHandler = null;
        vocabularies = null;

    }

    private void parseVocabularyFromPath(VocabularyHandler vocabularyHandler, String prop, String path) throws InitializationException {
        InputStream stream = null;
        SAXParser parser = null;

        try {
            stream = new FileInputStream(new File(path));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalArgumentException("Can not open " + prop + " : " + path));
        }

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, vocabularyHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document: " + path));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + prop + " : " + path));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            Logger.getLogger(DDRService.class.getName()).log(Level.WARNING, null, ex);
        }

        parser = null;
    }

    private void parseVocabularyFromStream(VocabularyHandler vocabularyHandler, String prop, InputStream inputStream) throws InitializationException {
        InputStream stream = null;
        SAXParser parser = null;
        stream = inputStream;

        try {
            parser = SAXParserFactory.newInstance().newSAXParser();

        } catch (ParserConfigurationException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new IllegalStateException("Can not instantiate SAXParserFactory.newInstance().newSAXParser()"));
        }

        try {
            parser.parse(stream, vocabularyHandler);

        } catch (SAXException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not parse document in property: " + prop));

        } catch (IOException ex) {
            throw new InitializationException(InitializationException.INITIALIZATION_ERROR, new RuntimeException("Can not open " + prop));
        }

        try {
            stream.close();

        } catch (IOException ex) {
            Logger.getLogger(DDRService.class.getName()).log(Level.WARNING, null, ex);
        }

        parser = null;
    }

    public VocabularyHolder getVocabularyHolder() {
        return vocabularyHolder;
    }
}
