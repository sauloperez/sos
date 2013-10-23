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
package org.n52.sos.decode;

import static org.junit.Assert.*;

import org.junit.Test;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.http.HTTPConstants;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OperationDecoderKeyTest {

    @Test
    public void testHashCode() {
        assertEquals(new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode());
        assertEquals(
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                        SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode(),
                new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetCapabilities
                        .name(), MediaTypes.APPLICATION_KVP).hashCode());
        assertEquals(new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                MediaTypes.APPLICATION_KVP).hashCode(), new OperationDecoderKey(SosConstants.SOS, null,
                SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP).hashCode());
        assertEquals(new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                MediaTypes.APPLICATION_KVP).hashCode(), new OperationDecoderKey(SosConstants.SOS,
                Sos2Constants.SERVICEVERSION, (String) null, MediaTypes.APPLICATION_KVP).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP), new OperationDecoderKey(
                SosConstants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetCapabilities.name(),
                MediaTypes.APPLICATION_KVP));
        assertEquals(new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP), new OperationDecoderKey(
                null, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetCapabilities.name(),
                MediaTypes.APPLICATION_KVP));
        assertEquals(new OperationDecoderKey(SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(),
                MediaTypes.APPLICATION_KVP), new OperationDecoderKey(SosConstants.SOS, null,
                SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP));
        assertEquals(new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, (String) null,
                MediaTypes.APPLICATION_KVP), new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                (String) null, MediaTypes.APPLICATION_KVP));
    }
}
