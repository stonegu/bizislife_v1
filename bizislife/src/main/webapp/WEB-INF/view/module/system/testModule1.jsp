<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%-- 
<p>${testModule1} :</p>

<h1>loop test</h1>

<ul>
	<c:if test="${!empty testModule1}">
		<c:forEach items="${testModule1.paramGroups}" var="pGroup" varStatus="pGroupIndex">
			<c:if test="${!empty pGroup}">
				<li>array: ${pGroup.array}</li>
				<c:forEach items="${pGroup.params}" var="p" varStatus="pIndex">
					<ul>
						<li>name: ${p.name}</li>
						<li>type: ${p.type}</li>
						<li>title: ${p.title}</li>
						<li>visibility: ${p.visibility}</li>
					</ul>		
				</c:forEach>
			</c:if>		
		</c:forEach>
	</c:if>
</ul>

<h1>no loop test</h1>
<c:if test="${!empty testModule1}">
	<strong><p>first param: </p></strong>
	<p>name: ${testModule1.paramGroups[0].params[0].name} | type: ${testModule1.paramGroups[0].params[0].type}</p>
	

</c:if>
 --%>
<h1>nice to have</h1>
<h2>not array</h2>
<%-- 
<p>${testModule1.paramGroups[0].paramsMap["string1"].name}: ${testModule1.paramGroups[0].paramsMap["string1"].type}
<p>${testModule1.nameGroupsMap["group1"][0].paramsMap["string1"][0].name}: ${testModule1.nameGroupsMap["group1"][0].paramsMap["string1"][0].type}
 --%>

${module.groups["group1"][0].paramsMap["param1"][0].name}
<%-- 
${module.group1[0].param1[0].name}
 --%>
