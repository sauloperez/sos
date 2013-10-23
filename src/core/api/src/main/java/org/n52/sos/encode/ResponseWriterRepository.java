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
package org.n52.sos.encode;

import static org.n52.sos.util.ClassHelper.getSimiliarity;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.ClassHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Comparables;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public class ResponseWriterRepository extends AbstractConfiguringServiceLoaderRepository<ResponseWriter> {
    private static ResponseWriterRepository instance;

    private final Map<Class<?>, ResponseWriter<?>> writersByClass = CollectionHelper.synchronizedMap();

    private final Set<ResponseWriter<?>> writers = CollectionHelper.synchronizedSet();

    public ResponseWriterRepository() {
        super(ResponseWriter.class, false);
        load(false);
    }

    public static ResponseWriterRepository getInstance() {
        if (instance == null) {
            instance = new ResponseWriterRepository();
        }
        return instance;
    }

    @Override
    protected void processConfiguredImplementations(Set<ResponseWriter> implementations) throws ConfigurationException {
        this.writersByClass.clear();
        this.writers.clear();
        for (ResponseWriter<?> i : implementations) {
            this.writers.add(i);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseWriter<T> getWriter(Class<? extends T> clazz) {
        if (!writersByClass.containsKey(clazz)) {
            Set<ResponseWriter<?>> compatible = Sets.newHashSet();
            for (ResponseWriter<?> w : writers) {
                if (ClassHelper.getSimiliarity(w.getType(), clazz) >= 0) {
                    compatible.add(w);
                }
            }
            writersByClass.put(clazz, chooseWriter(compatible, clazz));
        }
        return (ResponseWriter<T>) writersByClass.get(clazz);
    }

    private ResponseWriter<?> chooseWriter(Set<ResponseWriter<?>> compatible, Class<?> clazz) {
        return compatible.isEmpty() ? null : Collections.min(compatible, new ResponseWriterComparator(clazz));
    }

    private class ResponseWriterComparator implements Comparator<ResponseWriter<?>> {
        private final Class<?> clazz;

        ResponseWriterComparator(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public int compare(ResponseWriter<?> o1, ResponseWriter<?> o2) {
            return Comparables.compare(getSimiliarity(o1.getType(), clazz), getSimiliarity(o2.getType(), clazz));
        }
    }
}
