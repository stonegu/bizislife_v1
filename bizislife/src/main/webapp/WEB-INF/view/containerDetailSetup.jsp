<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="containerDetailSetup">

	<div class="containerUpdateStatusInfo" style="color: red;"></div>


	<div id="containName_${containerDetail.containeruuid}" class="containName containElement" style="background-color: #adadad; text-align: center;">
<!-- 	
		<span class="title">Title: </span>
 -->		
		<strong><span class="value valueSection">${containerDetail.prettyname}</span></strong>
		
		<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="containName_${containerDetail.containeruuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		<span class="valueEdit editSection displaynone"><input id="containName_v_${containerDetail.containeruuid}" type="text" name="container name" value="${containerDetail.prettyname}" /></span>
		<span class="editActionIcons editSection displaynone">
			<img class="domReady_saveContainerValue" 
				domvalue="{'typeToSave':'containerValue', 'webSectionId':'containName_${containerDetail.containeruuid}', 'valueName':'prettyname', 'valueId':'containName_v_${containerDetail.containeruuid}', 'containerUuid':'${containerDetail.containeruuid}'}" 
				alt="save" src="/img/vendor/web-icons/tick.png"> 
			<img class="domReady_cancelToEditValue" 
				domvalue="containName_${containerDetail.containeruuid}" 
				alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
	</div>
	
	<div id="containCssClass_${containerDetail.containeruuid}" class="containClassName containElement">
		<span class="title">Css Classnames: </span>
		
		<span class="value valueSection">${containerDetail.classnames}</span>
		
		<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="containCssClass_${containerDetail.containeruuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		<span class="valueEdit editSection displaynone"><input id="containCssClass_v_${containerDetail.containeruuid}" type="text" name="classname" value="${containerDetail.classnames}" /></span>
		<span class="editActionIcons editSection displaynone">
			<img class="domReady_saveContainerValue" 
				domvalue="{'typeToSave':'containerValue', 'webSectionId':'containCssClass_${containerDetail.containeruuid}', 'valueName':'classnames', 'valueId':'containCssClass_v_${containerDetail.containeruuid}', 'containerUuid':'${containerDetail.containeruuid}'}" 
				alt="save" src="/img/vendor/web-icons/tick.png"> 
			<img class="domReady_cancelToEditValue" 
				domvalue="containCssClass_${containerDetail.containeruuid}" 
				alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
	</div>
	
	<div id="containDefaultMod_${containerDetail.containeruuid}" class="containClassName containElement">
		<span class="title">Default module: </span>
		
		<span class="value valueSection">${(!empty defaultModuleDetailName)?defaultModuleDetailName:'No default module selected'}</span>
<%-- 		
		<span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="containDefaultMod_${containerDetail.containeruuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		<span class="valueEdit editSection displaynone">${(!empty defaultModuleDetailName)?defaultModuleDetailName:'No default module selected'}**</span>
		<span class="editActionIcons editSection displaynone">
			<img class="domReady_saveContainerValue" 
				domvalue="{'typeToSave':'containerValue', 'webSectionId':'containDefaultMod_${containerDetail.containeruuid}', 'valueName':'moduleuuid', 'valueId':'containDefaultMod_v_${containerDetail.containeruuid}', 'containerUuid':'${containerDetail.containeruuid}'}" 
				alt="save" src="/img/vendor/web-icons/tick.png"> 
			<img class="domReady_cancelToEditValue" 
				domvalue="containDefaultMod_${containerDetail.containeruuid}" 
				alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
 --%>				
	</div>
	
	
	
	
	
	


</div>