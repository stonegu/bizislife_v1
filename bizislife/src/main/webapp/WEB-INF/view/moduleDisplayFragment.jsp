<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="moduleDisplayFragment" id="${moduleDetail.moduleuuid}">

	<input type="hidden" name="moduleUuid" id="moduleUuidForDisplayFragment" value="${moduleDetail.moduleuuid}">

	<c:choose>
		<c:when test="${moduleDetail.type eq 'pm'}">
			<div class="moduleDataStruct">
				<div style="text-align: center; color: blue; padding: 30px;">
					<strong>Product Module</strong><br/>
					<span>Your selection is the product module (template). You can choose any product modules on the left, but the result is the same: which product module will be display for product is defined in product, not in page container!</span>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="moduleDataStruct">
				<c:if test="${!empty module && !empty module.attrGroupList}">
					<c:forEach items="${module.attrGroupList}" var="attrGroup" varStatus="attrGroupIndex">
						<c:set var="attrGroup" value="${attrGroup}" scope="request"/>
						<%-- isDisplayOnly: to hide all 'config' functions since 'isDisplayOnly=true' means only module structure display needed --%>					
						<c:set var="isDisplayOnly" value="${true}" scope="request"/>
						<c:import url="module/moduleAttrGroupSetTemplate.jsp"/>
					</c:forEach>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>


</div>