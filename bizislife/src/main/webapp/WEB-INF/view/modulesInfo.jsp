<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="modulesInfo" style="padding: 10px 0; ">
	Maximum ModuleDetail, ModuleInstance and Product "${orgName}" can have : <span class="maxiModuleCanHave">${maxiModuleAndInstanceCanHave}</span><br/>
	Total ModuleDetail, ModuleInstance and Product "${orgName}" created :
	<c:choose>
		<c:when test="${totalModuleAndInstanceHave <= maxiModuleAndInstanceCanHave}">
			<span class="totalModuleCreated">
				${totalModuleAndInstanceHave}
			</span>
		</c:when>
		<c:otherwise>
			<span class="totalModuleCreated" style="color: red;">
				${totalModuleAndInstanceHave}
			</span>
		</c:otherwise>
	</c:choose>
	<span><img src="/img/vendor/web-icons/information-white.png" domvalue="{'popupContent':'${detailForTotalModuleAndInstanceHave}'}" title="click for more info" alt="click for more info" class="detailInfoPopupClick"></span>
</div>