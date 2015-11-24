<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="instanceViewManageForEntity">

	<div class="viewList" style="float: left; width: 30%; padding-top: 30px;">
	
		<input type="button" value="Collapse All" onclick="$('#instanceViewsTreeForEntity').jstree('close_all');">
		<input type="button" value="Expand All" onclick="$('#instanceViewsTreeForEntity').jstree('open_all');">
	
		<div id="instanceViewsTreeForEntity" style="position: relative;">
			<c:if test="${!empty entityRoot}">
				<ul>
					<c:import url="instanceViewManageFragment.jsp"/>
				</ul>
			</c:if>
		</div>
	</div>

	<div class="instanceViewManagerContainer" style="float: right; width: 65%; position: relative; background-color: white; border: 1px solid;">
	
		<div>
			<strong>Detail information for ${instance.name}</strong>
			<div>...</div>
		</div>
	
	</div>
</div>
