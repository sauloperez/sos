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
package org.n52.sos.ds.hibernate.util;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class HibernateHelperTest {
    
    @Test
    public void shouldReturnListsOfLists() {
        assertTrue( HibernateHelper.getValidSizedLists(getList()).size() == 10);
    }
    
    @Test
    public void shouldListSizeLessThanLimitExpressionDepth() {
        for (List<Long> validSizedList :  HibernateHelper.getValidSizedLists(getList())) {
            assertTrue(validSizedList.size() <= HibernateConstants.LIMIT_EXPRESSION_DEPTH);
        }
    }
    
    private List<Long> getList() {
        List<Long> list = Lists.newArrayList(); 
        for (long i = 0; i < 9999; i++) {
            list.add(i);
        }
        return list;
    }
}
