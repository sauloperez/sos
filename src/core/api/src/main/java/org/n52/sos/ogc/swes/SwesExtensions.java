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
package org.n52.sos.ogc.swes;

import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensions {

    private final Set<SwesExtension<?>> extensions = new HashSet<SwesExtension<?>>(0);

    /**
     * @param extensionName
     * 
     * @return <b><tt>true</tt></b>, only if the extension with the definition
     *         <tt>extensionName</tt> is holding a {@link Boolean} and is set to
     *         <tt>true</tt>.
     */
    public boolean isBooleanExtensionSet(final String extensionName) {
        for (final SwesExtension<?> swesExtension : extensions) {
            if (isExtensionNameEquals(extensionName, swesExtension)) {
                final Object value = swesExtension.getValue();
                if (value instanceof SweBoolean) {
                    return ((SweBoolean) value).getValue();
                }
                return false;
            }
        }
        return false;
    }

    private boolean isExtensionNameEquals(final String extensionName, final SwesExtension<?> swesExtension) {
        return swesExtension.getDefinition().equalsIgnoreCase(extensionName)
                || (swesExtension.getValue() instanceof SweAbstractDataComponent
                        && ((SweAbstractDataComponent) swesExtension.getValue()).isSetDefinition() && ((SweAbstractDataComponent) swesExtension
                            .getValue()).getDefinition().equalsIgnoreCase(extensionName));
    }

    public boolean addSwesExtension(final SwesExtension<?> extension) {
        return extensions.add(extension);
    }

    public boolean isEmpty() {
        return extensions.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("SwesExtensions [extensions=%s]", extensions);
    }

}
