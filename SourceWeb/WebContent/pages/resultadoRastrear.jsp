<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<html>
	<head>
		<title>Insert title here</title>
	</head>
	<body>
		<f:view>
			<h:form>
				Última atualização: <h:outputText value="#{RastrearBean.ultimaAtualizacao }"/>
				<rich:gmap lat="#{RastrearBean.latitude }" lng="#{RastrearBean.longitude }" mapType="G_NORMAL_MAP"
				zoom="18" gmapKey="ABQIAAAA1bS2ANqW2Y5QKk_LXw3F0RSx1rRqXjvkWiZxTWl72Yjd30X1DBTpygQ9LsEYnnXAD0xvRjKFeFZq2Q" />
				<h:commandButton action="voltar" value="Voltar!"/>
			</h:form>
		</f:view>
	</body>
</html>