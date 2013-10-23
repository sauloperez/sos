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

/**
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2006.<br />
 * Licensed under the Aduna BSD-style license.<br />
 * Author not defined in source!<br />
 * <ol>
 * <li><a href=
 * "http://www.java2s.com/Code/Java/XML/CheckswhetherthesuppliedStringisanNCNameNamespaceClassifiedName.htm"
 * >Source1</a></li>
 * <li><a href=
 * "http://www.openrdf.org/doc/sesame2/api/index.html?info/aduna/xml/XMLUtil.html"
 * >Source2</a></li>
 * </ol>
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public final class NcNameResolver implements NcNameResolverConstants {

    /**
     * Checks whether the supplied String is an NCName (Namespace Classified
     * Name) as specified at <a
     * href="http://www.w3.org/TR/REC-xml-names/#NT-NCName">
     * http://www.w3.org/TR/REC-xml-names/#NT-NCName</a>.
     * 
     * @param name
     *            the {@link java.lang.String String} to test
     * @return <code>true</code>, if the given {@link java.lang.String String}
     *         is a NCName,<br >
     *         else <code>false</code>.
     */
    public static boolean isValidNCName(String name) {
        int nameLength = name.length();

        if (nameLength == 0) {
            return false;
        }

        // Check first character
        char c = name.charAt(0);

        if (c == '_' || isLetter(c)) {
            // Check the rest of the characters
            for (int i = 1; i < nameLength; i++) {
                c = name.charAt(i);
                if (!isNCNameChar(c)) {
                    return false;
                }
            }

            // All characters have been checked
            return true;
        }

        return false;
    }

    /**
     * Replaces all invalid characters with an '_' to make it a valid NCName.
     * 
     * @param nonNcName
     *            an invalid ncName.
     * @return a valid ncName
     * @see <a
     *      href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#NCName">http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#NCName</a>
     * @see <a
     *      href="http://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">http://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName</a>
     */
    public static String fixNcName(String nonNcName) {
        if (isValidNCName(nonNcName)) {
            return nonNcName;
        }

        StringBuilder sb = new StringBuilder();

        // Check the characters
        for (int i = 0; i < nonNcName.length(); i++) {
            char currentChar = nonNcName.charAt(i);
            if (NcNameResolver.isNCNameChar(currentChar)) {
                sb.append(currentChar);
            } else {
                sb.append(UNDERSCORE_CHAR);
            }
        }
        return sb.toString();
    }

    private static boolean isNCNameChar(char c) {
        return isAsciiBaseChar(c) || isAsciiDigit(c) || c == DOT_CHAR || c == DASH_CHAR || c == UNDERSCORE_CHAR
                || isNonAsciiBaseChar(c) || isNonAsciiDigit(c) || isIdeographic(c) || isCombiningChar(c)
                || isExtender(c);
    }

    private static boolean isLetter(char c) {
        return isAsciiBaseChar(c) || isNonAsciiBaseChar(c) || isIdeographic(c);
    }

    private static boolean isAsciiBaseChar(char c) {
        return charInRange(c, VALUE_0X0041, VALUE_0X005A) || charInRange(c, VALUE_0X0061, VALUE_0X007A);
    }

    private static boolean isNonAsciiBaseChar(char c) {
        return charInRange(c, VALUE_0X00C0, VALUE_0X00D6) || charInRange(c, VALUE_0X00D8, VALUE_0X00F6)
                || charInRange(c, VALUE_0X00F8, VALUE_0X00FF) || charInRange(c, VALUE_0X0100, VALUE_0X0131)
                || charInRange(c, VALUE_0X0134, VALUE_0X013E) || charInRange(c, VALUE_0X0141, VALUE_0X0148)
                || charInRange(c, VALUE_0X014A, VALUE_0X017E) || charInRange(c, VALUE_0X0180, VALUE_0X01C3)
                || charInRange(c, VALUE_0X01CD, VALUE_0X01F0) || charInRange(c, VALUE_0X01F4, VALUE_0X01F5)
                || charInRange(c, VALUE_0X01FA, VALUE_0X0217) || charInRange(c, VALUE_0X0250, VALUE_0X02A8)
                || charInRange(c, VALUE_0X02BB, VALUE_0X02C1) || c == VALUE_0X0386
                || charInRange(c, VALUE_0X0388, VALUE_0X038A) || c == VALUE_0X038C
                || charInRange(c, VALUE_0X038E, VALUE_0X03A1) || charInRange(c, VALUE_0X03A3, VALUE_0X03CE)
                || charInRange(c, VALUE_0X03D0, VALUE_0X03D6) || c == VALUE_0X03DA || c == VALUE_0X03DC
                || c == VALUE_0X03DE || c == VALUE_0X03E0 || charInRange(c, VALUE_0X03E2, VALUE_0X03F3)
                || charInRange(c, VALUE_0X0401, VALUE_0X040C) || charInRange(c, VALUE_0X040E, VALUE_0X044F)
                || charInRange(c, VALUE_0X0451, VALUE_0X045C) || charInRange(c, VALUE_0X045E, VALUE_0X0481)
                || charInRange(c, VALUE_0X0490, VALUE_0X04C4) || charInRange(c, VALUE_0X04C7, VALUE_0X04C8)
                || charInRange(c, VALUE_0X04CB, VALUE_0X04CC) || charInRange(c, VALUE_0X04D0, VALUE_0X04EB)
                || charInRange(c, VALUE_0X04EE, VALUE_0X04F5) || charInRange(c, VALUE_0X04F8, VALUE_0X04F9)
                || charInRange(c, VALUE_0X0531, VALUE_0X0556) || c == VALUE_0X0559
                || charInRange(c, VALUE_0X0561, VALUE_0X0586) || charInRange(c, VALUE_0X05D0, VALUE_0X05EA)
                || charInRange(c, VALUE_0X05F0, VALUE_0X05F2) || charInRange(c, VALUE_0X0621, VALUE_0X063A)
                || charInRange(c, VALUE_0X0641, VALUE_0X064A) || charInRange(c, VALUE_0X0671, VALUE_0X06B7)
                || charInRange(c, VALUE_0X06BA, VALUE_0X06BE) || charInRange(c, VALUE_0X06C0, VALUE_0X06CE)
                || charInRange(c, VALUE_0X06D0, VALUE_0X06D3) || c == VALUE_0X06D5
                || charInRange(c, VALUE_0X06E5, VALUE_0X06E6) || charInRange(c, VALUE_0X0905, VALUE_0X0939)
                || c == VALUE_0X093D || charInRange(c, VALUE_0X0958, VALUE_0X0961)
                || charInRange(c, VALUE_0X0985, VALUE_0X098C) || charInRange(c, VALUE_0X098F, VALUE_0X0990)
                || charInRange(c, VALUE_0X0993, VALUE_0X09A8) || charInRange(c, VALUE_0X09AA, VALUE_0X09B0)
                || c == VALUE_0X09B2 || charInRange(c, VALUE_0X09B6, VALUE_0X09B9)
                || charInRange(c, VALUE_0X09DC, VALUE_0X09DD) || charInRange(c, VALUE_0X09DF, VALUE_0X09E1)
                || charInRange(c, VALUE_0X09F0, VALUE_0X09F1) || charInRange(c, VALUE_0X0A05, VALUE_0X0A0A)
                || charInRange(c, VALUE_0X0A0F, VALUE_0X0A10) || charInRange(c, VALUE_0X0A13, VALUE_0X0A28)
                || charInRange(c, VALUE_0X0A2A, VALUE_0X0A30) || charInRange(c, VALUE_0X0A32, VALUE_0X0A33)
                || charInRange(c, VALUE_0X0A35, VALUE_0X0A36) || charInRange(c, VALUE_0X0A38, VALUE_0X0A39)
                || charInRange(c, VALUE_0X0A59, VALUE_0X0A5C) || c == VALUE_0X0A5E
                || charInRange(c, VALUE_0X0A72, VALUE_0X0A74) || charInRange(c, VALUE_0X0A85, VALUE_0X0A8B)
                || c == VALUE_0X0A8D || charInRange(c, VALUE_0X0A8F, VALUE_0X0A91)
                || charInRange(c, VALUE_0X0A93, VALUE_0X0AA8) || charInRange(c, VALUE_0X0AAA, VALUE_0X0AB0)
                || charInRange(c, VALUE_0X0AB2, VALUE_0X0AB3) || charInRange(c, VALUE_0X0AB5, VALUE_0X0AB9)
                || c == VALUE_0X0ABD || c == VALUE_0X0AE0 || charInRange(c, VALUE_0X0B05, VALUE_0X0B0C)
                || charInRange(c, VALUE_0X0B0F, VALUE_0X0B10) || charInRange(c, VALUE_0X0B13, VALUE_0X0B28)
                || charInRange(c, VALUE_0X0B2A, VALUE_0X0B30) || charInRange(c, VALUE_0X0B32, VALUE_0X0B33)
                || charInRange(c, VALUE_0X0B36, VALUE_0X0B39) || c == VALUE_0X0B3D
                || charInRange(c, VALUE_0X0B5C, VALUE_0X0B5D) || charInRange(c, VALUE_0X0B5F, VALUE_0X0B61)
                || charInRange(c, VALUE_0X0B85, VALUE_0X0B8A) || charInRange(c, VALUE_0X0B8E, VALUE_0X0B90)
                || charInRange(c, VALUE_0X0B92, VALUE_0X0B95) || charInRange(c, VALUE_0X0B99, VALUE_0X0B9A)
                || c == VALUE_0X0B9C || charInRange(c, VALUE_0X0B9E, VALUE_0X0B9F)
                || charInRange(c, VALUE_0X0BA3, VALUE_0X0BA4) || charInRange(c, VALUE_0X0BA8, VALUE_0X0BAA)
                || charInRange(c, VALUE_0X0BAE, VALUE_0X0BB5) || charInRange(c, VALUE_0X0BB7, VALUE_0X0BB9)
                || charInRange(c, VALUE_0X0C05, VALUE_0X0C0C) || charInRange(c, VALUE_0X0C0E, VALUE_0X0C10)
                || charInRange(c, VALUE_0X0C12, VALUE_0X0C28) || charInRange(c, VALUE_0X0C2A, VALUE_0X0C33)
                || charInRange(c, VALUE_0X0C35, VALUE_0X0C39) || charInRange(c, VALUE_0X0C60, VALUE_0X0C61)
                || charInRange(c, VALUE_0X0C85, VALUE_0X0C8C) || charInRange(c, VALUE_0X0C8E, VALUE_0X0C90)
                || charInRange(c, VALUE_0X0C92, VALUE_0X0CA8) || charInRange(c, VALUE_0X0CAA, VALUE_0X0CB3)
                || charInRange(c, VALUE_0X0CB5, VALUE_0X0CB9) || c == VALUE_0X0CDE
                || charInRange(c, VALUE_0X0CE0, VALUE_0X0CE1) || charInRange(c, VALUE_0X0D05, VALUE_0X0D0C)
                || charInRange(c, VALUE_0X0D0E, VALUE_0X0D10) || charInRange(c, VALUE_0X0D12, VALUE_0X0D28)
                || charInRange(c, VALUE_0X0D2A, VALUE_0X0D39) || charInRange(c, VALUE_0X0D60, VALUE_0X0D61)
                || charInRange(c, VALUE_0X0E01, VALUE_0X0E2E) || c == VALUE_0X0E30
                || charInRange(c, VALUE_0X0E32, VALUE_0X0E33) || charInRange(c, VALUE_0X0E40, VALUE_0X0E45)
                || charInRange(c, VALUE_0X0E81, VALUE_0X0E82) || c == VALUE_0X0E84
                || charInRange(c, VALUE_0X0E87, VALUE_0X0E88) || c == VALUE_0X0E8A || c == VALUE_0X0E8D
                || charInRange(c, VALUE_0X0E94, VALUE_0X0E97) || charInRange(c, VALUE_0X0E99, VALUE_0X0E9F)
                || charInRange(c, VALUE_0X0EA1, VALUE_0X0EA3) || c == VALUE_0X0EA5 || c == VALUE_0X0EA7
                || charInRange(c, VALUE_0X0EAA, VALUE_0X0EAB) || charInRange(c, VALUE_0X0EAD, VALUE_0X0EAE)
                || c == VALUE_0X0EB0 || charInRange(c, VALUE_0X0EB2, VALUE_0X0EB3) || c == VALUE_0X0EBD
                || charInRange(c, VALUE_0X0EC0, VALUE_0X0EC4) || charInRange(c, VALUE_0X0F40, VALUE_0X0F47)
                || charInRange(c, VALUE_0X0F49, VALUE_0X0F69) || charInRange(c, VALUE_0X10A0, VALUE_0X10C5)
                || charInRange(c, VALUE_0X10D0, VALUE_0X10F6) || c == VALUE_0X1100
                || charInRange(c, VALUE_0X1102, VALUE_0X1103) || charInRange(c, VALUE_0X1105, VALUE_0X1107)
                || c == VALUE_0X1109 || charInRange(c, VALUE_0X110B, VALUE_0X110C)
                || charInRange(c, VALUE_0X110E, VALUE_0X1112) || c == VALUE_0X113C || c == VALUE_0X113E
                || c == VALUE_0X1140 || c == VALUE_0X114C || c == VALUE_0X114E || c == VALUE_0X1150
                || charInRange(c, VALUE_0X1154, VALUE_0X1155) || c == VALUE_0X1159
                || charInRange(c, VALUE_0X115F, VALUE_0X1161) || c == VALUE_0X1163 || c == VALUE_0X1165
                || c == VALUE_0X1167 || c == VALUE_0X1169 || charInRange(c, VALUE_0X116D, VALUE_0X116E)
                || charInRange(c, VALUE_0X1172, VALUE_0X1173) || c == VALUE_0X1175 || c == VALUE_0X119E
                || c == VALUE_0X11A8 || c == VALUE_0X11AB || charInRange(c, VALUE_0X11AE, VALUE_0X11AF)
                || charInRange(c, VALUE_0X11B7, VALUE_0X11B8) || c == VALUE_0X11BA
                || charInRange(c, VALUE_0X11BC, VALUE_0X11C2) || c == VALUE_0X11EB || c == VALUE_0X11F0
                || c == VALUE_0X11F9 || charInRange(c, VALUE_0X1E00, VALUE_0X1E9B)
                || charInRange(c, VALUE_0X1EA0, VALUE_0X1EF9) || charInRange(c, VALUE_0X1F00, VALUE_0X1F15)
                || charInRange(c, VALUE_0X1F18, VALUE_0X1F1D) || charInRange(c, VALUE_0X1F20, VALUE_0X1F45)
                || charInRange(c, VALUE_0X1F48, VALUE_0X1F4D) || charInRange(c, VALUE_0X1F50, VALUE_0X1F57)
                || c == VALUE_0X1F59 || c == VALUE_0X1F5B || c == VALUE_0X1F5D
                || charInRange(c, VALUE_0X1F5F, VALUE_0X1F7D) || charInRange(c, VALUE_0X1F80, VALUE_0X1FB4)
                || charInRange(c, VALUE_0X1FB6, VALUE_0X1FBC) || c == VALUE_0X1FBE
                || charInRange(c, VALUE_0X1FC2, VALUE_0X1FC4) || charInRange(c, VALUE_0X1FC6, VALUE_0X1FCC)
                || charInRange(c, VALUE_0X1FD0, VALUE_0X1FD3) || charInRange(c, VALUE_0X1FD6, VALUE_0X1FDB)
                || charInRange(c, VALUE_0X1FE0, VALUE_0X1FEC) || charInRange(c, VALUE_0X1FF2, VALUE_0X1FF4)
                || charInRange(c, VALUE_0X1FF6, VALUE_0X1FFC) || c == VALUE_0X2126
                || charInRange(c, VALUE_0X212A, VALUE_0X212B) || c == VALUE_0X212E
                || charInRange(c, VALUE_0X2180, VALUE_0X2182) || charInRange(c, VALUE_0X3041, VALUE_0X3094)
                || charInRange(c, VALUE_0X30A1, VALUE_0X30FA) || charInRange(c, VALUE_0X3105, VALUE_0X312C)
                || charInRange(c, VALUE_0XAC00, VALUE_0XD7A3);
    }

    private static boolean isIdeographic(char c) {
        return charInRange(c, VALUE_0X4E00, VALUE_0X9FA5) || c == VALUE_0X3007
                || charInRange(c, VALUE_0X3021, VALUE_0X3029);
    }

    private static boolean isCombiningChar(char c) {
        return charInRange(c, VALUE_0X0300, VALUE_0X0345) || charInRange(c, VALUE_0X0360, VALUE_0X0361)
                || charInRange(c, VALUE_0X0483, VALUE_0X0486) || charInRange(c, VALUE_0X0591, VALUE_0X05A1)
                || charInRange(c, VALUE_0X05A3, VALUE_0X05B9) || charInRange(c, VALUE_0X05BB, VALUE_0X05BD)
                || c == VALUE_0X05BF || charInRange(c, VALUE_0X05C1, VALUE_0X05C2) || c == VALUE_0X05C4
                || charInRange(c, VALUE_0X064B, VALUE_0X0652) || c == VALUE_0X0670
                || charInRange(c, VALUE_0X06D6, VALUE_0X06DC) || charInRange(c, VALUE_0X06DD, VALUE_0X06DF)
                || charInRange(c, VALUE_0X06E0, VALUE_0X06E4) || charInRange(c, VALUE_0X06E7, VALUE_0X06E8)
                || charInRange(c, VALUE_0X06EA, VALUE_0X06ED) || charInRange(c, VALUE_0X0901, VALUE_0X0903)
                || c == VALUE_0X093C || charInRange(c, VALUE_0X093E, VALUE_0X094C) || c == VALUE_0X094D
                || charInRange(c, VALUE_0X0951, VALUE_0X0954) || charInRange(c, VALUE_0X0962, VALUE_0X0963)
                || charInRange(c, VALUE_0X0981, VALUE_0X0983) || c == VALUE_0X09BC || c == VALUE_0X09BE
                || c == VALUE_0X09BF || charInRange(c, VALUE_0X09C0, VALUE_0X09C4)
                || charInRange(c, VALUE_0X09C7, VALUE_0X09C8) || charInRange(c, VALUE_0X09CB, VALUE_0X09CD)
                || c == VALUE_0X09D7 || charInRange(c, VALUE_0X09E2, VALUE_0X09E3) || c == VALUE_0X0A02
                || c == VALUE_0X0A3C || c == VALUE_0X0A3E || c == VALUE_0X0A3F
                || charInRange(c, VALUE_0X0A40, VALUE_0X0A42) || charInRange(c, VALUE_0X0A47, VALUE_0X0A48)
                || charInRange(c, VALUE_0X0A4B, VALUE_0X0A4D) || charInRange(c, VALUE_0X0A70, VALUE_0X0A71)
                || charInRange(c, VALUE_0X0A81, VALUE_0X0A83) || c == VALUE_0X0ABC
                || charInRange(c, VALUE_0X0ABE, VALUE_0X0AC5) || charInRange(c, VALUE_0X0AC7, VALUE_0X0AC9)
                || charInRange(c, VALUE_0X0ACB, VALUE_0X0ACD) || charInRange(c, VALUE_0X0B01, VALUE_0X0B03)
                || c == VALUE_0X0B3C || charInRange(c, VALUE_0X0B3E, VALUE_0X0B43)
                || charInRange(c, VALUE_0X0B47, VALUE_0X0B48) || charInRange(c, VALUE_0X0B4B, VALUE_0X0B4D)
                || charInRange(c, VALUE_0X0B56, VALUE_0X0B57) || charInRange(c, VALUE_0X0B82, VALUE_0X0B83)
                || charInRange(c, VALUE_0X0BBE, VALUE_0X0BC2) || charInRange(c, VALUE_0X0BC6, VALUE_0X0BC8)
                || charInRange(c, VALUE_0X0BCA, VALUE_0X0BCD) || c == VALUE_0X0BD7
                || charInRange(c, VALUE_0X0C01, VALUE_0X0C03) || charInRange(c, VALUE_0X0C3E, VALUE_0X0C44)
                || charInRange(c, VALUE_0X0C46, VALUE_0X0C48) || charInRange(c, VALUE_0X0C4A, VALUE_0X0C4D)
                || charInRange(c, VALUE_0X0C55, VALUE_0X0C56) || charInRange(c, VALUE_0X0C82, VALUE_0X0C83)
                || charInRange(c, VALUE_0X0CBE, VALUE_0X0CC4) || charInRange(c, VALUE_0X0CC6, VALUE_0X0CC8)
                || charInRange(c, VALUE_0X0CCA, VALUE_0X0CCD) || charInRange(c, VALUE_0X0CD5, VALUE_0X0CD6)
                || charInRange(c, VALUE_0X0D02, VALUE_0X0D03) || charInRange(c, VALUE_0X0D3E, VALUE_0X0D43)
                || charInRange(c, VALUE_0X0D46, VALUE_0X0D48) || charInRange(c, VALUE_0X0D4A, VALUE_0X0D4D)
                || c == VALUE_0X0D57 || c == VALUE_0X0E31 || charInRange(c, VALUE_0X0E34, VALUE_0X0E3A)
                || charInRange(c, VALUE_0X0E47, VALUE_0X0E4E) || c == VALUE_0X0EB1
                || charInRange(c, VALUE_0X0EB4, VALUE_0X0EB9) || charInRange(c, VALUE_0X0EBB, VALUE_0X0EBC)
                || charInRange(c, VALUE_0X0EC8, VALUE_0X0ECD) || charInRange(c, VALUE_0X0F18, VALUE_0X0F19)
                || c == VALUE_0X0F35 || c == VALUE_0X0F37 || c == VALUE_0X0F39 || c == VALUE_0X0F3E
                || c == VALUE_0X0F3F || charInRange(c, VALUE_0X0F71, VALUE_0X0F84)
                || charInRange(c, VALUE_0X0F86, VALUE_0X0F8B) || charInRange(c, VALUE_0X0F90, VALUE_0X0F95)
                || c == VALUE_0X0F97 || charInRange(c, VALUE_0X0F99, VALUE_0X0FAD)
                || charInRange(c, VALUE_0X0FB1, VALUE_0X0FB7) || c == VALUE_0X0FB9
                || charInRange(c, VALUE_0X20D0, VALUE_0X20DC) || c == VALUE_0X20E1
                || charInRange(c, VALUE_0X302A, VALUE_0X302F) || c == VALUE_0X3099 || c == VALUE_0X309A;
    }

    private static boolean isDigit(char c) {
        return isAsciiDigit(c) || isNonAsciiDigit(c);
    }

    private static boolean isAsciiDigit(char c) {
        return charInRange(c, VALUE_0X0030, VALUE_0X0039);
    }

    private static boolean isNonAsciiDigit(char c) {
        return charInRange(c, VALUE_0X0660, VALUE_0X0669) || charInRange(c, VALUE_0X06F0, VALUE_0X06F9)
                || charInRange(c, VALUE_0X0966, VALUE_0X096F) || charInRange(c, VALUE_0X09E6, VALUE_0X09EF)
                || charInRange(c, VALUE_0X0A66, VALUE_0X0A6F) || charInRange(c, VALUE_0X0AE6, VALUE_0X0AEF)
                || charInRange(c, VALUE_0X0B66, VALUE_0X0B6F) || charInRange(c, VALUE_0X0BE7, VALUE_0X0BEF)
                || charInRange(c, VALUE_0X0C66, VALUE_0X0C6F) || charInRange(c, VALUE_0X0CE6, VALUE_0X0CEF)
                || charInRange(c, VALUE_0X0D66, VALUE_0X0D6F) || charInRange(c, VALUE_0X0E50, VALUE_0X0E59)
                || charInRange(c, VALUE_0X0ED0, VALUE_0X0ED9) || charInRange(c, VALUE_0X0F20, VALUE_0X0F29);
    }

    private static boolean isExtender(char c) {
        return c == VALUE_0X00B7 || c == VALUE_0X02D0 || c == VALUE_0X02D1 || c == VALUE_0X0387 || c == VALUE_0X0640
                || c == VALUE_0X0E46 || c == VALUE_0X0EC6 || c == VALUE_0X3005
                || charInRange(c, VALUE_0X3031, VALUE_0X3035) || charInRange(c, VALUE_0X309D, VALUE_0X309E)
                || charInRange(c, VALUE_0X30FC, VALUE_0X30FE);
    }

    private static boolean charInRange(char c, int start, int end) {
        return c >= start && c <= end;
    }

    private NcNameResolver() {

    }
}
