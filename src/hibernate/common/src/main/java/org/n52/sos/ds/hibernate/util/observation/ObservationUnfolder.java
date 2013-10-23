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

package org.n52.sos.ds.hibernate.util.observation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.DateTimeHelper;

import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ObservationUnfolder {
    private final OmObservation multiObservation;

    public ObservationUnfolder(OmObservation multiObservation) {
        this.multiObservation = multiObservation;
    }

    public List<OmObservation> unfold() throws OwsExceptionReport {
        if (multiObservation.getValue() instanceof SingleObservationValue) {
            return Collections.singletonList(multiObservation);
        } else {

            final SweDataArrayValue arrayValue =
                    ((SweDataArrayValue) ((MultiObservationValues) multiObservation.getValue()).getValue());
            final List<List<String>> values = arrayValue.getValue().getValues();
            final List<OmObservation> observationCollection = new ArrayList<OmObservation>(values.size());
            SweDataRecord elementType = null;
            if (arrayValue.getValue().getElementType() != null
                    && arrayValue.getValue().getElementType() instanceof SweDataRecord) {
                elementType = (SweDataRecord) arrayValue.getValue().getElementType();
            } else {
                throw new NoApplicableCodeException().withMessage("sweElementType type \"%s\" not supported",
                        elementType != null ? elementType.getClass().getName() : "null");
            }

            for (final List<String> block : values) {
                int tokenIndex = 0;
                Time phenomenonTime = null;
                final List<Value<?>> observedValues = new LinkedList<Value<?>>();
                // map to store the observed properties
                final Map<Value<?>, String> definitionsForObservedValues = Maps.newHashMap();
                Value<?> observedValue = null;
                for (final String token : block) {
                    // get values from block via definition in
                    // SosSweDataArray#getElementType
                    final SweAbstractDataComponent fieldForToken =
                            elementType.getFields().get(tokenIndex).getElement();
                    /*
                     * get phenomenon time
                     */
                    if (fieldForToken instanceof SweTime) {
                        try {
                            phenomenonTime = new TimeInstant(DateTimeHelper.parseIsoString2DateTime(token));
                        } catch (final OwsExceptionReport e) {
                            throw e;
                        } catch (final Exception e) {
                            /*
                             * FIXME what is the valid exception code if the
                             * result is not correct?
                             */
                            throw new NoApplicableCodeException().causedBy(e).withMessage(
                                    "Error while parse time String to DateTime!");
                        }
                    } else if (fieldForToken instanceof SweTimeRange) {
                        try {
                            final String[] subTokens = token.split("/");
                            phenomenonTime =
                                    new TimePeriod(DateTimeHelper.parseIsoString2DateTime(subTokens[0]),
                                            DateTimeHelper.parseIsoString2DateTime(subTokens[1]));
                        } catch (final OwsExceptionReport e) {
                            throw e;
                        } catch (final Exception e) {
                            /*
                             * FIXME what is the valid exception code if the
                             * result is not correct?
                             */
                            throw new NoApplicableCodeException().causedBy(e).withMessage(
                                    "Error while parse time String to DateTime!");
                        }
                    }
                    /*
                     * observation values
                     */
                    else if (fieldForToken instanceof SweQuantity) {
                        observedValue = new QuantityValue(new BigDecimal(token));
                        observedValue.setUnit(((SweQuantity) fieldForToken).getUom());
                    } else if (fieldForToken instanceof SweBoolean) {
                        observedValue = new BooleanValue(Boolean.parseBoolean(token));
                    } else if (fieldForToken instanceof SweText) {
                        observedValue = new TextValue(token);
                    } else if (fieldForToken instanceof SweCategory) {
                        observedValue = new CategoryValue(token);
                        observedValue.setUnit(((SweCategory) fieldForToken).getCodeSpace());
                    } else if (fieldForToken instanceof SweCount) {
                        observedValue = new CountValue(Integer.parseInt(token));
                    } else {
                        throw new NoApplicableCodeException().withMessage("sweField type '%s' not supported",
                                fieldForToken != null ? fieldForToken.getClass().getName() : "null");
                    }
                    if (observedValue != null) {
                        definitionsForObservedValues.put(observedValue, fieldForToken.getDefinition());
                        observedValues.add(observedValue);
                        observedValue = null;
                    }
                    tokenIndex++;
                }
                for (final Value<?> iValue : observedValues) {
                    final OmObservation newObservation =
                            createSingleValueObservation(multiObservation, phenomenonTime, iValue);
                    observationCollection.add(newObservation);
                }
            }
            return observationCollection;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createSingleValueObservation(final OmObservation multiObservation,
            final Time phenomenonTime, final Value<?> iValue) {
        final ObservationValue<?> value = new SingleObservationValue(phenomenonTime, iValue);
        final OmObservation newObservation = new OmObservation();
        newObservation.setNoDataValue(multiObservation.getNoDataValue());
        /*
         * TODO create new ObservationConstellation only with the specified
         * observed property and observation type
         */
        final OmObservationConstellation obsConst = multiObservation.getObservationConstellation();
        /*
         * createObservationConstellationForSubObservation ( multiObservation .
         * getObservationConstellation ( ) , iValue ,
         * definitionsForObservedValues . get ( iValue ) )
         */
        newObservation.setObservationConstellation(obsConst);
        newObservation.setValidTime(multiObservation.getValidTime());
        newObservation.setResultTime(multiObservation.getResultTime());
        newObservation.setTokenSeparator(multiObservation.getTokenSeparator());
        newObservation.setTupleSeparator(multiObservation.getTupleSeparator());
        newObservation.setResultType(multiObservation.getResultType());
        newObservation.setValue(value);
        return newObservation;
    }

}
