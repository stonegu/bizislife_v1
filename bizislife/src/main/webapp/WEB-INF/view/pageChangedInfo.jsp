<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="pageChangedInfo">
	<div class="pageChangedinfo_msg_content" style="color: red;"></div>

	<c:if test="${!empty changeList}">
		<ul>
			<c:forEach items="${changeList}" var="change" varStatus="changeIndex">
				<li>${change.key} : ${change.value}</li>			
			</c:forEach>	
		</ul>
		
		<form action="/pagepublish" method="post" name="pagePublish">
			<input type="hidden" name="pageUuid" value="${pageUuid}">
			<button class="domReady_pagePublish" type="button">Page Publish</button>
		</form>
	</c:if>

</div>
