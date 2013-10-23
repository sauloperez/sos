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
package org.n52.sos.util.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MediaType implements Comparable<MediaType> {
    private static final String WILDCARD_TYPE = "*";

    private static final String QUALITY_PARAMETER = "q";

    private static final ImmutableListMultimap<String, String> EMPTY_MULTI_MAP = ImmutableListMultimap.of();

    private final com.google.common.net.MediaType delegate;

    /**
     * Constructs a <code>*&#47;*</code> media type.
     */
    public MediaType() {
        this(WILDCARD_TYPE, WILDCARD_TYPE, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;*</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     */
    public MediaType(String type) {
        this(type, WILDCARD_TYPE, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;subtype</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     */
    public MediaType(String type, String subtype) {
        this(type, subtype, EMPTY_MULTI_MAP);
    }

    /**
     * Constructs a <code>type&#47;subtype;parameter="name"</code> media type.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     * @param parameter
     *            the parameter
     * @param parameterValue
     *            the parameter value
     */
    public MediaType(String type, String subtype, String parameter, String parameterValue) {
        this(type, subtype, ImmutableListMultimap.of(checkNotNull(parameter).toLowerCase(),
                checkNotNull(parameterValue)));
    }

    /**
     * Constructs a media type using the supplied parameters.
     * 
     * @param type
     *            the type (may be <code>null</code> for a wild card)
     * @param subtype
     *            the subtype (may be <code>null</code> for a wild card)
     * @param parameters
     *            the parameter map
     */
    public MediaType(String type, String subtype, Multimap<String, String> parameters) {
        this(com.google.common.net.MediaType.create(type, subtype).withParameters(parameters));
    }

    private MediaType(com.google.common.net.MediaType mediaType) {
        this.delegate = mediaType;
    }

    public String getType() {
        return getDelegate().type();
    }

    public String getSubtype() {
        return getDelegate().subtype();
    }

    public ImmutableListMultimap<String, String> getParameters() {
        return getDelegate().parameters();
    }

    public boolean isWildcard() {
        return isWildcardType() && isWildcardSubtype();
    }

    public boolean isWildcardType() {
        return getType().equals(WILDCARD_TYPE);
    }

    public boolean isWildcardSubtype() {
        return getSubtype().equals(WILDCARD_TYPE);
    }

    public boolean isCompatible(MediaType other) {
        return getDelegate().is(other.getDelegate());
    }

    public List<String> getParameter(String parameter) {
        return getParameters().get(parameter.toLowerCase());
    }

    public boolean hasParameter(String parameter) {
        return getParameters().containsKey(parameter.toLowerCase());
    }

    public float getQuality() {
        if (hasParameter(QUALITY_PARAMETER)) {
            return Float.valueOf(getParameter(QUALITY_PARAMETER).get(0));
        } else {
            return 1;
        }
    }

    public MediaType withType(String type) {
        return new MediaType(type, getSubtype(), getParameters());

    }

    public MediaType withSubType(String subtype) {
        return new MediaType(getType(), subtype, getParameters());
    }

    public boolean hasParameters() {
        return getParameters().isEmpty();
    }

    public MediaType withParameter(String parameter, String value) {
        return new MediaType(getDelegate().withParameter(value, value));
    }

    public MediaType withParameters(Multimap<String, String> parameters) {
        return new MediaType(getType(), getSubtype(), parameters);
    }

    public MediaType withoutParameter(String parameter) {
        if (!hasParameter(parameter)) {
            return this;
        }
        ArrayListMultimap<String, String> parameters = ArrayListMultimap.create(getParameters());
        parameters.removeAll(parameter);
        return new MediaType(getDelegate().withParameters(parameters));
    }

    public MediaType withoutParameters() {
        if (!hasParameters()) {
            return this;
        }
        return new MediaType(getDelegate().withoutParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDelegate());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MediaType) {
            MediaType other = (MediaType) obj;
            return Objects.equal(getDelegate(), other.getDelegate());
        }
        return false;
    }

    @Override
    public int compareTo(MediaType o) {
        checkNotNull(o);
        return ComparisonChain.start().compare(getType(), o.getType()).compare(getSubtype(), o.getSubtype()).result();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    public static MediaType parse(String string) {
        Preconditions.checkArgument(string != null);
        return new MediaType(com.google.common.net.MediaType.parse(string.trim()));
    }

    private com.google.common.net.MediaType getDelegate() {
        return delegate;
    }
}