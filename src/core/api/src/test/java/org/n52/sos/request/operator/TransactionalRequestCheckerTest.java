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
package org.n52.sos.request.operator;

import static org.n52.sos.util.HasStatusCode.hasStatusCode;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.RequestContext;
import org.n52.sos.service.TransactionalSecurityConfiguration;
import org.n52.sos.util.net.IPAddress;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @since 4.0.0
 *
 */
public class TransactionalRequestCheckerTest {
    private static TransactionalSecurityConfiguration tsc =
            TransactionalSecurityConfiguration.getInstance();
    private static final IPAddress IP = new IPAddress("123.123.123.123");
    private static final String TOKEN = "I_HAVE_THE_PERMISSION";
    private static final IPAddress INVALID_IP = new IPAddress("234.234.234.234");
    private static final String INVALID_TOKEN = "YOU_ARE_NOT_ALLOWED";
    private static final String NULL = null;
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void shouldPass_NotActive()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(false);
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, true));
    }

    @Test
    public void shouldPass_ActiveIpNoToken_RcValidIpNoToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(NULL);
        new TransactionalRequestChecker(tsc).check(getRequestContextIp(true));
    }

    @Test
    public void shouldPass_ActiveNoIpToken_RcNoIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(NULL);
        tsc.setTransactionalToken(TOKEN);
        new TransactionalRequestChecker(tsc).check(getRequestContextToken(true));
    }

    @Test
    public void shouldPass_ActiveIpToken_RcValidIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, true));
    }

    @Test
    public void shouldException_ActiveIpNoToke_RcInvalidIpNoToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(NULL);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextIp(false));
    }

    @Test
    public void shouldException_ActiveNoIpToken_RcNoIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(NULL);
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextToken(false));
    }

    @Test
    public void shouldException_ActiveIpToke_RcInvalidIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(false, false));
    }

    @Test
    public void shouldException_ActiveIpToken_RcValidIpInvalidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(true, false));
    }

    @Test
    public void shouldException_ActiveIpToken_RcInvalidIpValidToken()
            throws OwsExceptionReport {
        tsc.setTransactionalActive(true);
        tsc.setTransactionalAllowedIps(IP.toString());
        tsc.setTransactionalToken(TOKEN);
        thrown.expect(OwsExceptionReport.class);
        thrown.expect(hasStatusCode(HTTPStatus.UNAUTHORIZED));
        new TransactionalRequestChecker(tsc).check(getRequestContextBoth(false, true));
    }

    private RequestContext getRequestContextIp(boolean validIp) {
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(validIp ? IP : INVALID_IP);
        return requestContext;
    }

    private RequestContext getRequestContextToken(boolean validToken) {
        RequestContext requestContext = new RequestContext();
        requestContext.setToken(validToken ? TOKEN : INVALID_TOKEN);
        return requestContext;
    }

    private RequestContext getRequestContextBoth(boolean validIp,
                                                 boolean validToken) {
        RequestContext requestContext = new RequestContext();
        requestContext.setIPAddress(validIp ? IP : INVALID_IP);
        requestContext.setToken(validToken ? TOKEN : INVALID_TOKEN);
        return requestContext;
    }
}
