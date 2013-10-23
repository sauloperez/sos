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
package org.n52.sos.web.admin;

import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @since 4.0.0
 * 
 */
@Controller
public class AdminCacheController extends AbstractController {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AdminCacheController.class);

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE, method = RequestMethod.GET)
    public String view() {
        return ControllerConstants.Views.ADMIN_CACHE;
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_CACHE_SUMMARY, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getCacheSummary() {
        return new JSONObject(CacheSummaryHandler.getCacheValues()).toString();
    }
}