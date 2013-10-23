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
package org.n52.sos.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;

import com.google.common.base.Strings;

/**
 * Utility class for Key-Value-Pair (KVP) requests
 * 
 * @since 4.0.0
 * 
 */
public final class KvpHelper {
    private KvpHelper() {
    }

    public static Map<String, String> getKvpParameterValueMap(HttpServletRequest req) {
        Map<String, String> kvp = new HashMap<String, String>();
        Enumeration<?> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            // all key names to lower case
            String key = (String) parameterNames.nextElement();
            kvp.put(key.replace("amp;", "").toLowerCase(), req.getParameter(key));
        }
        return kvp;
    }

    public static String checkParameterSingleValue(String value, String name) throws MissingParameterValueException,
            InvalidParameterValueException {
        if (checkParameterMultipleValues(value, name).size() == 1) {
            return value;
        } else {
            throw new InvalidParameterValueException(name, value);
        }
    }

    public static String checkParameterSingleValue(String value, Enum<?> name) throws MissingParameterValueException,
            InvalidParameterValueException {
        return checkParameterSingleValue(value, name.name());
    }

    public static List<String> checkParameterMultipleValues(String values, String name)
            throws MissingParameterValueException, InvalidParameterValueException {
        if (values.isEmpty()) {
            throw new MissingParameterValueException(name);
        }
        List<String> splittedParameterValues = Arrays.asList(values.split(","));
        for (String parameterValue : splittedParameterValues) {
            if (Strings.isNullOrEmpty(parameterValue)) {
                throw new MissingParameterValueException(name);
            }
        }
        return splittedParameterValues;
    }

    public static List<String> checkParameterMultipleValues(String values, Enum<?> name)
            throws MissingParameterValueException, InvalidParameterValueException {
        return checkParameterMultipleValues(values, name.name());
    }

    public static void checkParameterMultipleValues(List<String> values, String name)
            throws MissingParameterValueException {
        if (CollectionHelper.isEmpty(values)) {
            throw new MissingParameterValueException(name);
        }
        for (String parameterValue : values) {
            if (Strings.isNullOrEmpty(parameterValue)) {
                throw new MissingParameterValueException(name);
            }
        }
    }

    public static void checkParameterValue(String value, String name) throws MissingParameterValueException,
            InvalidParameterValueException {
        if (Strings.isNullOrEmpty(value)) {
            throw new MissingParameterValueException(name);
        }
    }

    public static void checkParameterValue(String value, Enum<?> name) throws MissingParameterValueException,
            InvalidParameterValueException {
        checkParameterValue(value, name.name());
    }

    private static String getParameterValue(String name, Map<String, String> map) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return map.get(key);
            }
        }
        return null;
    }

    public static String getParameterValue(Enum<?> name, Map<String, String> map) {
        return getParameterValue(name.name(), map);
    }
}
