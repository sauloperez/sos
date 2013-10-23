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
package org.n52.sos.ds;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.service.operator.ServiceOperatorRepository;

//import org.n52.sos.service.operator.ServiceOperatorRepository;

/**
 * interface for getting capabilities for a passed GetCapabilities request from
 * the data source
 * 
 * @since 4.0.0
 */
public abstract class AbstractGetCapabilitiesDAO extends AbstractOperationDAO {

    public AbstractGetCapabilitiesDAO(String service) {
        super(service, SosConstants.Operations.GetCapabilities.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        // set param Sections
        List<String> sectionsValues = new LinkedList<String>();
        /* common sections */
        sectionsValues.add(SosConstants.CapabilitiesSections.ServiceIdentification.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.ServiceProvider.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.OperationsMetadata.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.Contents.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.All.name());

        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            sectionsValues.add(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name());
        } else if (version.equals(Sos2Constants.SERVICEVERSION)) {
            sectionsValues.add(Sos2Constants.CapabilitiesSections.FilterCapabilities.name());
            /* sections of extension points */
            for (String section : getExtensionSections(service, version)) {
                sectionsValues.add(section);
            }
        }

        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.Sections, sectionsValues);
        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.AcceptFormats,
                SosConstants.ACCEPT_FORMATS);
        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.AcceptVersions,
                ServiceOperatorRepository.getInstance().getSupportedVersions(service));
        opsMeta.addAnyParameterValue(SosConstants.GetCapabilitiesParams.updateSequence);
    }

    protected abstract Set<String> getExtensionSections(final String service, final String version)
            throws OwsExceptionReport;

    /**
     * Get the SOS capabilities
     * 
     * @param request
     *            GetCapabilities request
     * @return internal SOS capabilities representation
     * 
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    public abstract GetCapabilitiesResponse getCapabilities(GetCapabilitiesRequest request) throws OwsExceptionReport;

}
