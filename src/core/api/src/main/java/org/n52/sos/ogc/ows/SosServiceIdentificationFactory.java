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
package org.n52.sos.ogc.ows;

import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.ABSTRACT;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.ACCESS_CONSTRAINTS;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.FEES;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.FILE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.KEYWORDS;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.SERVICE_TYPE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.SERVICE_TYPE_CODE_SPACE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.TITLE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.LazyThreadSafeProducer;
import org.n52.sos.util.Validation;
import org.n52.sos.util.XmlHelper;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@Configurable
public class SosServiceIdentificationFactory extends LazyThreadSafeProducer<SosServiceIdentification> {

    private File file;

    private String[] keywords;

    private String title;

    private String description;

    private String serviceType;

    private String serviceTypeCodeSpace;

    private String fees;

    private String constraints;

    public SosServiceIdentificationFactory() throws ConfigurationException {
        SettingsManager.getInstance().configure(this);
    }

    @Setting(FILE)
    public void setFile(File file) {
        this.file = file;
        setRecreate();
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords == null ? new String[0] : Arrays.copyOf(keywords, keywords.length);
        setRecreate();
    }

    @Setting(KEYWORDS)
    public void setKeywords(String keywords) {
        if (keywords != null) {
            String[] keywordArray = keywords.split(",");
            ArrayList<String> keywordList = new ArrayList<String>(keywordArray.length);
            for (String s : keywordArray) {
                if (s != null && !s.trim().isEmpty()) {
                    keywordList.add(s.trim());
                }
            }
            setKeywords(keywordList.toArray(new String[keywordList.size()]));
        } else {
            setKeywords(new String[0]);
        }
    }

    @Setting(TITLE)
    public void setTitle(String title) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Title", title);
        this.title = title;
        setRecreate();
    }

    @Setting(ABSTRACT)
    public void setAbstract(String description) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Abstract", description);
        this.description = description;
        setRecreate();
    }

    @Setting(SERVICE_TYPE)
    public void setServiceType(String serviceType) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Service Type", serviceType);
        this.serviceType = serviceType;
        setRecreate();
    }

    @Setting(SERVICE_TYPE_CODE_SPACE)
    public void setServiceTypeCodeSpace(String serviceTypeCodeSpace) throws ConfigurationException {
        this.serviceTypeCodeSpace = serviceTypeCodeSpace;
        setRecreate();
    }

    @Setting(FEES)
    public void setFees(String fees) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Fees", fees);
        this.fees = fees;
        setRecreate();
    }

    @Setting(ACCESS_CONSTRAINTS)
    public void setConstraints(String constraints) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Access Constraints", constraints);
        this.constraints = constraints;
        setRecreate();
    }

    @Override
    protected SosServiceIdentification create() throws ConfigurationException {
        SosServiceIdentification serviceIdentification = new SosServiceIdentification();
        if (this.file != null) {
            try {
                serviceIdentification.setServiceIdentification(XmlHelper.loadXmlDocumentFromFile(this.file));
            } catch (OwsExceptionReport ex) {
                throw new ConfigurationException(ex);
            }
        } else {
            serviceIdentification.setAbstract(this.description);
            serviceIdentification.setAccessConstraints(this.constraints);
            serviceIdentification.setFees(this.fees);
            serviceIdentification.setServiceType(this.serviceType);
            serviceIdentification.setServiceTypeCodeSpace(this.serviceTypeCodeSpace);
            serviceIdentification.setTitle(this.title);
            serviceIdentification.setVersions(ServiceOperatorRepository.getInstance().getSupportedVersions(
                    SosConstants.SOS));
            serviceIdentification.setKeywords(Arrays.asList(this.keywords));
        }
        return serviceIdentification;
    }
}
