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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.n52.sos.service.SosContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ConfiguredSettingsManager extends ExternalResource {
    private static final Logger LOG = LoggerFactory.getLogger(ConfiguredSettingsManager.class);

    private File tempDir;

    @Override
    protected void before() throws Throwable {
        createDirectory();
    }

    @Override
    protected void after() {
        deleteDirectory();
    }

    protected void createDirectory() throws IOException {
        tempDir = File.createTempFile("sos-test-case", "");
        FileUtils.forceDelete(tempDir);
        FileUtils.forceMkdir(tempDir);
        SosContextListener.setPath(tempDir.getAbsolutePath());
    }

    protected void deleteDirectory() {
        try {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir);
            }
        } catch (IOException ex) {
            LOG.error("Error deleting temp dir", ex);
        }
    }
}
