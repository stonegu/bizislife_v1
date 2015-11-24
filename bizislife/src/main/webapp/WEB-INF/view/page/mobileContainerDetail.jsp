<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.bizislife.core.controller.component.ContainerTreeNode" %>

<div id="ct_${container.systemName}" class="container ${container.prettyName} ${container.cssClassInfos}">

	<%-- for container's content, which is from module's jsp or view's jsp --%>
	<c:if test="${containerContentInfoMap!=null && containerContentInfoMap[container.systemName]!=null}">
		<div class="content">
			<c:import url="/getContainerModuleContent?pageid=${pageMeta.pageuuid}&moduleid=${containerContentInfoMap[container.systemName]['moduleuuid']}&instanceid=${containerContentInfoMap[container.systemName]['instanceuuid']}&viewid=${containerContentInfoMap[container.systemName]['viewuuid']}"/>
		</div>
	</c:if>
	
	
<%-- 	
	<%
	ContainerTreeNode ctn = (ContainerTreeNode)request.getAttribute("container");
	if(ctn!=null) System.out.println("*****************"+ctn.getSystemName());
	
	%>
 --%>

	<c:forEach items="${container.subnodes}" var="container" varStatus="containerIdx">
		<c:set var="container" value="${container}" scope="request"/>
		<c:import url="mobileContainerDetail.jsp" />
	</c:forEach>
</div>
