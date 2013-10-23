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
package org.n52.sos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.hibernate.spatial.dialect.oracle.OracleSpatial10gDialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;

import com.google.common.collect.Lists;

/**
 * Class to generate the create and drop scripts for different databases.
 * Currently supported spatial databases to create scripts - PostgreSQL/PostGIS
 * - Oracle - H2/GeoDB
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class SQLScriptGenerator {
    
    private SQLScriptGenerator() {
        
    }

    private Dialect getDialect(int selection) throws Exception {
        switch (selection) {
        case 1:
            return new PostgisDialect();
        case 2:
            return new OracleSpatial10gDialect();
        case 3:
            return new GeoDBDialect();
        default:
            throw new Exception("The entered value is invalid!");
        }
    }

    private void setDirectoriesForModelSelection(int selection, Configuration configuration) throws Exception {
        switch (selection) {
        case 1:
            SQLScriptGenerator.class.getResource("/mapping/core");
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/core").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/spatialFilteringProfile").toURI()));
            break;
        case 2:
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/core").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/transactional").toURI()));
            configuration.addDirectory(new File(SQLScriptGenerator.class.getResource("/mapping/spatialFilteringProfile").toURI()));
            break;
        default:
            throw new Exception("The entered value is invalid!");
        }
    }

    private int getDialectSelection() throws IOException {
        printToScreen("This SQL script generator supports:");
        printToScreen("1   PostGIS");
        printToScreen("2   Oracle");
        printToScreen("3   H2/GeoDB");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }

    private int getModelSelection() throws IOException {
        printToScreen("Which database model should be created:");
        printToScreen("1   Core");
        printToScreen("2   Transcational");
        printToScreen("");
        printToScreen("Enter your selection: ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String selection = null;
        selection = br.readLine();
        return Integer.parseInt(selection);
    }
    
    public static void printToScreen(String lineToPrint) {
        System.out.println(lineToPrint);
    }

    public static void main(String[] args) {
        try {
            SQLScriptGenerator sqlScriptGenerator = new SQLScriptGenerator();
            Configuration configuration = new Configuration().configure("/sos-hibernate.cfg.xml");
            int dialectSelection = sqlScriptGenerator.getDialectSelection();
            Dialect dia = sqlScriptGenerator.getDialect(dialectSelection);
            int modelSelection = sqlScriptGenerator.getModelSelection();
            sqlScriptGenerator.setDirectoriesForModelSelection(modelSelection, configuration);
            // create script
            String[] create = configuration.generateSchemaCreationScript(dia);
            String hexStringToCheck = new StringBuilder("FK").append(Integer.toHexString( "observationHasOffering".hashCode() ).toUpperCase()).toString();
            boolean duplicate = false;
            List<String> checkedSchema = Lists.newLinkedList();
            for (String string : create) {
                if (string.contains(hexStringToCheck)) {
                    if (!duplicate) {
                        checkedSchema.add(string);
                        duplicate = true;
                    }
                } else {
                    checkedSchema.add(string);
                }
            }
            printToScreen("Scripts are created for: " + dia.toString());
            printToScreen("");
            printToScreen("#######################################");
            printToScreen("##           Create-Script           ##");
            printToScreen("#######################################");
            printToScreen("");
            for (String t : checkedSchema.toArray(new String[checkedSchema.size()])) {
                printToScreen(t + ";");
            }
            // drop script
            String[] drop = configuration.generateDropSchemaScript(dia);
            printToScreen("");
            printToScreen("#######################################");
            printToScreen("##            Drop-Script            ##");
            printToScreen("#######################################");
            printToScreen("");
            for (String t : drop) {
                printToScreen(t + ";");
            }
            printToScreen("");
            printToScreen("#######################################");
        } catch (IOException ioe) {
            printToScreen("ERROR: IO error trying to read your input!");
            System.exit(1);
        } catch (Exception e) {
            printToScreen("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }
}
