<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<html>
	<head>
		<title>Alarme Android</title>
	</head>
	<body style="background-image: url(auto-estrada.jpg); background-size: 100% 120%; background-repeat:no-repeat;">
		<f:view>
			<h:form>
			<center>
				Última atualização: <h:outputText value="#{RastrearBean.ultimaAtualizacao }"/>
				<div style="background-image: url(gps.png); width: 799px; height: 561px; position: relative; float:center;">
					<div style="position: relative; float:center; padding-top: 112px;">
						<rich:gmap gmapVar="mapVar" lat="#{RastrearBean.latitude }" lng="#{RastrearBean.longitude }" mapType="G_NORMAL_MAP"
						zoom="16" gmapKey="ABQIAAAA1bS2ANqW2Y5QKk_LXw3F0RSx1rRqXjvkWiZxTWl72Yjd30X1DBTpygQ9LsEYnnXAD0xvRjKFeFZq2Q" 
						style="width: 540px; height: 400px;" />
					</div>				
				</div>
				<script>
					window.onload = function() {
					    mapVar.addOverlay(
					        new GMarker(new GLatLng('${RastrearBean.latitude }', '${RastrearBean.longitude }')));
					}
				</script>
				<h:commandButton action="voltar" value="Voltar"/>
			</center>
			</h:form>
		</f:view>
	</body>
</html>