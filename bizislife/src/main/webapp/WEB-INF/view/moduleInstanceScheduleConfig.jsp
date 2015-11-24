<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="moduleInstanceScheduleConfig">
	<form action="/moduleInstanceSchedConfig" name="moduleInstanceSchedConfig">
		<c:if test="${pageModifyPermission}">
			<input type="hidden" name="containerModuleSchedUuid" value="${containerModuleSchedule.uuid}">
			
			<c:if test="${!empty sched}">
				<input type="hidden" name="schedUuid" value="${sched.uuid}">
			</c:if>
		</c:if>

		<h2 class="scheduleTitle" style="text-align: center; background-color: gray; color: white;">
			<c:choose>
				<c:when test="${!empty sched}">
					Schedule for ${containerModuleSchedule.schedulename}: ${sched.schedulename}
				</c:when>
				<c:otherwise>
					New Module-Instance Schedule For ${containerModuleSchedule.schedulename}
				</c:otherwise>
			</c:choose>
		</h2>

		<div class="instanceTreeForSched" style="float: left; width: 40%; margin: 10px 0 0;">
		
			<input type="button" value="Collapse All" onclick="$('#instanceTreeForSched').jstree('close_all');">
			<input type="button" value="Expand All" onclick="$('#instanceTreeForSched').jstree('open_all');">
			<div id="instanceTreeForSched" <c:if test="${!pageModifyPermission}">class="preview"</c:if>>
				<c:if test="${!empty moduleDetail && !empty moduleInstances}">
					<ul>
						<li id="${moduleDetail.moduleuuid}" rel="module" class=""> 
							<a href="#">${moduleDetail.prettyname}</a>
							<ul>
								<c:forEach items="${moduleInstances}" var="instance" varStatus="instanceIndex">
									<li id="${instance.moduleinstanceuuid}" rel="instance" class="">
										
										<c:choose>
											<c:when test="${instance.moduleinstanceuuid eq moduleInstance.moduleinstanceuuid}">
												<a href="#">
													<span class="redBorder">${instance.name}</span><c:if test="${instanceUsageMap[instance.moduleinstanceuuid]>0}"><span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' domvalue='{"topOffset":20, "leftOffset":-30, "ajaxCall":{"url":"/getModuleInstanceInfo", "params":"instanceid=${instance.moduleinstanceuuid}"}}'>${instanceUsageMap[instance.moduleinstanceuuid]}</span></c:if>
												</a>
											</c:when>
											<c:otherwise>
												<a href="#">
													${instance.name}<c:if test="${instanceUsageMap[instance.moduleinstanceuuid]>0}"><span class='detailInfoPopup' style='color:red; font-size:.6em; font-family:times new roman; margin-top:-4px; position:absolute;' domvalue='{"topOffset":20, "leftOffset":-30, "ajaxCall":{"url":"/getModuleInstanceInfo", "params":"instanceid=${instance.moduleinstanceuuid}"}}'>${instanceUsageMap[instance.moduleinstanceuuid]}</span></c:if>
												</a>
											</c:otherwise>
										</c:choose>
									</li>
								</c:forEach>
							</ul>
						</li>
					</ul>
				</c:if>
			</div>
		
		</div>
		
		<div class="schedConfirmationArea" style="float: right; width: 59%; margin: 10px 0 0;">
			<h2 style="text-decoration: underline;">3-steps to setup the schedule:</h2>
		
			<h3>Step #1: select instance from the left instance tree.</h3>
			<div class="schedInstanceSelected" style="float: left; width: 100%;">
				<c:choose>
					<c:when test="${!empty instance}">
						<c:set var="instance" value="${instance}" scope="request"/>
						<c:import url="moduleInstancePreview.jsp"/>
					</c:when>
					<c:otherwise>
						<div class="moduleInstanceDataStruct moduleDataStruct moduleInstanceDataStructPreview">
							Please to select a instance!
						</div>					
					</c:otherwise>
				</c:choose>
			
			</div>
			
			<div class="schedDate" style="float: left; width: 100%;">
				<h3>Step #2: pick the date range:</h3>
				<p style="margin: 0 0 5px; width: 200px;">From: <input type="text" name="startdate" class="domReady_datePicker" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${sched.startdate}" />'></p>
				<p style="width: 200px;">To: <input type="text" name="enddate" class="domReady_datePicker" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${sched.enddate}" />'></p>
			</div>
			<div class="schedPriority" style="float: left; width: 100%;">
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
				<div class="schedConfirm" style="float: left; width: 100%; margin: 20px 0 0;">
					<c:choose>
						<c:when test="${empty sched}">
							<button class="newModuleInstanceSchedConfirm" type="button">New Schedule</button>
						</c:when>
						<c:otherwise>
							<button class="editModuleInstanceSchedConfirm" type="button">Confirm</button>
						</c:otherwise>
					</c:choose>
					
				</div>
			</c:if>
			
		</div>
	
	</form>
</div>