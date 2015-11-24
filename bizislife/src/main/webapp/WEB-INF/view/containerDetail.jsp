<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.bizislife.core.controller.component.ContainerTreeNode" %>

<div 
	id="ct_${pageContainer.systemName}" 
	colorvalue="${pageContainer.hexColor}" 
	class="clickForContainer <c:if test='${!pageModifyPermission}'>preview</c:if> <c:if test='${isContainerScheduled[pageContainer.systemName]}'>scheduledContainer </c:if>${(pageContainer.editable eq '0')?'noEditable':(empty pageContainer.subnodes?'toplevel':'underlevel')} ${pageContainer.direction=='1'?'leftright':'topdown'}" 
	style="<c:choose><c:when test='${isContainerScheduled[pageContainer.systemName]}'>background-image: url('/img/vendor/web-icons/calendar-medium.png'); background-repeat: no-repeat; background-attachment: scroll; background-position: right 3px; background-color: #${pageContainer.hexColor};</c:when><c:otherwise>background-color: #${pageContainer.hexColor}</c:otherwise></c:choose>; height: ${pageContainer.height}px; width: ${pageContainer.width}px; top: ${pageContainer.topposition}px; left: ${pageContainer.leftposition}px; position: absolute;"
>
	<c:forEach items="${pageContainer.subnodes}" var="pageContainer" varStatus="pageContainerIdx">
		<c:set var="pageContainer" value="${pageContainer}" scope="request"/>
		<c:set var="isContainerScheduled" value="${isContainerScheduled}" scope="request"/>
		<c:set var="pageModifyPermission" value="${pageModifyPermission}" scope="request"/>
		<c:import url="containerDetail.jsp" />
	</c:forEach>
</div>



