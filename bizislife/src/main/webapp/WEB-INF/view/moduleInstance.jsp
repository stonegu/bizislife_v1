<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="moduleInstanceDataStruct moduleDataStruct">


	<c:if test="${!empty moduleInstance && !isPreview}">
		<c:choose>
			<c:when test="${moduleInstance.getClass().getSimpleName() eq 'EntityDetail'}">
				<input type="hidden" name="instanceId" id="instanceId" value="${moduleInstance.entityuuid}">
			</c:when>
			<c:when test="${moduleInstance.getClass().getSimpleName() eq 'ModuleInstance'}">
				<h3>Instance: <span style="font-style: italic;">"${moduleInstance.name}"</span> in Module : <span style="font-style: italic;">"${moduleDetail.prettyname}"</span><br/>
					<span style="font-size: 11px;">[used space / total space : <span class="instanceUsedSpace">${moduleInstanceUsageWith100Mutipled}/100</span>]</span>
				</h3>
				<input type="hidden" name="instanceId" id="instanceId" value="${moduleInstance.moduleinstanceuuid}">
			</c:when>
		</c:choose>
	</c:if>
	<c:if test="${!empty orgUuid && !isPreview}">
		<input type="hidden" name="orgId" value="${orgUuid}">
	</c:if>

	<c:if test="${!empty instance && !empty instance.attrGroupList}">
		<c:forEach items="${instance.modulegroupuuidGroupsMap}" var="groups" varStatus="groupsIdx">
			<c:if test="${!empty groups.value}">
				<c:forEach items="${groups.value}" var="attrGroup" varStatus="attrGroupIdx">
					<c:set var="attrGroup" value="${attrGroup}" scope="request"/>
					<c:set var="attrGroupIdx" value="${attrGroupIdx.index}" scope="request"/>
					<c:set var="moduleInstance" value="${moduleInstance}" scope="request"/>
					<c:set var="isPreview" value="${isPreview}" scope="request"/>
					<c:import url="/WEB-INF/view/module/instanceAttrGroupSetTemplate.jsp"/>
				</c:forEach>
			</c:if>
		</c:forEach>
		
		
	</c:if>
	
	<c:if test="${!isPreview}">
	
		<div id="medialistForModuleConfigId" class="medialistForModuleConfig displaynone">
		
		  <div class="medialistContainer">
            <div class="mediaTreeArea" style="float: left; width: 200px;">
                <div id="mediaTreeForModuleConfig">
                
                </div>
                
                <form action="/updateInstanceValue" id="mediaSelectForm">
                    <input type="hidden" name="updateType" id="mediaSelectForm_updateType" value="attrValue">
			        <c:choose>
			            <c:when test="${moduleInstance.getClass().getSimpleName() eq 'EntityDetail'}">
			                <input type="hidden" name="instanceId" id="mediaSelectForm_instanceId" value="${moduleInstance.entityuuid}">
			            </c:when>
			            <c:when test="${moduleInstance.getClass().getSimpleName() eq 'ModuleInstance'}">
			                <input type="hidden" name="instanceId" id="mediaSelectForm_instanceId" value="${moduleInstance.moduleinstanceuuid}">
			            </c:when>
			        </c:choose>
                    <input type="hidden" name="groupUuid" id="mediaSelectForm_groupUuid">
                    <input type="hidden" name="attrUuid" id="mediaSelectForm_attrUuid">
                    <input type="hidden" name="valueName" id="mediaSelectForm_valueName">
                    <input type="hidden" name="updateValue" id="mediaSelectForm_defaultValue">
                </form>
                
            </div>
            <div class="mediaSelectionArea" style="margin-left: 205px;">
            
            
            </div>
            
		  </div>
		  
		</div>
		
		<div id="moduleColoringInput" style="display: none;">
			<input class="jsColor"><br/>
			<input type="checkbox" name="defaultMetaColor" class="defaultMetaColor"> Using Default Color
		</div>	
	
	</c:if>
	
	
	
</div>

