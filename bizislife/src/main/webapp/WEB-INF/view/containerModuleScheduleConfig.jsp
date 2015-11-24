<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="containerModuleScheduleConfig">

	<form action="/containerModuleSchedConfig" name="containerModuleSchedConfig">
	
		<c:if test="${pageModifyPermission}">
			<input type="hidden" name="containerUuid" value="${container.containeruuid}">
		</c:if>
		
		<c:if test="${!empty sched}">
			<input type="hidden" name="schedUuid" value="${sched.uuid}">
		</c:if>
	
	
		<h2 class="scheduleTitle" style="text-align: center; background-color: gray; color: white;">
			<c:choose>
				<c:when test="${!empty sched}">
					Schedule for ${container.prettyname}: ${sched.schedulename}
				</c:when>
				<c:otherwise>
					New Container-Module Schedule For ${container.prettyname}
				</c:otherwise>
			</c:choose>
		</h2>
	
		<div class="moduleTreeForSched" style="float: left; width: 40%; margin: 10px 0 0; overflow: auto;">
		
			<input type="button" value="Collapse All" onclick="$('#moduleTreeForSched').jstree('close_all');">
			<input type="button" value="Expand All" onclick="$('#moduleTreeForSched').jstree('open_all');">
			<div id="moduleTreeForSched" <c:if test="${!pageModifyPermission}">class="preview"</c:if>>
			
			</div>
		
		</div>
		
		<div class="schedConfirmationArea" style="float: right; width: 59%; margin: 10px 0 0;">
			<h2 style="text-decoration: underline;">3-steps to setup the schedule:</h2>
		
			<h3>Step #1: select module from the left module tree.</h3>
			<div class="schedModuleSelected" style="float: left; width: 100%; margin-top: 10px;">
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
			<div class="schedDate" style="float: left; width: 100%; margin-top: 10px;">
				<h3>Step #2: pick the date range:</h3>
				<p style="margin: 0 0 5px; width: 200px;">From: <input type="text" name="startdate" class="domReady_datePicker" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${sched.startdate}" />'></p>
				<p style="width: 200px;">To: <input type="text" name="enddate" class="domReady_datePicker" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${sched.enddate}" />'></p>
			</div>
			<div class="schedPriority" style="float: left; width: 100%; margin-top: 10px;">
				<h3>Step #3: choose the priority:</h3>
				<select name="priority" style="width: 100%;">
					<c:forEach items="${priorityLevels}" var="priority" varStatus="priorityIdx">
						<c:choose>
							<c:when test="${priority.code eq sched.priority }">
								<option value="${priority.code}" selected="selected">${priority.name()}</option>
							</c:when>
							<c:otherwise>
								<option value="${priority.code}">${priority.name()}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</div>
			
			<c:if test="${pageModifyPermission}">
				<div class="schedConfirm" style="float: left; width: 100%; margin: 10px 0 0;">
					<c:choose>
						<c:when test="${empty sched}">
							<button class="newContainerModuleSchedConfirm" type="button">New Schedule</button>
						</c:when>
						<c:otherwise>
							<button class="editContainerModuleSchedConfirm" type="button">Confirm</button>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
			
		
		</div>
	
	</form>

</div>

