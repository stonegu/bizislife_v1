<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>

	<table style="width: 90%;">
		<tbody>
			<tr id="account_firstName" style="background-color: #909090;">
				<td class="title" style="width: 100px;">First Name:</td>
				<td>
					<div class="valueSection">
						<span class="value">${accountProfile.firstname}</span>
					</div>
					<div class="valueEdit editSection displaynone">
						<form action="#" id="account_firstName_form" style="float: left;">
							<input type="text" value="${accountProfile.firstname}" name="updatedValue">
							<input type="hidden" value="firstName" name="updatedName">
							<input type="hidden" value="${account.accountuuid}" name="accountid">
						</form>
					
					</div>
				</td>
				<td style="width: 40px; text-align: right;">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_firstName" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_updateAccountProfile" 
							domvalue="account_firstName" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="account_firstName" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</td>		
			</tr>
			<tr id="account_lastName">
				<td class="title" style="width: 100px;">Last Name:</td>
				<td>
					<div class="valueSection">
						<span class="value">${accountProfile.lastname}</span>
					</div>
					<div class="valueEdit editSection displaynone">
						<form action="#" id="account_lastName_form" style="float: left;">
							<input type="text" value="${accountProfile.lastname}" name="updatedValue">
							<input type="hidden" value="lastName" name="updatedName">
							<input type="hidden" value="${account.accountuuid}" name="accountid">
						</form>
					
					</div>
					
				</td>
				<td style="width: 40px; text-align: right;">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_lastName" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_updateAccountProfile" 
							domvalue="account_lastName" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="account_lastName" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</td>		
			</tr>
			<tr id="account_password" style="background-color: #909090;">
				<td class="title" style="width: 100px;">Password:</td>
				<td>
					<div class="valueSection">
						<span class="value">******</span>
					</div>
					<div class="valueEdit editSection displaynone">
						<form action="#" id="account_password_form">
						
							<table style="width: 100%;">
								<tr>
									<td>Old Password: </td>
									<td><input type="password" value="" name="oldPwd"></td>
								</tr>
								<tr>
									<td>New Password: </td>
									<td><input type="password" value="" name="newPwd"></td>
								</tr>
								<tr>
									<td>Confirm New Password: </td>
									<td><input type="password" value="" name="confirmNewPwd"></td>
								</tr>
							</table>
						
							<input type="hidden" value="${account.accountuuid}" name="accountid">
						</form>
					
					</div>
					
				</td>
				<td style="width: 40px; text-align: right;">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_password" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_updateAccountPwd" 
							domvalue="account_password" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="account_password" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</td>
			</tr>
			<tr id="account_email">
				<td class="title" style="width: 100px;">Email:</td>
				<td>
					
					<div class="valueSection">
						<span class="value">${contactInfo.email}</span>
					</div>
					<div class="valueEdit editSection displaynone">
						<form action="#" id="account_email_form" style="float: left;">
							<input type="text" value="${contactInfo.email}" name="updatedValue">
							<input type="hidden" value="email" name="updatedName">
							<input type="hidden" value="${account.accountuuid}" name="accountid">
							<input type="hidden" value="${contactInfo.id}" name="contactid">
						</form>
					
					</div>
				</td>
				<td style="width: 40px; text-align: right;">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_email" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_updateAccountContact" 
							domvalue="account_email" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="account_email" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</td>
			</tr>
			<tr id="account_timezone" style="background-color: #909090;">
				<td class="title" style="width: 100px;">Time zone:</td>
				<td>
					<div class="valueSection">
						<span class="value">
							<c:forEach items="${timezones}" var="timezone" varStatus="timezoneIdx">
								<c:if test="${timezone.key eq accountProfile.timezone}">
									${timezone.value}
								</c:if>
							</c:forEach>
						</span>
					</div>
					<div class="valueEdit editSection displaynone">
						<form action="#" id="account_timezone_form" style="float: left;">
							<select name="updatedValue" style="width: 250px;">
								<c:forEach items="${timezones}" var="timezone" varStatus="timezoneIdx">
									<option value="${timezone.key}">${timezone.value}</option>
								</c:forEach>
							</select>
							<input type="hidden" value="timezone" name="updatedName">
							<input type="hidden" value="${account.accountuuid}" name="accountid">
						</form>
					
					</div>
					
				</td>
				<td style="width: 40px; text-align: right;">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_timezone" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_updateAccountProfile" 
							domvalue="account_timezone" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="account_timezone" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</td>
			</tr>
			<tr id="account_groups">
				<td class="title" style="width: 100px;">Groups</td>
				<td>
					<div class="valueSection">
					
						<div class="value">
							<c:import url="accountInfoFragment_group_value.jsp" />
						</div>
					</div>
					<div class="valueEdit editSection displaynone">
						<strong>Join/un-join self organization's groups</strong>
						<c:if test="${!empty accountGroups}">
							<table>
								<c:forEach items="${accountGroups}" var="group" varStatus="groupIndex">
									<tr>
										<td>
											<c:choose>
												<c:when test="${(!empty selectedGroupsMap) && (!empty selectedGroupsMap[group.id])}">
													
													<c:choose>
														<c:when test="${group.grouptype eq 'Everyone'}">
															<img src="/img/vendor/web-icons/tick_gray.png" alt="joined everyone group" title="joined everyone group" 
																class="">
														</c:when>
														<c:otherwise>
															<img src="/img/vendor/web-icons/tick.png" alt="click to un-join the group" title="click to un-join the group" 
																domvalue="{'account':'${account.accountuuid}', 'group':'${group.id}', 'type':'unjoinGroup', 'websectionid':'account_groups'}" 
																class="toggleGroupSelect">
														</c:otherwise>
													</c:choose>
														
												</c:when>
												<c:otherwise>
													<img src="/img/vendor/web-icons/question-white.png" alt="click to join the group" title="click to join the group"
														domvalue="{'account':'${account.accountuuid}', 'group':'${group.id}', 'type':'joinGroup', 'websectionid':'account_groups'}" 
														class="toggleGroupSelect">
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${group.groupname}
										</td>
									</tr>
								</c:forEach>
							</table>
						
						</c:if>
						<c:if test="${!empty selectedGroups_other}">
							<strong>Un-join other organizations' groups</strong>
							<table>
								<c:forEach items="${selectedGroups_other}" var="group" varStatus="groupIndex">
									<tr>
										<td>
											<c:choose>
												<c:when test="${group.grouptype eq 'Everyone'}">
													<img src="/img/vendor/web-icons/tick_gray.png" alt="joined everyone group" title="joined everyone group" 
														class="">
												</c:when>
												<c:otherwise>
													<img src="/img/vendor/web-icons/tick.png" alt="click to un-join the group" title="click to un-join the group" 
														domvalue="{'account':'${account.accountuuid}', 'group':'${group.id}', 'type':'unjoinGroup_outside', 'websectionid':'account_groups'}" 
														class="toggleGroupSelect">
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${group.groupname}
										</td>
										<td>
											belongs: ${orgIdNameMap[group.organization_id]}
										</td>
										
									</tr>
								</c:forEach>
							</table>
						</c:if>
						
					</div>
					
				</td>
				<td style="width: 40px; text-align: right;">
					<c:if test="${loginAccount.isSystemDefaultAccount()}">
						<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="account_groups" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_cancelToEditValue" 
								domvalue="account_groups" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
				</td>
			</tr>
		</tbody>
	
	</table>

</div>