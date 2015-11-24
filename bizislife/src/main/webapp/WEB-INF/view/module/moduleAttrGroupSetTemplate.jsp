<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<fieldset class="groupset">
	<legend>
		<span id="groupName_${attrGroup.groupUuid}">
			Group: <span class="value valueSection">${attrGroup.groupName}</span>
			<c:if test="${!isDisplayOnly && modifyPermissionAllowed}">
				<span class="valueEdit editSection displaynone"><input id="groupName_v_${attrGroup.groupUuid}" type="text" name="groupName" value="${attrGroup.groupName}" /></span> 
				<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="groupName_${attrGroup.groupUuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
				<span class="editActionIcons editSection displaynone"><img class="domReady_saveModuleValue" domvalue="{'typeToSave':'groupValue', 'webSectionId':'groupName_${attrGroup.groupUuid}', 'valueName':'groupName', 'valueId':'groupName_v_${attrGroup.groupUuid}', 'groupUuid':'${attrGroup.groupUuid}'}" alt="save" src="/img/vendor/web-icons/tick.png"> <img class="domReady_cancelToEditValue" domvalue="groupName_${attrGroup.groupUuid}" alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
			</c:if>
		</span>
		<span id="groupArray_${attrGroup.groupUuid}">
			[ <span class="title">isArray: </span><span class="value valueSection">${attrGroup.array?"true":"false"}</span>
			<c:if test="${!isDisplayOnly && modifyPermissionAllowed}">
				<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="groupArray_${attrGroup.groupUuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
				<span class="valueEdit editSection displaynone"><input class="moduleCheckboxChangeVal" id="groupArray_v_${attrGroup.groupUuid}" type="checkbox" name="array" value="1" <c:if test="${attrGroup.array}">checked="checked"</c:if>></span>
				<span class="editActionIcons editSection displaynone"><img class="domReady_saveModuleValue" domvalue="{'typeToSave':'groupValue', 'webSectionId':'groupArray_${attrGroup.groupUuid}', 'valueName':'array', 'valueId':'groupArray_v_${attrGroup.groupUuid}', 'groupUuid':'${attrGroup.groupUuid}'}" alt="save" src="/img/vendor/web-icons/tick.png"> <img class="domReady_cancelToEditValue" domvalue="groupArray_${attrGroup.groupUuid}" alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
			</c:if>
			 ]
		</span>
		<c:if test="${!isDisplayOnly && modifyPermissionAllowed}">
			<span style="float: right;" class="groupDelete"><img src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" domvalue="${attrGroup.groupUuid}" class="groupDel delete domReady_deleteAttrGroupSet" title="delete"></span>
		</c:if>
	</legend>
	<div class="attrListContainer" id="${attrGroup.groupUuid}">
		<c:if test="${!empty moduleDetailUsageWith100Multipled_newAttrGroupSet}">
			<input type="hidden" class="moduleDetailUsageWith100Multipled_newAttrGroupSet" name="moduleDetailUsageWith100Multipled_newAttrGroupSet" value="${moduleDetailUsageWith100Multipled_newAttrGroupSet}"/>
		</c:if>
	
		<div id="moduleValueSaveStatus_${attrGroup.groupUuid}" class="attrGroupStatus"></div>
	
		<c:if test="${!empty attrGroup.attrList}">
			<c:forEach items="${attrGroup.attrList}" var="attr" varStatus="attrIndex">
				<c:set var="moduleAttr" value="${attr}" scope="request"/>
				<c:set var="attrGroup" value="${attrGroup}" scope="request"/>
				<c:set var="isDisplayOnly" value="${isDisplayOnly}" scope="request"/>
				<c:set var="targetWithMetaDataMap" value="${targetWithMetaDataMap}" scope="request"/>
				<c:set var="moduleDetail" value="${moduleDetail}" scope="request"/>
				<c:set var="modifyPermissionAllowed" value="${modifyPermissionAllowed}" scope="request"/>
				<c:import url="module/ModuleAttrGeneral.jsp"/>
			</c:forEach>
		</c:if>
	
	</div>
	
</fieldset>
