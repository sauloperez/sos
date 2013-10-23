<%--

    Copyright (C) 2013
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    This program is free software; you can redistribute and/or modify it under
    the terms of the GNU General Public License version 2 as published by the
    Free Software Foundation.

    This program is distributed WITHOUT ANY WARRANTY; even without the implied
    WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License along with
    this program (see gnu-gpl v2.txt). If not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
    visit the Free Software Foundation web page, http://www.fsf.org.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="common/header.jsp">
    <jsp:param name="activeMenu" value="client" />
</jsp:include>
<link rel="stylesheet" href="<c:url value="/static/lib/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/codemirror-3.16.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-3.16.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/vkbeautify-0.99.00.beta.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/client.js" />"></script>
<jsp:include page="common/logotitle.jsp">
    <jsp:param name="title" value="52&deg;North SOS Test Client" />
    <jsp:param name="leadParagraph" value="Choose a request from the examples or write your own to test the SOS." />
</jsp:include>

<script type="text/javascript">
    /* redirect from "client/"" to "client" */
    if (window.location.pathname.slice(-1) === "/") {
        window.location.href = window.location.href.slice(0, -1);
    }
</script>
<style type="text/css">
    #send-inline-label { margin-right: 10px; }
    #send-group { margin-bottom: 15px; margin-top: 15px; }
    #response { margin-left: 0; }
    .CodeMirror { height: auto; }
    .CodeMirror-foldmarker {
        color: blue;
        text-shadow: #b9f 1px 1px 2px, #b9f -1px -1px 2px, #b9f 1px -1px 2px, #b9f -1px 1px 2px;
        font-family: arial;
        line-height: .3;
        cursor: pointer;
    }
    .CodeMirror-foldgutter { width: .7em; }
    .CodeMirror-foldgutter-open,
    .CodeMirror-foldgutter-folded { color: #555; cursor: pointer; }
    .CodeMirror-foldgutter-open:after { content: "\25BE"; }
    .CodeMirror-foldgutter-folded:after { content: "\25B8"; }
</style>

<form id="form" action="">
    <h3>Examples</h3>
    <p>NOTE: Requests use example values and are not dynamically generated from values in this SOS. Construct valid requests by changing request values to match values in the Capabilities response.</p>
    <div class="controls-row">
        <select id="service" class="span3"></select>
        <select id="version" class="span3"></select>
        <select id="binding" class="span3"></select>
        <select id="operation" class="span3"></select>
    </div>
    <div class="controls-row">
        <select id="request" class="span12"></select>
    </div>
    <h3 id="top">Service URL</h3>
    <input id="url" class="span12" type="text" placeholder="Service URL" value=""/>
    <h3>Request</h3>
    <div class="controls-row">
        <select id="method" class="span2">
            <option>GET</option>
            <option>HEAD</option>
            <option>POST</option>
            <option>PUT</option>
            <option>DELETE</option>
            <option>OPTIONS</option>
        </select>
        <input id="content-type" type="text" class="span3" placeholder="Content-Type" />
        <input id="accept" type="text" class="span3" placeholder="Accept" />
        <div class="btn-group pull-right">
            <button id="permalink" class="btn btn-small" type="button">Permalink</button>
            <button class="btn btn-small dropdown-toggle" data-toggle="dropdown">
                Syntax <span class="caret"></span>
            </button>
            <ul id="mode" class="dropdown-menu">
                <li><a href="#" data-mode="application/xml">XML</a></li>
                <li><a href="#" data-mode="application/json">JSON</a></li>
            </ul>
        </div>
    </div>
    <textarea id="editor" name="request" class="span12"></textarea>
    <div id="send-group" class="pull-right control-group">
        <label id="send-inline-label" class="checkbox inline">
            <input id="send-inline" type="checkbox" checked="" /> show response inline
        </label>
        <button id="send-button" type="button" class="btn btn-info inline">Send</button>
    </div>
    <div id="response" class="span12"></div>
</form>

<script type="text/javascript">
    var availableOperations = [
    <c:forEach items="${operations}" var="ao">
        { method: "${ao.method}", binding: "${ao.binding}", service: "${ao.service}", version: "${ao.version}", operation: "${ao.operation}" },
    </c:forEach>
    ];
</script>

<script type="text/javascript">
    $.getJSON("<c:url value="/static/conf/client-config.json"/>", function(config) {
        function flatten(requests) {
            var s, v, b, o, t, r, transformed = [];
            for (s in requests) {
                for (v in requests[s]) {
                    for (b in requests[s][v]) {
                        for (o in requests[s][v][b]) {
                            for (t in requests[s][v][b][o]) {
                                r = requests[s][v][b][o][t];
                                r.service = s;
                                r.version = v;
                                r.binding = b;
                                r.operation = o;
                                r.title = t;
                                r.headers = {};
                                if (b === "/kvp") {
                                    r.method = "GET";
                                    r.headers["Accept"] = "application/xml";
                                } else if (b === "/pox") {
                                    r.method = "POST";
                                    r.headers["Accept"] = "application/xml";
                                    r.headers["Content-Type"] = "application/xml";
                                } else if (b === "/soap") {
                                    r.method = "POST";
                                    r.headers["Accept"] = "application/soap+xml";
                                    r.headers["Content-Type"] = "application/soap+xml";
                                } else if (b === "/json") {
                                    r.method = "POST";
                                    r.headers["Accept"] = "application/json";
                                    r.headers["Content-Type"] = "application/json";
                                } else {
                                    throw new Error("Unsupported binding" + b);
                                }
                                transformed.push(r);
                            }
                        }
                    }
                }
            }
            return transformed;
        }

        if (!config.examples) {
            /* transform old format */
            config = { examples: flatten(config) };
        }

        config.sosUrl = document.location.protocol + "//"
                + document.location.host + "<c:url value="/sos" />";
        config.availableOperations = availableOperations;
        new Client(config);
    })
    .fail(function() {
    	alert('Error while loading request data!');
    });
</script>
<jsp:include page="common/footer.jsp" />
