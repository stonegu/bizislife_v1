<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="productTreeNodeDetail">
	<div class="ptnd_topControlSection">
	    <span style="float: left;padding: 6px;"><strong>${entityDetail.name}</strong></span>
	    <span style="float: left;padding: 6px;">
	        <img src="/img/vendor/web-icons/share.png" 
	            domvalue="{'topOffset':'20', 'leftOffset':'-150', 'width':'400', 'popupContentContainerId':'productApiHowto'}" 
	            title="click for API howto" alt="click for API howto" class="detailInfoPopupClick">
	    </span>
	    
	    <c:if test="${!empty moduleDetailForProduct.value}">
		    <span style="float: left;padding: 6px;">
		        <img src="/img/vendor/web-icons/eye.png" 
		            domvalue="${entityDetail.entityuuid}" 
		            title="click for preview" alt="click for preview" class="domReady_instancePreview">
		    </span>
	    </c:if>

	    <span style="float: left;padding: 6px;">
	        <img src="/img/vendor/web-icons/speaker-volume.png" 
	            domvalue="${entityDetail.entityuuid}" 
	            title="click for annoncement" alt="click for annoncement" class="domReady_instanceAnnoncement">
	    </span>

	    
		<span style="float: right; padding: 6px; cursor: pointer; "><img title="close window" class="domReady_xProductDetail" src="/img/vendor/web-icons/cross-script.png"></span>
	</div>
	<div id="productApiHowto" class="productApiHowto" style="display: none;">
	   <c:choose>
	       <c:when test="${entityDetail.type eq 'fd'}">
	            <p>Category List Api: <a href="${bizhost}/categoryApi/list/org/${org.orguuid}/category/${entityDetail.entityuuid}" target="_blank">${bizhost}/categoryApi/list/org/${org.orguuid}/category/${entityDetail.entityuuid}</a></p>
	            <p>Category Api: <a href="${bizhost}/productApi/org/${org.orguuid}/product/${entityDetail.entityuuid}" target="_blank">${bizhost}/productApi/org/${org.orguuid}/product/${entityDetail.entityuuid}</a></p>
	       </c:when>
	       <c:otherwise>
                <p>Product Api: <a href="${bizhost}/productApi/org/${org.orguuid}/product/${entityDetail.entityuuid}" target="_blank">${bizhost}/productApi/org/${org.orguuid}/product/${entityDetail.entityuuid}</a></p>
	       </c:otherwise>
	   </c:choose>
	   
	   
	</div>
	<div class="ptnd_contentSection">
		
		<div class="ptnd_productInfoSection" style="float: left; width: 100%;">
			<div style="width: 100%; float: left;"><strong>Template in using:</strong> ${moduleDetailForProduct.value} 
				<c:choose>
					<c:when test="${!empty entityDetail.moduleuuid}">
						<c:if test="${modifyPermissionAllow}">
							<span id="delMoSelected_${entityDetail.moduleuuid}" style="cursor: pointer;"><img domvalue="${entityDetail.entityuuid}" src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" class="deleteModuleSelectionForEntity" title="delete"></span>
						</c:if>
						
					</c:when>
					<c:otherwise>
						No template selected.
					</c:otherwise>
				</c:choose>
				<span>
				    <img src="/img/vendor/web-icons/information-white.png" 
				        domvalue="{'topOffset':'20', 'leftOffset':'-150', 'width':'400', 'popupContentContainerId':'templateHowtoInfo'}" 
				        title="click for more information" alt="click for more information" class="detailInfoPopupClick">
				</span>
				<div id="templateHowtoInfo" style="display: none;">
				    <div>
				        <p>Each product needs a product template (A.K.A. product module), where you can input all product informations, 
				        like description, images, etc. You can select product template for category, and all products under this category
				        will inherit the template automatically.</p>
				        <p>Also, the view (the way to display product information) is also defined in product template (jsp, css). You can overwrite
				        the view by create a view and set as default view or create a list of views and schedule the views.</p>
				        <p>Note: If you defined view in template and also create views in product, the display order of view is: </p>
				        <p><span style="color: #4183C4">#1. follow view schedule in product, #2. find default view in product,
				        #3. use view in template. 
				        </span></p>
				        <p><strong>How to:</strong><br/>
				        You can select template from right 'Modules' tree if you already defined product template(s) in Module section.</p>
				    </div>
				</div>
			</div>
			
	        <c:if test="${!empty folderHasModuleSetup}">
	            <c:choose>
	                <c:when test="${(!empty entityDetail.moduleuuid) && (entityDetail.moduleuuid!=moduleDetailForFolder.key)}">
	                    <div class="notification" style="color: #FF9494; float: left; width: 100%;">
	                         <strong>Template defined in category "${folderHasModuleSetup.value}":</strong> ${moduleDetailForFolder.value}
	                    </div>
	                </c:when>
	                <c:otherwise>
	                    <div class="notification" style="color: gray; float: left; width: 100%;">
                             <strong>Template defined in category "${folderHasModuleSetup.value}":</strong> ${moduleDetailForFolder.value}
	                    </div>
	                </c:otherwise>
	            </c:choose>
	        </c:if>     
			
	        <div class="pageSchedule" style="float: left; width: 100%;">
	            <c:if test="${entityDetail.type eq 'fd'}">
	                <div class="categoryPageSchedule">
	                    <span class="scheduleTitle"><strong>Page binding for category:</strong></span>
	                    <span class="schedulePage">
	                        <span class="desktop">
                               <c:if test="${!empty defaultCategoryPage_desk}">
                                   ${defaultCategoryPage_desk.value}
                                   <c:if test="${empty entityDetail.catpageuuid_desktop}"><span style="color: gray;">defined from upper level</span></c:if>
                               </c:if>
	                        </span>
	                    </span>
	                    <c:if test="${modifyPermissionAllow}">
	                        <span style="cursor: pointer;">
	                            <img class="editPageSchedule4cat domReady_editPageSchedule" domvalue="{'entityId':'${entityDetail.entityuuid}', 'pagetype':'categoryPage'}" alt="edit page schedule" src="/img/vendor/web-icons/gear--pencil.png">
	                        </span>
	                    </c:if>
	                    <span>
		                    <img src="/img/vendor/web-icons/information-white.png" 
		                        domvalue="{'topOffset':'20', 'leftOffset':'-150', 'width':'400', 'popupContentContainerId':'pageBind4CatHowtoInfo'}" 
		                        title="click for more information" alt="click for more information" class="detailInfoPopupClick">
	                    </span>
                        <div id="pageBind4CatHowtoInfo" style="display: none;">
                            <div>
                                <p>
                                    You can bind category page for the category here.<br/>
                                    You may have many category pages predefined for different categories, 
                                    you can select one category page for the category here.
                                </p>
                                <p>
                                    <strong>What is category page?</strong><br/>
                                    Page has productList module scheduled for page's one container, and the schedule is in using 
                                    (system will check fromDate, toDate, and priority to determine if schedule in using), Or,<br/>
                                    page has productList module as default module for page's one container, and there has no any
                                    schedule module in using for the container.  
                                </p>
                                <p>
                                    <strong>What is productList module</strong><br/>
                                    Module has productList attribute in it.
                                </p>
                                <p>
                                    Note: System allows you to bind any kind of page to the category, like to bind blank page which 
                                    has no any module scheduled for all page's containers. 
                                </p>
                            </div>
                        </div>
	                </div>
	            </c:if>
	            
	            <div class="productPageSchedule">
	                <span class="scheduleTitle"><strong>Page binding for product:</strong></span>
	                    <span class="schedulePage">
	                        <span class="desktop">
                               <c:if test="${!empty defaultProductPage_desk}">
                                   ${defaultProductPage_desk.value}
                                   <c:if test="${empty entityDetail.prodpageuuid_desktop}"><span style="color: gray;">defined from upper level</span></c:if>
                               </c:if>
	                        </span>
	                    </span>
	                    <c:if test="${modifyPermissionAllow}">
	                        <span style="cursor: pointer;">
	                            <img class="editPageSchedule4prod domReady_editPageSchedule" domvalue="{'entityId':'${entityDetail.entityuuid}', 'pagetype':'productPage'}" alt="edit page schedule" src="/img/vendor/web-icons/gear--pencil.png">
	                        </span>
	                    </c:if>
	                    <span>
		                    <img src="/img/vendor/web-icons/information-white.png" 
		                        domvalue="{'topOffset':'20', 'leftOffset':'-150', 'width':'400', 'popupContentContainerId':'pageBind4ProdHowtoInfo'}" 
		                        title="click for more information" alt="click for more information" class="detailInfoPopupClick">
	                    </span>
	                    <div id="pageBind4ProdHowtoInfo" style="display: none;">
                            <div>
                                <p>
                                    You can bind product page for the product here.<br/>
                                    You may have many product pages pre-designed for different products, 
                                    you can select one product page for the product or products under the category.
                                </p>
                                <p>
                                    <strong>What is product page?</strong><br/>
                                    Page has product module scheduled for page's one container, and the schedule is in using 
                                    (system will check fromDate, toDate, and priority to determine if schedule in using), Or,<br/>
                                    page has product module as default module for page's one container, and there has no any
                                    schedule module in using for the container.  
                                </p>
                                <p>
                                    <strong>What is product module (A.K.A product template)</strong><br/>
                                    Module is created as product module<br/>
                                    System will ask you "Do you like this new module be used as product template?" when you try to create 
                                    a new module.
                                </p>
                                <p>
                                    Note: System allows you to bind any kind of page to the product, like to bind blank page which 
                                    has no any module scheduled for all page's containers. 
                                </p>
                            </div>
	                    </div>
	                    
	            </div>
	            
	        </div>
			
			
		</div>

		<div class="productAttrsContainer">
			<div>
				<span>[used space / total space : <span class="entityUsedSpace">${entityUsageWith100Multipled}/100</span>]</span>
				
				
				<c:if test="${!empty instance && !empty instance.attrGroupList}">
                    <span class="productEditAreaMax" style="float: right; margin-right: 30px; cursor: pointer; border: 1px solid; padding: 2px;"><img title="maximize window" class="domReady_productEditAreaMax" src="/img/vendor/web-icons/arrow-out.png"></span>
				</c:if>
				
				
			</div>
		

			<c:set var="moduleInstance" value="${entityDetail}" scope="request"/>
			<c:set var="orgUuid" value="${org.orguuid}" scope="request"/>
			<c:set var="instance" value="${instance}" scope="request"/>
			<c:set var="isPreview" value="${!modifyPermissionAllow}" scope="request"/>
			<c:import url="/WEB-INF/view/moduleInstance.jsp"/>
		
		</div>
	
	</div>
	
	<div class="ptnd_rightExtraFunctions">
		<div class="moduletreeSection">
			<div id="moduleProductTree" <c:if test="${!modifyPermissionAllow}">class="preview"</c:if>>
			</div>
		</div>
	</div>
	
</div>
