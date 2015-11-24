<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="viewConfigInfoId" style="color: red;">

</div>

<input type="hidden" name="instanceId" value="${instanceUuid}">
<input type="hidden" name="instanceViewId" id="instanceViewId" value="${instanceviewuuid}">
<input type="hidden" name="instanceViewType" value="${instanceViewType}">
<div id="tabs">
	<ul>
	<li><a href="#tabs-1">Description</a></li>
	<li><a href="#tabs-2">JSP</a></li>
	<li><a href="#tabs-3">CSS</a></li>
	</ul>
	<div id="tabs-1">
		
		<p>
			<strong>Name: ${view.viewname}</strong> 





			<div id="viewDefaultView_${view.instanceviewuuid}">
				<p class="title">This is the default view for the instance: 
					<span class="value valueSection">
						${view.isdefault=="1"?"yes":"no"}
					</span>
					
					<c:if test="${modifyPermissionAllow}">
						<span class="editIcon valueSection">
							<img class="domReady_toEditValue" 
							domvalue="viewDefaultView_${view.instanceviewuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="valueEdit editSection displaynone">
							<input class="moduleCheckboxChangeVal" id="viewDefaultView_v_${view.instanceviewuuid}" type="checkbox" name="isdefault" value="1" <c:if test="${view.isdefault eq '1'}">checked="checked"</c:if> />
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_saveViewValue" 
								domvalue="{'typeToSave':'instanceViewValue', 'webSectionId':'viewDefaultView_${view.instanceviewuuid}', 'valueName':'isdefault', 'valueId':'viewDefaultView_v_${view.instanceviewuuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="domReady_cancelToEditValue" 
								domvalue="viewDefaultView_${view.instanceviewuuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					
					</c:if>
					
				
				</p>
			
			</div>

		</p>
	
	
	
		<div id="viewDesc_${view.instanceviewuuid}">
		
			<p>
				<strong>Description:</strong>
				<c:if test="${modifyPermissionAllow}">
					<span class="editIcon valueSection">
						<img class="domReady_toEditValue" 
						domvalue="viewDesc_${view.instanceviewuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
					</span>
					<span class="editActionIcons editSection displaynone">
						<img class="domReady_saveViewValue" 
							domvalue="{'typeToSave':'instanceViewValue', 'webSectionId':'viewDesc_${view.instanceviewuuid}', 'valueName':'description', 'valueId':'viewDesc_v_${view.instanceviewuuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="viewDesc_${view.instanceviewuuid}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</c:if>
				
			</p>
			<div class="value valueSection">
				${view.description}
			</div>
			<c:if test="${modifyPermissionAllow}">
				<div class="valueEdit editSection displaynone">
					<textarea id="viewDesc_v_${view.instanceviewuuid}" name="desc">${view.description}</textarea>
				</div>
			</c:if>
			
		</div>
	
	</div>
	<div id="tabs-2">
	
		<div id="viewJsp_${view.instanceviewuuid}">
			<p>
				<strong>View JSP:</strong>
				
				<c:if test="${modifyPermissionAllow}">
					<span class="editIcon valueSection">
						<img class="editViewJspCssValue" 
						domvalue="viewJsp_${view.instanceviewuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
					</span>
					<span class="editActionIcons editSection displaynone">
						<img class="saveViewJspCssValue" 
							domvalue="{'webSectionId':'viewJsp_${view.instanceviewuuid}', 'formId':'viewJspUpdateForm_${view.instanceviewuuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="cancelEditViewJspCssValue" 
							domvalue="viewJsp_${view.instanceviewuuid}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</c:if>
				<span style="font-size: 11px;">[used space / total space : <span class="viewJspUsedSpace">${viewUsedJspSpaceWith100Multipled}/100</span>]</span>
                <span class="valueSection">
                   <img class="detailInfoPopupClick" 
                       domvalue="{'width':'700', 'height':'400', 'topOffset':'20', 'leftOffset':'-300', 'stay':'true', 'ajaxCall':{'url':'jspReviewPreset', 'params':'type=instance&objuuid=${instanceUuid}&viewuuid=${view.instanceviewuuid}'}}" 
                       title="preview" src="/img/vendor/web-icons/document-text-image.png">
                </span>
				
			</p>
			<div class="valueEdit">
				<form name="viewJspUpdateForm" id="viewJspUpdateForm_${view.instanceviewuuid}" method="post" action="/test">
					<input type="hidden" name="viewId" value="${view.instanceviewuuid}">
					<input type="hidden" name="updateType" value="instanceViewValue">
					<input type="hidden" name="valueName" value="jsp">

					<textarea id="jspEditor" name="updateValue">${view.jsp}</textarea>
				</form>
			
			</div>
		
		</div>
	</div>
	<div id="tabs-3">

		<div id="viewCss_${view.instanceviewuuid}">
			<p>
				<strong>View CSS:</strong>
				
				<c:if test="${modifyPermissionAllow}">
					<span class="editIcon valueSection">
						<img class="editViewJspCssValue" 
						domvalue="viewCss_${view.instanceviewuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
					</span>
					<span class="editActionIcons editSection displaynone">
						<img class="saveViewJspCssValue" 
							domvalue="{'webSectionId':'viewCss_${view.instanceviewuuid}', 'formId':'viewCssUpdateForm_${view.instanceviewuuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="cancelEditViewJspCssValue" 
							domvalue="viewCss_${view.instanceviewuuid}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</c:if>
				
				<span style="font-size: 11px;">[used space / total space : <span class="viewCssUsedSpace">${viewUsedCssSpaceWith100Multipled}/100</span>]</span>
				
			</p>
			<div class="valueEdit">
				<form name="viewCssUpdateForm" id="viewCssUpdateForm_${view.instanceviewuuid}" method="post" action="/test">
					<input type="hidden" name="viewId" value="${view.instanceviewuuid}">
					<input type="hidden" name="updateType" value="instanceViewValue">
					<input type="hidden" name="valueName" value="css">

					<textarea id="cssEditor" name="updateValue">${view.css}</textarea>
				</form>
			
			</div>
		
		</div>
	</div>
	
</div>
