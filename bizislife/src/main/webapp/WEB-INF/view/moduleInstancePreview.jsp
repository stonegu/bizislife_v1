<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="moduleInstanceDataStruct moduleDataStruct moduleInstanceDataStructPreview" <c:if test="${!empty moduleInstance && !empty moduleInstance.moduleinstanceuuid}">id="${moduleInstance.moduleinstanceuuid}"</c:if>>
	<input type="hidden" name="moduleInstanceUuid" id="moduleInstanceUuidForDisplayFragment" value="${moduleInstance.moduleinstanceuuid}">

	<c:if test="${!empty instance && !empty instance.attrGroupList}">
		<c:forEach items="${instance.modulegroupuuidGroupsMap}" var="groups" varStatus="groupsIdx">
			<c:if test="${!empty groups.value}">
				<c:forEach items="${groups.value}" var="attrGroup" varStatus="attrGroupIdx">
					<c:set var="attrGroup" value="${attrGroup}" scope="request"/>
					<c:set var="attrGroupIdx" value="${attrGroupIdx.index}" scope="request"/>
					<c:set var="isPreview" value="${true}" scope="request"/>
					<c:import url="/WEB-INF/view/module/instanceAttrGroupSetTemplate.jsp"/>
				</c:forEach>
			</c:if>
		</c:forEach>
		
		
	</c:if>
	
	<%-- for product module preview --%>
	
	<c:if test="${!empty previewType && previewType eq 'productModule'}">
		<div class="btnSection">
			<img class="domReady_newProductInstance" domvalue='${domvalue}' 
				alt="save" title="save" src="/img/vendor/web-icons/tick_32_32.png"> 
		</div>
	</c:if>
	
</div>

