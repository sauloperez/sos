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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.HibernateObservationBuilder;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 */
// Don't execute during normal builds. This test should be used for manual query
// performance evaluation.
public class CacheQueryTest extends HibernateTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheQueryTest.class);

    private enum QueryType {
        OBSERVATION_INFO, OBSERVATION, HQL
    }

    @BeforeClass
    public static void fillObservations() {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            HibernateObservationBuilder b = new HibernateObservationBuilder(session);
            DateTime begin = new DateTime();
            int numObs = 10000;
            for (int i = 0; i < numObs; ++i) {
                if (i % 50 == 0) {
                    LOGGER.debug("Creating test observation {} of {}", i, numObs);
                    session.flush();
                    session.clear();
                }
                b.createObservation(String.valueOf(i), begin.plusHours(i));
            }
            LOGGER.debug("Creating test observation {} of {}", numObs, numObs);
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
    }

    @AfterClass
    public static void clearObservations() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();
            ScrollableIterable<Observation> i =
                    ScrollableIterable.fromCriteria(session.createCriteria(Observation.class));
            for (Observation o : i) {
                session.delete(o);
            }
            i.close();
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            returnSession(session);
        }
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void runtimeComparisonObservationQueries() throws ConnectionProviderException {
        // run the query types in random order
        List<QueryType> queryTypes = Arrays.asList(QueryType.values());

        // run each query once and discard the result to warm up hibernate
        getFoiForOfferingObservationInfoTime();
        getFoiForOfferingObservationTime();
        getFoiForOfferingHqlTime();

        // note: performance tests on multiple types of queries seem to be
        // affected by each other (first run is slower,
        // subsequent queries are very fast).

        Map<QueryType, Long> resultTimes = new HashMap<QueryType, Long>();

        final int runs = 100;
        for (int i = 0; i < runs; i++) {
            Collections.shuffle(queryTypes);
            for (QueryType qt : queryTypes) {
                LOGGER.info("Running foiForOffering query: " + qt.name());
                switch (qt) {
                case OBSERVATION_INFO:
                    addToResultTimeMap(resultTimes, qt, getFoiForOfferingObservationInfoTime());
                    break;
                case OBSERVATION:
                    addToResultTimeMap(resultTimes, qt, getFoiForOfferingObservationTime());
                    break;
                case HQL:
                    addToResultTimeMap(resultTimes, qt, getFoiForOfferingHqlTime());
                    break;
                }
            }
        }

        long observationInfoTime = resultTimes.get(QueryType.OBSERVATION_INFO) / runs;
        long observationTime = resultTimes.get(QueryType.OBSERVATION) / runs;
        long hqlTime = resultTimes.get(QueryType.HQL) / runs;

        LOGGER.info("foi for offering, new way {} ms, the arithmetic mean of {} runs", observationInfoTime, runs);
        LOGGER.info("foi for offering, old way {} ms, the arithmetic mean of {} runs", observationTime, runs);
        LOGGER.info("foi for offering, hql way {} ms, the arithmetic mean of {} runs", hqlTime, runs);

        // note: this will fail for very low numbers of observations (old way is
        // faster)
        assertTrue("Old way is faster", observationInfoTime < observationTime);

        // note: hql is faster!
        // assertTrue("HQL is faster", observationInfoTime < hqlTime);
    }

    private void addToResultTimeMap(Map<QueryType, Long> resultTimes, QueryType qt,
            long foiForOfferingObservationInfoTime) {
        if (resultTimes.containsKey(qt)) {
            foiForOfferingObservationInfoTime += resultTimes.get(qt);
        }
        resultTimes.put(qt, foiForOfferingObservationInfoTime);

    }

    private long getFoiForOfferingObservationInfoTime() {
        // new way using ObservationInfo class (excludes value table joins)
        Session session = getSession();
        long start = System.currentTimeMillis();
        Criteria c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
        c.createCriteria(Observation.FEATURE_OF_INTEREST).setProjection(
                Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        c.createCriteria(Observation.OFFERINGS).add(
                Restrictions.eq(Offering.IDENTIFIER, HibernateObservationBuilder.OFFERING_1));
        c.list();
        long time = System.currentTimeMillis() - start;
        LOGGER.debug("QUERY get featureOfInterest identifiers for offering new way: {}",
                HibernateHelper.getSqlString(c));
        returnSession(session);
        return time;
    }

    private long getFoiForOfferingObservationTime() {
        // old way using full Observation class (includes value table joins)
        Session session = getSession();
        long start = System.currentTimeMillis();
        final Criteria c = session.createCriteria(Observation.class).add(Restrictions.eq(Observation.DELETED, false));
        c.createCriteria(Observation.FEATURE_OF_INTEREST).setProjection(
                Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        c.createCriteria(Observation.OFFERINGS).add(
                Restrictions.eq(Offering.IDENTIFIER, HibernateObservationBuilder.OFFERING_1));
        c.list();
        long time = System.currentTimeMillis() - start;
        LOGGER.debug("QUERY get featureOfInterest identifiers for offering old way: {}",
                HibernateHelper.getSqlString(c));
        returnSession(session);
        return time;
    }

    private long getFoiForOfferingHqlTime() {
        // hql method
        Session session = getSession();
        long start = System.currentTimeMillis();
        Query query =
                session.createQuery(
                        "select distinct foi." + FeatureOfInterest.IDENTIFIER + " from Observation o" + " join o."
                                + Observation.OFFERINGS + " offs " + " join o." + Observation.FEATURE_OF_INTEREST
                                + " foi" + " where o.deleted = 'F' and offs." + Offering.IDENTIFIER + " = :offering")
                        .setString("offering", HibernateObservationBuilder.OFFERING_1);
        query.list();
        long time = System.currentTimeMillis() - start;
        LOGGER.debug("QUERY get featureOfInterest identifiers for offering HQL way: {}",
                HibernateHelper.getSqlString(query, session));
        return time;
    }
}
