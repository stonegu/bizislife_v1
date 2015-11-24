<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="instanceViewManage">
	<c:if test="${!empty instance && (instance.getClass().getSimpleName() eq 'ModuleInstance' || instance.getClass().getSimpleName() eq 'EntityDetail')}">
		<div class="viewList" style="float: left; width: 30%; padding-top: 30px;">
			<div id="instanceViewsTree">
				<ul>
					<li id="${(instance.getClass().getSimpleName() eq 'ModuleInstance')?instance.moduleinstanceuuid:instance.entityuuid}" rel="instance" class="newView">
						<a href="#">${instance.name}</a>
						<c:if test="${!empty views}">
							<ul>
								<c:forEach items="${views}" var="view" varStatus="viewIndex">
									<li id="${view.instanceviewuuid}" rel="instanceView" class="newSchedule delView editView toRename${(!empty activatedViewsAndScheds[view.instanceviewuuid])?' schedActivated':''}">
										<a href="#">${view.viewname}</a>
										<c:if test="${(!empty viewSchedsMap) && (!empty viewSchedsMap[view.instanceviewuuid])}">
											<ul>
												<c:forEach items="${viewSchedsMap[view.instanceviewuuid]}" var="sched" varStatus="schedIndex">
													<li id="${sched.uuid}" rel="schedule" class="editSchedule delSchedule toRename${(!empty activatedViewsAndScheds[sched.uuid])?' schedActivated':''}">
														<a href="#">${sched.schedulename}</a>
													</li>
												</c:forEach>
											</ul>
										</c:if>
									</li>
								</c:forEach>
							</ul>
						</c:if>
					</li>
				</ul>
			</div>
		
		</div>
		
		<div class="instanceViewManagerContainer" style="float: left; width: 65%;">
		
			<div>
				<strong>Detail information for instance ${instance.name}</strong>
				<div>...</div>
			</div>
		
		</div>
	</c:if>

</div>
