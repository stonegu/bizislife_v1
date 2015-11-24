<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="field4page_${uuid}" class="fieldElement">


	<c:choose>
		<c:when test="${fieldType eq 'csstextarea'}">
			<div id="csstextarea_${uuid}">
				<p>
					<strong>${fieldTitle}:</strong>
					
					<c:if test="${pageModifyPermission}">
						<span class="editIcon valueSection">
							<img class="editTextAreaValue" 
							domvalue="csstextarea_${uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="${jsDomAction}" 
								domvalue="{'pageuuid':'${uuid}', 'webSectionId':'csstextarea_${uuid}', 'formId':'csstextarea_f_${uuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="cancelEditTextAreaValue" 
								domvalue="csstextarea_${uuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
							
				</p>
				<div class="valueEdit">
					<form name="textareaUpdateForm" id="csstextarea_f_${uuid}" method="post" action="/test">
						<input type="hidden" name="pageId" value="${uuid}">
						<input type="hidden" name="valueName" value="css">
						<textarea id="codeMirrorCss" class="codeMirrorTextArea codeMirrorCss" name="updateValue">${fieldValue}</textarea>
					</form>
				
				</div>
			
			</div>
		
		</c:when>
		
		<c:when test="${fieldType eq 'defaultcss'}">
			<div id="defaultcss_${uuid}">
				<span class="title">${fieldTitle}: </span>
				<span class="value valueSection">${fieldValue}</span>
				
				<c:if test="${pageModifyPermission}">
				<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="defaultcss_${uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<div class="valueEdit editSection displaynone">
						<form name="systemDefaultCssForPage" id="defaultcss_f_${uuid}" method="post" action="/test">
							<input type="hidden" name="pageId" value="${uuid}">
							<input type="hidden" name="valueName" value="${fieldName}">
			 				<c:forEach items="${defaultCssSelections}" var="defaultCssSelection" varStatus="defaultCssSelectionIdx">
			 					<c:choose>
			 						<c:when test="${defaultCssSelection.name() eq fieldValue}">
						 				[<input type="radio" name="updateValue" value="${defaultCssSelection.code}" checked="checked" title="${defaultCssSelection.desc}"> ${defaultCssSelection.name()}]
						 				<span style="color: gray;">: ${defaultCssSelection.desc}</span>
						 				<br/>			
			 						</c:when>
			 						<c:otherwise>
						 				[<input type="radio" name="updateValue" value="${defaultCssSelection.code}" title="${defaultCssSelection.desc}"> ${defaultCssSelection.name()}]
						 				<span style="color: gray;">: ${defaultCssSelection.desc}</span>
						 				<br/>			
			 						</c:otherwise>
			 					</c:choose>
			 				</c:forEach>
						</form>
					</div>
					
					<span class="editActionIcons editSection displaynone">
						<img class="${jsDomAction}" 
							domvalue="{'pageuuid':'${uuid}', 'webSectionId':'defaultcss_${uuid}', 'valueName':'${fieldName}', 'formId':'defaultcss_f_${uuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="defaultcss_${uuid}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				
				</c:if>
				
			</div>
		</c:when>
		
		<c:when test="${fieldType eq 'pagehead'}">
		
			<div id="pageheadarea_${uuid}">
				<p>
					<strong>${fieldTitle}:</strong>
					
					<c:if test="${pageModifyPermission}">
						<span class="editIcon valueSection">
							<img class="editTextAreaValue" 
							domvalue="pageheadarea_${uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
						</span>
						<span class="editActionIcons editSection displaynone">
							<img class="${jsDomAction}" 
								domvalue="{'pageuuid':'${uuid}', 'webSectionId':'pageheadarea_${uuid}', 'formId':'pageheadarea_f_${uuid}'}" 
								alt="save" src="/img/vendor/web-icons/tick.png"> 
							<img class="cancelEditTextAreaValue" 
								domvalue="pageheadarea_${uuid}" 
								alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
					</c:if>
					
				</p>
				<div class="valueEdit">
					<form name="headareaUpdateForm" id="pageheadarea_f_${uuid}" method="post" action="/test">
						<input type="hidden" name="pageId" value="${uuid}">
						<input type="hidden" name="valueName" value="headcontent">
						<textarea id="codeMirrorHead" class="codeMirrorTextArea codeMirrorHead" name="updateValue">${fieldValue}</textarea>
					</form>
				
				</div>
			
			</div>
		
		</c:when>
		
		<c:otherwise>
			<div id="${fieldName}_${uuid}">
				<span class="title">${fieldTitle}: </span>
				<span class="value valueSection">${fieldValue}</span>
				
				<c:if test="${pageModifyPermission}">
					<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="${fieldName}_${uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
					<span class="valueEdit editSection displaynone"><input id="${fieldName}_v_${uuid}" type="text" name="${fieldName}" value="<c:out value='${fieldValue}'/>" /></span>
					<span class="editActionIcons editSection displaynone">
						<img class="${jsDomAction}" 
							domvalue="{'pageuuid':'${uuid}', 'webSectionId':'${fieldName}_${uuid}', 'valueName':'${fieldName}', 'valueId':'${fieldName}_v_${uuid}'}" 
							alt="save" src="/img/vendor/web-icons/tick.png"> 
						<img class="domReady_cancelToEditValue" 
							domvalue="${fieldName}_${uuid}" 
							alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>


</div>

<c:if test="${!empty pagelinks}">
	<div>
		<c:forEach items="${pagelinks}" var="pageLink" varStatus="pageLinkIdx">
			Click <a class="clickHereToOpenPage" target="_blank" href="${pageLink.value}">here</a> to open the page from "${pageLink.key}". <br/>
		</c:forEach>
	</div>
</c:if>
