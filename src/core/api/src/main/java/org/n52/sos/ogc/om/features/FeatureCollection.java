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
package org.n52.sos.ogc.om.features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.EmptyIterator;

/**
 * class represents a GMl feature collection
 * 
 * @since 4.0.0
 */
public class FeatureCollection extends AbstractFeature implements Iterable<AbstractFeature> {
    private static final long serialVersionUID = -6527441724827160710L;

    /**
     * members of this feature collection
     */
    private Map<String, AbstractFeature> members = new HashMap<String, AbstractFeature>(0);

    /**
     * constructor
     */
    public FeatureCollection() {
        super(new CodeWithAuthority("gml:FeatureCollection"));
    }

    /**
     * constructor
     * 
     * @param members
     *            collection with feature members of this collection
     */
    public FeatureCollection(final Map<String, AbstractFeature> members) {
        super(new CodeWithAuthority("gml:FeatureCollection"));
        this.members = members;
    }

    /**
     * Get features
     * 
     * @return the members
     */
    public Map<String, AbstractFeature> getMembers() {
        return members;
    }

    /**
     * Set features
     * 
     * @param members
     *            the members to set
     */
    public void setMembers(Map<String, AbstractFeature> members) {
        this.members.putAll(members);
    }

    public void addMember(AbstractFeature member) {
        members.put(member.getIdentifier().getValue(), member);
    }

    /**
     * @param featureIdentifier
     *            the id
     * @return the removed feature
     * @see Map#remove(Object)
     */
    public AbstractFeature removeMember(String featureIdentifier) {
        return members.remove(featureIdentifier);
    }

    /**
     * Check whether members are set
     * 
     * @return <code>true</code>, if members are set
     */
    public boolean isSetMembers() {
        return CollectionHelper.isNotEmpty(getMembers());
    }

    @Override
    public Iterator<AbstractFeature> iterator() {
        if (isSetMembers()) {
            return getMembers().values().iterator();
        } else {
            return EmptyIterator.instance();
        }
    }
}
