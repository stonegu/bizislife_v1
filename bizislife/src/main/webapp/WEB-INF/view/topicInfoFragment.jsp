<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="topicInfoFragment">

	<c:choose>
		<c:when test="${editable}">
			<form name="topicPropsModify" method="post" action="/topicPropsModify">
				<input type="hidden" name="topicuuid" value="${topic.topicuuid}">
			
				<span class="topictitle">topic</span>: ${topic.topicroute} <br/>
				<span class="topictitle">accessLevel</span>: 
				Public Topic: <input type="radio" name="accesslevel" value="1" <c:if test="${topic.accesslevel.code eq '1' }">checked="checked"</c:if> />, 
				Private Topic: <input type="radio" name="accesslevel" value="0" <c:if test="${topic.accesslevel.code eq '0' }">checked="checked"</c:if> /> 
				<br/>
				
				<c:if test="${topic.accesslevel.name() eq 'privateTopic'}">
					<span class="topictitle">visible to outside organizations</span>: 
					<c:if test="${!empty allOrgs}">
						<c:forEach items="${allOrgs}" var="org" varStatus="orgIndex">
							[${org.value}: <input type="checkbox" name="visibleOrg" value="${org.key}" <c:if test="${org.selected}">checked="checked"</c:if> />] 
						</c:forEach>
					</c:if>
					
					<br/>
					<span class="topictitle">visible to groups in ${org.orgname}</span>: 
					<c:if test="${!empty allGroup}">
						<c:forEach items="${allGroup}" var="group" varStatus="groupIndex">
							[${group.value}: <input type="checkbox" name="visibleGroup" value="${group.key}" <c:if test="${group.selected}">checked="checked"</c:if> />] 
						</c:forEach>
					</c:if>
					
					<br/>	
				</c:if>
				
				<span class="topictitle">belong to</span>: ${org.orgname} <br/>
				
				<input type="submit" class="topicPropsModify" />
			
			</form>
		
		</c:when>
		<c:otherwise>
			<span class="topictitle">topic</span>: ${topic.topicroute} <br/>
			<span class="topictitle">accessLevel</span>: 
			<c:choose>
				<c:when test="${topic.accesslevel.code eq '1'}">
					Public Topic
				</c:when>
				<c:otherwise>
					Private Topic
				</c:otherwise>
			</c:choose>
			<br/>
			
			<c:if test="${topic.accesslevel.code eq '0'}">
				<span class="topictitle">visible to outside organizations</span>: 
				<c:if test="${!empty allOrgs}">
					<c:forEach items="${allOrgs}" var="org" varStatus="orgIndex">
						<c:if test="${org.selected}">
							[${org.value}]&nbsp;
						</c:if>
					</c:forEach>
				</c:if>
				
				<br/>
				<span class="topictitle">visible to groups in ${org.orgname}</span>: 
				<c:if test="${!empty allGroup}">
					<c:forEach items="${allGroup}" var="group" varStatus="groupIndex">
						<c:if test="${group.selected}">
							[${group.value}]&nbsp;
						</c:if>
						 
					</c:forEach>
				</c:if>
				
				<br/>	
			</c:if>
			
			<span class="topictitle">belong to</span>: ${org.orgname} <br/>
		
		</c:otherwise>
	</c:choose>



</div>




