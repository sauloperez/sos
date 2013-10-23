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
 * Default constants interface with constants used by other constant interfaces
 * or classes
 * 
 * @since 4.0.0
 * 
 */
public interface Constants {
    
    int EPSG_WGS84_3D = 4979;

    int EPSG_WGS84 = 4326;

    // String constants
    String EMPTY_STRING = "";

    String BLANK_STRING = " ";

    String SEMICOLON_STRING = ";";

    String COLON_STRING = ":";

    String DOT_STRING = ".";

    String COMMA_STRING = ",";

    String COMMA_SPACE_STRING = ", ";

    String CSV_BLOCK_SEPARATOR = COMMA_STRING;

    String CSV_TOKEN_SEPARATOR = "@@";

    String AMPERSAND_STRING = "&";

    String EQUAL_SIGN_STRING = "=";

    String QUERSTIONMARK_STRING = "?";

    String SLASH_STRING = "/";

    String BACKSLASH_STRING = "\\";

    String LINE_SEPARATOR_STRING = "\n";

    String DASH_STRING = "-";

    String UNDERSCORE_STRING = "_";

    String NUMBER_SIGN_STRING = "#";

    // char constants
    char BLANK_CHAR = ' ';

    char SEMICOLON_CHAR = ';';

    char COLON_CHAR = ':';

    char DOT_CHAR = '.';

    char COMMA_CHAR = ',';

    char AMPERSAND_CHAR = '&';

    char EQUAL_SIGN_CHAR = '=';

    char QUERSTIONMARK_CHAR = '?';

    char SLASH_CHAR = '/';

    char BACKSLASH_CHAR = '\\';

    char LINE_SEPARATOR_CHAR = '\n';

    char DASH_CHAR = '-';

    char UNDERSCORE_CHAR = '_';

    char NUMBER_SIGN_CHAR = '#';

    int INT_0 = 0;

    int INT_1 = 1;

    int INT_2 = 2;

    int INT_3 = 3;

    int INT_4 = 4;

}
