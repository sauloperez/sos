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
package org.n52.sos.ds.hibernate.cache.base;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;
import org.n52.sos.ds.hibernate.cache.AbstractDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SridCacheUpdate extends AbstractDatasourceCacheUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SridCacheUpdate.class);
    
    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_POSTGIS = "getDefaultFeatureGeomSridPostgis";

    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_ORACLE = "getDefaultFeatureGeomSridOracle";

    private static final String SQL_QUERY_GET_DEFAULT_FEATURE_SRID_H2 = "getDefaultFeatureGeomSridGeoDB";

    private static final String SQL_QUERY_GET_EPSG_POSTGIS = "getEpsgPostgis";

    private static final String SQL_QUERY_GET_EPSG_ORACLE = "getEpsgOracle";

    private static final String SQL_QUERY_GET_EPSG_H2 = "getEpsgGeoDB";

    @Override
    public void execute() {
        // TODO change if transformation is supported
        // List<SpatialRefSys> spatialRefSyss =
        // getSpatialRefSysObjects(getSession());
        // List<Integer> srids = new ArrayList<Integer>(spatialRefSyss.size());
        // for (SpatialRefSys spatialRefSys : spatialRefSyss) {
        // srids.add(spatialRefSys.getSrid());
        // }
        // getCache().setSrids(srids);
        checkEpsgCode(getSession());
        if (!checkAndGetEpsgCodes(getSession())) {
            getCache().addEpsgCode(
                    Integer.valueOf(Configurator.getInstance().getFeatureQueryHandler().getDefaultEPSG()));
        }

    }

    @SuppressWarnings("unchecked")
    private boolean checkAndGetEpsgCodes(Session session) {
        Dialect dialect = HibernateHelper.getDialect(session);
        String namedQueryName = null;
        if (dialect instanceof PostgisDialect || dialect instanceof PostgreSQL81Dialect) {
            namedQueryName = SQL_QUERY_GET_EPSG_POSTGIS;
        } else if (dialect instanceof Oracle8iDialect) {
            namedQueryName = SQL_QUERY_GET_EPSG_ORACLE;
        } else if (dialect instanceof GeoDBDialect) {
            namedQueryName = SQL_QUERY_GET_EPSG_H2;
        }
        if (StringHelper.isNotEmpty(namedQueryName) && HibernateHelper.isNamedQuerySupported(namedQueryName, session)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}", namedQuery);
            getCache().addEpsgCodes(namedQuery.list());
            return true;
        }
        return false;
    }
    
    private void checkEpsgCode(Session session) {
        Dialect dialect = HibernateHelper.getDialect(session);
        String namedQueryName = null;
        if (dialect instanceof PostgisDialect || dialect instanceof PostgreSQL81Dialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_POSTGIS;
        } else if (dialect instanceof Oracle8iDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_ORACLE;
        } else if (dialect instanceof GeoDBDialect) {
            namedQueryName = SQL_QUERY_GET_DEFAULT_FEATURE_SRID_H2;
        }
        if (StringHelper.isNotEmpty(namedQueryName) && HibernateHelper.isNamedQuerySupported(namedQueryName, session)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}", namedQuery);
            Integer uniqueResult = (Integer) namedQuery.uniqueResult();
            if (GeometryHandler.getInstance().getDefaultEPSG() != uniqueResult) {
                GeometryHandler.getInstance().setDefaultEpsg(uniqueResult);
            }
        }
    }
}
