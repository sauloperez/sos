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
package org.n52.sos.ogc.sensorML.elements;

import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SmlIdentifierPredicates {
    private SmlIdentifierPredicates() {
    }

    public static Predicate<SmlIdentifier> name(String name) {
        return new NamePredicate(name);
    }

    public static Predicate<SmlIdentifier> definition(String definition) {
        return new DefinitionPredicate(definition);
    }

    public static Predicate<SmlIdentifier> nameOrDefinition(String name,
                                                            String definition) {
        return Predicates.or(name(name), definition(definition));
    }

    public static Predicate<SmlIdentifier> nameAndDefinition(String name,
                                                             String definition) {
        return Predicates.and(name(name), definition(definition));
    }

    private static class DefinitionPredicate implements Predicate<SmlIdentifier> {
        private final String definition;

        DefinitionPredicate(String definition) {
            this.definition = definition;
        }

        @Override
        public boolean apply(SmlIdentifier input) {
            return input.isSetDefinition() &&
                   input.getDefinition().equalsIgnoreCase(definition);
        }
    }

    private static class NamePredicate implements Predicate<SmlIdentifier> {
        private final String name;

        NamePredicate(String name) {
            this.name = name;
        }

        @Override
        public boolean apply(SmlIdentifier input) {
            return input.isSetName() &&
                   input.getName().equalsIgnoreCase(name);
        }
    }
}
