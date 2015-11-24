<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="permissionSetSegment" style="float: left; padding: 10px 0 0 0; width: 100%;">

	<h3 style="text-align: center;">Set Permission for ${targetOrg.orgname}'s <span style="text-decoration: underline;">${(!empty targetGroup)?targetGroup.groupname:targetAccount.firstname}</span></h3>

	<div class="permissionTree">
	
		<div id="permissionTree">
			<c:if test="${loginAccount.isSystemDefaultAccount()}">
				<ul>
					<li rel="folder" class="create" domvalue="{'target':'${target}', 'targetId':'${targetId}'}">
						<a href="#">Permissions</a>
						<ul>
							<c:if test="${!empty permissions}">
								<c:forEach items="${permissions}" var="permission" varStatus="permissionIdx">
									<li rel="permission" id="${permission.uuid}" class="rename delete config">
										<a href="#">${permission.name}</a>
									</li>
								</c:forEach>
							</c:if>
						</ul>
					</li>
				</ul>
			</c:if>
		
		</div>
	
	</div>

	<div class="permissionContent">
	
	</div>

</div>