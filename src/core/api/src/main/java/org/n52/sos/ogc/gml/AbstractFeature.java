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
package org.n52.sos.ogc.gml;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

/**
 * Abstract class for encoding the feature of interest. Necessary because
 * different feature types should be supported. The SOS database or another
 * feature source (e.g. WFS) should provide information about the application
 * schema.
 * 
 * @since 4.0.0
 */
public abstract class AbstractFeature implements Serializable {

    /**
     * serial number
     */
    private static final long serialVersionUID = -6117378246552782214L;

    /** Feature identifier */
    private CodeWithAuthority identifier;

    /**
     * List of feature names
     */
    private List<CodeType> names = new LinkedList<CodeType>();

    /**
     * Feature description
     */
    private String description;

    /**
     * GML id
     */
    private String gmlId;

    /**
     * constructor
     */
    public AbstractFeature() {
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            Feature identifier
     */
    public AbstractFeature(CodeWithAuthority featureIdentifier) {
        this.identifier = featureIdentifier;
    }

    /**
     * constructor
     * 
     * @param featureIdentifier
     *            Feature identifier
     * @param gmlId
     *            GML id
     */
    public AbstractFeature(CodeWithAuthority featureIdentifier, String gmlId) {
        this.identifier = featureIdentifier;
        this.gmlId = gmlId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractFeature) {
            AbstractFeature feature = (AbstractFeature) o;
            if (feature.isSetIdentifier() && this.isSetIdentifier() && feature.isSetGmlID() && this.isSetGmlID()) {
                return feature.getIdentifier().equals(this.getIdentifier())
                        && feature.getGmlId().equals(this.getGmlId());
            } else if (feature.isSetIdentifier() && this.isSetIdentifier()) {
                return feature.getIdentifier().equals(this.getIdentifier());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isSetIdentifier()) ? getIdentifier().hashCode() : 0);
        result = prime * result + ((isSetGmlID()) ? getGmlId().hashCode() : 0);
        return result;
    }

    /**
     * Get identifier
     * 
     * @return Returns the identifier.
     */
    public CodeWithAuthority getIdentifier() {
        return identifier;
    }

    /**
     * Set observation identifier
     * 
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(CodeWithAuthority identifier) {
        this.identifier = identifier;
    }

    /**
     * Set observation identifier
     * 
     * @param identifier
     *            the identifier to set
     */
    public void setIdentifier(String identifier) {
        setIdentifier(new CodeWithAuthority(identifier));
    }

    /**
     * @return <tt>true</tt>, if identifier is set and value is not an empty
     *         string,<br>
     *         else <tt>false</tt>
     */
    public boolean isSetIdentifier() {
        return identifier != null && identifier.isSetValue();
    }

    /**
     * Get feature names
     * 
     * @return Feature names
     */
    public List<CodeType> getName() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Add feature names
     * 
     * @param name
     *            Feature names to ad
     */
    public void setName(final List<CodeType> name) {
        this.names.addAll(name);
    }

    /**
     * @param name
     */
    public void addName(final CodeType name) {
        this.names.add(name);
    }

    /**
     * Add a feature name
     * 
     * @param name
     *            Feature name to add
     */
    public void addName(final String name) {
        addName(new CodeType(name));
    }

    /**
     * Get feature description
     * 
     * @return Feature description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set feature description
     * 
     * @param description
     *            Feature description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Check whether feature has names
     * 
     * @return <code>true</code> if feature has names
     */
    public boolean isSetNames() {
        return CollectionHelper.isNotEmpty(names);
    }

    /**
     * Get first feature name or null if feature has no names
     * 
     * @return First feature name or null if feature has no names
     */
    public CodeType getFirstName() {
        if (isSetNames()) {
            return names.get(0);
        }
        return null;
    }

    /**
     * Get GML id
     * 
     * @return GML id
     */
    public String getGmlId() {
        return gmlId == null ? null : gmlId.replaceFirst(Constants.NUMBER_SIGN_STRING, Constants.EMPTY_STRING);
    }

    /**
     * Set GML id
     * 
     * @param gmlId
     *            GML id to set
     */
    public void setGmlId(String gmlId) {
        this.gmlId = gmlId;
    }

    /**
     * Check whether GML id is set
     * 
     * @return <code>true</code> if GML id is set
     */
    public boolean isSetGmlID() {
        return StringHelper.isNotEmpty(getGmlId());
    }

    /**
     * Check whether feature is still contained in XML document by sign
     * {@link Constants#NUMBER_SIGN_STRING}.
     * 
     * @return <code>true</code> if feature is still contained in XML document
     */
    public boolean isReferenced() {
        return isSetGmlID() && gmlId.startsWith(Constants.NUMBER_SIGN_STRING);
    }

}
