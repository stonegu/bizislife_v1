<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="moduleInstanceSchedule">

	<p>Container: ${container.prettyname}</p>
	<p>Container Schedule: ${cmSchedule.schedulename} - <strong>From </strong><fmt:formatDate pattern="yyyy-MM-dd" value="${cmSchedule.startdate}" /> <strong>To </strong><fmt:formatDate pattern="yyyy-MM-dd" value="${cmSchedule.enddate}" /> </p>
	<p>Module: ${module.value}</p>
	
	<div class="scheduleTable">
		<table style="border: 1px solid;" width="590">
			<thead>
				<tr style="background-color: gray; color: white;">
					<th width="16%">Schedule Name</th>
					<th width="16%">Instance Name</th>
					<th width="16%">From Date</th>
					<th width="16%">To Date</th>
					<th width="16%">Priority</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${miSchedules}" var="sch" varStatus="schIndex">
					<tr id="${sch.uuid}" style="background-color: silver;" class="scheduleLine">
						<td style="text-align: center;">${sch.schedulename}</td>
						<td style="text-align: center;">
							<c:forEach items="${instances}" var="instance" varStatus="instanceIndex">
								<c:if test="${instance.key eq sch.moduleinstanceuuid}">
									${instance.value}
								</c:if>
							</c:forEach>
						</td>
						<td style="text-align: center;"><fmt:formatDate pattern="yyyy-MM-dd" value="${sch.startdate}" /></td>
						<td style="text-align: center;"><fmt:formatDate pattern="yyyy-MM-dd" value="${sch.enddate}" /></td>
						<td style="text-align: center;">
							<c:forEach items="${priorityLevels}" var="priority" varStatus="priorityIdx">
								<c:if test="${priority.code eq sch.priority}">
									${priority.name()}
								</c:if>
							</c:forEach>
						</td>
						<td style="text-align: center;">
							<button type="button" class="scheduleEdit" domvalue="${sch.uuid}">Edit</button>
						</td>
					</tr>
					<tr id="edit_${sch.uuid}" style="display: none; background-color: silver;" class="scheduleEditContainer edit_scheduleLine">
						<td colspan="6">
							<form action="/editModuleInstanceSchedule" method="post" name="edit_${sch.uuid}"> 
								<input type="hidden" name="scheduleId" value="${sch.uuid}">
								<table style="border: 1px solid gray; width: 100%;">
									<tbody>
										<tr>
											<td width="16%" style="text-align: center;">
												<input type="text" name="scheduleName" value="${sch.schedulename}">
											</td>
											<td width="16%" style="text-align: center;">
											
												<c:forEach items="${instances}" var="instance" varStatus="instanceIndex">
													<c:if test="${instance.key eq sch.moduleinstanceuuid}">
														${instance.value}
													</c:if>
												</c:forEach>
											</td>
											<td width="16%" style="text-align: center;"><input type="text" name="fromDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.startdate}" />"></td>
											<td width="16%" style="text-align: center;"><input type="text" name="toDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${sch.enddate}" />"></td>
											<td width="16%" style="text-align: center;">
												<select name="priority" style="width: 100%;">
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
											</td>
											<td style="text-align: center;">
												<button type="button" style="float: left;" class="editModuleInstanceScheduleSubmitBtn" domvalue="${sch.uuid}">Save</button> <button type="button" style="float: right; color: red;" class="delModuleInstanceScheduleSubmitBtn" domvalue="${sch.uuid}">Del</button>
											</td>
										</tr>
									</tbody>
								</table>
							</form>
						</td>
					</tr>
				</c:forEach>
				<tr class="newSchedule">
					<td colspan="6">
						<c:choose>
							<c:when test="${empty instances}">
								<span>You need to setup instance for module (${module.value}) first!</span>
							</c:when>
							<c:otherwise>
								<form action="/newModuleInstanceSchedule" method="post" name="newModuleInstanceSchedule"> 
									<input type="hidden" name="containerModuleScheduleId" value="${cmSchedule.uuid}">
									<table style="border: 1px solid; width: 100%">
										<tbody>
											<tr>
												<td width="16%" style="text-align: center;">
													<input type="text" name="scheduleName">
												</td>
												<td width="16%" style="text-align: center;">
													<select name="instanceId" style="width: 100%;">
														<c:forEach items="${instances}" var="instance" varStatus="instanceIndex">
															<option value="${instance.key}">${instance.value}</option>
														</c:forEach>
													</select>
												</td>
												<td width="16%" style="text-align: center;"><input type="text" name="fromDate" class="domReady_datePicker"></td>
												<td width="16%" style="text-align: center;"><input type="text" name="toDate" class="domReady_datePicker"></td>
												<td width="16%" style="text-align: center;">
													<select name="priority" style="width: 100%;">
														<c:forEach items="${priorityLevels}" var="priority" varStatus="priorityIdx">
															<option value="${priority.code}">${priority.name()}</option>
														</c:forEach>
													</select>
												</td>
												<td style="text-align: center;">
													<button type="button" class="newModuleInstanceScheduleSubmitBtn">New</button>
												</td>
											</tr>
										</tbody>
									</table>
								</form>
							</c:otherwise>
						</c:choose>
					</td>
				
				</tr>
				
				
				
			</tbody>
			
			
		</table>
	</div>

</div>
