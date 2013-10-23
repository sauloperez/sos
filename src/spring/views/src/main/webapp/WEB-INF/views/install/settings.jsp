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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="header.jsp">
	<jsp:param name="step" value="3" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Settings - change with care!" />
	<jsp:param name="leadParagraph" value="You can change these settings later in the administrative backend." />
</jsp:include>

<script type="text/javascript">
    if (document.referrer) {
        if (document.referrer.matches(/install\/database/)) {
            showSuccess("Database configuration successfully tested.");
        }
    }
</script>

<script type="text/javascript">
    function overwriteDefaultSettings(settings) {
    <c:if test="${not empty settings}">
        <c:set var="search" value='"' />
        <c:set var="replace" value='\\"' />
        <c:forEach items="${settings}" var="entry">
            <c:if test="${not empty entry.value}">
                setSetting("${entry.key}","${fn:replace(entry.value, search, replace)}", settings);
            </c:if>
        </c:forEach>
    </c:if>
    }
</script>


<form action="<c:url value="/install/settings" />" method="POST" class="form-horizontal">
	<div id="settings"></div>
	<script type="text/javascript">
	$.getJSON('<c:url value="/settingDefinitions.json" />', function(settings) {
 		var $container = $("#settings");

		generateSettings(settings, $container, true);
		$("#service_identification .control-group:first").before("<legend>Standard Settings</legend>");
		$("#service_provider .control-group:first").before("<legend>Standard Settings</legend>");
		$("#service_identification .control-group:last").before("<legend>Extended Settings</legend>");
		$("#service_provider .control-group:last").before("<legend>Extended Settings</legend>");
		$("input[name='service.sosUrl']").val(window.location.toString()
			.replace(/install\/settings.*/, "sos")).trigger("input");		

		overwriteDefaultSettings(settings);

		$(".required").bind("keyup input change", function() {
            var valid = true;
            $(".required").each(function(){ 
                var val = $(this).val();
                return valid = (val !== null && val !== undefined && val !== "");
            });
            if (valid) {
                $("button[type=submit]").removeAttr("disabled");
            } else {
                $("button[type=submit]").attr("disabled", true);
            }
        });

        $(".required:first").trigger("change");
        parsehash();
	});
    </script>

	<hr/>
	<div>
        <a href="<c:url value="/install/datasource" />" class="btn">Back</a>
        <button type="submit" class="btn btn-info pull-right">Next</button>
	</div>
</form>

<jsp:include page="../common/footer.jsp" />
