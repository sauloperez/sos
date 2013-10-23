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

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class SosServiceIdentification {
    private XmlObject serviceIdentification;

    private String title;

    private String abstrakt;

    private String serviceType;

    private String serviceTypeCodeSpace;

    private String fees;

    private String accessConstraints;

    private SortedSet<String> versions = new TreeSet<String>();

    private SortedSet<String> profiles = new TreeSet<String>();

    private SortedSet<String> keywords = new TreeSet<String>();

    public XmlObject getServiceIdentification() {
        return serviceIdentification;
    }

    public void setServiceIdentification(XmlObject serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
    }

    public SortedSet<String> getVersions() {
        return Collections.unmodifiableSortedSet(versions);
    }

    public void setVersions(Collection<String> versions) {
        this.versions.clear();
        if (versions != null) {
            this.versions.addAll(versions);
        }
    }

    public boolean hasVersions() {
        return CollectionHelper.isNotEmpty(getVersions());
    }

    public SortedSet<String> getProfiles() {
        return Collections.unmodifiableSortedSet(profiles);
    }

    public void setProfiles(Collection<String> profiles) {
        this.profiles.clear();
        if (profiles != null) {
            this.profiles.addAll(profiles);
        }
    }

    public boolean hasProfiles() {
        return CollectionHelper.isNotEmpty(getProfiles());
    }

    public SortedSet<String> getKeywords() {
        return Collections.unmodifiableSortedSet(keywords);
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords.clear();
        if (keywords != null) {
            this.keywords.addAll(keywords);
        }
    }

    public boolean hasKeywords() {
        return CollectionHelper.isNotEmpty(getKeywords());
    }

    public String getTitle() {
        return title;
    }

    public boolean hasTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return abstrakt;
    }

    public boolean hasAbstract() {
        return StringHelper.isNotEmpty(getAbstract());
    }

    public void setAbstract(String abstrakt) {
        this.abstrakt = abstrakt;
    }

    public String getServiceType() {
        return serviceType;
    }

    public boolean hasServiceType() {
        return StringHelper.isNotEmpty(getServiceType());
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceTypeCodeSpace() {
        return serviceTypeCodeSpace;
    }

    public boolean hasServiceTypeCodeSpace() {
        return StringHelper.isNotEmpty(getServiceTypeCodeSpace());
    }

    public void setServiceTypeCodeSpace(String serviceTypeCodeSpace) {
        this.serviceTypeCodeSpace = serviceTypeCodeSpace;
    }

    public String getFees() {
        return fees;
    }

    public boolean hasFees() {
        return StringHelper.isNotEmpty(getFees());
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getAccessConstraints() {
        return accessConstraints;
    }

    public boolean hasAccessConstraints() {
        return StringHelper.isNotEmpty(getAccessConstraints());
    }

    public void setAccessConstraints(String accessConstraints) {
        this.accessConstraints = accessConstraints;
    }
}
