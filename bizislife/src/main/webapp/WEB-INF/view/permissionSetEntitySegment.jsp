<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<input type="hidden" name="permissionUuid" id="selectedPermissionUuid" value="${permission.uuid}" />

<div class="datePicker" style="height: 20%; width: 97%; padding: 0 0 10px 0; float: left;">

	<div id="permFromDate_${permission.uuid}" style="width: 49%; float: left;">
		<p>
			<strong>From:</strong>
			<span class="value valueSection">
				<fmt:formatDate pattern="yyyy-MM-dd" value="${permission.startdate}" />
			</span>
			<span class="valueEdit editSection displaynone">
				<input id="permFromDate_v_${permission.uuid}" type="text" name="fromDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${permission.startdate}" />" style="width: 50%;">
			</span>
			<span class="editIcon valueSection">
				<img class="domReady_toEditValue" 
				domvalue="permFromDate_${permission.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
			</span>
			<span class="editActionIcons editSection displaynone">
				<img class="domReady_savePermissionValue" 
					domvalue="{'webSectionId':'permFromDate_${permission.uuid}', 'valueName':'startdate', 'valueId':'permFromDate_v_${permission.uuid}'}" 
					alt="save" src="/img/vendor/web-icons/tick.png"> 
				<img class="domReady_cancelToEditValue" 
					domvalue="permFromDate_${permission.uuid}" 
					alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		</p>
		
	</div>

	<div id="permToDate_${permission.uuid}" style="width: 49%; float: left;">
		<p>
			<strong>To:</strong>
			<span class="value valueSection">
				<fmt:formatDate pattern="yyyy-MM-dd" value="${permission.enddate}" />
			</span>
			<span class="valueEdit editSection displaynone">
				<input id="permToDate_v_${permission.uuid}" type="text" name="fromDate" class="domReady_datePicker" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${permission.enddate}" />" style="width: 50%;">
			</span>
			<span class="editIcon valueSection">
				<img class="domReady_toEditValue" 
				domvalue="permToDate_${permission.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
			</span>
			<span class="editActionIcons editSection displaynone">
				<img class="domReady_savePermissionValue" 
					domvalue="{'webSectionId':'permToDate_${permission.uuid}', 'valueName':'enddate', 'valueId':'permToDate_v_${permission.uuid}'}" 
					alt="save" src="/img/vendor/web-icons/tick.png"> 
				<img class="domReady_cancelToEditValue" 
					domvalue="permToDate_${permission.uuid}" 
					alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		</p>
		
	</div>
	
</div>
<div id="permissionEntitySetTabs" style="height: 80%; width: 97%; float: left;">
	<ul>
		<li><a href="#tabs-1">Module</a>
		</li>
		<li><a href="#tabs-2">Product</a>
		</li>
		<li><a href="#tabs-3">Page</a>
		</li>
        <li><a href="#tabs-4">Media</a>
        </li>
	</ul>
	<div id="tabs-1">
	
	
		<div class="permissionIcons" style="text-align: right; margin-right: 4px;">
			<c:choose>
				<c:when test="${!empty targetGroup && targetGroup.accesslevel eq '1'}">
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="copy" class="permissionIconCopy" />
				</c:when>
				<c:otherwise>
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="detail read" class="permissionIconRead" /><button type="button" title="copy" class="permissionIconCopy" /><button type="button" title="modify" class="permissionIconModify" />
				</c:otherwise>
			</c:choose>
		</div>
	
		<div id="moduleTreeForPermissionSet">
		</div>
	
	</div>
	<div id="tabs-2">
		<div class="permissionIcons" style="text-align: right; margin-right: 4px;">
			<c:choose>
				<c:when test="${!empty targetGroup && targetGroup.accesslevel eq '1'}">
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="copy" class="permissionIconCopy" />
				</c:when>
				<c:otherwise>
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="detail read" class="permissionIconRead" /><button type="button" title="copy" class="permissionIconCopy" /><button type="button" title="modify" class="permissionIconModify" />
				</c:otherwise>
			</c:choose>
		</div>
		<div id="productTreeForPermissionSet">
		</div>
	
	</div>
	<div id="tabs-3">
		<div class="permissionIcons" style="text-align: right; margin-right: 4px;">
			<c:choose>
				<c:when test="${!empty targetGroup && targetGroup.accesslevel eq '1'}">
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="copy" class="permissionIconCopy" />
				</c:when>
				<c:otherwise>
					<button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="detail read" class="permissionIconRead" /><button type="button" title="copy" class="permissionIconCopy" /><button type="button" title="modify" class="permissionIconModify" />
				</c:otherwise>
			</c:choose>
		</div>
		<div id="pageTreeForPermissionSet">
		</div>
	</div>
	
    <div id="tabs-4">
        <div class="permissionIcons" style="text-align: right; margin-right: 4px;">
            <c:choose>
                <c:when test="${!empty targetGroup && targetGroup.accesslevel eq '1'}">
                    <button type="button" title="preview" class="permissionIconPreview" />
                </c:when>
                <c:otherwise>
                    <button type="button" title="preview" class="permissionIconPreview" /><button type="button" title="modify" class="permissionIconModify" />
                </c:otherwise>
            </c:choose>
        </div>
        <div id="mediaTreeForPermissionSet">
        </div>
    </div>
	
</div>
