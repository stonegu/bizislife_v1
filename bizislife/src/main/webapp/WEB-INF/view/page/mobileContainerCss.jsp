<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${cssContainer!=null && cssContainer.subnodes!=null}">

	<c:choose>
	
		<c:when test="${(empty pageMeta) || (pageMeta.defaultcss eq '1')}">
			<c:forEach items="${cssContainer.subnodes}" var="subnode" varStatus="subnodeIndex">
				 #ct_${subnode.systemName}{
					background-color: #${subnode.hexColor};
				 	float: left;
				 	<c:choose>
				 		<c:when test="${subnode.direction=='0'}">
				 			width: 100%;
				 			height: ${subnode.relativeHeight}%;
				 			<c:if test="${subnode.marginTop!=null}">
				 				margin-top:${subnode.marginTop}px;
				 			</c:if>
				 		</c:when>
				 		<c:otherwise>
				 			width: ${subnode.relativeWidth}%;
				 			height: 100%;
				 			<c:if test="${subnode.marginLeft!=null}">
				 				margin-left:${subnode.relativeMarginLeft}%;
				 			</c:if>
				 		</c:otherwise>
				 	</c:choose>
				 }
				 <c:if test="${subnode.subnodes!=null}">
				 	<c:set var="cssContainer" value="${subnode}" scope="request" />
					<c:set var="pageMeta" value="${pageMeta}" scope="request"/>
				 	<c:import url="mobileContainerCss.jsp" />
				 </c:if>
			
			</c:forEach>
		</c:when>
		<c:when test="${pageMeta.defaultcss eq '2'}">
			<c:forEach items="${cssContainer.subnodes}" var="subnode" varStatus="subnodeIndex">
				 #ct_${subnode.systemName}{
				 	float: left;
				 	<c:choose>
				 		<c:when test="${subnode.direction=='0'}">
				 			width: 100%;
				 			height: ${subnode.relativeHeight}%;
				 		</c:when>
				 		<c:otherwise>
				 			width: ${subnode.relativeWidth}%;
				 			height: 100%;
				 		</c:otherwise>
				 	</c:choose>
				 }
				 <c:if test="${subnode.subnodes!=null}">
				 	<c:set var="cssContainer" value="${subnode}" scope="request" />
					<c:set var="pageMeta" value="${pageMeta}" scope="request"/>
				 	<c:import url="mobileContainerCss.jsp" />
				 </c:if>
			
			</c:forEach>
		</c:when>
	</c:choose>

</c:if>