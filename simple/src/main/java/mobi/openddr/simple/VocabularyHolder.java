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

import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import mobi.openddr.simple.cache.Cache;
import mobi.openddr.simple.cache.CacheImpl;
import mobi.openddr.simple.model.vocabulary.Vocabulary;
import mobi.openddr.simple.model.vocabulary.VocabularyProperty;
import org.w3c.ddr.simple.exception.NameException;

public class VocabularyHolder {

    private Map<String, Vocabulary> vocabularies = null;
    private Cache vocabularyPropertyCache = new CacheImpl();

    public VocabularyHolder(Map<String, Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public void existVocabulary(String vocabularyIRI) throws NameException {
        if (vocabularies.get(vocabularyIRI) == null) {
            throw new NameException(NameException.VOCABULARY_NOT_RECOGNIZED, "unknow \"" + vocabularyIRI + "\" vacabulary");
        }
    }

    public VocabularyProperty existProperty(String propertyName, String aspect, String vocabularyIRI) throws NameException {
        String realAspect = aspect;
        VocabularyProperty vocabularyProperty = (VocabularyProperty) vocabularyPropertyCache.getCachedElement(propertyName + aspect + vocabularyIRI);

        if (vocabularyProperty == null) {
            if (vocabularies.get(vocabularyIRI) != null) {
                Map<String, VocabularyProperty> propertyMap = vocabularies.get(vocabularyIRI).getProperties();
                vocabularyProperty = propertyMap.get(propertyName);

                if (vocabularyProperty != null) {
                    if (realAspect != null && realAspect.trim().length() > 0) {
                        if (ArrayUtils.contains(vocabularyProperty.getAspects(), realAspect)) {
                            vocabularyPropertyCache.setCachedElement(propertyName + aspect + vocabularyIRI, vocabularyProperty);
                            return vocabularyProperty;

                        } else {
                            throw new NameException(NameException.ASPECT_NOT_RECOGNIZED, "unknow \"" + realAspect + "\" aspect");
                        }

                    } else {
                        return vocabularyProperty;
                    }

                } else {
                    throw new NameException(NameException.PROPERTY_NOT_RECOGNIZED, "unknow \"" + propertyName + "\" property");
                }

            } else {
                throw new NameException(NameException.VOCABULARY_NOT_RECOGNIZED, "unknow \"" + vocabularyIRI + "\" vacabulary");
            }

        } else {
            return vocabularyProperty;
        }
    }

    public Map<String, Vocabulary> getVocabularies() {
        return vocabularies;
    }
}
