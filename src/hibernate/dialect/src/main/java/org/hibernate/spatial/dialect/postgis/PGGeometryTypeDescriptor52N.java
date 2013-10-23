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
package org.hibernate.spatial.dialect.postgis;

import java.sql.Types;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/*
 * This file is part of Hibernate Spatial, an extension to the
 *  hibernate ORM solution for spatial (geographic) data.
 *
 *  Copyright Â© 2007-2012 Geovise BVBA
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/**
 * @since 4.0.0
 * 
 */
public class PGGeometryTypeDescriptor52N implements SqlTypeDescriptor {

    public static final SqlTypeDescriptor INSTANCE = new PGGeometryTypeDescriptor52N();

    private static final long serialVersionUID = 1259846599170907138L;

    @Override
    public int getSqlType() {
        return Types.STRUCT;
    }

    @Override
    public boolean canBeRemapped() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return (ValueBinder<X>) new PGGeometryValueBinder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return (ValueExtractor<X>) new PGGeometryValueExtractor52N();
    }
    
    public String getLicense() {
        StringBuilder builder = new StringBuilder();
        String lineBreak = System.getProperty("line.separator");
        builder.append(" /*").append(lineBreak);
        builder.append(" * This file is part of Hibernate Spatial, an extension to the").append(
                lineBreak);
        builder.append("*  hibernate ORM solution for spatial (geographic) data.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  Copyright Â© 2007-2012 Geovise BVBA").append(lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  This library is free software; you can redistribute it and/or").append(
                lineBreak);
        builder.append(" *  modify it under the terms of the GNU Lesser General Public").append(
                lineBreak);
        builder.append(" *  License as published by the Free Software Foundation; either").append(
                lineBreak);
        builder.append(" *  version 2.1 of the License, or (at your option) any later version.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  This library is distributed in the hope that it will be useful,").append(
                lineBreak);
        builder.append(" *  but WITHOUT ANY WARRANTY; without even the implied warranty of").append(
                lineBreak);
        builder.append(" *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU").append(
                lineBreak);
        builder.append(" *  Lesser General Public License for more details.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  You should have received a copy of the GNU Lesser General Public").append(
                lineBreak);
        builder.append(" *  License along with this library; if not, write to the Free Software").append(
                lineBreak);
        builder.append(" *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA").append(
                lineBreak);
        builder.append(" */").append(lineBreak);
        return builder.toString();
    }
}
