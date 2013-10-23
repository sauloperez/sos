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
package org.n52.sos.ds.hibernate;

import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.Stoppable;
import org.n52.sos.ds.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractSessionFactoryProvider implements ConnectionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSessionFactoryProvider.class);

    private final ReentrantLock lock = new ReentrantLock();

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.ds.ConnectionProvider#cleanup()
     */
    @Override
    public void cleanup() {
        lock.lock();
        SessionFactory sessionFactory = getSessionFactory();
        try {
            if (getSessionFactory() != null) {
                try {
                    if (SessionFactoryImpl.class.isInstance(sessionFactory)
                            && Stoppable.class.isInstance(((SessionFactoryImpl) sessionFactory)
                                    .getConnectionProvider())) {
                        ((Stoppable) ((SessionFactoryImpl) sessionFactory).getConnectionProvider()).stop();
                    }
                    sessionFactory.close();
                    LOG.info("Connection provider closed successfully!");
                } catch (HibernateException he) {
                    LOG.error("Error while closing connection provider!", he);
                }
            }
        } finally {
            sessionFactory = null;
            lock.unlock();
        }
    }

    protected abstract SessionFactory getSessionFactory();

}
