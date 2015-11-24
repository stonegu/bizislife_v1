<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tr>
	<td style="text-align: center;">
		<c:choose>
			<c:when test="${empty orgCanJoin}">
				<img src="/img/vendor/web-icons/cross.png" alt="click to allow &quot;${org.orgsysname}&quot; to join &quot;${group.groupname}&quot;" class="toggleJoinAllowDeny joinDeny" domvalue="{'type':'allow', 'group':'${group.id}', 'targetOrg':'${org.orguuid}'}" title="click to allow &quot;${org.orgsysname}&quot; to join &quot;${group.groupname}&quot;">
			</c:when>
			<c:otherwise>
				<img src="/img/vendor/web-icons/tick.png" alt="click to deny &quot;${org.orgsysname}&quot; to join &quot;${group.groupname}&quot;" class="toggleJoinAllowDeny joinAllow" domvalue="{'type':'deny', 'group':'${group.id}', 'targetOrg':'${org.orguuid}'}" title="click to deny &quot;${org.orgsysname}&quot; to join &quot;${group.groupname}&quot;">
			</c:otherwise>
		</c:choose>
	</td>
	<td>${org.orgsysname}</td>
	<td>
		<c:choose>
			<c:when test="${empty orgCanJoin}">
				&nbsp;
			</c:when>
			<c:otherwise>
				<div id="numAcctJoin_${org.id}" style="text-align: center;">
					<span class="value valueSection">${(empty orgCanJoin.totalaccountcanjoin)?0:orgCanJoin.totalaccountcanjoin}</span>
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="numAcctJoin_${org.id}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="valueEdit editSection displaynone"><input id="numAcctJoin_v_${org.id}" type="text" name="totalaccountcanjoin" value="${orgCanJoin.totalaccountcanjoin}" size="5" /></span>
					<span class="editActionIcons editSection displaynone">
						<img class="updateOrgCanJoinValue" 
							domvalue="{'webSectionId':'numAcctJoin_${org.id}', 'valueName':'totalaccountcanjoin', 'valueId':'numAcctJoin_v_${org.id}', 'group':'${group.id}', 'targetOrg':'${org.orguuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="numAcctJoin_${org.id}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</div>
			</c:otherwise>
		</c:choose>
	</td>
	<td style="text-align: center;">
		<c:choose>
			<c:when test="${empty orgCanJoin}">
				&nbsp;
			</c:when>
			<c:otherwise>
				<span class="joinKey">${orgCanJoin.joinkey}</span>
				<img src="/img/vendor/web-icons/refresh_16x16.png" alt="click to stop the old key and generate a new key" class="groupJoinKeyGenerate" domvalue="{'group':'${group.id}', 'targetOrg':'${org.orguuid}'}" title="click to stop the old key and generate a new key" style="float: right;">
			</c:otherwise>
		</c:choose>
	</td>
	<td style="text-align: center;">
		<c:choose>
			<c:when test="${empty orgCanJoin}">
				&nbsp;
			</c:when>
			<c:otherwise>
				<img src="/img/vendor/web-icons/arrow-join.png" 
					alt="click to get group join link" 
					class="groupJoinLink detailInfoPopupClick" 
					domvalue="{'ajaxCall':{'url':'joinGroupLinkGenerator', 'params':'group=${group.id}&targetOrg=${org.orguuid}'}, 'topOffset':'20', 'leftOffset':'-500'}"					 
					title="click to get group join link">
			</c:otherwise>
		</c:choose>
	</td>
</tr>
