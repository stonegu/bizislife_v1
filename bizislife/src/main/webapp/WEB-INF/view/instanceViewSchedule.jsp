<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="instanceViewSchedule">

	<div class="instanceViewSchedule_info" style="color: red;">
	
	</div>
	
	<form action="/editInstanceViewSchedule" method="post" name="edit_${sch.uuid}">
		<input type="hidden" name="scheduleId" id="scheduleId" value="${sch.uuid}">
<%-- 	
		<p>Instance: ${instance.value}</p>
 --%>		
		<div class="scheduleContent">
			<h3 style="text-align: center; margin: 0; padding: 5px; background-color: gray; color: white;">${sch.schedulename}</h3>
			
			<div id="schedFromDate_${sch.uuid}">
				<p>
					<strong>From Date:</strong>
					
					<c:if test="${modifyPermissionAllow}">
						<span class="editIcon valueSection">
							<img class="domReady_toEditValue" 
							domvalue="schedFromDate_${sch.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_saveViewSchedValue" 
								domvalue="{'typeToSave':'schedValue', 'webSectionId':'schedFromDate_${sch.uuid}', 'valueName':'startdate', 'valueId':'schedFromDate_v_${sch.uuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="domReady_cancelToEditValue" 
								domvalue="schedFromDate_${sch.uuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					
					</c:if>
							
				</p>
				<div class="value valueSection">
					<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.startdate}" />
				</div>
				
				<c:if test="${modifyPermissionAllow}">
					<div class="valueEdit editSection displaynone">
						<input id="schedFromDate_v_${sch.uuid}" type="text" name="fromDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.startdate}" />">
					</div>
				</c:if>
				
			</div>
			
			
			<div id="schedToDate_${sch.uuid}">
			
				<p>
					<strong>To Date:</strong>
					
					<c:if test="${modifyPermissionAllow}">
						<span class="editIcon valueSection">
							<img class="domReady_toEditValue" 
							domvalue="schedToDate_${sch.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_saveViewSchedValue" 
								domvalue="{'typeToSave':'schedValue', 'webSectionId':'schedToDate_${sch.uuid}', 'valueName':'enddate', 'valueId':'schedToDate_v_${sch.uuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="domReady_cancelToEditValue" 
								domvalue="schedToDate_${sch.uuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
					
				</p>
				<div class="value valueSection">
					<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.enddate}" />
				</div>
				
				<c:if test="${modifyPermissionAllow}">
					<div class="valueEdit editSection displaynone">
						<input id="schedToDate_v_${sch.uuid}" type="text" name="toDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.enddate}" />">
					</div>
				</c:if>
				
			</div>
			
 			<div id="schedPriority_${sch.uuid}">
			
				<p>
					<strong>Priority:</strong>
					
					<c:if test="${modifyPermissionAllow}">
						<span class="editIcon valueSection">
							<img class="domReady_toEditValue" 
							domvalue="schedPriority_${sch.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_saveViewSchedValue" 
								domvalue="{'typeToSave':'schedValue', 'webSectionId':'schedPriority_${sch.uuid}', 'valueName':'priority', 'valueId':'schedPriority_v_${sch.uuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="domReady_cancelToEditValue" 
								domvalue="schedPriority_${sch.uuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					
					</c:if>
				</p>
				<div class="value valueSection">
					<c:forEach items="${priorityLevels}" var="priority" varStatus="priorityIdx">
						<c:if test="${priority.code eq sch.priority}">
							${priority.name()}
						</c:if>
					</c:forEach>
				</div>
				<c:if test="${modifyPermissionAllow}">
					<div class="valueEdit editSection displaynone">
						<select id="schedPriority_v_${sch.uuid}" name="priority" style="width: 100%;">
							<c:forEach items="${priorityLevels}" var="priority" varStatus="priorityIdx">
								<c:choose>
									<c:when test="${priority.code eq sch.priority }">
										<option value="${priority.code}" selected="selected">${priority.name()}</option>
									</c:when>
									<c:otherwise>
										<option value="${priority.code}">${priority.name()}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div>
				</c:if>
				
				
			</div>
			
		</div>
		
	
	</form>

	
</div>




