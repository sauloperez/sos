/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.ProcessModel;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractSimpleType;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.wml.ObservationProcess;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 * 
 */
public class WaterMLv20SensorMLv101Converter implements Converter<SosProcedureDescription, SosProcedureDescription> {

    /*
     * TODO - Add function to read mapping information
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv101Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(new ConverterKeyType(
            WaterMLConstants.NS_WML_20, SensorMLConstants.NS_SML), new ConverterKeyType(SensorMLConstants.NS_SML,
            WaterMLConstants.NS_WML_20));

    public WaterMLv20SensorMLv101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(SosProcedureDescription objectToConvert) throws ConverterException {
        if (objectToConvert.getDescriptionFormat().equals(WaterMLConstants.NS_WML_20)) {
            return convertWML2ObservationProcessToSensorML101(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML101ToWML2ObservationProcess(objectToConvert);
        }
        return null;
    }

    private SosProcedureDescription convertSensorML101ToWML2ObservationProcess(SosProcedureDescription objectToConvert) {
        ObservationProcess observationProcess = new ObservationProcess();

        if (objectToConvert instanceof SensorML) {
            SensorML sensorML = (SensorML) objectToConvert;
            if (sensorML.isWrapper()) {
                for (AbstractProcess member : sensorML.getMembers()) {
                    // TODO get values and add to obsProcess
                    if (member.isSetIdentifier()) {
                        observationProcess.setIdentifier(member.getIdentifier());
                    }
                    if (member instanceof System) {
                        convertSystemToObservationProcess(observationProcess, (System) member);
                    } else if (member instanceof ProcessModel) {
                        convertProcessModelToObservationProcess(observationProcess, (ProcessModel) member);
                    }
                }
            }
            // TODO add 'else' to get values and add to obsProcess from sensorML
        } else {
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        observationProcess.setIdentifier(objectToConvert.getIdentifier());
        observationProcess.setDescriptionFormat(WaterMLConstants.NS_WML_20);
        return observationProcess;
    }

    private SosProcedureDescription convertWML2ObservationProcessToSensorML101(SosProcedureDescription objectToConvert) {
        SensorML sensorML = new SensorML();
        if (objectToConvert instanceof ObservationProcess) {
            ObservationProcess observationProcess = new ObservationProcess();
            if (observationProcess.isSetProcessType()) {
                AbstractProcess process;
                if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                    process = convertObservationProcessToSystem(observationProcess);
                } else {
                    process = convertObservationProcessToProcessModel(observationProcess);
                }
                process.setClassifications(convertProcessTypeToClassification(observationProcess.getProcessType()));
                sensorML.addMember(process);
            }
        } else {
            sensorML.addIdentifier(createUniqueIDIdentifier(objectToConvert.getIdentifier()));
        }
        return sensorML;
    }

    private System convertObservationProcessToSystem(ObservationProcess observationProcess) {
        System system = new System();
        system.addIdentifier(createUniqueIDIdentifier(observationProcess.getIdentifier()));
        // TODO add all other stuff
        // if (observationProcess.isSetVerticalDatum()) {
        // }

        return system;
    }

    private ProcessModel convertObservationProcessToProcessModel(ObservationProcess observationProcess) {
        ProcessModel processModel = new ProcessModel();
        processModel.addIdentifier(createUniqueIDIdentifier(observationProcess.getIdentifier()));
        // duration is not valid for validTime element
        observationProcess.getAggregationDuration();
        if (observationProcess.isSetComments()) {
            processModel.addDocumentation(convertCommentsToDocumentation(observationProcess.getComments()));
        }
        if (observationProcess.isSetInputs()) {
            processModel.setInputs(convertObservationProcessInputsToSMLInputs(observationProcess.getInputs()));
        }

        observationProcess.getOriginatingProcess();
        observationProcess.getParameters();
        if (observationProcess.isSetProcessReference()) {
            processModel.addDocumentation(convertProcessReferenceToDocumentation(observationProcess
                    .getProcessReference()));
        }

        observationProcess.getVerticalDatum();

        // TODO add all other stuff
        return processModel;
    }

    private boolean checkProcessType(ReferenceType processType, String processTypeName) {
        if (processType.isSetHref()) {
            return processType.getHref().equals(processTypeName);
        }
        return false;
    }

    private SmlIdentifier createUniqueIDIdentifier(String procedureIdentifier) {
        return new SmlIdentifier("uniqueID", "urn:ogc:def:identifier:OGC:uniqueID", procedureIdentifier);
    }

    private List<SmlClassifier> convertProcessTypeToClassification(ReferenceType processType) {
        String definition = "urn:ogc:def:classifier:OGC:1.0:sensorType";
        SmlClassifier sosSMLClassifier = new SmlClassifier(processType.getTitle(), definition, processType.getHref());
        return Collections.singletonList(sosSMLClassifier);
    }

    private AbstractSmlDocumentation convertCommentsToDocumentation(List<String> comments) {
        // TODO check for correctness
        if (comments.size() > 1) {
            SmlDocumentation documentation = new SmlDocumentation();
            documentation.setDescription(comments.get(0));
            return documentation;
        } else {
            SmlDocumentationList documentationList = new SmlDocumentationList();
            for (String comment : comments) {
                SmlDocumentationListMember member = new SmlDocumentationListMember();
                SmlDocumentation documentation = new SmlDocumentation();
                documentation.setDescription(comment);
                member.setDocumentation(documentation);
                documentationList.addMember(member);
            }
            return documentationList;
        }
    }

    private List<SmlIo<?>> convertObservationProcessInputsToSMLInputs(List<ReferenceType> inputs) {
        List<SmlIo<?>> smlInputs = new ArrayList<SmlIo<?>>(inputs.size());
        for (ReferenceType referenceType : inputs) {
            SmlIo<String> io = new SmlIo<String>();
            if (referenceType.isSetTitle()) {
                io.setIoName(referenceType.getTitle());
            }
            SweObservableProperty ioValue = new SweObservableProperty();
            ioValue.setDefinition(referenceType.getHref());
            io.setIoValue(ioValue);
        }
        return smlInputs;
    }

    private AbstractSmlDocumentation convertProcessReferenceToDocumentation(ReferenceType processReference) {
        SmlDocumentation documentation = new SmlDocumentation();
        StringBuilder builder = new StringBuilder();
        builder.append(processReference.getHref());
        builder.append(";");
        builder.append(processReference.getTitle());
        documentation.setDescription(builder.toString());
        return documentation;
    }

    private void convertAbstractSensorMLToObservationProcess(ObservationProcess observationProcess,
            AbstractSensorML abstractSensorML) {
        if (abstractSensorML.isSetCapabilities()) {
            convertSMLCapabilitiesToObservationProcessParameter(observationProcess, abstractSensorML.getCapabilities());
        }
        if (abstractSensorML.isSetCharacteristics()) {
            convertSMLCharacteristicsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getCharacteristics());
        }
        if (abstractSensorML.isSetClassifications()) {
            convertSMLClassificationsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getClassifications());
        }
        if (abstractSensorML.isSetDocumentation()) {
            convertSMLDocumentationToObservationProcessComment(observationProcess, abstractSensorML.getDocumentation());
        }
        if (abstractSensorML.isSetIdentifications()) {
            convertSMLIdentificationsToObservationProcessParameter(observationProcess,
                    abstractSensorML.getIdentifications());
        }

    }

    private void convertAbstractProcessToObservationProcess(ObservationProcess observationProcess,
            AbstractProcess abstractProces) {
        if (abstractProces.isSetParameters()) {
            convertSMLParametersToObservationProcessParameter(observationProcess, abstractProces.getParameters());
        }
        if (abstractProces.isSetInputs()) {
            observationProcess.setInputs(convertSMLInputsToObservationProcessInputs(abstractProces.getInputs()));
        }
        if (abstractProces.isSetOutputs()) {
            convertSMLOutputsToObservationProcessParameter(observationProcess, abstractProces.getOutputs());
        }
    }

    private void convertSystemToObservationProcess(ObservationProcess observationProcess, System system) {
        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
        convertAbstractSensorMLToObservationProcess(observationProcess, system);
        convertAbstractProcessToObservationProcess(observationProcess, system);
        // TODO the rest
    }

    private void convertProcessModelToObservationProcess(ObservationProcess observationProcess,
            ProcessModel processModel) {
        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        convertAbstractSensorMLToObservationProcess(observationProcess, processModel);
        convertAbstractProcessToObservationProcess(observationProcess, processModel);
        // TODO the rest
    }

    private void convertSMLCharacteristicsToObservationProcessParameter(ObservationProcess observationProcess,
            List<SmlCharacteristics> characteristics) {
        for (SmlCharacteristics characteristic : characteristics) {
            if (characteristic.isSetAbstractDataRecord() && characteristic.getDataRecord().isSetFields()) {
                for (SweField field : characteristic.getDataRecord().getFields()) {
                    NamedValue<String> namedValueProperty = convertSMLFieldToNamedValuePair(field);
                    if (namedValueProperty != null) {
                        observationProcess.addParameter(namedValueProperty);
                    }
                }
            }
        }
    }

    private void convertSMLClassificationsToObservationProcessParameter(ObservationProcess observationProcess,
            List<SmlClassifier> classifications) {
        for (SmlClassifier classifier : classifications) {
            NamedValue<String> namedValueProperty = new NamedValue<String>();
            ReferenceType refType = new ReferenceType(classifier.getDefinition());
            refType.setTitle(classifier.getName());
            namedValueProperty.setName(refType);
            namedValueProperty.setValue(new TextValue(classifier.getValue()));
            observationProcess.addParameter(namedValueProperty);
        }

    }

    private void convertSMLIdentificationsToObservationProcessParameter(ObservationProcess observationProcess,
            List<SmlIdentifier> identifications) {
        for (SmlIdentifier identifier : identifications) {
            NamedValue<String> namedValueProperty = new NamedValue<String>();
            ReferenceType refType = new ReferenceType(identifier.getDefinition());
            refType.setTitle(identifier.getName());
            // TODO uncomment if supported
//            if (identifier.getDefinition().contains("name")) {
//                CodeType codeType = new CodeType(identifier.getValue());
//                codeType.setCodeSpace(identifier.getDefinition());
//                observationProcess.addName(codeType);
//            }
            namedValueProperty.setName(refType);
            namedValueProperty.setValue(new TextValue(identifier.getValue()));
            observationProcess.addParameter(namedValueProperty);
        }
    }

    private void convertSMLDocumentationToObservationProcessComment(ObservationProcess observationProcess,
            List<AbstractSmlDocumentation> documentation) {
        // TODO Auto-generated method stub
    }

    private void convertSMLParametersToObservationProcessParameter(ObservationProcess observationProcess,
            List<String> parameters) {
        // TODO Auto-generated method stub
    }

    private void convertSMLOutputsToObservationProcessParameter(ObservationProcess observationProcess,
            List<SmlIo<?>> outputs) {
        for (SmlIo<?> sosSMLIo : outputs) {
            ReferenceType referenceType = new ReferenceType("output");
            NamedValue<String> namedValueProperty = new NamedValue<String>();
            namedValueProperty.setName(referenceType);
            namedValueProperty.setValue(new TextValue(sosSMLIo.getIoValue().getDefinition()));
            // NamedValuePair namedValueProperty =
            // getNamedValuePairForSosSweAbstractSimpleType(sosSMLIo.getIoValue());
            // namedValueProperty.getName().setTitle(sosSMLIo.getIoName());
            observationProcess.addParameter(namedValueProperty);
        }
    }

    private List<ReferenceType> convertSMLInputsToObservationProcessInputs(List<SmlIo<?>> inputs) {
        List<ReferenceType> oPInputs = new ArrayList<ReferenceType>(inputs.size());
        for (SmlIo<?> sosSMLIo : inputs) {
            ReferenceType refType = new ReferenceType(sosSMLIo.getIoValue().getDefinition());
            refType.setTitle(sosSMLIo.getIoName());
            oPInputs.add(refType);
        }
        return oPInputs;
    }

    private void convertSMLCapabilitiesToObservationProcessParameter(ObservationProcess observationProcess,
            List<SmlCapabilities> capabilities) {
        for (SmlCapabilities capability : capabilities) {
            if (capability.isSetAbstractDataRecord() && capability.getDataRecord().isSetFields()) {
                for (SweField field : capability.getDataRecord().getFields()) {
                    NamedValue<String> namedValueProperty = convertSMLFieldToNamedValuePair(field);
                    if (namedValueProperty != null) {
                        observationProcess.addParameter(namedValueProperty);
                    }
                }
            }
        }
    }

    private NamedValue<String> convertSMLFieldToNamedValuePair(SweField field) {
        if (field.getElement() instanceof SweAbstractSimpleType) {
            NamedValue<String> namedValueProperty =
                    getNamedValuePairForSosSweAbstractSimpleType((SweAbstractSimpleType) field.getElement());
            namedValueProperty.getName().setTitle(field.getName());
            return namedValueProperty;
        }
        return null;
    }

    private NamedValue<String> getNamedValuePairForSosSweAbstractSimpleType(SweAbstractSimpleType<?> element) {
        NamedValue<String> namedValueProperty = new NamedValue<String>();
        ReferenceType refType = new ReferenceType(element.getDefinition());
        namedValueProperty.setName(refType);
        if (element.isSetValue()) {
            namedValueProperty.setValue(new TextValue(element.getStringValue()));
        }
        return namedValueProperty;
    }

}
