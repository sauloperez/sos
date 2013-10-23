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
package org.n52.sos.ds.hibernate.util.procedure.create;

import java.io.InputStream;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

/**
 * Strategy to create the {@link SosProcedureDescription} from a file.
 */
public class FileDescriptionCreationStrategy implements
        DescriptionCreationStrategy {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileDescriptionCreationStrategy.class);

    @Override
    public SosProcedureDescription create(Procedure p, Session s)
            throws OwsExceptionReport {
        XmlObject xml = read(p.getDescriptionFile());
        SosProcedureDescription desc = decode(xml);
        desc.setIdentifier(p.getIdentifier());
        return desc;
    }

    private InputStream getDocumentAsStream(String filename) {
        final StringBuilder builder = new StringBuilder();
        // check if filename contains placeholder for configured
        // sensor directory
        if (filename.startsWith("standard")) {
            filename = filename.replace("standard", "");
            builder.append(getServiceConfig().getSensorDir());
            builder.append("/");
        }
        builder.append(filename);
        LOGGER.debug("Procedure description file name '{}'!", filename);
        return Configurator.getInstance().getClass().
                getResourceAsStream(builder.toString());
    }

    private SosProcedureDescription decode(XmlObject xml) throws OwsExceptionReport {
        return (SosProcedureDescription) CodingHelper.decodeXmlElement(xml);
    }

    private XmlObject read(String path) throws OwsExceptionReport {
        InputStream stream = getDocumentAsStream(path);
        String string = StringHelper.convertStreamToString(stream);
        XmlObject xml = XmlHelper.parseXmlString(string);
        return xml;
    }

    @Override
    public boolean apply(Procedure p) {
        return !Strings.isNullOrEmpty(p.getDescriptionFile());
    }

    @VisibleForTesting
    ServiceConfiguration getServiceConfig() {
        return ServiceConfiguration.getInstance();
    }
}
