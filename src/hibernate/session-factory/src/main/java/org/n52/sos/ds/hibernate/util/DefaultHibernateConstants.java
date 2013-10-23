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
package org.n52.sos.ds.hibernate.util;

/**
 * @since 4.0.0
 * 
 */
public interface DefaultHibernateConstants {

    // SQL query statements
    String SELECT = "SELECT ";

    String DISTINCT = " DISTINCT ";

    String FROM = " FROM ";

    String WHERE = " WHERE ";

    String AND = " AND ";

    /* dssos.config properties */
    String DRIVER_PROPERTY = "hibernate.connection.driver_class";

    String PASS_PROPERTY = "hibernate.connection.password";

    String USER_PROPERTY = "hibernate.connection.username";

    String CONNECTION_STRING_PROPERTY = "hibernate.connection.url";

    String CONNECTION_POOL_PROPERTY = "hibernate.connection.provider_class";

    String DIALECT_PROPERTY = "hibernate.dialect";

    String CLAZZ = "clazz";

    String CATALOG_PROPERTY = "hibernate.default_catalog";

}
