<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>

	<fieldset>
		<legend>
			${(!empty groupname)?groupname:"Group"} in ${targetOrg.orgname}
		</legend>
		
		<p><span class="${(!empty errorMsgs['groupname'])?'errorHighlight':''}">Group Name *: </span>
			<input type="text" id="groupname" name="groupname" value="${groupname}" />
			<span class="errorHighlight">${errorMsgs['groupname']}</span>
		</p>
		<p><span class="${(!empty errorMsgs['description'])?'errorHighlight':''}">Description: </span>
			<input type="text" name="description" value="${description}" />
			<span class="errorHighlight">${errorMsgs['description']}</span>
		</p>
		<p><span class="${(!empty errorMsgs['accesslevel'])?'errorHighlight':''}">Usage *: </span>
		
			<c:choose>
				<c:when test="${!empty groupid}">
					<c:forEach items="${groupAccessLevels}" var="item" varStatus="itemIndex">
						<c:if test="${item.key eq accesslevel}">
							${item.value}
						</c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<select name="accesslevel">
						<c:forEach items="${groupAccessLevels}" var="item" varStatus="itemIndex">
							<c:choose>
								<c:when test="${item.key eq accesslevel}">
									<option value="${item.key}" selected="selected">${item.value}</option>
								</c:when>
								<c:otherwise>
									<option value="${item.key}">${item.value}</option>
								</c:otherwise>
							</c:choose>
							
						</c:forEach>
					</select>
					<span class="errorHighlight">${errorMsgs['accesslevel']}</span>
				</c:otherwise>
			</c:choose>		
		
		</p>
		
		<input type="hidden" name="org" value="${targetOrg.orguuid}">
		
	</fieldset>

</div>

