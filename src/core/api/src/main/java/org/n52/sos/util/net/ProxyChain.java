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
package org.n52.sos.util.net;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Representation of a proxy chain as found in HTTP {@code X-Forwarded-For}
 * header.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ProxyChain {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyChain.class);
    private final ImmutableList<IPAddress> proxies;
    private final IPAddress origin;

    /**
     * Creates a new chain from a origin (the original client) and all
     * intermediate proxies.
     *
     * @param origin  the origin
     * @param proxies the proxies
     */
    public ProxyChain(IPAddress origin, List<IPAddress> proxies) {
        Preconditions.checkArgument(origin != null && proxies != null);
        this.proxies = ImmutableList.copyOf(proxies);
        this.origin = origin;
    }

    /**
     * Creates a new chain from a list of addresses as found in the
     * {@code X-Forwarded-For} header. The list has to have at least one member.
     *
     * @param chain the chain
     */
    public ProxyChain(List<IPAddress> chain) {
        Preconditions.checkArgument(chain != null && !chain.isEmpty());
        this.origin = chain.get(0);
        this.proxies = ImmutableList.copyOf(chain.subList(1, chain.size()));
    }

    /**
     * Get the origin of the request (the clients address).
     *
     * @return the origin
     */
    public IPAddress getOrigin() {
        return origin;
    }

    /**
     * Get a list of all intermediate proxy servers.
     *
     * @return the proxies
     */
    public ImmutableList<IPAddress> getProxies() {
        return proxies;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOrigin(), getProxies());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProxyChain) {
            ProxyChain other = (ProxyChain) obj;
            return Objects.equal(getOrigin(), other.getOrigin()) &&
                   Objects.equal(getProxies(), other.getProxies());

        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("origin", getOrigin())
                .add("proxies", getProxies())
                .toString();
    }

    /**
     * Creates a Proxy chain from the {@code X-Forwarded-For} HTTP header.
     *
     * @param header the {@code X-Forwarded-For} header
     *
     * @return a {@code ProxyChain} if the header is present, non empty and well
     *         formed.
     */
    public static Optional<ProxyChain> fromForwardedForHeader(String header) {
        try {
            if (Strings.emptyToNull(header) != null) {
                String[] split = header.split(",");
                List<IPAddress> chain = Lists
                        .newArrayListWithExpectedSize(split.length);
                for (String splitted : split) {
                    chain.add(new IPAddress(splitted.trim()));
                }
                return Optional.of(new ProxyChain(chain));
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Ignoring invalid IP address in X-Forwared-For header: " +
                     header, e);
        }
        return Optional.absent();
    }
}