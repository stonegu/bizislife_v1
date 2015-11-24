<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${(!empty pageMeta.defaultcss) && (pageMeta.defaultcss eq '4')}"> <%-- purecss --%>
		<div id="ct_${container.systemName}" class="container ${container.prettyName} ${container.cssClassInfos} ${containerPureUnitMap[container.systemName]}">
		
			<%-- for container's content, which is from module's jsp or view's jsp --%>
			<c:if test="${containerContentInfoMap!=null && containerContentInfoMap[container.systemName]!=null}">
				<c:import url="http://${bizhost}/getContainerModuleContent?pageid=${pageMeta.pageuuid}&moduleid=${containerContentInfoMap[container.systemName]['moduleuuid']}&instanceid=${containerContentInfoMap[container.systemName]['instanceuuid']}&viewid=${containerContentInfoMap[container.systemName]['viewuuid']}&categoryid=${categoryid}&pageidx=${pageidx}&entityid=${entityid}&hostname=${hostname}"/>
			</c:if>
			
		
		<%-- 
			<%
			ContainerTreeNode ctn = (ContainerTreeNode)request.getAttribute("container");
			if(ctn!=null) System.out.println("*****************"+ctn.getSystemName());
			
			%>
		 --%>
		
			<c:forEach items="${container.subnodes}" var="container" varStatus="containerIdx">
				<c:set var="container" value="${container}" scope="request"/>
				<c:import url="desktopContainerDetail.jsp" />
			</c:forEach>
		</div>
	
	</c:when>
	<c:otherwise>
		<div id="ct_${container.systemName}" class="container ${container.prettyName} ${container.cssClassInfos}">
		
			<%-- for container's content, which is from module's jsp or view's jsp --%>
			<c:if test="${containerContentInfoMap!=null && containerContentInfoMap[container.systemName]!=null}">
				<c:import url="http://${bizhost}/getContainerModuleContent?pageid=${pageMeta.pageuuid}&moduleid=${containerContentInfoMap[container.systemName]['moduleuuid']}&instanceid=${containerContentInfoMap[container.systemName]['instanceuuid']}&viewid=${containerContentInfoMap[container.systemName]['viewuuid']}&categoryid=${categoryid}&pageidx=${pageidx}&entityid=${entityid}&hostname=${hostname}"/>
			</c:if>
		
			<c:forEach items="${container.subnodes}" var="container" varStatus="containerIdx">
				<c:set var="container" value="${container}" scope="request"/>
				<c:import url="desktopContainerDetail.jsp" />
			</c:forEach>
		</div>
	
	</c:otherwise>
</c:choose>

