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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.ows.OwsCapabilities;
import org.n52.sos.util.CollectionHelper;

/**
 * Class which represents the Capabilities.
 * 
 * @since 4.0.0
 * 
 */
public class SosCapabilities extends OwsCapabilities {

    /**
     * Metadata for all supported filter
     */
    private FilterCapabilities filterCapabilities;

    /**
     * All ObservationOfferings provided by this SOS.
     */
    private SortedSet<SosObservationOffering> contents = new TreeSet<SosObservationOffering>();

    /**
     * extensions
     */
    private List<CapabilitiesExtension> extensions = new LinkedList<CapabilitiesExtension>();

    public SosCapabilities(String version) {
        super(SosConstants.SOS, version);
    }

    /**
     * Get filter capabilities
     * 
     * @return filter capabilities
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Set filter capabilities
     * 
     * @param filterCapabilities
     *            filter capabilities
     */
    public void setFilterCapabilities(FilterCapabilities filterCapabilities) {
        this.filterCapabilities = filterCapabilities;
    }

    public boolean isSetFilterCapabilities() {
        return getFilterCapabilities() != null;
    }

    /**
     * Get contents data
     * 
     * @return contents data
     */
    public SortedSet<SosObservationOffering> getContents() {
        return Collections.unmodifiableSortedSet(contents);
    }

    /**
     * Set contents data
     * 
     * @param contents
     *            contents data
     */
    public void setContents(Collection<SosObservationOffering> contents) {
        this.contents =
                contents == null ? new TreeSet<SosObservationOffering>() : new TreeSet<SosObservationOffering>(
                        contents);
    }

    public boolean isSetContents() {
        return contents != null && !contents.isEmpty();
    }

    /**
     * Set extension data
     * 
     * @param extensions
     *            extension data
     */
    public void setExensions(Collection<CapabilitiesExtension> extensions) {
        this.extensions =
                extensions == null ? new LinkedList<CapabilitiesExtension>() : new ArrayList<CapabilitiesExtension>(
                        extensions);
    }

    /**
     * Get extension data
     * 
     * @return extension data
     */
    public List<CapabilitiesExtension> getExtensions() {
        return Collections.unmodifiableList(this.extensions);
    }

    public boolean isSetExtensions() {
        return CollectionHelper.isNotEmpty(getExtensions());
    }

}
