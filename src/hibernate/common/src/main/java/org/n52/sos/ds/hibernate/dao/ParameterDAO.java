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
package org.n52.sos.ds.hibernate.dao;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Parameter;
import org.n52.sos.ogc.om.NamedValue;

/**
 * Hibernate DAO class to om:pramameter
 * 
 * @since 4.0.0
 * 
 */
public class ParameterDAO {

    /**
     * Insert a new om:parameter into the datasource
     * 
     * @param namedValue
     *            Parameter definition
     * @param observationId
     *            Related observation
     * @param session
     *            Hibernate session
     */
    public void insertParameter(NamedValue<?> namedValue, long observationId, Session session) {
        Parameter parameter = new Parameter();
        parameter.setObservationId(observationId);
        parameter.setDefinition(namedValue.getName().getHref());
        if (namedValue.getName().isSetTitle()) {
            parameter.setTitle(namedValue.getName().getTitle());
        }
        parameter.setValue(namedValue.getValue().getValue());
        session.saveOrUpdate(parameter);
        session.flush();

    }

}
