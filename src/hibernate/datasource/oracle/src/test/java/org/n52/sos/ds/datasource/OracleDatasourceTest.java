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

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @since 4.0.0
 *
 */
public class OracleDatasourceTest extends TestCase {
	private static final String SOS_TEST_CONF = "SOS_TEST_CONF";
	private static final String ORACLE_HOST = "oracle_host";
	private static final String ORACLE_PORT = "oracle_port";
	private static final String ORACLE_SCHEMA = "oracle_schema";
	private static final String ORACLE_USER = "oracle_user";
	private static final String ORACLE_PASS = "oracle_pass";
	private static final String ORACLE_USER_NO_RIGHTS = "oracle_user_no_rights";
	private static final String ORACLE_PASS_NO_RIGHTS = "oracle_pass_no_rights";

	private static String host, user, pass, schema, userNoRights, passNoRights;
	private static int port;

	static {
		initialize();
	}

	private static final void initialize() {
		String conf = System.getenv(SOS_TEST_CONF);
		if (conf == null) {
			throw new RuntimeException(
					"SOS_TEST_CONF environment variable not set!!");
		}

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(conf));
		} catch (IOException e) {
			throw new RuntimeException("Invalid SOS_TEST_CONF file: " + conf, e);
		}

		host = props.getProperty(ORACLE_HOST);
		port = Integer.parseInt(props.getProperty(ORACLE_PORT));
		schema = props.getProperty(ORACLE_SCHEMA);
		user = props.getProperty(ORACLE_USER);
		pass = props.getProperty(ORACLE_PASS);
		userNoRights = props.getProperty(ORACLE_USER_NO_RIGHTS);
		passNoRights = props.getProperty(ORACLE_PASS_NO_RIGHTS);
	}

	private OracleDatasource ds;
	private Connection conn, connNoRights;
	private Statement stmt, stmtNoRights;

	protected void setUp() throws Exception {
		ds = new OracleDatasource();
		Map<String, Object> settings = getDefaultSettings();
		conn = ds.openConnection(settings);
		stmt = conn.createStatement();

		settings = getDefaultSettings();
		settings.put(AbstractHibernateDatasource.USERNAME_KEY, userNoRights);
		settings.put(AbstractHibernateDatasource.PASSWORD_KEY, passNoRights);
		connNoRights = ds.openConnection(settings);
		stmtNoRights = connNoRights.createStatement();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (stmt != null && !stmt.isClosed()) {
			stmt.close();
		}
		if (stmtNoRights != null && !stmtNoRights.isClosed()) {
			stmtNoRights.close();
		}
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
		if (connNoRights != null && !connNoRights.isClosed()) {
			connNoRights.close();
		}
	}

	public void testSchemaCreationSuccess() throws Exception {
		ds.doCheckSchemaCreation("oracle", stmt);
	}

	public void testSchemaCreationFailure() throws Exception {
		try {
			ds.doCheckSchemaCreation("", stmtNoRights);
			fail();
		} catch (SQLException e) {
			// ignore
		}
	}

	@SuppressWarnings("unchecked")
	public void testCheckSchemaCreationSuccess() throws Exception {
		ds = spy(ds);
		Statement stmt = mock(Statement.class);
		Connection conn = mock(Connection.class);
		when(conn.createStatement()).thenReturn(stmt);
		doReturn(conn).when(ds).openConnection(anyMap());
		assertTrue(ds.checkSchemaCreation(new HashMap<String, Object>()));
	}

	@SuppressWarnings("unchecked")
	public void testCheckSchemaCreationFailure() throws Exception {
		ds = spy(ds);
		Statement stmt = mock(Statement.class);
		when(stmt.execute(anyString())).thenThrow(new SQLException());

		Connection conn = mock(Connection.class);
		when(conn.createStatement()).thenReturn(stmt);
		doReturn(conn).when(ds).openConnection(anyMap());

		assertFalse(ds.checkSchemaCreation(new HashMap<String, Object>()));
	}

	public void testToURL() throws Exception {
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(AbstractHibernateDatasource.HOST_KEY, "localhost");
		settings.put(AbstractHibernateDatasource.PORT_KEY, 1521);
		settings.put(AbstractHibernateDatasource.USERNAME_KEY, "oracle");
		settings.put(AbstractHibernateDatasource.PASSWORD_KEY, "oracle");
		settings.put(AbstractHibernateDatasource.DATABASE_KEY, "db");
		settings.put(AbstractHibernateDatasource.SCHEMA_KEY, "schema");

		assertEquals("jdbc:oracle:thin://localhost:1521/db", ds.toURL(settings));
	}

	public void testFromURL() throws Exception {
		String url = "jdbc:oracle:thin://localhost:1521/db";
		String[] parsed = ds.parseURL(url);

		assertEquals(3, parsed.length);
		assertEquals("localhost", parsed[0]);
		assertEquals("1521", parsed[1]);
		assertEquals("db", parsed[2]);
	}

	private static Map<String, Object> getDefaultSettings() {
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(AbstractHibernateDatasource.HOST_KEY, host);
		settings.put(AbstractHibernateDatasource.PORT_KEY, port);
		settings.put(AbstractHibernateDatasource.USERNAME_KEY, user);
		settings.put(AbstractHibernateDatasource.PASSWORD_KEY, pass);
		settings.put(AbstractHibernateDatasource.SCHEMA_KEY, schema);
		settings.put(AbstractHibernateDatasource.TRANSACTIONAL_KEY, true);
		return settings;
	}
}
