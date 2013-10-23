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
package org.n52.sos.ds.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import oracle.jdbc.OracleConnection;

import org.hibernate.spatial.dialect.oracle.ConnectionFinder;
import org.hibernate.spatial.helper.FinderException;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

/**
 * @since 4.0.0
 * 
 */
public class OracleC3P0ConnectionFinder implements ConnectionFinder {
    public static Connection getRawConnection(Connection con) {
        return con;
    }

    @Override
    public OracleConnection find(Connection con) throws FinderException {
        Connection conn = con;
        if (con instanceof Proxy) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(con);
                conn = (Connection) handler.invoke(con, con.getClass().getMethod("getWrappedObject"), null);
            } catch (Throwable e) {
                throw new FinderException(e.getMessage());
            }
        }

        if (conn instanceof OracleConnection) {
            return (OracleConnection) conn;
        }

        if (conn instanceof C3P0ProxyConnection) {
            C3P0ProxyConnection cpCon = (C3P0ProxyConnection) conn;
            Connection unwrappedCon = null;
            try {
                Method rawConnectionMethod =
                        getClass().getMethod("getRawConnection", new Class[] { Connection.class });
                unwrappedCon =
                        (Connection) cpCon.rawConnectionOperation(rawConnectionMethod, null,
                                new Object[] { C3P0ProxyConnection.RAW_CONNECTION });
            } catch (Throwable ex) {
                throw new FinderException(ex.getMessage());
            }
            if (unwrappedCon != null && unwrappedCon instanceof OracleConnection) {
                return (OracleConnection) unwrappedCon;
            }
        }
        throw new FinderException("Couldn't get Oracle Connection in OracleConnectionFinder");
    }
}
