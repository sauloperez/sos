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

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Comparables {

    private Comparables() {
    }

    public static int compare(int x, int y) {
        return Ints.compare(x, y);
    }

    public static int compare(byte x, byte y) {
        return x - y;
    }

    public static int compare(short x, short y) {
        return Shorts.compare(x, y);
    }

    public static int compare(char x, char y) {
        return Chars.compare(x, y);
    }

    public static int compare(long x, long y) {
        return Longs.compare(x, y);
    }

    public static int compare(boolean x, boolean y) {
        return Booleans.compare(x, y);
    }

    public static int compare(float a, float b) {
        return Floats.compare(a, b);
    }

    public static int compare(double a, double b) {
        return Doubles.compare(a, b);
    }

    public static <T extends Comparable<T>> int compare(T a, T b) {
        return (a == b) ? 0 : ((a == null) ? -1 : ((b == null) ? 1 : a.compareTo(b)));
    }

    public static ComparisonChain chain(Object o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start();
    }
}
