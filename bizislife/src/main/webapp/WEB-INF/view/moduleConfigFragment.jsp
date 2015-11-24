<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="moduleConfigContainer">

	<input type="hidden" name="moduleId" id="moduleId" value="${moduleDetail.moduleuuid}">
	<input type="hidden" name="orgId" value="${org.orguuid}">
	<div id="tabs">
		<ul>
		<li><a href="#tabs-1">Description</a></li>
		<li><a href="#tabs-3">Design</a></li>
		<li><a href="#tabs-4">JSP</a></li>
		<li><a href="#tabs-5">CSS</a></li>
		</ul>
		<div id="tabs-1">
		
			<div>
				<h3 style="text-decoration: underline;">${moduleDetail.prettyname}</h3>
				<c:choose>
					<c:when test="${moduleDetail.type eq 'pm'}">
						<p>This module is used as product template only, which means that this module only can be instanced as products in product section.</p>
					</c:when>
					<c:otherwise>
						<p>This module is the regular module, which means user can create multiple instances under the module in the tree.</p>
					</c:otherwise>
				</c:choose>
			</div>		
			<div id="moduleDesc_${moduleDetail.moduleuuid}">
			
				<p>
					<strong>Description:</strong>
					
					<c:if test="${modifyPermissionAllowed}">
						<span class="editIcon valueSection">
							<img class="domReady_toEditValue" 
							domvalue="moduleDesc_${moduleDetail.moduleuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="domReady_saveModuleValue" 
								domvalue="{'typeToSave':'moduledetailValue', 'webSectionId':'moduleDesc_${moduleDetail.moduleuuid}', 'valueName':'description', 'valueId':'moduleDesc_v_${moduleDetail.moduleuuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="domReady_cancelToEditValue" 
								domvalue="moduleDesc_${moduleDetail.moduleuuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
					
				</p>
				<div class="value valueSection">
					${moduleDetail.description}
				</div>
				<div class="valueEdit editSection displaynone">
					<textarea id="moduleDesc_v_${moduleDetail.moduleuuid}" name="desc" cols="80">${moduleDetail.description}</textarea>
				</div>
				
			</div>
		
		</div>

		<div id="tabs-3">
			<div class="moduleDataStructDesign">
				<h3><span style="text-decoration: underline;">${moduleDetail.prettyname}</span> Data Structure Design: <span style="font-size: 11px;">[used space / total space : <span class="moduleUsedSpace">${usedSpaceWith100Multipled}/100</span>]</span></h3>
				
				<div class="moduleDataStructErrorSection" style="color: red;"></div>
				
				<div class="moduleAttrListContainer">
					<strong>Attribute List</strong>
					<ul class="moduleAttrList">
						<c:if test="${!empty moduleAttrList}">
							<c:forEach items="${moduleAttrList}" var="moduleAttr" varStatus="moduleAttrIndex">
								<c:choose>
									<c:when test="${empty moduleAttr.className || !modifyPermissionAllowed}">
										<li class="noDraggable">
											${moduleAttr.name()} 
											<span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
													domvalue="{'popupContent':'${moduleAttr.description}'}" 
													src="/img/vendor/web-icons/information-white.png"></span>
										</li>
									</c:when>
									<c:otherwise>
										<li class="draggable" domvalue="{'classname':'${moduleAttr.className}', 'jspname':'${moduleAttr.jspName}'}">
										    ${moduleAttr.name()}
                                            <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                                    domvalue="{'popupContent':'${moduleAttr.description}'}" 
                                                    src="/img/vendor/web-icons/information-white.png"></span>
										</li>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:if>
					</ul>
				</div>
				<div class="moduleDataStruct">
					<c:if test="${!empty module && !empty module.attrGroupList}">
						<c:forEach items="${module.attrGroupList}" var="attrGroup" varStatus="attrGroupIndex">
							<c:set var="attrGroup" value="${attrGroup}" scope="request"/>
							<%-- isDisplayOnly: to hide all 'config' functions since 'isDisplayOnly=true' means only module structure display needed --%>					
							<c:set var="isDisplayOnly" value="${isDisplayOnly}" scope="request"/>
							<c:set var="targetWithMetaDataMap" value="${targetWithMetaDataMap}" scope="request"/>
							<c:set var="moduleDetail" value="${moduleDetail}" scope="request"/>
							<c:set var="modifyPermissionAllowed" value="${modifyPermissionAllowed}" scope="request"/>
							<c:import url="module/moduleAttrGroupSetTemplate.jsp"/>
						</c:forEach>
					</c:if>
					
					<c:if test="${!isDisplayOnly && modifyPermissionAllowed}">
						<div class="newGroupSetBtn domReady_newAttrGroupSet">+ New Group</div>
					</c:if>
					
					
				</div>
			</div>
			
			<c:if test="${modifyPermissionAllowed}">
				<div class="moduleAttrSimpleExample displaynone">
					<div class="moduleDataStructTemp">
						<div class="attrTemp attr attrSortable">
							<div class="attrName attrElement">
								<span class="title">Name: </span><span class="value">xxx</span><span class="valueEdit displaynone"><input type="text" name="attrName" value="xxx" /></span>
								<div class="attrType">attr-type</div>
							</div>
<%-- 							
							<div class="attrTitle attrElement"><span class="title">Title: </span><span class="value">xxx</span><span class="valueEdit displaynone"><input type="text" name="title" value="xxx" /></span></div>
 --%>							
							<div class="attrVisibility attrElement"><span class="title">Editable: </span><span class="value">true</span><span class="valueEdit displaynone"><input type="checkbox" name="editable" value="1" checked="checked" /></span></div>
							<div class="attrRequired attrElement"><span class="title">Required: </span><span class="value">true</span><span class="valueEdit displaynone"><input type="checkbox" name="required" value="1" checked="checked" /></span></div>
							<div class="attrArray attrElement"><span class="title">IsArray: </span><span class="value">true</span><span class="valueEdit displaynone"><input type="checkbox" name="attrIsArray" value="1" checked="checked" /></span></div>
						</div>
					</div>
				</div>
			</c:if>
			
		</div>
		<div id="tabs-4">

			<div id="moduleJsp_${moduleDetail.moduleuuid}">
				<h3><span style="text-decoration: underline;">${moduleDetail.prettyname}</span> JSP Fragment:
				
					<c:if test="${modifyPermissionAllowed}">
						<span class="editIcon valueSection">
							<img class="editModuleJspCssValue" 
							domvalue="moduleJsp_${moduleDetail.moduleuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="saveModuleJspCssValue" 
								domvalue="{'webSectionId':'moduleJsp_${moduleDetail.moduleuuid}', 'formId':'moduleJspUpdateForm_${moduleDetail.moduleuuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="cancelEditModuleJspCssValue" 
								domvalue="moduleJsp_${moduleDetail.moduleuuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
					
					<span style="font-size: 11px;">[used space / total space : <span class="moduleJspUsedSpace">${usedJspSpaceWith100Multipled}/100</span>]</span>
					<span class="valueSection">
					   <img class="detailInfoPopupClick" 
					       domvalue="{'width':'700', 'height':'400', 'topOffset':'20', 'leftOffset':'-500', 'stay':'true', 'ajaxCall':{'url':'jspReviewPreset', 'params':'type=moduledetail&objuuid=${moduleDetail.moduleuuid}'}}" 
					       title="preview" src="/img/vendor/web-icons/document-text-image.png">
					</span>
							
				</h3>
				<div class="valueEdit">
					<form name="moduleJspUpdateForm" id="moduleJspUpdateForm_${moduleDetail.moduleuuid}" method="post" action="/test">
						<c:if test="${modifyPermissionAllowed}">
							<input type="hidden" name="moduleId" value="${moduleDetail.moduleuuid}">
							<input type="hidden" name="updateType" value="moduledetailValue">
							<input type="hidden" name="valueName" value="jsp">
						</c:if>
						<textarea id="jspEditor" name="updateValue">${moduleDetail.jsp}</textarea>
					</form>
				
				</div>
			
			</div>
			
			
			
		</div>
		<div id="tabs-5">
<%-- 		
			<strong><p>Module CSS:</p></strong>
			<textarea id="cssEditor" name="css">${moduleDetail.css}</textarea>
 --%>
 
			<div id="moduleCss_${moduleDetail.moduleuuid}">
				<h3><span style="text-decoration: underline;">${moduleDetail.prettyname}</span> CSS Definition:
				
					<c:if test="${modifyPermissionAllowed}">
						<span class="editIcon valueSection">
							<img class="editModuleJspCssValue" 
							domvalue="moduleCss_${moduleDetail.moduleuuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="saveModuleJspCssValue" 
								domvalue="{'webSectionId':'moduleCss_${moduleDetail.moduleuuid}', 'formId':'moduleCssUpdateForm_${moduleDetail.moduleuuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="cancelEditModuleJspCssValue" 
								domvalue="moduleCss_${moduleDetail.moduleuuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
					<span style="font-size: 11px;">[used space / total space : <span class="moduleCssUsedSpace">${usedCssSpaceWith100Multipled}/100</span>]</span>
				</h3>
				<div class="valueEdit">
					<form name="moduleCssUpdateForm" id="moduleCssUpdateForm_${moduleDetail.moduleuuid}" method="post" action="/test">
						<c:if test="${modifyPermissionAllowed}">
							<input type="hidden" name="moduleId" value="${moduleDetail.moduleuuid}">
							<input type="hidden" name="updateType" value="moduledetailValue">
							<input type="hidden" name="valueName" value="css">
						</c:if>
						<textarea id="cssEditor" name="updateValue">${moduleDetail.css}</textarea>
					</form>
				
				</div>
			
			</div>
			
 
 			
		</div>
		
	</div>
	
	<c:if test="${modifyPermissionAllowed}">
	
        <div id="medialistForModuleConfigId" class="medialistForModuleConfig displaynone">
        
          <div class="medialistContainer">
            <div class="mediaTreeArea" style="float: left; width: 200px;">
                <div id="mediaTreeForModuleConfig">
                
                </div>
                
                <form action="/updateModuleValue" id="mediaSelectForm">
                    <input type="hidden" name="updateType" id="mediaSelectForm_updateType" value="attrValue">
                    <input type="hidden" name="moduleId" id="mediaSelectForm_moduleId" value="${moduleDetail.moduleuuid}">
                    <input type="hidden" name="groupUuid" id="mediaSelectForm_groupUuid">
                    <input type="hidden" name="attrUuid" id="mediaSelectForm_attrUuid">
                    <input type="hidden" name="valueName" id="mediaSelectForm_valueName">
                    <input type="hidden" name="updateValue" id="mediaSelectForm_defaultValue">
                </form>
                
            </div>
            <div class="mediaSelectionArea" style="margin-left: 205px;">
            
            
            </div>
            
          </div>
          
        </div>
		
		
		
		
		
		
		
		<div id="moduleColoringInput" style="display: none;">
			<input class="jsColor"><br/>
			<input type="checkbox" name="defaultMetaColor" class="defaultMetaColor"> Using Default Color
		</div>	
	
	</c:if>
	
	
</div>
