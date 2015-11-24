<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
 
<c:if test="${!empty selectedGroups_self}">
	<strong>Joined self org's groups:</strong><br/>
	<c:forEach items="${selectedGroups_self}" var="group" varStatus="groupIdx">
		<c:if test="${groupIdx.count>1}">
		,&nbsp;
		</c:if>
		${group.groupname}
	</c:forEach>
</c:if>

<c:if test="${!empty selectedGroups_other}">
	<br/>
	<strong>Joined other org's groups:</strong><br/>
	<c:forEach items="${selectedGroups_other}" var="group" varStatus="groupIdx">
		<c:if test="${groupIdx.count>1}">
		,&nbsp;
		</c:if>
		${group.groupname} (from: ${orgIdNameMap[group.organization_id]})
	</c:forEach>
</c:if>
 