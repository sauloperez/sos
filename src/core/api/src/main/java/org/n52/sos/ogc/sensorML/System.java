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

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.gml.EngineeringCRS;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;

/**
 * @since 4.0.0
 * 
 */
public class System extends AbstractComponent {

    private EngineeringCRS spatialReferenceFrame;

    private final List<SmlComponent> components = new ArrayList<SmlComponent>(0);

    public List<SmlComponent> getComponents() {
        return components;
    }

    public System addComponents(final List<SmlComponent> components) {
        if (components != null) {
            checkAndSetChildProcedures(components);
            this.components.addAll(components);
        }
        return this;
    }

    public System addComponent(final SmlComponent component) {
        if (component != null) {
            checkAndSetChildProcedures(component);
            components.add(component);
        }
        return this;
    }

    public boolean isSetComponents() {
        return components != null && !components.isEmpty();
    }

    private void checkAndSetChildProcedures(final List<SmlComponent> components) {
        if (components != null) {
            for (final SmlComponent component : components) {
                checkAndSetChildProcedures(component);
            }
        }
    }

    private void checkAndSetChildProcedures(final SmlComponent component) {
        if (component != null && component.isSetName()
                && component.getName().contains(SensorMLConstants.ELEMENT_NAME_CHILD_PROCEDURES)) {
            addChildProcedure(component.getProcess());
        }
    }

}
