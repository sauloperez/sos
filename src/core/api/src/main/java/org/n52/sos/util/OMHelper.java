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
package org.n52.sos.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.util.http.HTTPStatus;

/**
 * Utility class for Observation and Measurement
 * 
 * @since 4.0.0
 * 
 */
public final class OMHelper {

    private OMHelper() {
    }

    public static String getNamespaceForFeatureType(final String featureType) {
        if (SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT.equals(featureType)
                || SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE.equals(featureType)
                || SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE.equals(featureType)) {
            return SfConstants.NS_SAMS;
        } else if (SfConstants.FT_SAMPLINGPOINT.equals(featureType)
                || SfConstants.FT_SAMPLINGCURVE.equals(featureType)
                || SfConstants.FT_SAMPLINGSURFACE.equals(featureType)) {
            return SfConstants.NS_SA;
        }
        return SfConstants.NS_SAMS;
    }

    public static String getObservationTypeFrom(final SweAbstractDataComponent component) throws OwsExceptionReport {
        if (component instanceof SweBoolean) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        } else if (component instanceof SweQuantity) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        } else if (component instanceof SweText) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        } else if (component instanceof SweCount) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        } else if (component instanceof SweCategory) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        }
        // TODO Check for missing types
        throw new NoApplicableCodeException().withMessage(
                "Not able to derive observation type from swe:AbstractDataComponent element '{}'.", component)
                .setStatus(HTTPStatus.BAD_REQUEST);
    }

    public static String getObservationTypeFor(final Value<?> value) {
        if (value instanceof BooleanValue) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        } else if (value instanceof CategoryValue) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        } else if (value instanceof CountValue) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        } else if (value instanceof QuantityValue) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        } else if (value instanceof TextValue) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        } else if (value instanceof SweDataArrayValue) {
            return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
        } else if (value instanceof GeometryValue) {
            return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
        }
        return OmConstants.OBS_TYPE_OBSERVATION;
    }

    public static String getObservationTypeFor(final QName resultModel) {
        if (OmConstants.RESULT_MODEL_MEASUREMENT.equals(resultModel)) {
            return OmConstants.OBS_TYPE_MEASUREMENT;
        } else if (OmConstants.RESULT_MODEL_CATEGORY_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_GEOMETRY_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_COUNT_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_TRUTH_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
        } else if (OmConstants.RESULT_MODEL_TEXT_OBSERVATION.equals(resultModel)) {
            return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
        }
        return OmConstants.OBS_TYPE_OBSERVATION;
    }

    /**
     * Get the QName for resultModels from observationType constant
     * 
     * @param resultModels4Offering
     *            Observation types
     * @return QNames for resultModel parameter
     */
    public static Collection<QName> getQNamesForResultModel(final Collection<String> resultModels4Offering) {
        final List<QName> resultModels = new ArrayList<QName>(9);
        for (final String string : resultModels4Offering) {
            resultModels.add(getQNameFor(string));
        }
        return resultModels;
    }

    public static QName getQNameFor(final String observationType) {
        if (OmConstants.OBS_TYPE_MEASUREMENT.equals(observationType)) {
            return OmConstants.RESULT_MODEL_MEASUREMENT;
        } else if (OmConstants.OBS_TYPE_CATEGORY_OBSERVATION.equals(observationType)) {
            return OmConstants.RESULT_MODEL_CATEGORY_OBSERVATION;
        } else if (OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION.equals(observationType)) {
            return OmConstants.RESULT_MODEL_GEOMETRY_OBSERVATION;
        } else if (OmConstants.OBS_TYPE_COUNT_OBSERVATION.equals(observationType)) {
            return OmConstants.RESULT_MODEL_COUNT_OBSERVATION;
        } else if (OmConstants.OBS_TYPE_TRUTH_OBSERVATION.equals(observationType)) {
            return OmConstants.RESULT_MODEL_TRUTH_OBSERVATION;
        } else if (OmConstants.OBS_TYPE_TEXT_OBSERVATION.equals(observationType)) {
            return OmConstants.RESULT_MODEL_TEXT_OBSERVATION;
        } else {
            return OmConstants.RESULT_MODEL_OBSERVATION;
        }
    }

    public static Object getEncodedResultModelFor(final String resultModel) {
        final QName qNameFor = getQNameFor(resultModel);
        final StringBuilder builder = new StringBuilder();
        builder.append(qNameFor.getPrefix());
        builder.append(":");
        builder.append(qNameFor.getLocalPart());
        return builder.toString();
    }
}
