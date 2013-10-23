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

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Hibernate helper class.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public final class HibernateHelper {
    /**
     * Private constructor
     */
    private HibernateHelper() {

    }

    /**
     * Get the SQL query string from Criteria.
     * 
     * @param criteria
     *            Criteria to get SQL query string from
     * @return SQL query string from criteria
     */
    public static String getSqlString(Criteria criteria) {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        SessionImplementor session = criteriaImpl.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator =
                new CriteriaQueryTranslator(factory, criteriaImpl, criteriaImpl.getEntityOrClassName(),
                        CriteriaQueryTranslator.ROOT_SQL_ALIAS);
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());

        CriteriaJoinWalker walker =
                new CriteriaJoinWalker((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), translator,
                        factory, criteriaImpl, criteriaImpl.getEntityOrClassName(), session.getLoadQueryInfluencers());

        return walker.getSQLString();
    }

    /**
     * Get the SQL query string from HQL Query.
     * 
     * @param query
     *            HQL query to convert to SQL
     * @return SQL query string from HQL
     */
    public static String getSqlString(Query query, Session session) {
        final QueryTranslatorFactory ast = new ASTQueryTranslatorFactory();
        SessionFactory sessionFactory = session.getSessionFactory();
        final QueryTranslatorImpl qt =
                (QueryTranslatorImpl) ast.createQueryTranslator("id", query.getQueryString(), Maps.newHashMap(),
                        (SessionFactoryImplementor) sessionFactory);
        qt.compile(null, false);
        return qt.getSQLString();
    }

    public static boolean isEntitySupported(Class<?> clazz, Session session) {
        if (session.getSessionFactory() != null) {
            return session.getSessionFactory().getAllClassMetadata().keySet().contains(clazz.getName());
        }
        return false;
    }

    public static boolean isNamedQuerySupported(String namedQuery, Session session) {
        NamedQueryDefinition namedQueryDef = ((SessionImpl) session).getSessionFactory().getNamedQuery(namedQuery);
        NamedSQLQueryDefinition namedSQLQueryDef =
                ((SessionImpl) session).getSessionFactory().getNamedSQLQuery(namedQuery);
        if (namedQueryDef == null && namedSQLQueryDef == null) {
            return false;
        }
        return true;
    }

    public static Dialect getDialect(Session session) {
        return ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
    }

    public static List<List<Long>> getValidSizedLists(Collection<Long> queryIds) {
        List<Long> queryIdsList = Lists.newArrayList(queryIds);
        List<List<Long>> lists = Lists.newArrayList();
        if (queryIds.size() > HibernateConstants.LIMIT_EXPRESSION_DEPTH) {
            List<Long> ids = Lists.newArrayList();
            for (int i = 0; i < queryIds.size(); i++) {
                if (i != 0 && i % (HibernateConstants.LIMIT_EXPRESSION_DEPTH - 1) == 0) {
                    lists.add(ids);
                    ids = Lists.newArrayList();
                    ids.add(queryIdsList.get(i));
                } else {
                    ids.add(queryIdsList.get(i));
                }
            }
        } else {
            lists.add(queryIdsList);
        }
        return lists;
    }

    /**
     * Check if the requested function is supported by the requested dialect
     * 
     * @param dialect
     *            Dialect to check
     * @param function
     *            Function to check
     * @return <code>true</code>, if function is supported
     */
    public static boolean supportsFunction(Dialect dialect, String function) {
        return dialect.getFunctions().containsKey(function);
    }

}
