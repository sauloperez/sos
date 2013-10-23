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

import javax.xml.namespace.QName;

import org.n52.sos.ogc.ows.OWSConstants;

/**
 * Helper class for OGC OWS
 * 
 * @since 4.0.0
 * 
 */
public final class OwsHelper {

    /**
     * Sets the first character to UpperCase.
     * 
     * @param name
     *            String to be modified.
     * @return Modified string.
     */
    public static String refactorOpsName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);

    }

    /**
     * Get OWS QName for localName
     * 
     * @param localName
     *            Local name
     * @return QName for localName
     */
    public static QName getQNameForLocalName(String localName) {
        return new QName(OWSConstants.NS_OWS, localName, OWSConstants.NS_OWS_PREFIX);
    }

    private OwsHelper() {
    }
}
