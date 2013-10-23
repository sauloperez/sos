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
<%@ taglib prefix="sos" uri="http://52north.org/communities/sensorweb/sos/tags" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Administration Panel" />
	<jsp:param name="leadParagraph" value="Use the admin menu above to select different administrative tasks." />
</jsp:include>
<p class="pull-right">
    <button type="button" id="reloadCapsCache" class="btn">Reload Capabilities Cache</button>
</p>

<c:if test="${warning}">
    <script type="text/javascript">
    showMessage('<b>Warning!</b> You are used the default credentials to log in. Please change them \
                   <a href="<c:url value="/admin/settings#credentials"/>">here</a> as soon as possible!');
    </script>
</c:if>

<script type="text/javascript">
    $("#reloadCapsCache").click(function() {
        var $b = $(this);
        $b.attr("disabled", true);
        $.ajax({
            url: "<c:url value="/admin/cache/reload"/>",
            type: "POST"
        }).done(function(e) {
            showSuccess("Capabilties cache reloaded.");
            $b.removeAttr("disabled");
        }).fail(function(error){
            showError("Capabilites cache reload failed: " + error.responseText);
            $b.removeAttr("disabled");
        });
    });
</script>


<c:if test="${sos:hasClient()}">
<div class="row" style="margin-top: 50px">
    <div class="span12">
    <p>A test data set can be insert using the <a href="<c:url value="/client?load=exampleData"/>">Test client</a>. For this the JSON Binding and the <code>Batch</code>, <code>InsertSensor</code>, <code>InsertObservation</code> and <code>InsertResultTemplate</code> operations have to be active. Be aware that it only can be removed by cleaning the entire database.</p>
</c:if>


<div class="row" style="margin-top: 40px">
    <div class="span12">
        <c:if test="${not empty metadata.VERSION}">
            <p><strong>Version:</strong> ${fn:escapeXml(metadata.VERSION)}</p>
        </c:if>
        <c:if test="${not empty metadata.SVN_VERSION}">
            <p><strong>Revision:</strong> ${fn:escapeXml(metadata.SVN_VERSION)}</p>
        </c:if>
        <c:if test="${not empty metadata.BUILD_DATE}">
            <p><strong>Build date:</strong> ${fn:escapeXml(metadata.BUILD_DATE)}</p>
        </c:if>
        <c:if test="${not empty metadata.INSTALL_DATE}">
            <p><strong>Installation date:</strong> ${fn:escapeXml(metadata.INSTALL_DATE)}</p>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
