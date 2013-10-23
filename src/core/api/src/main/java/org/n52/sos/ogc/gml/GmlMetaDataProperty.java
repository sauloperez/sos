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

/**
 * Class represents a GML conform MetaDataProperty element
 * @since 4.0.0
 * 
 */
public class GmlMetaDataProperty {

    /**
     * Title
     */
    private String title;

    /**
     * Role
     */
    private String role;

    /**
     * Href
     */
    private String href;

    /**
     * Set title
     * @param title Title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set role
     * @param role Role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Set href
     * @param href Href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Get title
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get role
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Get href
     * @return Href
     */
    public String getHref() {
        return href;
    }
    
    @Override
    public String toString() {
        return String.format("GmlMetaDataProperty [title=%s, role=%s, href=%s]", getTitle(), getRole(), getHref());
    }
}
