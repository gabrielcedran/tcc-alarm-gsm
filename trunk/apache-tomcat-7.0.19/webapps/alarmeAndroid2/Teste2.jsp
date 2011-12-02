<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<c:out value="x"></c:out>
<f:view>
	<h:form>
		<h:outputText value="Didão viado!"></h:outputText>
		<rich:calendar ></rich:calendar>
		<rich:gmap lat="-22.975527" lng="-46.991388" mapType="G_NORMAL_MAP" ></rich:gmap>
	</h:form>
</f:view>
</body>
</html>