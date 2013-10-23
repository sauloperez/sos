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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlClassifierPredicates;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ClassifierEnrichment extends ProcedureDescriptionEnrichment {

    @Override
    public void enrich() throws OwsExceptionReport {
        if (procedureSettings().isGenerateClassification()) {
            addIntendedApplicationClassifier();
            addProcedureTypeClassification();
        }
    }

    private void addIntendedApplicationClassifier() {
        addClassifier(SmlClassifier.INTENDED_APPLICATION,
                      procedureSettings().getClassifierIntendedApplicationDefinition(),
                      procedureSettings().getClassifierIntendedApplicationValue());
    }

    private void addProcedureTypeClassification() {
        addClassifier(SmlClassifier.PROCEDURE_TYPE,
                      procedureSettings().getClassifierProcedureTypeDefinition(),
                      procedureSettings().getClassifierProcedureTypeValue());
    }

    private void addClassifier(String name, String definition, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            Predicate<SmlClassifier> p = SmlClassifierPredicates.name(name);
            if (!getSensorML().findClassifier(p).isPresent()) {
                SmlClassifier classifier = new SmlClassifier(name, definition, value);
                getSensorML().addClassification(classifier);
            }
        }
    }
}
