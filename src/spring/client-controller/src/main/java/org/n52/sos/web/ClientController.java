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
package org.n52.sos.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.http.HTTPMethods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
@Controller
@RequestMapping(ControllerConstants.Paths.CLIENT)
public class ClientController extends AbstractController {
    public static final String BINDINGS = "bindings";

    public static final String VERSIONS = "versions";

    public static final String OPERATIONS = "operations";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() {
        if (Configurator.getInstance() != null) {
            return new ModelAndView(ControllerConstants.Views.CLIENT, OPERATIONS, getAvailableOperations());
        } else {
            return new ModelAndView(ControllerConstants.Views.CLIENT);
        }
    }

    private List<AvailableOperation> getAvailableOperations() {
        final List<AvailableOperation> ops = new LinkedList<AvailableOperation>();
        for (RequestOperatorKey rokt : RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys()) {
            final String service = rokt.getServiceOperatorKey().getService();
            final String version = rokt.getServiceOperatorKey().getVersion();
            final String operation = rokt.getOperationName();
            final OperationKey ok = new OperationKey(service, version, operation);
            for (Entry<String, Binding> b : BindingRepository.getInstance().getBindings().entrySet()) {
                try {
                    final String pattern = b.getKey();
                    final Binding binding = b.getValue();
                    if (binding.checkOperationHttpDeleteSupported(ok)) {
                        ops.add(new AvailableOperation(service, version, operation, pattern, HTTPMethods.DELETE));
                    }
                    if (binding.checkOperationHttpGetSupported(ok)) {
                        ops.add(new AvailableOperation(service, version, operation, pattern, HTTPMethods.GET));
                    }
                    if (binding.checkOperationHttpOptionsSupported(ok)) {
                        ops.add(new AvailableOperation(service, version, operation, pattern, HTTPMethods.OPTIONS));
                    }
                    if (binding.checkOperationHttpPostSupported(ok)) {
                        ops.add(new AvailableOperation(service, version, operation, pattern, HTTPMethods.POST));
                    }
                    if (binding.checkOperationHttpPutSupported(ok)) {
                        ops.add(new AvailableOperation(service, version, operation, pattern, HTTPMethods.PUT));
                    }
                } catch (HTTPException ex) {
                    /* ignore */
                }
            }
        }
        return ops;
    }

    public static class AvailableOperation {
        private final String service;

        private final String version;

        private final String operation;

        private final String binding;

        private final String method;

        public AvailableOperation(String service, String version, String operation, String binding, String method) {
            this.service = service;
            this.version = version;
            this.operation = operation;
            this.binding = binding;
            this.method = method;
        }

        public String getService() {
            return service;
        }

        public String getVersion() {
            return version;
        }

        public String getOperation() {
            return operation;
        }

        public String getBinding() {
            return binding;
        }

        public String getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return String.format("AvailableOperation[method=%s, service=%s, version=%s, operation=%s, binding=%s]",
                    getMethod(), getService(), getVersion(), getOperation(), getBinding());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getMethod(), getService(), getVersion(), getOperation(), getBinding());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AvailableOperation) {
                AvailableOperation other = (AvailableOperation) obj;
                return Objects.equal(getMethod(), other.getMethod())
                        && Objects.equal(getService(), other.getService())
                        && Objects.equal(getVersion(), other.getVersion())
                        && Objects.equal(getOperation(), other.getOperation())
                        && Objects.equal(getBinding(), other.getBinding());
            }
            return false;
        }
    }
}
