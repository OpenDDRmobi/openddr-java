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
package test;

import org.w3c.ddr.simple.*;
import org.w3c.ddr.simple.exception.*;

public class DDRSimpleAPITester {
  
  // (localPropertyKnownString,localAspectKnownString) is in the database with value localKnownStringValue
  // (localPropertyUnknownString,localAspectUnknownString) is not in the database
  
  private String   vocabularyIRI;
  private String   aspect1;
  private String   aspect2;
  private int      totalPropertRefsInService;
  
  private String   localPropertyKnownString;
  private String   localPropertyUnknownString;
  private String   localAspectKnownString;
  private String   localAspectUnknownString;
  private String   localKnownStringValue;
  
  private String   localPropertyKnownInteger;
  private String   localAspectKnownInteger;
  private int      localKnownIntegerValue;
  
  private String   localPropertyKnownBoolean;
  private String   localAspectKnownBoolean;
  private boolean  localKnownBooleanValue;
  
  private String   localPropertyKnownEnumeration;
  private String   localAspectKnownEnumeration;
  private String[] localKnownEnumerationValue;

  private java.util.Hashtable report;
  
  private org.w3c.ddr.simple.Service s;
  private org.w3c.ddr.simple.Evidence e;
  
  /**
   * A test class for the DDR Simple API.
   * This is a simple text of an instance of org.w3c.ddr.simple.Service such that all of the methods of
   * the DDR Simple API are exercised at least once and their results compared against the expected results.
   * The tests make no statement about the validity of an implementation, the performance of an
   * implementation or the correctness of the data to which the Service is an interface.
   * An implementation must be able to support more than one vocabulary, and it is expected that a
   * conformant implementation would be able to provide at least one vocabulary and associated data so
   * that the tests represented by this class can be passed.
   * The motivation for this class is to provide a common independent means of supporting a claim
   * of conformance to the DDR Simple API for any implementation in Java.
   * Similar tests may be devised for other implementation languages.
   * 
   * @param s An instance of the Service implementation that has already been initialised.
   * @param e An instance of Evidence for which several properties are known to the repository instance.
   * @param vocabularyIRI The IRI of the vocabulary used in this test, which must have at least two aspects.
   * @param aspect1 An aspect of the vocabulary.
   * @param aspect2 An aspect of the vocabulary.
   * @param totalPropertiesRepresentedInVocabulary The total number of property/aspect in all vocabularies known to the Service.
   * @param localPropertyKnownString The local property name X of a String property whose value is known in the repository.
   * @param localPropertyUnknownString The local property name Y of a String property whose value is unknown in the repository.
   * @param localAspectKnownString The aspect of property X whose value is known.
   * @param localAspectUnknownString The aspect of property Y whose value is unknown.
   * @param localKnownStringValue The value for the property X that is known in the repository.
   * @param localPropertyKnownInteger The local property name for an Integer known in the repository.
   * @param localAspectKnownInteger The aspect of the Integer that is known in the repository.
   * @param localKnownIntegerValue The value of the Integer that is known in the repository.
   * @param localPropertyKnownBoolean The local property bane for a Boolean known in the repository.
   * @param localAspectKnownBoolean The aspect of the Boolean known in the repository.
   * @param localKnownBooleanValue The value of the Boolean known in the repository.
   * @param localPropertyKnownEnumeration The local property name of an Enumeration known in the repository.
   * @param localAspectKnownEnumeration The aspect of the Enumeration known in the repository.
   * @param localKnownEnumerationValue The value if the Enumeration known in the repository.
   */
  public DDRSimpleAPITester(
      org.w3c.ddr.simple.Service s, org.w3c.ddr.simple.Evidence e,
      String vocabularyIRI, String aspect1, String aspect2, int totalPropertiesRepresentedInVocabulary,
      // For the test, localAspectKnownString must be the default aspect for localPropertyKnownString
      String localPropertyKnownString, String localPropertyUnknownString, String localAspectKnownString, String localAspectUnknownString, String localKnownStringValue,
      String localPropertyKnownInteger, String localAspectKnownInteger, int localKnownIntegerValue,
      String localPropertyKnownBoolean, String localAspectKnownBoolean, boolean localKnownBooleanValue,
      String localPropertyKnownEnumeration, String localAspectKnownEnumeration, String[] localKnownEnumerationValue
      ) {
    this.s = s; this.e = e;
    this.vocabularyIRI = vocabularyIRI; this.aspect1 = aspect1; this.aspect2 = aspect2;
    this.totalPropertRefsInService = totalPropertiesRepresentedInVocabulary;
    this.localPropertyKnownString      = localPropertyKnownString;
    this.localPropertyUnknownString    = localPropertyUnknownString;
    this.localAspectKnownString        = localAspectKnownString;
    this.localAspectUnknownString      = localAspectUnknownString;
    this.localKnownStringValue         = localKnownStringValue;
    this.localPropertyKnownInteger     = localPropertyKnownInteger;
    this.localAspectKnownInteger       = localAspectKnownInteger;
    this.localKnownIntegerValue        = localKnownIntegerValue;
    this.localPropertyKnownBoolean     = localPropertyKnownBoolean;
    this.localAspectKnownBoolean       = localAspectKnownBoolean;
    this.localKnownBooleanValue        = localKnownBooleanValue;
    this.localPropertyKnownEnumeration = localPropertyKnownEnumeration;
    this.localAspectKnownEnumeration   = localAspectKnownEnumeration;
    this.localKnownEnumerationValue    = localKnownEnumerationValue;
  }
  
  private void setReport(boolean status, String testID) {
    report.put(testID, status?"Pass":"FAIL"); // This will cause [unchecked] warnings in Java 1.5+. OK to ignore.
  }
  
  private void Clear() {
    report = new java.util.Hashtable();
  }
  
  private void Clear(String testID) {
    if (report == null) {
      Clear();
    }
    report.put(testID, "????"); // This will cause [unchecked] warnings in Java 1.5+. OK to ignore.
  }

  public java.util.Hashtable getReport() {
    Clear();
    Clear("#sec-Evidence");
    Clear("#sec-Evidence-get");
    Clear("#sec-Evidence-exists");
    Clear("#sec-Evidence-put");
    
    Clear("#sec-PropertyName");
    Clear("#sec-PropertyName-getLocalPropertyName");
    Clear("#sec-PropertyName-getNamespace");
    
    Clear("#sec-PropertyRef");
    Clear("#sec-PropertyRef-getLocalPropertyName");
    Clear("#sec-PropertyRef-getAspectName");
    Clear("#sec-PropertyRef-getNamespace");
    
    Clear("#sec-PropertyValue");
    Clear("#sec-PropertyValue-getXXX Double");       // Untested
    Clear("#sec-PropertyValue-getXXX Long");         // Untested
    Clear("#sec-PropertyValue-getXXX String");
    Clear("#sec-PropertyValue-getXXX Boolean");
    Clear("#sec-PropertyValue-getXXX Integer");
    Clear("#sec-PropertyValue-getXXX Enumeration");
    Clear("#sec-PropertyValue-getXXX Float");        // Untested
    Clear("#sec-PropertyValue-exists");
    Clear("#sec-PropertyValue-getPropertyRef");
    
    Clear("#sec-PropertyValues");
    Clear("#sec-PropertyValues-getAll");
    Clear("#sec-PropertyValues-getValue");
    
    Clear("#sec-Service");
    Clear("#sec-Service-newHTTPEvidence-1");
    Clear("#sec-Service-newHTTPEvidence-2");
    Clear("#sec-Service-newPropertyName-1");
    Clear("#sec-Service-newPropertyName-2");
    Clear("#sec-Service-newPropertyRef-1");
    Clear("#sec-Service-newPropertyRef-2");
    Clear("#sec-Service-newPropertyRef-3");
    Clear("#sec-Service-getPropertyValues-1");
    Clear("#sec-Service-getPropertyValues-2");
    Clear("#sec-Service-getPropertyValues-3");
    Clear("#sec-Service-getPropertyValues-4");
    Clear("#sec-Service-getPropertyValue-1");
    Clear("#sec-Service-getPropertyValue-2");
    Clear("#sec-Service-getPropertyValue-3");
    Clear("#sec-Service-getPropertyValue-4");
    Clear("#sec-Service-getImplementationVersion");
    Clear("#sec-Service-getDataVersion");
    Clear("#sec-Service-listPropertyRefs");
    Clear("#sec-Service-initialize");

    try {

      // Service tests included in other tests
      boolean factoryCreatedEvidence = false;
      boolean factoryCreatedPropertyName = false;
      boolean factoryCreatedPropertyRef = false;
      boolean obtainedPropertyValueInstance = false;
      boolean obtainedPropertyValuesInstance = false;

      
      // Evidence
      {
        boolean putCausedNoException = false;
        boolean getReturnedCorrectValue = false;
        boolean getDoesNotKnowUnputs = false;
        boolean existsWorks = false;
        
        Evidence evidence = s.newHTTPEvidence();
        factoryCreatedEvidence = (evidence != null && evidence instanceof Evidence);
        evidence.put("TestHeader", "TestHeaderValue");
        putCausedNoException = true;
        getReturnedCorrectValue = evidence.get("TestHeader").equals("TestHeaderValue");
        existsWorks = evidence.exists("TestHeader") && !evidence.exists("UnknownHeader");
        try {
          getDoesNotKnowUnputs = (evidence.get("UnknownHeader") == null || "".equals(evidence.get("UnknownHeader")));
        }
        catch (Exception exc) {
          getDoesNotKnowUnputs = true; // Throwing an exception is also a valid response to not finding the header
        }
        setReport(factoryCreatedEvidence, "#sec-Service-newHTTPEvidence-1");
        setReport(putCausedNoException,"#sec-Evidence-put"); // Only need to be sure put() didn't cause an exception
        setReport(getReturnedCorrectValue && getDoesNotKnowUnputs,"#sec-Evidence-get");
        setReport(existsWorks,"#sec-Evidence-exists");
        setReport(putCausedNoException && getReturnedCorrectValue && getDoesNotKnowUnputs && existsWorks,"#sec-Evidence");
      }
      
      // PropertyName
      {
        boolean knowsName = false;
        boolean knowsNamespace = false;
        
        PropertyName propertyName = s.newPropertyName(localPropertyKnownString, vocabularyIRI);
        factoryCreatedPropertyName = true;
        knowsName = localPropertyKnownString.equals(propertyName.getLocalPropertyName());
        knowsNamespace = vocabularyIRI.equals(propertyName.getNamespace());
        
        setReport(factoryCreatedPropertyName,"#sec-Service-newPropertyName-2");
        setReport(knowsName, "#sec-PropertyName-getLocalPropertyName");
        setReport(knowsNamespace, "#sec-PropertyName-getNamespace");
        setReport(knowsName && knowsNamespace, "#sec-PropertyName");
      }
      
      // PropertyRef
      {
        boolean knowsName = false;
        boolean knowsNamespace = false;
        boolean knowsAspect = false;
        
        PropertyRef propertyRef = s.newPropertyRef(s.newPropertyName(localPropertyKnownString, vocabularyIRI), localAspectKnownString);
        factoryCreatedPropertyRef = (propertyRef != null && propertyRef instanceof PropertyRef);
        knowsName = localPropertyKnownString.equals(propertyRef.getLocalPropertyName());
        knowsAspect = localAspectKnownString.equals(propertyRef.getAspectName());
        knowsNamespace = vocabularyIRI.equals(propertyRef.getNamespace());
       
        setReport(factoryCreatedPropertyRef, "#sec-Service-newPropertyRef-3");
        setReport(knowsName, "#sec-PropertyRef-getLocalPropertyName");
        setReport(knowsAspect, "#sec-PropertyRef-getAspectName");
        setReport(knowsNamespace, "#sec-PropertyRef-getNamespace");
        setReport(knowsName && knowsAspect && knowsNamespace,"#sec-PropertyRef");
      }
      
      // PropertyValue
      {
        boolean obtainedExistingValue = false;
        boolean detectedUnknownValue = false;
        boolean gotSomeStringRepresentation = false;
        boolean gotPropertyRef = false;
        boolean gotCorrectString = false;
        boolean gotCorrectInteger = false;
        boolean gotCorrectBoolean = false;
        boolean gotCorrectEnumeration = false;
        
        PropertyValue propertyValue = s.getPropertyValue(e, localPropertyKnownString, localAspectKnownString, vocabularyIRI);
        obtainedPropertyValueInstance = (propertyValue != null && propertyValue instanceof PropertyValue);
        obtainedExistingValue = propertyValue.exists();
        try {
          PropertyValue propertyValueBad = s.getPropertyValue(e, localPropertyUnknownString, localAspectUnknownString, vocabularyIRI);
        }
        catch (NameException nex) {
          detectedUnknownValue = true; // Implementations are free to use alternative codes, so just check for the exception
        }
        String stringRepresentation = propertyValue.getString();
        gotSomeStringRepresentation = (stringRepresentation != null && !stringRepresentation.equals(""));
        gotCorrectString = (stringRepresentation != null && stringRepresentation.equals(localKnownStringValue));
        PropertyRef propertyRef = propertyValue.getPropertyRef();
        gotPropertyRef = (
            propertyRef != null && propertyRef instanceof PropertyRef &&
            localPropertyKnownString.equals(propertyRef.getLocalPropertyName()) &&
            localAspectKnownString.equals(propertyRef.getAspectName()) &&
            vocabularyIRI.equals(propertyRef.getNamespace())
            );
        PropertyValue propertyValueInteger = s.getPropertyValue(e, localPropertyKnownInteger, localAspectKnownInteger, vocabularyIRI);
        gotCorrectInteger = (propertyValueInteger.getInteger() == localKnownIntegerValue);
        PropertyValue propertyValueBoolean = s.getPropertyValue(e, localPropertyKnownBoolean, localAspectKnownBoolean, vocabularyIRI);
        gotCorrectBoolean = (propertyValueBoolean.getBoolean() == localKnownBooleanValue);
        PropertyValue propertyValueEnumeration = s.getPropertyValue(e, localPropertyKnownEnumeration, localAspectKnownEnumeration, vocabularyIRI);
        String[] enumeratedValue = propertyValueEnumeration.getEnumeration();
        gotCorrectEnumeration = (enumeratedValue.length == localKnownEnumerationValue.length);
        if (gotCorrectEnumeration) {
          for (int i = 0; i < enumeratedValue.length; i++) {
            // Warning: the order of values in the enumeration is prescribed for this test,
            // although nothing has been said regarding the significance of any such ordering.
            gotCorrectEnumeration = gotCorrectEnumeration && localKnownEnumerationValue[i].equals(enumeratedValue[i]);
          }
        }
        
        // not tested: #sec-PropertyValue-getXXX Double
        // not tested: #sec-PropertyValue-getXXX Long
        // not tested: #sec-PropertyValue-getXXX Float
        setReport(obtainedExistingValue && gotCorrectString && gotSomeStringRepresentation, "#sec-PropertyValue-getXXX String");
        setReport(gotCorrectInteger, "#sec-PropertyValue-getXXX Integer");
        setReport(gotCorrectBoolean, "#sec-PropertyValue-getXXX Boolean");
        setReport(gotCorrectEnumeration, "#sec-PropertyValue-getXXX Enumeration");
        setReport(obtainedExistingValue && detectedUnknownValue,"#sec-PropertyValue-exists");
        setReport(gotPropertyRef,"#sec-PropertyValue-getPropertyRef");
        setReport(gotCorrectInteger && gotCorrectBoolean && gotCorrectEnumeration && gotPropertyRef, "#sec-PropertyValue");
        setReport(obtainedPropertyValueInstance, "#sec-Service-getPropertyValue-4");
      }
      
      // PropertyValues
      {
        boolean gotCorrectArray = false;
        boolean gotCorrectValue = false;
        
        PropertyRef[] propertyRefs = new PropertyRef[]{
          s.newPropertyRef(s.newPropertyName(localPropertyKnownString, vocabularyIRI), localAspectKnownString),
          s.newPropertyRef(s.newPropertyName(localPropertyKnownInteger, vocabularyIRI), localAspectKnownInteger),
          s.newPropertyRef(s.newPropertyName(localPropertyKnownBoolean, vocabularyIRI), localAspectKnownBoolean)
        };
        PropertyValues propertyValues = s.getPropertyValues(e, propertyRefs);
        obtainedPropertyValuesInstance = (propertyValues != null && propertyValues instanceof PropertyValues);
        PropertyValue[] propertyValueArray = propertyValues.getAll();
        gotCorrectArray = (
            propertyValueArray != null && propertyValueArray.length == 3 &&
            localKnownStringValue.equals(propertyValueArray[0].getString()) &&
            localKnownIntegerValue == propertyValueArray[1].getInteger() &&
            localKnownBooleanValue == propertyValueArray[2].getBoolean()
            );
        PropertyRef innerPropertyRef = s.newPropertyRef(
              s.newPropertyName(localPropertyKnownInteger, vocabularyIRI), localAspectKnownInteger
            );
        PropertyValue innerPropertyValue = propertyValues.getValue(innerPropertyRef);
        gotCorrectValue = (innerPropertyValue.getInteger() == localKnownIntegerValue);

        setReport(gotCorrectArray, "#sec-PropertyValues-getAll");
        setReport(gotCorrectValue, "#sec-PropertyValues-getValue");
        setReport(gotCorrectArray && gotCorrectValue, "#sec-PropertyValues");
        setReport(obtainedPropertyValuesInstance, "#sec-Service-getPropertyValues-4");
      }
      
      // Service
      // Tests cover features not already covered by previous tests
      {
        boolean gotListOfProperties = false;
        boolean evidenceViaMapOK = false;
        boolean propNameDefaultIRIOK = false;
        boolean propRefStringOK = false;
        boolean propRefPropNameOK = false;
        boolean propValuesEvidenceOK = false;
        boolean propValuesEvidenceAspectOK = false;
        boolean propValuesEvidenceAspectVocabOK = false;
        boolean propValueEvidencePropRefOK = false;
        boolean propValueEvidencePropNameOK = false;
        boolean propValueEvidenceNameOK = false;
        
        PropertyRef[] propertyRefArray = s.listPropertyRefs();
        if (propertyRefArray != null && propertyRefArray.length == totalPropertRefsInService) {
          gotListOfProperties = true;
        }

        // #sec-Service-newHTTPEvidence-2
        java.util.HashMap<String,String> map = new java.util.HashMap<String, String>();
        map.put("X-Header1", "HeaderValue1");
        map.put("X-Header2", "HeaderValue2");
        map.put("X-Header1", "HeaderValue3");
        Evidence e2 = s.newHTTPEvidence(map);
        if ("HeaderValue3".equals(e2.get("X-Header1")) && "HeaderValue2".equals(e2.get("X-Header2"))) {
          evidenceViaMapOK = e2.exists("X-Header1") && e2.exists("X-Header2") && !e2.exists("X-Header3");
        }
        
        PropertyName pn = s.newPropertyName(localPropertyKnownString, vocabularyIRI);
        PropertyRef pr = s.newPropertyRef(pn, localAspectKnownString);
        
        // #sec-Service-newPropertyName-1
        // Test assumes that default vocabulary was used in the class constructor.
        PropertyName pn1 = s.newPropertyName(localPropertyKnownString);
        propNameDefaultIRIOK = pn1.getLocalPropertyName().equals(localPropertyKnownString) && pn1.getNamespace().equals(vocabularyIRI);
        
        // #sec-Service-newPropertyRef-1
        // Test assumes that default vocabulary was used in the class constructor.
        PropertyRef pr1 = s.newPropertyRef(localPropertyKnownString);
        propRefStringOK = pr1.getLocalPropertyName().equals(localPropertyKnownString) &&
          pr1.getNamespace().equals(vocabularyIRI) && pr1.getAspectName().equals(PropertyRef.NULL_ASPECT);

        // #sec-Service-newPropertyRef-2
        PropertyRef pr2 = s.newPropertyRef(pn1);
        propRefPropNameOK = pr2.getLocalPropertyName().equals(localPropertyKnownString) &&
          pr2.getNamespace().equals(vocabularyIRI) && pr2.getAspectName().equals(PropertyRef.NULL_ASPECT);
        
        // #sec-Service-getPropertyValues-1
        PropertyValues pvs1 = s.getPropertyValues(e);
        propValuesEvidenceOK = pvs1.getValue(pr).getString().equals(localKnownStringValue);
        
        // #sec-Service-getPropertyValues-2
        PropertyValues pvs2 = s.getPropertyValues(e, localAspectKnownString);
        propValuesEvidenceAspectOK = pvs2.getValue(pr).getString().equals(localKnownStringValue);
        
        // #sec-Service-getPropertyValues-3
        PropertyValues pvs3 = s.getPropertyValues(e, localAspectKnownString, vocabularyIRI);
        propValuesEvidenceAspectVocabOK = pvs3.getValue(pr).getString().equals(localKnownStringValue);
        
        // #sec-Service-getPropertyValue-1
        PropertyValue pv1 = s.getPropertyValue(e, pr);
        propValueEvidencePropRefOK = pv1.getString().equals(localKnownStringValue);
        
        // #sec-Service-getPropertyValue-2
        PropertyValue pv2 = s.getPropertyValue(e, pn);
        propValueEvidencePropNameOK = pv2.getString().equals(localKnownStringValue);
        
        // #sec-Service-getPropertyValue-3
        PropertyValue pv3 = s.getPropertyValue(e, localPropertyKnownString);
        propValueEvidenceNameOK = pv3.getString().equals(localKnownStringValue);
        
        setReport(evidenceViaMapOK,"#sec-Service-newHTTPEvidence-2");
        setReport(propNameDefaultIRIOK,"#sec-Service-newPropertyName-1");
        setReport(propRefStringOK,"#sec-Service-newPropertyRef-1");
        setReport(propRefPropNameOK,"#sec-Service-newPropertyRef-2");
        setReport(propValuesEvidenceOK,"#sec-Service-getPropertyValues-1");
        setReport(propValuesEvidenceAspectOK,"#sec-Service-getPropertyValues-2");
        setReport(propValuesEvidenceAspectVocabOK,"#sec-Service-getPropertyValues-3");
        setReport(propValueEvidencePropRefOK,"#sec-Service-getPropertyValue-1");
        setReport(propValueEvidencePropNameOK,"#sec-Service-getPropertyValue-2");
        setReport(propValueEvidenceNameOK,"#sec-Service-getPropertyValue-3");
        setReport(s.getImplementationVersion() != null,"#sec-Service-getImplementationVersion"); // Just has to work without causing an exception
        setReport(s.getDataVersion() != null,"#sec-Service-getDataVersion"); // Just has to work without causing an exception
        setReport(gotListOfProperties,"#sec-Service-listPropertyRefs");
        setReport(true,"#sec-Service-initialize"); // Just has to work without causing an exception
        setReport(
          factoryCreatedEvidence && factoryCreatedPropertyName && factoryCreatedPropertyRef &&
          obtainedPropertyValueInstance && obtainedPropertyValuesInstance &&
          evidenceViaMapOK && propNameDefaultIRIOK && propRefStringOK && propRefPropNameOK &&
          propValuesEvidenceOK && propValuesEvidenceAspectOK && propValuesEvidenceAspectVocabOK &&
          propValueEvidencePropRefOK && propValueEvidencePropNameOK &&
          propValueEvidenceNameOK && gotListOfProperties &&
          s.getImplementationVersion() != null && s.getDataVersion() != null
          ,"#sec-Service");
      }
      
    }
    catch (NameException ne) {
      System.out.println("NameException: "+ne.getMessage());
      ne.printStackTrace();
    }
    catch (ValueException ve) {
      System.out.println("ValueException: "+ve.getMessage());
      ve.printStackTrace();
    }
    return report;
  }
  
}
