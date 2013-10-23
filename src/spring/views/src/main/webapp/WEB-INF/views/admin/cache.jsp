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
<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>
<jsp:include page="../common/logotitle.jsp">
	<jsp:param name="title" value="Cache Summary" />
	<jsp:param name="leadParagraph" value="Summary information for the SOS cache." />
</jsp:include>
<p class="pull-right">
<button type="button" id="reloadCapsCache" class="btn">Reload Capabilities Cache</button>
</p>

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
            loadCacheSummary();
        }).fail(function(error){
            showError("Capabilites cache reload failed: " + error.responseText);
            $b.removeAttr("disabled");
        });
    });
    
    var loadCacheSummary = function() {
        $.ajax({
            url: "<c:url value="/admin/cache/summary"/>",
            type: "GET",
            dataType: "json"
        }).done(function(data) {
        	var $cacheSummaryDiv = $("#cacheSummary");
        	$cacheSummaryDiv.empty();
        	var $table = $("<table />").appendTo($cacheSummaryDiv); 
        	$.each(data, function (key, val) {
        		var $tr = $("<tr />").appendTo($table);
        		$("<td />").appendTo($tr).text(key);
        		$("<td />").appendTo($tr).text(val);
        	});
        }).fail(function(error){
            showError("Capabilites cache summary request failed: " + error.responseText);
        });
    };
    
    //document ready
    $(function() {
    	loadCacheSummary();
    });
</script>

<style>
div#cacheSummary table {
  border-collapse:collapse;
  
}

div#cacheSummary table td {
  text-align: left;
  padding: 3px;
}

div#cacheSummary table td:first-child{
  font-weight: bold;
}
</style>

<div id="cacheSummary" class="row"></div>
        
<jsp:include page="../common/footer.jsp" />
