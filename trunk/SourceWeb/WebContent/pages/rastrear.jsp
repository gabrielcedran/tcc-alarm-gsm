<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<html>
	<head>
		<title>Insert title here</title>
	</head>
	<body>
		<f:view>
			<h:form>
				<h:outputLabel value="ID:"/>
				<h:inputText value="#{RastrearBean.idCarro }" />
				<h:commandButton action="#{RastrearBean.consultarUltimaLocalizacao }" value="Rastrear!"/>
			</h:form>
		</f:view>
	</body>
</html>