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
package org.n52.sos.binding.rest.resources.offerings;


import javax.servlet.http.HttpServletRequest;

import org.n52.sos.binding.rest.decode.ResourceDecoder;
import org.n52.sos.binding.rest.requests.BadRequestException;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.OptionsRestRequest;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.util.http.HTTPMethods;

import com.google.common.base.Strings;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class OfferingsDecoder extends ResourceDecoder {
    
    @Override
    protected RestRequest decodeGetRequest(final HttpServletRequest httpRequest,
            final String pathPayload) throws OwsExceptionReport
    {
        // 0 variables
        RestRequest result = null;
        
        // 1 identify type of request: by id OR search (OR atom feed)
        if (pathPayload != null && !pathPayload.isEmpty() && httpRequest.getQueryString() == null) {
             result = decodeOfferingByIdRequest(pathPayload);
            
        } else if (pathPayload == null && Strings.isNullOrEmpty(httpRequest.getQueryString())) {
            // 2.2 global resource
            result = decodeOfferingsGetRequest(httpRequest);
        } else {
            final String errorMsg = createBadGetRequestMessage(bindingConstants.getResourceOfferings(),true,true,false);
            final BadRequestException bR = new BadRequestException(errorMsg);
            throw new NoApplicableCodeException().causedBy(bR); 
        }
        
        // 3 return result
        return result;
    }

    private RestRequest decodeOfferingsGetRequest(final HttpServletRequest httpRequest)
    {
        final GetCapabilitiesRequest request = createGetCapabilitiesRequestWithContentSectionOnly();
        
        return new OfferingsRequest(request);
    }

    private RestRequest decodeOfferingByIdRequest(final String pathPayload)
    {
        final GetCapabilitiesRequest request = createGetCapabilitiesRequestWithContentSectionOnly();
        
        return new OfferingByIdRequest(request, pathPayload);
    }

    @Override
    protected RestRequest decodeDeleteRequest(final HttpServletRequest httpRequest,
            final String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.DELETE,
                bindingConstants.getResourceOfferings());
    }

    @Override
    protected RestRequest decodePostRequest(final HttpServletRequest httpRequest,
            final String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.POST,
                bindingConstants.getResourceOfferings());
    }

    @Override
    protected RestRequest decodePutRequest(final HttpServletRequest httpRequest,
            final String pathPayload) throws OwsExceptionReport
    {
        throw createHttpMethodForThisResourceNotSupportedException(HTTPMethods.PUT,
                bindingConstants.getResourceOfferings());
    }
    
    @Override
    protected RestRequest decodeOptionsRequest(final HttpServletRequest httpRequest,
            final String pathPayload)
    {
        return new OptionsRestRequest(bindingConstants.getResourceOfferings(),false,false);
    }
    

}