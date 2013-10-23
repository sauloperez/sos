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

import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class KeywordEnrichment extends ProcedureDescriptionEnrichment {
    @Override
    public void enrich() throws OwsExceptionReport {
        List<String> keywords = createKeywordsList();
        if (CollectionHelper.isNotEmpty(keywords)) {
            getSensorML().setKeywords(keywords);
        }
    }

    private List<String> createKeywordsList() {
        Set<String> keywords = Sets.newHashSet();
        addExisting(keywords);
        addObservableProperties(keywords);
        addIdentifier(keywords);
        addIntendedApplication(keywords);
        addProcedureType(keywords);
        addOfferings(keywords);
        addLongName(keywords);
        addShortName(keywords);
        addFeatures(keywords);
        return Lists.newArrayList(keywords);
    }

    private void addLongName(Set<String> keywords) {
        Optional<SmlIdentifier> longName = getSensorML()
                .findIdentification(longNamePredicate());
        if (longName.isPresent()) {
            keywords.add(longName.get().getName());
        }
    }

    private void addShortName(Set<String> keywords) {
        Optional<SmlIdentifier> shortName = getSensorML()
                .findIdentification(shortNamePredicate());
        if (shortName.isPresent()) {
            keywords.add(shortName.get().getName());
        }
    }

    private void addFeatures(Set<String> keywords) {
        if (procedureSettings().isEnrichWithFeatures() &&
            getSensorML().isSetFeaturesOfInterest()) {
            keywords.addAll(getSensorML().getFeaturesOfInterest());
        }
    }

    private void addOfferings(Set<String> keywords) {
        if (procedureSettings().isEnrichWithOfferings()) {
            keywords.addAll(getCache().getOfferingsForProcedure(getIdentifier()));
        }
    }

    private void addProcedureType(Set<String> keywords) {
        if (procedureSettings().isGenerateClassification() &&
            !procedureSettings().getClassifierProcedureTypeValue().isEmpty()) {
            keywords.add(procedureSettings().getClassifierProcedureTypeValue());
        }
    }

    private void addIntendedApplication(Set<String> keywords) {
        if (procedureSettings().isGenerateClassification() &&
            !procedureSettings().getClassifierIntendedApplicationValue().isEmpty()) {
            keywords.add(procedureSettings().getClassifierIntendedApplicationValue());
        }
    }

    private void addObservableProperties(Set<String> keywords) {
        keywords.addAll(getCache()
                .getObservablePropertiesForProcedure(getIdentifier()));
    }

    private void addExisting(Set<String> keywords) {
        if (getSensorML().isSetKeywords()) {
            keywords.addAll(getSensorML().getKeywords());
        }
    }

    private void addIdentifier(Set<String> keywords) {
        keywords.add(getIdentifier());
    }
}
