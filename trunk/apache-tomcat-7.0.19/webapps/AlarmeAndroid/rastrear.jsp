<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<html>
	<head>
		<title>Alarme Android</title>
	</head>
	<body style="background-color: black;">
		<f:view>
			<h:form>
				<center>
					<div style="background-image: url(tela_login.png); width: 799px; height: 561px; position: relative; float:center;">
						<div style="position: relative; float: left; width: 799px; padding-top: 310px;">
							<h:inputText value="#{RastrearBean.idCarro }" size="40" style="margin-left: 70px;" /><br/>
						</div>
						<div style="position: relative; float: left; width: 799px; padding-top: 40px; ">
							<h:inputSecret value="#{RastrearBean.senha }" size="40" style="margin-left: 70px;" /><br/>
						</div>
						<div style="position: relative; float: left; width: 799px; padding-top: 50px;">
							<h:commandButton action="#{RastrearBean.consultarUltimaLocalizacao }" value="Rastrear" style="margin-left: 70px;"/>
						</div>					
					</div>
				</center>
			</h:form>
		</f:view>
	</body>
</html>