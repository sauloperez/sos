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

package org.n52.sos.ds.datasource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorAggregator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.mapping.AuxiliaryDatabaseObject;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CustomConfiguration extends Configuration {
    private static final long serialVersionUID = 149360549522727961L;

    /**
     * Based on
     * {@link org.hibernate.cfg.Configuration#generateDropSchemaScript(Dialect)}
     * . Rewritten to only create drop commands for existing tables/sequences.
     * 
     * 
     * @param d
     * @param m
     * 
     * @return SQL script to drop schema as String array
     * 
     * @throws HibernateException
     */
    public String[] generateDropSchemaScript(final Dialect d, final DatabaseMetadata m) throws HibernateException {
        secondPassCompile();
        final String c = getProperties().getProperty(Environment.DEFAULT_CATALOG);
        final String s = getProperties().getProperty(Environment.DEFAULT_SCHEMA);
        final List<String> script = new LinkedList<String>();
        script.addAll(generateAuxiliaryDatabaseObjectDropScript(d, c, s));
        if (d.dropConstraints()) {
            script.addAll(generateConstraintDropScript(d, c, s, m));
        }
        script.addAll(generateTableDropScript(d, c, s, m));
        script.addAll(generateIdentifierGeneratorDropScript(d, c, s, m));
        return ArrayHelper.toStringArray(script);
    }

    /**
     * Copied from
     * {@link org.hibernate.cfg.Configuration#iterateGenerators(Dialect)}.
     */
    private Iterator<PersistentIdentifierGenerator> iterateGenerators(final Dialect d, final String c, final String s)
            throws MappingException {
        final TreeMap<Object, PersistentIdentifierGenerator> generators =
                new TreeMap<Object, PersistentIdentifierGenerator>();
        for (final PersistentClass pc : classes.values()) {
            if (!pc.isInherited()) {
                final IdentifierGenerator ig =
                        pc.getIdentifier().createIdentifierGenerator(getIdentifierGeneratorFactory(), d, c, s,
                                (RootClass) pc);
                if (ig instanceof PersistentIdentifierGenerator) {
                    final PersistentIdentifierGenerator pig = (PersistentIdentifierGenerator) ig;
                    generators.put(pig.generatorKey(), pig);
                } else if (ig instanceof IdentifierGeneratorAggregator) {
                    ((IdentifierGeneratorAggregator) ig).registerPersistentGenerators(generators);
                }
            }
        }
        for (final Collection collection : collections.values()) {
            if (collection.isIdentified()) {
                final IdentifierGenerator ig =
                        ((IdentifierCollection) collection).getIdentifier().createIdentifierGenerator(
                                getIdentifierGeneratorFactory(), d, c, s, null);
                if (ig instanceof PersistentIdentifierGenerator) {
                    final PersistentIdentifierGenerator pig = (PersistentIdentifierGenerator) ig;
                    generators.put(pig.generatorKey(), pig);
                }
            }
        }

        return generators.values().iterator();
    }

    protected List<String> generateConstraintDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<Table> itr = getTableMappings();
        while (itr.hasNext()) {
            final Table table = itr.next();
            final String tableName = table.getQualifiedName(d, c, s);
            if (table.isPhysicalTable() && m.isTable(tableName)) {
                @SuppressWarnings("unchecked")
                final Iterator<ForeignKey> subItr = table.getForeignKeyIterator();
                final TableMetadata tableMeta = m.getTableMetadata(table.getName(), s, c, true);
                while (subItr.hasNext()) {
                    final ForeignKey fk = subItr.next();
                    if (fk.isPhysicalConstraint() && tableMeta.getForeignKeyMetadata(fk) != null) {
                        script.add(fk.sqlDropString(d, c, s));
                    }
                }
            }
        }
        return script;
    }

    protected List<String> generateTableDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<Table> itr = getTableMappings();
        while (itr.hasNext()) {
            final Table table = itr.next();
            final String tableName = table.getQualifiedName(d, c, s);
            if (table.isPhysicalTable() && m.isTable(tableName)) {
                script.add(table.sqlDropString(d, c, s));
            }
        }
        return script;
    }

    protected List<String> generateAuxiliaryDatabaseObjectDropScript(final Dialect d, final String c, final String s) {
        final List<String> script = new LinkedList<String>();
        final ListIterator<AuxiliaryDatabaseObject> itr =
                auxiliaryDatabaseObjects.listIterator(auxiliaryDatabaseObjects.size());
        while (itr.hasPrevious()) {
            // FIXME how to check if ADO exists?
            final AuxiliaryDatabaseObject object = itr.previous();
            if (object.appliesToDialect(d)) {
                script.add(object.sqlDropString(d, c, s));
            }
        }
        return script;
    }

    protected List<String> generateIdentifierGeneratorDropScript(final Dialect d, final String c, final String s,
            final DatabaseMetadata m) throws MappingException, HibernateException {
        final List<String> script = new LinkedList<String>();
        final Iterator<PersistentIdentifierGenerator> itr = iterateGenerators(d, c, s);
        while (itr.hasNext()) {
            final PersistentIdentifierGenerator pig = itr.next();
            if (pig instanceof SequenceGenerator) {
                final SequenceGenerator sg = (SequenceGenerator) pig;
                if (!m.isSequence(sg.getSequenceName())) {
                    continue;
                }
            }
            script.addAll(Arrays.asList(pig.sqlDropStrings(d)));
        }
        return script;
    }
}