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
package org.n52.sos.binding;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class SimpleBindingTest {
    private static final List<MediaType> XML = newArrayList(MediaTypes.APPLICATION_XML);

    private static final List<MediaType> JSON = newArrayList(MediaTypes.APPLICATION_JSON);

    private static final List<MediaType> ANYTHING = newArrayList(MediaTypes.WILD_CARD);

    private static final List<MediaType> XML_AND_JSON = newArrayList(MediaTypes.APPLICATION_XML,
            MediaTypes.APPLICATION_JSON);

    private static final List<MediaType> NOTHING = newArrayList();

    private TestBinding binding;

    private GetObservationResponse response;

    private MediaType defaultContentType;

    @Before
    public void setUp() {
        this.response = new GetObservationResponse();
        this.binding = new TestBinding();
        this.defaultContentType = binding.getDefaultContentType();
    }

    @Test
    public void should_use_default_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(NOTHING), is(defaultContentType));
    }

    @Test
    public void should_Accept_Defaul_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(XML), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_Accept_NotSupported_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(JSON), is(MediaTypes.APPLICATION_JSON));
    }

    @Test
    public void should_Accept_Wildcard_ContentType() throws HTTPException {
        assertThat(chosenContentTypeWithAccept(ANYTHING), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_ResponseFormat_NotSupported_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(NOTHING), is(defaultContentType));
    }

    @Test
    public void should_Acept_Equals_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_XML);
        assertThat(chosenContentTypeWithAccept(XML), is(defaultContentType));
    }

    @Test(expected = HTTPException.class)
    public void should_Accept_NotContains_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(XML_AND_JSON), is(defaultContentType));
    }

    @Test
    public void should_Accept_Wildcard_ResponseFormat_ContentType() throws HTTPException {
        response.setContentType(MediaTypes.APPLICATION_NETCDF);
        assertThat(chosenContentTypeWithAccept(ANYTHING), is(MediaTypes.APPLICATION_NETCDF));
    }

    private MediaType chosenContentTypeWithAccept(List<MediaType> accept) throws HTTPException {
        return binding.chooseResponseContentType(response, accept, defaultContentType);
    }

}
