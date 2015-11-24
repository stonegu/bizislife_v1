<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="containerDefaultModuleSetup">

	<form action="/containerDefaultModuleConfig" name="containerDefaultModuleConfig">
	
		<c:if test="${pageModifyPermissionAllow}">
			<input type="hidden" name="containerUuid" value="${container.containeruuid}">
		</c:if>
		
		<h2 class="defaultModuleSetupTitle" style="text-align: center; background-color: gray; color: white;">
			Default Module Setup For ${container.prettyname}
		</h2>
	
		<div class="moduleTreeForContainerDefaultModule" style="float: left; width: 40%; margin: 10px 0 0;">
		
			<input type="button" value="Collapse All" onclick="$('#moduleTreeForContainerDefaultModule').jstree('close_all');">
			<input type="button" value="Expand All" onclick="$('#moduleTreeForContainerDefaultModule').jstree('open_all');">
			<div id="moduleTreeForContainerDefaultModule" <c:if test="${!pageModifyPermissionAllow}">class="preview"</c:if>>
			
			</div>
		
		</div>
		
		<div class="defaultModuleConfirmationArea" style="float: right; width: 59%; margin: 10px 0 0;">
			<h3>select module from the left module tree.</h3>
			<div class="defaultModuleSelected" style="float: left; width: 100%;">
				<c:choose>
					<c:when test="${!empty moduleDetail}">
						<c:choose>
							<c:when test="${!empty module}">
								<c:set var="module" value="${module}" scope="request"/>
								<c:set var="moduleDetail" value="${moduleDetail}" scope="request"/>
								<c:import url="moduleDisplayFragment.jsp"/>
							</c:when>
							<c:otherwise>
								The module hasn't designed yet!
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<div class="moduleDisplayFragment">
							<div class="moduleDataStruct" style="line-height: 250px; text-align: center;">
								Please to select a module!
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			
			</div>
			
			<c:if test="${pageModifyPermissionAllow}">
				<div class="defaultModuleConfirm" style="float: left; width: 100%; margin: 20px 0 0; text-align: center;">
					<button class="defaultContainerModuleConfirm" type="button">Confirm</button>
				</div>
			</c:if>
		
		</div>
	
	</form>

</div>

