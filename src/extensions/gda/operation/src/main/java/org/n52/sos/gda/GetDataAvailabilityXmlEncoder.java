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
package org.n52.sos.gda;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code Encoder} to handle {@link GetDataAvailabilityResponse}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityXmlEncoder extends AbstractResponseEncoder<GetDataAvailabilityResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(GetDataAvailabilityXmlEncoder.class);

    public GetDataAvailabilityXmlEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME,
                Sos2Constants.NS_SOS_20, SosConstants.NS_SOS_PREFIX, GetDataAvailabilityResponse.class, false);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Collections.emptySet();
    }

    @Override
    protected XmlObject create(GetDataAvailabilityResponse response) throws OwsExceptionReport {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new GetDataAvailabilityStreamWriter(response.getVersion(), response.getDataAvailabilities()).write(out);
            return XmlObject.Factory.parse(out.toString("UTF8"));
        } catch (XMLStreamException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (DateTimeFormatException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (XmlException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (UnsupportedEncodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        }
    }
}
