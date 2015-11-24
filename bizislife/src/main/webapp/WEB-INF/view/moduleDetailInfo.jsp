<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>
	<P>
		There have ${totalSchedUsed} schedules to use this module:
		<ul>
			<c:forEach items="${sortedKeys}" var="key" varStatus="keyIndex">
				<li style="margin: 0 0 0 13px;"><span>${key}</span>
					<c:if test="${!empty containerWithSchedulesMap[key]}">
						<ul>
							<c:forEach items="${containerWithSchedulesMap[key]}" var="cmSchedule" varStatus="cmScheduleIdx">
								<li style="margin: 0 0 0 13px;">
									<c:choose>
										<c:when test="${(!empty scheduleStatusMap[cmSchedule.uuid]) && scheduleStatusMap[cmSchedule.uuid]}">
											<span style="border: 1px solid red;">${cmSchedule.schedulename}</span> <span>[current using!]</span>
										</c:when>
										<c:when test="${(!empty scheduleStatusMap[cmSchedule.uuid]) && !scheduleStatusMap[cmSchedule.uuid]}">
											<span style="text-decoration: line-through;">${cmSchedule.schedulename}</span> <span>[outdated!]</span>
										</c:when>
										<c:otherwise>
											<span>${cmSchedule.schedulename}</span>
										</c:otherwise>
									</c:choose>
								
									
								</li>
							</c:forEach>
						</ul>
					</c:if>
				</li>
			</c:forEach>
		</ul>
	</P>
</div>