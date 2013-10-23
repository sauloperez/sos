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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Helper class for String objects. Contains methods to join Strings, convert
 * streams to Strings or to check for null and emptiness.
 * 
 * @since 4.0.0
 * 
 */
public final class StringHelper {
    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer join(final CharSequence sep, final StringBuffer buff, final Iterable<?> src) {
        final Iterator<?> it = src.iterator();
        if (it.hasNext()) {
            buff.append(it.next());
        }
        while (it.hasNext()) {
            buff.append(sep).append(it.next());
        }
        return buff;
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String join(final CharSequence sep, final Iterable<?> src) {
        return join(sep, new StringBuffer(), src).toString();
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer join(final CharSequence sep, final StringBuffer buff, final Object... src) {
        return join(sep, buff, Arrays.asList(src));
    }

    /**
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String join(final CharSequence sep, final Object... src) {
        return join(sep, Arrays.asList(src));
    }

    /**
     * Joins iterable content as given to passed StringBuffer
     * 
     * @param buff
     *            StringBuffer to add iterable content
     * @param src
     *            Iterable to join
     * 
     * @return StringBuffer with joined iterable content
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer concat(final StringBuffer buff, final Iterable<?> src) {

        final Iterator<?> it = src.iterator();
        while (it.hasNext()) {
            buff.append(it.next());
        }
        return buff;
    }

    /**
     * Joins iterable content as given to a single String
     * 
     * @param src
     *            Iterable to join
     * 
     * @return String with joined iterable content
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String concat(final Iterable<?> src) {
        return concat(new StringBuffer(), src).toString();
    }

    /**
     * Joins objects as given to passed StringBuffer
     * 
     * @param buff
     *            StringBuffer to add Objects
     * @param src
     *            Objects to join
     * 
     * @return StringBuffer with joined Objects
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static StringBuffer concat(final StringBuffer buff, final Object... src) {
        try {
            return Joiner.on("").appendTo(buff, src);
        } catch (IOException ex) {
        }
        return buff;
    }

    /**
     * Joins objects as given to a single String
     * 
     * @param src
     *            Objects to join
     * 
     * @return Joined String
     * 
     * @deprecated use {@link Joiner}.
     */
    @Deprecated
    public static String concat(final Object... src) {
        return Joiner.on("").join(src);
    }

    /**
     * @param toNormalize
     *            the string to normalize
     * 
     * @return a normalized String for use in a file path, i.e. all
     *         [\,/,:,*,?,",<,>,;] characters are replaced by '_'.
     */
    public static String normalize(final String toNormalize) {
        // toNormalize = toNormalize.replaceAll("ä", "ae");
        // toNormalize = toNormalize.replaceAll("ö", "oe");
        // toNormalize = toNormalize.replaceAll("ü", "ue");
        // toNormalize = toNormalize.replaceAll("Ä", "AE");
        // toNormalize = toNormalize.replaceAll("Ö", "OE");
        // toNormalize = toNormalize.replaceAll("Ü", "UE");
        // toNormalize = toNormalize.replaceAll("ß", "ss");
        return toNormalize.replaceAll("[\\\\/:\\*?\"<>;,#%=@]", "_");
    }

    /**
     * Check if string is not null and not empty
     * 
     * @param string
     *            string to check
     * 
     * @return empty or not
     */
    public static boolean isNotEmpty(final String string) {
        return !Strings.isNullOrEmpty(string);
    }

    /**
     * Check if string is null or empty
     * 
     * @param string
     *            string to check
     * @return <tt>true</tt>, if the string is null or empty
     * @deprecated use {@link Strings#isNullOrEmpty(java.lang.String) }
     */
    @Deprecated
    public static boolean isNullOrEmpty(final String string) {
        return Strings.isNullOrEmpty(string);
    }

    public static String convertStreamToString(InputStream is, String charset) throws OwsExceptionReport {
        try {
            Scanner scanner;
            if (isNotEmpty(charset)) {
                scanner = new Scanner(is, charset);
            } else {
                scanner = new Scanner(is);
            }
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (NoSuchElementException nsee) {
            throw new NoApplicableCodeException().causedBy(nsee).withMessage(
                    "Error while reading content of HTTP request: %s", nsee.getMessage());
        }
        return "";
    }

    public static String convertStreamToString(InputStream is) throws OwsExceptionReport {
        return convertStreamToString(is, null);
    }

    private StringHelper() {
    }
}
