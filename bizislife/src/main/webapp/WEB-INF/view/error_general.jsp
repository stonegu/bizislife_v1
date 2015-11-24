<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="error_general">
	<h2>Error Page</h2>
	<p>The reasons you get this page are: </p>
	
	<c:if test="${!empty errorList}">
		<ul>
			<c:forEach items="${errorList}" var="error" varStatus="errorIndex">
				<li>${error}</li>
			</c:forEach>
		</ul>
	</c:if>
	
	
</div>

