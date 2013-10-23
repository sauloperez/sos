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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.OperationDAORepository;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Activatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class RequestOperatorRepository extends AbstractConfiguringServiceLoaderRepository<RequestOperator> {
    private static final Logger LOG = LoggerFactory.getLogger(RequestOperatorRepository.class);

    private static RequestOperatorRepository instance;

    private final Map<RequestOperatorKey, Activatable<RequestOperator>> requestOperators =
            new HashMap<RequestOperatorKey, Activatable<RequestOperator>>(0);

    public static RequestOperatorRepository getInstance() {
        if (instance == null) {
            instance = new RequestOperatorRepository();
        }
        return instance;
    }

    /**
     * private constructor for singleton
     * 
     * @throws ConfigurationException
     */
    private RequestOperatorRepository() throws ConfigurationException {
        super(RequestOperator.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(Set<RequestOperator> requestOperators)
            throws ConfigurationException {
        this.requestOperators.clear();
        for (RequestOperator op : requestOperators) {
            try {
                LOG.info("Registered IRequestOperator for {}", op.getRequestOperatorKeyType());
                boolean active = SettingsManager.getInstance().isActive(op.getRequestOperatorKeyType());
                this.requestOperators
                        .put(op.getRequestOperatorKeyType(), new Activatable<RequestOperator>(op, active));
            } catch (ConnectionProviderException cpe) {
                throw new ConfigurationException("Error while checking RequestOperator", cpe);
            }
        }
    }

    @Override
    public void update() throws ConfigurationException {
        OperationDAORepository.getInstance().update();
        super.update();
    }

    public RequestOperator getRequestOperator(RequestOperatorKey key) {
        Activatable<RequestOperator> a = this.requestOperators.get(key);
        return a == null ? null : a.get();
    }

    public RequestOperator getRequestOperator(ServiceOperatorKey sok, String operationName) {
        return getRequestOperator(new RequestOperatorKey(sok, operationName));
    }

    public void setActive(RequestOperatorKey rokt, boolean active) {
        if (this.requestOperators.get(rokt) != null) {
            this.requestOperators.get(rokt).setActive(active);
        }
    }

    public Set<RequestOperatorKey> getActiveRequestOperatorKeys() {
        return Activatable.filter(this.requestOperators).keySet();
    }

    public Set<RequestOperatorKey> getAllRequestOperatorKeys() {
        return Collections.unmodifiableSet(this.requestOperators.keySet());
    }
}
