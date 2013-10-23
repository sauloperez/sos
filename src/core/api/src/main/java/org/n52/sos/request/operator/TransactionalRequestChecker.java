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

import java.util.List;
import java.util.Set;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.RequestContext;
import org.n52.sos.service.TransactionalSecurityConfiguration;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.net.IPAddress;
import org.n52.sos.util.net.IPAddressRange;
import org.n52.sos.util.net.ProxyChain;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class TransactionalRequestChecker {
    private final List<Predicate<RequestContext>> predicates;
    private Predicate<RequestContext> predicate;

    @SuppressWarnings("unchecked")
    public TransactionalRequestChecker(TransactionalSecurityConfiguration config) {
        this.predicates = Lists.newArrayList(createIpAdressPredicate(config),
                                             createTokenPredicate(config));
        this.predicate = Predicates.and(this.predicates);
    }

    public void add(Predicate<RequestContext> p) {
        this.predicates.add(p);
        this.predicate = Predicates.and(predicates);
    }

    public void check(RequestContext rc) throws OwsExceptionReport {
        if (!predicate.apply(rc)) {
            throw new NoApplicableCodeException()
                    .withMessage("Not authorized for transactional operations!")
                    .setStatus(HTTPStatus.UNAUTHORIZED);
                    
        }
    }

    private Predicate<RequestContext> createTokenPredicate(
            TransactionalSecurityConfiguration config) {
        if (!config.isTransactionalActive() ||
            !config.isSetTransactionalToken()) {
            return Predicates.alwaysTrue();
        } else {
            return new TokenPredicate(config.getTransactionalToken());
        }
    }

    private Predicate<RequestContext> createIpAdressPredicate(
            TransactionalSecurityConfiguration config) {
        if (!config.isTransactionalActive() ||
            !config.isSetTransactionalAllowedIps()) {
            return Predicates.alwaysTrue();
        } else {
            return new IpPredicate(config.getAllowedAddresses(),
                                   config.getAllowedProxies());
        }
    }

    private static class TokenPredicate implements Predicate<RequestContext> {
        private final String token;

        TokenPredicate(String token) {
            this.token = token;
        }

        @Override
        public boolean apply(RequestContext ctx) {
            return ctx.getToken().isPresent() &&
                   ctx.getToken().get().equals(this.token);
        }
    }

    private static class IpPredicate implements Predicate<RequestContext> {
        private final ImmutableSet<IPAddressRange> allowedAddresses;
        private final ImmutableSet<IPAddress> allowedProxies;

        IpPredicate(Set<IPAddressRange> allowedAddresses,
                    Set<IPAddress> allowedProxies) {
            this.allowedAddresses = ImmutableSet.copyOf(allowedAddresses);
            this.allowedProxies = ImmutableSet.copyOf(allowedProxies);
        }

        @Override
        public boolean apply(RequestContext ctx) {
            if (ctx.getIPAddress().isPresent()) {
                final IPAddress address;
                if (ctx.getForwardedForChain().isPresent()) {
                    if (!this.allowedProxies.contains(ctx.getIPAddress().get())) {
                        return false;
                    }
                    ProxyChain chain = ctx.getForwardedForChain().get();
                    for (IPAddress proxy : chain.getProxies()) {
                        if (!this.allowedProxies.contains(proxy)) {
                            return false;
                        }
                    }
                    address = chain.getOrigin();
                } else {
                    address = ctx.getIPAddress().get();
                }
                for (IPAddressRange range : this.allowedAddresses) {
                    if (range.contains(address)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}