<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<fieldset class="moduleParamGroup">
	<legend>param group <c:choose><c:when test="${paramGroup.array && groupIndex==1}"><span class="groupDuplicate domReady_dupliModuleElemt" domvalue="${paramGroup.groupName}">[+]</span></c:when><c:when test="${paramGroup.array && groupIndex>1}"><span class="groupRemove domReady_deleteModuleElemt">[-]</span></c:when></c:choose></legend>
	<c:if test="${!empty paramGroup.params}">
		<c:forEach items="${paramGroup.paramsMap}" var="params" varStatus="paramsIndex">
			<fieldset>
				<legend>${params.key}</legend>
				<c:forEach items="${params.value}" var="moduleParam" varStatus="paramIndex">
					<c:set var="groupName" value="${paramGroup.groupName}" scope="request"/>
					<c:set var="groupIndex" value="${groupIndex}" scope="request"/>
					<c:set var="paramIndex" value="${paramIndex.count}" scope="request"/>
					<c:set var="moduleParam" value="${moduleParam}" scope="request"/>
					<c:set var="isRemove" value="${false}" scope="request"/>
					<c:import url="moduleInstance_param.jsp" />
				</c:forEach>
			</fieldset>
		</c:forEach>
	</c:if>
</fieldset>
