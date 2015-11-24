<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${moduleParam.type eq 'ModuleStringParam'}">
		<div class="moduleParam ModuleStringParam">
			${moduleParam.title}: <input type="text" name="${groupName}_${moduleParam.name}_${groupIndex}" value="${moduleParam.defaultValue}"> <c:choose><c:when test="${moduleParam.array && paramIndex==1}"><span class="paramDuplicate domReady_dupliModuleElemt" domvalue="${groupName}_${moduleParam.name}" paramGroupIndex="${groupIndex}">[+]</span></c:when><c:when test="${(moduleParam.array && paramIndex>1) || isRemove}"><span class="paramRemove domReady_deleteModuleElemt">[-]</span></c:when></c:choose>
		</div>
	</c:when>
	<c:when test="${moduleParam.type eq 'ModuleIntegerParam'}">
		<div class="moduleParam ModuleIntegerParam">
			${moduleParam.title}: <input type="text" name="${groupName}_${moduleParam.name}_${groupIndex}" value="${moduleParam.defaultValue}"> <c:choose><c:when test="${moduleParam.array && !isRemove}"><span class="paramDuplicate domReady_dupliModuleElemt" domvalue="${groupName}_${moduleParam.name}" paramGroupIndex="${groupIndex}">[+]</span></c:when><c:when test="${moduleParam.array && isRemove}"><span class="paramRemove domReady_deleteModuleElemt">[-]</span></c:when></c:choose>
		</div>
	</c:when>
</c:choose>
