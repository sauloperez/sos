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

import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

/**
 * Class represents a GML conform ReferenceType element
 * 
 * @since 4.0.0
 * 
 */
public class ReferenceType {

    /**
     * Href
     */
    private String href;

    /**
     * Title
     */
    private String title;

    /**
     * Role
     */
    private String role;

    /**
     * constructor
     * 
     * @param href
     *            Href
     */
    public ReferenceType(String href) {
        this.href = href;
    }

    /**
     * Get href
     * 
     * @return Href
     */
    public String getHref() {
        return href;
    }

    /**
     * Get title
     * 
     * @return Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get role
     * 
     * @return Role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set href
     * 
     * @param href
     *            Href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Set title
     * 
     * @param title
     *            Title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set role
     * 
     * @param role
     *            Role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Check whether href is set
     * 
     * @return <code>true</code>, if href is set
     */
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(href);
    }

    /**
     * Check whether title is set
     * 
     * @return <code>true</code>, if title is set
     */
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(title);
    }

    /**
     * Check whether role is set
     * 
     * @return <code>true</code>, if role is set
     */
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(role);
    }

    /**
     * Check whether href, title, and role are set
     * 
     * @return <code>true</code>, if href, title, and role are set
     */
    public boolean hasValues() {
        return isSetHref() && isSetRole() && isSetTitle();
    }

    /**
     * Get title from href.<br>
     * Cuts href: <br>
     * - starts with 'http': cuts string at last {@link Constants#SLASH_CHAR}<br>
     * - starts with 'urn': cuts string at last {@link Constants#COLON_CHAR}<br>
     * - contains {@link Constants#NUMBER_SIGN_STRING}: cuts string at last
     * {@link Constants#NUMBER_SIGN_CHAR}<br>
     * 
     * @return Title from href
     */
    public String getTitleFromHref() {
        String title = getHref();
        if (title.startsWith("http")) {
            title = title.substring(title.lastIndexOf(Constants.SLASH_CHAR) + 1, title.length());
        } else if (title.startsWith("urn")) {
            title = title.substring(title.lastIndexOf(Constants.COLON_CHAR) + 1, title.length());
        }
        if (title.contains(Constants.NUMBER_SIGN_STRING)) {
            title = title.substring(title.lastIndexOf(Constants.NUMBER_SIGN_CHAR) + 1, title.length());
        }
        return title;
    }

    @Override
    public String toString() {
        return String.format("ReferenceType [title=%s, role=%s, href=%s]", getTitle(), getRole(), getHref());
    }
}
