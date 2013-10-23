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
package org.n52.sos.config.sqlite;

import java.io.File;
import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.sos.ds.ConnectionProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When testing, make the sqlite config database a temp file and delete on cleanup.
 * 
 * @author Shane StClair
 *
 */
public class SQLiteSessionFactoryForTesting extends SQLiteSessionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteSessionFactoryForTesting.class); 
    protected static final String TESTING_DATABASE_NAME = "testing-configuration";
    protected static final String TESTING_CONNECTION_URL_TEMPLATE = "jdbc:sqlite:%s";    
    
    private File dbFile;
    
    @Override
    protected String getFilename() {
    	if (dbFile == null) {
            try {
            	dbFile = File.createTempFile(TESTING_DATABASE_NAME, ".db");
            } catch (IOException ioe) {
            	LOG.warn("Couldn't create testing sqlite config database in temp directory.");
            }        
            if (dbFile == null) {
            	dbFile = new File("target/" + TESTING_DATABASE_NAME + ".db");
                LOG.warn("Creating testing sqlite config database in target directory.");
            }    		
    	}
        return String.format(TESTING_CONNECTION_URL_TEMPLATE, dbFile.getAbsolutePath());
    }
    
    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            return getSessionFactory().openSession();
        } catch (HibernateException e) {
            throw new ConnectionProviderException(e);
        }
    }
    
    @Override
    public void returnConnection(Object connection) {
        try {
            if (connection instanceof Session) {
                Session session = (Session) connection;
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
            }
        } catch (HibernateException he) {
            LOG.error("Error while returning connection!", he);
        }
    }
    
    @Override
    public void cleanup() {
    	super.cleanup();
    	if (dbFile != null && dbFile.exists() && dbFile.canWrite()){
    		dbFile.delete();
    	}
    }    
}
