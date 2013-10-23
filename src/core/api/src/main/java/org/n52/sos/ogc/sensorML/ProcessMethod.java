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

/**
 * Implementation for sml:ProcessMethod
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class ProcessMethod {

    private final RulesDefinition rulesDefinition;

    public ProcessMethod(final RulesDefinition rulesDefinition) {
        if (rulesDefinition == null) {
            throw new IllegalArgumentException("parameter 'rulesDefinition' is MANDATORY");
        }
        this.rulesDefinition = rulesDefinition;
    }

    /**
     * Text and/or language defining rules for process profile (e.g. inputs,
     * outputs, parameters, and metadata) (Source: SensorML 1.0.1)
     * 
     * @return SOS rules definition
     */
    public RulesDefinition getRulesDefinition() {
        return rulesDefinition;
    }

}
