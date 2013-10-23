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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasValue;

/**
 * @since 4.0.0
 * 
 */
public class Parameter implements Serializable, HasValue<Object> {

    private static final long serialVersionUID = -5823568610931042841L;

    private long parameterId;

    private long observationId;

    private String definition;

    private String title;

    private Object value;

    public Parameter() {
        super();
    }

    public long getParameterId() {
        return parameterId;
    }

    public void setParameterId(long parameterId) {
        this.parameterId = parameterId;
    }

    public long getObservationId() {
        return observationId;
    }

    public Parameter setObservationId(long observationId) {
        this.observationId = observationId;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public Parameter setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Parameter setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

}
