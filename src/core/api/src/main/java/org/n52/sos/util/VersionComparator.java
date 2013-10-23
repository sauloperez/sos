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

import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class VersionComparator implements Comparator<String> {
    private static final Pattern DELIMITER = Pattern.compile("[._-]");

    private static final Pattern EOF = Pattern.compile("\\z");

    private VersionComparator() {
    }

    @Override
    public int compare(String a, String b) {
        Scanner as = new Scanner(a).useDelimiter(DELIMITER);
        Scanner bs = new Scanner(b).useDelimiter(DELIMITER);
        while (as.hasNextInt() && bs.hasNextInt()) {
            int c = Comparables.compare(as.nextInt(), bs.nextInt());
            if (c != 0) {
                return c;
            }
        }
        if (as.hasNextInt()) {
            return 1;
        } else if (bs.hasNextInt()) {
            return -1;
        } else {
            boolean na = as.useDelimiter(EOF).hasNext();
            boolean nb = bs.useDelimiter(EOF).hasNext();
            if (na && nb) {
                return as.next().compareTo(bs.next());
            } else if (na) {
                return -1;
            } else if (nb) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static VersionComparator instance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final VersionComparator INSTANCE = new VersionComparator();

        private InstanceHolder() {
        }
    }
}
