<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="whoCanJoinGroup">

	<table style="width: 96%;">
		<thead style="background-color: gray; color: white;">
			<tr>
				<th>&nbsp;</th>
				<th>Org Name</th>
				<th># Account Can Join</th>
				<th>Key</th>
				<th>Link</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${orgsCanJoin}" var="org" varStatus="orgIdx">
				<c:set var="org" value="${org}" scope="request"/>
				<c:set var="orgCanJoin" value="${joinedOrgIdWithNum[org.id]}" scope="request"/>				
				<c:import url="/WEB-INF/view/whoCanJoinGroupSegment_tr.jsp"/>
			
			</c:forEach>
		</tbody>
	</table>

</div>