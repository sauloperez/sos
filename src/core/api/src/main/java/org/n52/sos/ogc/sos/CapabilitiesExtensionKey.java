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
package org.n52.sos.ogc.sos;

/**
 * {@link CapabilitiesExtension} key class to identify CapabilitiesExtensions.
 * 
 * @since 4.0.0
 * 
 */
public class CapabilitiesExtensionKey implements Comparable<CapabilitiesExtensionKey> {
    private String service;

    private String version;

    /**
     * Default constructor
     */
    public CapabilitiesExtensionKey() {
        super();
    }

    /**
     * Constructor
     * 
     * @param service
     *            Related service
     * @param version
     *            Related version
     */
    public CapabilitiesExtensionKey(String service, String version) {
        super();
        this.service = service;
        this.version = version;
    }

    /**
     * Get the key service
     * 
     * @return Key servcice
     */
    public String getService() {
        return service;
    }

    /**
     * Set the key service
     * 
     * @param service
     *            service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Get the key version
     * 
     * @return Key version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the key version
     * 
     * @param version
     *            version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(CapabilitiesExtensionKey o) {
        if (o instanceof CapabilitiesExtensionKey) {
            if (service.equals(o.service) && version.equals(o.version)) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object paramObject) {
        if (service != null && version != null && paramObject instanceof CapabilitiesExtensionKey) {
            CapabilitiesExtensionKey toCheck = (CapabilitiesExtensionKey) paramObject;
            return (service.equals(toCheck.service) && version.equals(toCheck.version));
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + ((this.service != null) ? this.service.hashCode() : 0);
        hash = prime * hash + ((this.version != null) ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("CapabilitiesExtensionKeyType[service=%s, version=%s]", this.service, this.version);
    }
}
