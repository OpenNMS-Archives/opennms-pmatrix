<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OpenNMS PMATRIX display</title>
<link rel="stylesheet" type="text/css" href="css/styles.css" media="screen" />
<link rel="stylesheet" type="text/css" href="css/gwt-asset.css" media="screen" />
<link rel="stylesheet" type="text/css" href="css/pmatrix-styles.css" media="screen" />


</head>
<%
// code to set default request value if no url query values supplied
String queryString="";
if (request.getQueryString()==null || "".equals(request.getQueryString())) {
	queryString= org.opennms.features.vaadin.pmatrix.ui.UiComponentFactory.COMPONENT_REQUEST_PARAMETER+"="+
			org.opennms.features.vaadin.pmatrix.ui.UiComponentFactory.DEFAULT_COMPONENT_REQUEST_VALUE;
} else queryString = request.getQueryString();
%>
<body>
	<div id="header">
		<h1 id="headerlogo">
			<a href="/index.jsp"><img src="images/logo.png"
				alt="OpenNMS Web Console Home" /></a>
		</h1>
	</div>
	<div id="headernavbarright">
		<div class="navbar">
			<ul>
				<li class="last"><a href="http://www.opennms.org/" target="_blank">About</a></li>
			</ul>
		</div>
	</div>
	<div class="spacer">
		<!-- -->
	</div>
	<div id="content>">
	<BR><BR><BR><BR>
	<table border="0">
<tr>
<td style="width:100%;horizontal-align:middle;">
		<div class="onms">
		     <!-- pass query string sent to the JSP on to the Vaadin UI -->
			 <iframe src="/vaadin-pmatrix/PmatrixUI?<%= queryString  %>" style="height:1500px; width:100%; overflow:visible;" frameborder="0"></iframe> 

		</div>
</td>
</tr>
</table> 

	</div>
	<div id="prefooter"></div>

	<div id="footer">
		<p>
			OpenNMS Copyright</a> &copy; 2002-2014 <a href="http://www.opennms.com/">The
				OpenNMS Group, Inc.</a> OpenNMS&reg; is a registered trademark of <a
				href="http://www.opennms.com">The OpenNMS Group, Inc.</a>
		</p>
	</div>

</body>
</html>