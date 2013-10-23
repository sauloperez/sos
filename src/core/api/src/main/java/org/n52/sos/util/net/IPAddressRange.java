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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

/**
 * Representation of an IPv4 address range based on an address and a subnet
 * mask.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IPAddressRange implements Predicate<IPAddress> {
    private static final int CIDR_MAX = 32;
    private static final int CIDR_MIN = 0;
    private final IPAddress address;
    private final IPAddress mask;

    /**
     * Creates a new address range from its string representation. This can be
     * either a CIDR or subnet notation.
     * <br/>
     * Examples:
     * <pre>
     * 192.168.1.1/24
     * 192.168.1.1/255.255.255.0
     * </pre>
     *
     * @param string the string representation
     */
    public IPAddressRange(String string) {
        Preconditions.checkNotNull(string);
        final String[] split = string.split("/", 2);
        Preconditions.checkArgument(split.length == 2,
                                    "Not a valid CIDR address!");
        address = new IPAddress(split[0]);
        final Integer cidr = Ints.tryParse(split[1]);
        if (cidr != null) {
            Preconditions.checkArgument(cidr.intValue() >= CIDR_MIN &&
                                        cidr.intValue() <= CIDR_MAX,
                                        "Not a valid CIDR address!");
            mask = new IPAddress(-1 << (CIDR_MAX - cidr.intValue()));
        } else {
            mask = new IPAddress(split[1]);
        }
    }

    /**
     * Creates a new address range from an address an a subnet mask.
     *
     * @param address the address
     * @param mask    the subnet mask
     */
    public IPAddressRange(IPAddress address, IPAddress mask) {
        this.address = checkNotNull(address);
        this.mask = checkNotNull(mask);
    }

    /**
     * @return the IP address
     */
    public IPAddress getAddress() {
        return address;
    }

    /**
     * @return the subnet mask
     */
    public IPAddress getSubnetMask() {
        return mask;
    }

    /**
     * @return the highest IP address in this range
     */
    public IPAddress getHigh() {
        return new IPAddress(getLow().asInt() + (~getSubnetMask().asInt()));
    }

    /**
     * @return the lowest IP address in this range
     */
    public IPAddress getLow() {
        return new IPAddress(getAddress().asInt() & getSubnetMask().asInt());
    }

    /**
     * Checks if a given IP address is in this range.
     *
     * @param ip the address
     *
     * @return whether this range contains the address
     */
    public boolean contains(IPAddress ip) {
        return ip.compareTo(getLow()) >= 0 &&
               ip.compareTo(getHigh()) <= 0;
    }

    /**
     * @return this address range as a {@link Range}
     */
    public Range<IPAddress> asRange() {
        return Range.closed(getLow(), getHigh());
    }

    @Override
    public boolean apply(IPAddress input) {
        return contains(input);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAddress(), getSubnetMask());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPAddressRange) {
            IPAddressRange other = (IPAddressRange) obj;
            return Objects.equal(getAddress(), other.getAddress()) &&
                   Objects.equal(getSubnetMask(), other.getSubnetMask());
        }
        return false;
    }

    @Override
    public String toString() {
        return getAddress() + "/" + getSubnetMask();
    }
}
