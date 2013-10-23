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
package org.n52.sos.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;

import com.google.common.collect.Sets;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class ConverterRepository extends AbstractConfiguringServiceLoaderRepository<Converter> {
    private static ConverterRepository instance;

    public static ConverterRepository getInstance() {
        if (instance == null) {
            instance = new ConverterRepository();
        }
        return instance;
    }

    private final Map<ConverterKeyType, Converter<?, ?>> converter = new HashMap<ConverterKeyType, Converter<?, ?>>(0);

    public ConverterRepository() {
        super(Converter.class, false);
        load(false);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void processConfiguredImplementations(Set<Converter> converter) throws ConfigurationException {
        this.converter.clear();
        for (Converter<?, ?> aConverter : converter) {
            for (ConverterKeyType converterKeyType : aConverter.getConverterKeyTypes()) {
                this.converter.put(converterKeyType, aConverter);
            }
        }
        // TODO check for encoder/decoder used by converter
    }

    public <T, F> Converter<T, F> getConverter(String fromNamespace, String toNamespace) {
        return getConverter(new ConverterKeyType(fromNamespace, toNamespace));
    }

    @SuppressWarnings("unchecked")
    public <T, F> Converter<T, F> getConverter(ConverterKeyType key) {
        return (Converter<T, F>) converter.get(key);
    }

    /**
     * Get all namespaces for which a converter is available to convert from
     * requested format to default format
     * 
     * @param toNamespace
     *            Requested format
     * @return Swt with all possible formats
     */
    public Set<String> getFromNamespaceConverterTo(String toNamespace) {
        Set<String> fromNamespaces = Sets.newHashSet();
        for (ConverterKeyType converterKey : converter.keySet()) {
            if (toNamespace.equals(converterKey.getToNamespace())) {
                fromNamespaces.add(converterKey.getFromNamespace());
            }
        }
        return fromNamespaces;
    }

    /**
     * Checks if a converter is available to convert the stored object from the
     * default format to the requested format
     * 
     * @param fromNamespace
     *            Default format
     * @param toNamespace
     *            Requested fromat
     * @return If a converter is available
     */
    public boolean hasConverter(String fromNamespace, String toNamespace) {
        return getConverter(new ConverterKeyType(fromNamespace, toNamespace)) != null;
    }
}
