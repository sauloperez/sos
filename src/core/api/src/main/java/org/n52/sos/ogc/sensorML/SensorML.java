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
package org.n52.sos.ogc.sensorML;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.sos.SosOffering;

/**
 * SOS internal representation of a sensor description
 * 
 * @since 4.0.0
 */
public class SensorML extends AbstractSensorML {

    private String version;

    private final List<AbstractProcess> members = new LinkedList<AbstractProcess>();

    /**
     * default constructor
     */
    public SensorML() {
    }

    public String getVersion() {
        return version;
    }

    public SensorML setVersion(final String version) {
        this.version = version;
        return this;
    }

    public List<AbstractProcess> getMembers() {
        return members;
    }

    public SensorML setMembers(final List<AbstractProcess> members) {
        for (final AbstractProcess member : members) {
            addMember(member);
        }
        return this;
    }

    public SensorML addMember(final AbstractProcess member) {
        if (isEmpty() && !isSetIdentifier() && member.isSetIdentifier()) {
            setIdentifier(member.getIdentifier());
        }
        if (isEmpty() && !isSetOfferings() && member.isSetOfferings()) {
            for (SosOffering offering : member.getOfferings()) {
                addOffering(offering);
            }
        }
        members.add(member);
        return this;
    }

    /**
     * @return <tt>true</tt>, if everything from the super class is not set
     */
    private boolean isEmpty() {
        return !isSetKeywords() && !isSetIdentifications() && !isSetClassifications() && !isSetCapabilities()
                && !isSetCharacteristics() && !isSetValidTime() && !isSetContact() && !isSetDocumentation()
                && !isSetHistory();
    }

    /**
     * @return <tt>true</tt>, if this instance contains only members and
     *         everything else is not set
     */
    public boolean isWrapper() {
        return isEmpty() && isSetMembers();
    }

    public boolean isSetMembers() {
        return members != null && !members.isEmpty();
    }

    /**
     * @return If member's parent procedures are set if this is a wrapper, if
     *         normal parent procedures are set otherwise
     */
    @Override
    public boolean isSetParentProcedures() {
        if (isWrapper() && !super.isSetParentProcedures()) {
            return members.get(0).isSetParentProcedures();
        }
        return super.isSetParentProcedures();
    }

    /**
     * @return Member's parent procedures if this is a wrapper, normal parent
     *         procedures otherwise
     */
    @Override
    public Set<String> getParentProcedures() {
        if (isWrapper() && !super.isSetParentProcedures()) {
            return members.get(0).getParentProcedures();
        }
        return super.getParentProcedures();
    }

    /**
     * @return If member's offerings are set if this is a wrapper, if normal
     *         offerings are set otherwise
     */
    @Override
    public boolean isSetOfferings() {
        if (isWrapper() && !super.isSetOfferings()) {
            return members.get(0).isSetOfferings();
        }
        return super.isSetOfferings();
    }

    /**
     * @return Member's offerings if this is a wrapper, normal offerings
     *         otherwise
     */
    @Override
    public Set<SosOffering> getOfferings() {
        if (isWrapper() && !isSetOfferings()) {
            return members.get(0).getOfferings();
        }
        return super.getOfferings();
    }
}
