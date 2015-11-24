<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<c:if test="${!empty entityRoot}">
	<li id="${entityRoot.systemName}" class="${entityRoot.cssClassInfos}" rel="${entityRoot.type.code=='fd'?'folder':'default'}">
		<a href="#">${entityRoot.prettyName}</a>
		
		<c:if test="${(!empty entityRoot.subnodes) || ((!empty entityWithViewsMap) && (!empty entityWithViewsMap[entityRoot.systemName]))}">
			<ul>
			
				<%-- for views --%>
				<c:if test="${(!empty entityWithViewsMap) && (!empty entityWithViewsMap[entityRoot.systemName])}">
					<c:forEach items="${entityWithViewsMap[entityRoot.systemName]}" var="view" varStatus="viewIdx">
						<li id="${view.instanceviewuuid}" rel="instanceView" class="newSchedule delView editView toRename${(!empty activatedViewsAndScheds[view.instanceviewuuid])?' schedActivated':''}">
							<a href="#">${view.viewname}</a>
							
							<%-- for schedules --%>
							<c:if test="${(!empty viewWithSchedsMap) && (!empty viewWithSchedsMap[view.instanceviewuuid])}">
								<ul>
									<c:forEach items="${viewWithSchedsMap[view.instanceviewuuid]}" var="sched" varStatus="schedIdx">
										<li id="${sched.uuid}" rel="schedule" class="editSchedule delSchedule toRename${(!empty activatedViewsAndScheds[sched.uuid])?' schedActivated':''}">
											<a href="#">${sched.schedulename}</a>
										</li>
									</c:forEach>
								</ul>
							</c:if>
							
							
						</li>
					</c:forEach>
				</c:if>
			
				<%-- for subnodes --%>
				<c:if test="${!empty entityRoot.subnodes}">
					<c:forEach items="${entityRoot.subnodes}" var="subnode" varStatus="subnodeIdx">
						<c:set var="entityRoot" value="${subnode}" scope="request"/>
						<c:import url="instanceViewManageFragment.jsp"/>
					</c:forEach>
				</c:if>
			</ul>
		</c:if>
		
	</li>
</c:if>
