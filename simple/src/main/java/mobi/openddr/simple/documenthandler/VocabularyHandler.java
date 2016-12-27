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
package mobi.openddr.simple.documenthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mobi.openddr.simple.model.vocabulary.Vocabulary;
import mobi.openddr.simple.model.vocabulary.VocabularyProperty;
import mobi.openddr.simple.model.vocabulary.VocabularyVariable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyHandler extends DefaultHandler {

    private static final String ELEMENT_VOCABULARY_DESCRIPTION = "VocabularyDescription";
    private static final String ELEMENT_ASPECTS = "Aspects";
    private static final String ELEMENT_ASPECT = "Aspect";
    private static final String ELEMENT_VARIABLES = "Variables";
    private static final String ELEMENT_VARIABLE = "Variable";
    private static final String ELEMENT_PROPERTIES = "Properties";
    private static final String ELEMENT_PROPERTY = "Property";
    private static final String ATTRIBUTE_PROPERTY_TARGET = "target";
    private static final String ATTRIBUTE_PROPERTY_ASPECT_NAME = "name";
    private static final String ATTRIBUTE_PROPERTY_ASPECT = "aspect";
    private static final String ATTRIBUTE_PROPERTY_NAME = "name";
    private static final String ATTRIBUTE_PROPERTY_VOCABULARY = "vocabulary";
    private static final String ATTRIBUTE_PROPERTY_ID = "id";
    private static final String ATTRIBUTE_PROPERTY_DATA_TYPE = "datatype";
    private static final String ATTRIBUTE_PROPERTY_EXPR = "expr";
    private static final String ATTRIBUTE_PROPERTY_ASPECTS = "aspects";
    private static final String ATTRIBUTE_PROPERTY_DEFAULT_ASPECT = "defaultAspect";
    private Vocabulary vocabulary = null;
    private String aspect = null;
    private List<String> aspects = null;
    private VocabularyProperty vocabularyProperty = null;
    private Map<String, VocabularyProperty> vocabularyProperties = null;
    private Map<String, VocabularyVariable> vocabularyVariables = null;
    private VocabularyVariable vocabularyVariable = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (ELEMENT_VOCABULARY_DESCRIPTION.equals(name)) {
            startVocabularyDescription(attributes);

        } else if (ELEMENT_ASPECTS.equals(name)) {
            startAspects(attributes);

        } else if (ELEMENT_ASPECT.equals(name)) {
            startAspect(attributes);

        } else if (ELEMENT_VARIABLES.equals(name)) {
            startVariables(attributes);

        } else if (ELEMENT_VARIABLE.equals(name)) {
            startVariable(attributes);

        } else if (ELEMENT_PROPERTIES.equals(name)) {
            startProperties(attributes);

        } else if (ELEMENT_PROPERTY.equals(name)) {
            startProperty(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (ELEMENT_ASPECT.equals(name)) {
            endAspectElement();

        } else if (ELEMENT_ASPECTS.equals(name)) {
            endAspectsElement();

        } else if (ELEMENT_VARIABLES.equals(name)) {
            endVariablesElement();

        } else if (ELEMENT_VARIABLE.equals(name)) {
            endVariableElement();
            
        } else if (ELEMENT_PROPERTIES.equals(name)) {
            endProperties();

        } else if (ELEMENT_PROPERTY.equals(name)) {
            endProperty();
        }
    }

    private void startVocabularyDescription(Attributes attributes) {
        vocabulary = new Vocabulary();
        vocabulary.setVocabularyIRI(attributes.getValue(ATTRIBUTE_PROPERTY_TARGET));
    }

    private void startAspects(Attributes attributes) {
        aspects = new ArrayList<String>();
    }

    private void startAspect(Attributes attributes) {
        aspect = attributes.getValue(ATTRIBUTE_PROPERTY_ASPECT_NAME);
        if (!aspects.contains(aspect)) {
            aspects.add(aspect);
        }
    }

    private void startVariables(Attributes attributes) {
        vocabularyVariables = new HashMap<String, VocabularyVariable>();
    }

    private void startVariable(Attributes attributes) {
        vocabularyVariable = new VocabularyVariable();
        vocabularyVariable.setAspect(attributes.getValue(ATTRIBUTE_PROPERTY_ASPECT));
        vocabularyVariable.setId(attributes.getValue(ATTRIBUTE_PROPERTY_ID));
        vocabularyVariable.setName(attributes.getValue(ATTRIBUTE_PROPERTY_NAME));
        vocabularyVariable.setVocabulary(attributes.getValue(ATTRIBUTE_PROPERTY_VOCABULARY));
        if (!vocabularyVariables.containsKey(vocabularyVariable.getId())) {
            vocabularyVariables.put(vocabularyVariable.getId(), vocabularyVariable);
        }
    }

    private void endAspectElement() {
        aspect = null;
    }

    private void endAspectsElement() {
        String[] aspectsArray = new String[aspects.size()];
        vocabulary.setAspects(aspects.toArray(aspectsArray));
        aspects = null;
    }

    private void endVariableElement() {
        vocabularyVariable = null;
    }

    private void endVariablesElement() {
        vocabulary.setVocabularyVariables(vocabularyVariables);
        vocabularyVariables = null;
    }

    private void startProperties(Attributes attributes) {
        vocabularyProperties = new HashMap<String, VocabularyProperty>();
    }

    private void startProperty(Attributes attributes) {
        vocabularyProperty = new VocabularyProperty();
        String[] aspectsArray = attributes.getValue(ATTRIBUTE_PROPERTY_ASPECTS).split(",");

        for (int i = 0; i < aspectsArray.length; i++) {
            aspectsArray[i] = aspectsArray[i].trim();
        }

        vocabularyProperty.setAspects(aspectsArray);
        vocabularyProperty.setDefaultAspect(attributes.getValue(ATTRIBUTE_PROPERTY_DEFAULT_ASPECT));
        vocabularyProperty.setExpr(attributes.getValue(ATTRIBUTE_PROPERTY_EXPR));
        vocabularyProperty.setName(attributes.getValue(ATTRIBUTE_PROPERTY_NAME));
        vocabularyProperty.setType(attributes.getValue(ATTRIBUTE_PROPERTY_DATA_TYPE));
    }

    private void endProperty() {
        vocabularyProperties.put(vocabularyProperty.getName(), vocabularyProperty);
        vocabularyProperty = null;
    }

    private void endProperties() {
        vocabulary.setProperties(vocabularyProperties);
    }

    @Override
    public void endDocument() throws SAXException {
        vocabularyProperties = null;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}
