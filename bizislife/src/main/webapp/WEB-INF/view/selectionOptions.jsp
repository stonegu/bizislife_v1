<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${options}" var="option" varStatus="optionIndex">
	<option value="${option.key}" <c:if test="${option.selected}"> selected="selected"</c:if>>${option.value}</option>
</c:forEach>

