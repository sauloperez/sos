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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.Inet4Address;

import com.google.common.base.Objects;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;

/**
 * Encapsulation of an IPv4 address.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IPAddress implements Comparable<IPAddress> {
    private final int address;

    /**
     * Creates a new IPAddress from an 32-Bit integer.
     *
     * @param address the address
     */
    public IPAddress(int address) {
        this.address = address;
    }

    /**
     * Creates a new IPAddress from an four element byte array.
     *
     * @param address the address
     */
    public IPAddress(byte[] address) {
        this(Ints.fromByteArray(address));
    }

    /**
     * Creates a new IPAddress from its string representation.
     *
     * @param address the address
     */
    public IPAddress(String address) {
        this(parse(address));
    }

    /**
     * Creates a new IPAddress from an {@link Inet4Address}.
     *
     * @param address the address
     */
    public IPAddress(Inet4Address address) {
        this(address.getAddress());
    }

    /**
     * @return the IP address as an 32-bit integer
     */
    public int asInt() {
        return this.address;
    }

    /**
     * @return the IP address as an {@code Inet4Address}
     */
    public Inet4Address asInetAddress() {
        return InetAddresses.fromInteger(this.address);
    }

    /**
     * @return the IP address as an 4 element byte array.
     */
    public byte[] asByteArray() {
        return Ints.toByteArray(this.address);
    }

    /**
     * @return the IP address as a string
     */
    public String asString() {
        return asInetAddress().getHostAddress();
    }

    @Override
    public int compareTo(IPAddress o) {
        return Ints.compare(this.address, checkNotNull(o).asInt());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.address);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPAddress) {
            IPAddress other = (IPAddress) obj;
            return this.address == other.asInt();
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }

    private static Inet4Address parse(String address) {
        try {
            return (Inet4Address) InetAddresses.forString(address);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("IPv6 addresses are not supported.", e);
        }
    }
}
