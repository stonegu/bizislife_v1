<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<c:choose>
    <c:when test="${empty moduleAttr.uuid}">
        <div class="attr" id="${moduleAttr.moduleAttrUuid}">
            <div class="attrName attrElement" colorvalue="808080" style="background-color: #808080;">
                <span class="title">Name: </span>
                <span class="value valueSection">${moduleAttr.name}</span>
                <div class="attrType">
                    <c:choose>
                        <c:when test="${moduleAttr.type eq 'ModuleTextAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">TEXT</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleNumberAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">NUMBER</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleImageAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">IMAGE</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleLinkAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">LINK</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleMoneyAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">MONEY</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleProductListAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">PRODUCTLIST</span>
                        </c:when>
                        <c:when test="${moduleAttr.type eq 'ModuleEntityCategoryListAttribute'}">
                            <span style="color: #0000FF; font-size: 10px; font-family: Arial;">CatList</span>
                        </c:when>
                        
                    </c:choose>
                </div>
            </div>
            
            <div class="attrElements">
                <div class="attrTitle attrElement">
                    <span class="title">Title: </span>
                    <span class="value valueSection">${moduleAttr.title}</span>
                </div>
            
            </div>
            
            <div class="attrStatus">
                <div class="attrValidationStatus">
                    <div>
						<c:choose>
						    <c:when test="${!empty moduleDetailForProduct.value}">
						        New attribute is created in "${moduleDetailForProduct.value}".
						        <c:if test="${!isPreview}">
                                    Click <a href="#" class="domReady_newInstanceAttr" domvalue="{'moduleUuid':'${moduleDetailForProduct.key}', 'moduleGroupUuid':'${attrGroup.moduleGroupUuid}', 'moduleAttrUuid':'${moduleAttr.moduleAttrUuid}', 'instanceUuid':'${moduleInstance.moduleinstanceuuid}', 'instanceGroupUuid':'${attrGroup.groupUuid}'}">here</a> to apply the change into this instance!
						        </c:if> 
						    </c:when>
						    <c:when test="${!empty moduleDetail.prettyname}">
						        New attribute is created in "${moduleDetail.prettyname}".
						        <c:if test="${!isPreview}">
                                    Click <a href="#" class="domReady_newInstanceAttr" domvalue="{'moduleUuid':'${moduleDetail.moduleuuid}', 'moduleGroupUuid':'${attrGroup.moduleGroupUuid}', 'moduleAttrUuid':'${moduleAttr.moduleAttrUuid}', 'instanceUuid':'${moduleInstance.moduleinstanceuuid}', 'instanceGroupUuid':'${attrGroup.groupUuid}'}">here</a> to apply the change into this instance!
						        </c:if>
						    </c:when>
						    <c:otherwise>
						        New attribute is created in ModuleDetail.
						    </c:otherwise>
						</c:choose>
                    </div>
                </div>
            </div>
            
            
        </div>
    </c:when>
    <c:otherwise>
		<c:set var="hexcolor" value="${((!empty targetWithMetaDataMap) && (!empty targetWithMetaDataMap[moduleAttr.uuid]) && (!empty targetWithMetaDataMap[moduleAttr.uuid].hexcolor))?targetWithMetaDataMap[moduleAttr.uuid].hexcolor:(((!empty targetWithMetaDataMap) && (!empty targetWithMetaDataMap[moduleAttr.moduleAttrUuid]) && (!empty targetWithMetaDataMap[moduleAttr.moduleAttrUuid].hexcolor))?targetWithMetaDataMap[moduleAttr.moduleAttrUuid].hexcolor:'808080')}"/>
		<c:set var="usingDefaultHexColor" value="${((empty targetWithMetaDataMap) || (empty targetWithMetaDataMap[moduleAttr.uuid]) || (empty targetWithMetaDataMap[moduleAttr.uuid].hexcolor))?true:false}"/>
		<c:set var="sysDefaultHexColor" value="${((!empty targetWithMetaDataMap) && (!empty targetWithMetaDataMap[moduleAttr.moduleAttrUuid]) && (!empty targetWithMetaDataMap[moduleAttr.moduleAttrUuid].hexcolor))?targetWithMetaDataMap[moduleAttr.moduleAttrUuid].hexcolor:'808080'}"/>
		
		<div class="attr attrSortable <c:if test='${attrIdx>0}'>duplicated</c:if>" id="${moduleAttr.uuid}">
		    <div id="attrName_${moduleAttr.uuid}" class="attrName attrElement" colorvalue="${hexcolor}" style="background-color: #${hexcolor};">
		        <span class="title">Name: </span>
		        <span class="value valueSection">${moduleAttr.name}</span>
		        <div class="attrType">
		            <c:choose>
		                <c:when test="${moduleAttr.type eq 'ModuleTextAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">TEXT</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleNumberAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">NUMBER</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleImageAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">IMAGE</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleLinkAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">LINK</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleMoneyAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">MONEY</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleProductListAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">PRODUCTLIST</span>
		                </c:when>
		                <c:when test="${moduleAttr.type eq 'ModuleEntityCategoryListAttribute'}">
		                    <span style="color: #0000FF; font-size: 10px; font-family: Arial;">CatList</span>
		                </c:when>
		                
		            </c:choose>
		        
		        </div>
		        
		        <c:if test="${!isPreview}">
		            <c:choose>
		                <c:when test="${attrIdx>0}">
		                    <div class="attrDelete">
		                        <img src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" domvalue="{'groupid':'${attrGroup.groupUuid}', 'attrid':'${moduleAttr.uuid}'}" class="attrDel deleteBtn domReady_deleteInstanceAttr" title="delete">
		                    </div>
		                </c:when>
		                <c:otherwise>
		                    <c:if test="${moduleAttr.array && !empty moduleAttr.name}">
		                        <div class="attrDuplicate">
		                            <img src="/img/vendor/web-icons/plus-circle-frame.png" alt="duplicate" domvalue="{'groupid':'${attrGroup.groupUuid}', 'attrid':'${moduleAttr.uuid}'}" class="attrDupl duplicateBtn domReady_duplicateModuleAttr" title="duplicate">
		                        </div>
		                    </c:if>
		                </c:otherwise>
		            </c:choose>
		        </c:if>
		
		    </div>
		    
		    
		    <div class="attrElements">
		    
		        <div id="attrVisibility_${moduleAttr.uuid}" class="attrVisibility attrElement">
		            <span class="title">Editable: </span>
		            <span class="value valueSection">${moduleAttr.editable?"true":"false"}</span>
		        </div>
		        <div id="attrRequied_${moduleAttr.uuid}" class="attrRequired attrElement">
		            <span class="title">Required: </span>
		            <span class="value valueSection">${moduleAttr.required?"true":"false"}</span>
		        </div>
		        <div id="attrArray_${moduleAttr.uuid}" class="attrArray attrElement">
		            <span class="title">IsArray: </span>
		            <span class="value valueSection">${moduleAttr.array?"true":"false"}</span>
		        </div>
		        <div id="attrTitle_${moduleAttr.uuid}" class="attrTitle attrElement">
		            <span class="title">Title: </span>
		            <span class="value valueSection">${moduleAttr.title}</span>
		            
		            <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrTitle_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                <span class="valueEdit editSection displaynone"><input id="attrTitle_v_${moduleAttr.uuid}" type="text" name="title" value="<c:out value='${moduleAttr.title}'/>" /></span>
		                <span class="editActionIcons editSection displaynone">
		                    <img class="domReady_saveInstanceValue" 
		                        domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrTitle_${moduleAttr.uuid}', 'valueName':'title', 'valueId':'attrTitle_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                        alt="save" src="/img/vendor/web-icons/tick.png"> 
		                    <img class="domReady_cancelToEditValue" 
		                        domvalue="attrTitle_${moduleAttr.uuid}" 
		                        alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
<%-- 		                        
		                <span><img class="detailInfoPopupClick" alt="click for How To Get The Value" title="click for How To Get The Value" 
		                            domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToTextAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
 --%>		                            
		            </c:if>
		            
		        </div>
		        
		        <%-- for different attr's special members --%>
		        <c:choose>
		            <c:when test="${moduleAttr.type eq 'ModuleTextAttribute'}">
		                <div id="attrMinLength_${moduleAttr.uuid}" class="attrMinLength attrElement">
		                    <span class="title">Min Length: </span>
		                    <span class="value valueSection">${moduleAttr.minLength}</span>
		                </div>
		                <div id="attrMaxLength_${moduleAttr.uuid}" class="attrMaxLength attrElement">
		                    <span class="title">Max Length: </span>
		                    <span class="value valueSection">${moduleAttr.maxLength}</span>
		                </div>
		                <div id="attrDefaultTxtValue_${moduleAttr.uuid}" class="attrDefaultValue attrElement">
		                    <span class="title">Value: 
		                        <%-- 
		                            moduleAttr.name is null, which indicates this attribute defined in module is not duplicable.
		                            I still put this instance attr here, but not editable 
		                            
		                        --%>
		                        <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                            <span class="editIcon valueSection">
		                                <img class="domReady_toEditAttrTxtValue txtAttrBtn" 
		                                domvalue="${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
		                            </span>
		                            
		                            <span class="editActionIcons editSection displaynone">
		                                <img class="domReady_toSaveAttrTxtValue txtAttrBtn" 
		                                    domvalue="${moduleAttr.uuid}"  
		                                    alt="save" src="/img/vendor/web-icons/tick.png"> 
		                                <img class="domReady_toCancelAttrTxtValue txtAttrBtn" 
		                                    domvalue="${moduleAttr.uuid}" 
		                                    alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                            <span><img class="detailInfoPopupClick" alt="click for How To Get The Value" title="click for How To Get The Value" 
                                                domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToTextAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                                        src="/img/vendor/web-icons/information-white.png"></span>
		                        </c:if>
		                    </span>
		                    
		                    <div class="valueEdit">
		                       <c:choose>
		                           <c:when test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                                <form name="attrDefaultTxtUpdateForm" id="attrDefaultTxtUpdateForm_${moduleAttr.uuid}" method="post" action="/test">
                                            <input type="hidden" name="instanceId" value="${moduleInstance.moduleinstanceuuid}">
                                            <input type="hidden" name="updateType" value="attrValue">
                                            <input type="hidden" name="groupUuid" value="${attrGroup.groupUuid}">
                                            <input type="hidden" name="attrUuid" value="${moduleAttr.uuid}">
                                            <input type="hidden" name="valueName" value="defaultValue">
		                                    <textarea class="attrDefaultTxtValueEditor" domvalue="${moduleAttr.uuid}" name="updateValue">${moduleAttr.defaultValue}</textarea>
		                                </form>
		                           </c:when>
		                           <c:otherwise>
		                               ${moduleAttr.defaultValue}
		                           </c:otherwise>
		                       </c:choose>
		                    
		                    </div>
		                    
		                </div>
		                
		            </c:when>
		            <c:when test="${moduleAttr.type eq 'ModuleNumberAttribute'}">
		                <div id="attrMinValue_${moduleAttr.uuid}" class="attrMinValue attrElement">
		                    <span class="title">Min Value: </span>
		                    <span class="value valueSection">${moduleAttr.minValue}</span>
		                </div>
		                <div id="attrMaxValue_${moduleAttr.uuid}" class="attrMaxValue attrElement">
		                    <span class="title">Max Value: </span>
		                    <span class="value valueSection">${moduleAttr.maxValue}</span>
		                </div>
		                <div id="attrScale_${moduleAttr.uuid}" class="attrScale attrElement">
		                    <span class="title">Scale: </span>
		                    <span class="value valueSection">${moduleAttr.scale}</span>
		                </div>
		                <div id="attrDefaultValue_${moduleAttr.uuid}" class="attrDefaultValue attrElement">
		                    <span class="title">Value: </span>
		                    <span class="value valueSection">${moduleAttr.defaultValue}</span>
		                    
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrDefaultValue_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrDefaultValue_v_${moduleAttr.uuid}" type="text" name="defaultValue" value="${moduleAttr.defaultValue}" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrDefaultValue_${moduleAttr.uuid}', 'valueName':'defaultValue', 'valueId':'attrDefaultValue_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrDefaultValue_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                        <span><img class="detailInfoPopupClick" alt="click for How To Get The Value" title="click for How To Get The Value"
                                            domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToNumberAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                                    src="/img/vendor/web-icons/information-white.png"></span>
		                    </c:if>
		                </div>
		            
		            </c:when>
		            <c:when test="${moduleAttr.type eq 'ModuleImageAttribute'}">
		    
		                <c:if test="${moduleInstance.getClass().getSimpleName() eq 'EntityDetail' }">
		                
		                    <div id="attrDefaultImg__${moduleAttr.uuid}" class="attrDefaultImg attrElement">
		                        <span class="title">Is Default Image: </span>
		                        <span class="value valueSection">${moduleAttr.defaultPicture?"true":"false"}</span>
		                    </div>
		                
		                </c:if>
		                
		                <div id="attrImgFile_${moduleAttr.uuid}" class="attrImgFile attrElement">
		                    <span class="title">Image: </span>
		                    <span class="value valueSection">
		                        <c:choose>
		                            <c:when test="${!empty moduleAttr.fileSystemName}">
		                                <img class="detailInfoPopup" src="/getphoto?id=${moduleAttr.fileSystemName}&size=50"  domvalue="{'popupContent':'<img src=&quot;/getphoto?id=${moduleAttr.fileSystemName}&size=200&quot;>'}"/>
		                            </c:when>
		                            <c:otherwise>
		                            
		                            </c:otherwise>
		                        </c:choose>
		                        
		                    </span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection">
		<%--                        
		                            <img class="domReady_editModuleImg" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrImgFile_${moduleAttr.uuid}', 'valueName':'fileSystemName', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
		 --%>                       
		                            <img class="detailInfoPopupClick medialistForModuleConfigIcon" 
		                                domvalue="{'popupContentContainerAppendId':'medialistForModuleConfigId', 'stay':true, 'width':'600px', 'height':'400px', 'topOffset': '-100', 'replacement': [{'objId':'mediaSelectForm_groupUuid', 'objVal':'${attrGroup.groupUuid}'},{'objId':'mediaSelectForm_attrUuid', 'objVal':'${moduleAttr.uuid}'},{'objId':'mediaSelectForm_valueName', 'objVal':'fileSystemName'},{'objId':'mediaSelectForm_defaultValue', 'objVal':'${moduleAttr.fileSystemName}'}]}"
		                                alt="edit" src="/img/vendor/web-icons/gear--pencil.png">
		                                
		                        </span>
		                        <span><img class="detailInfoPopupClick" alt="click for How To Get The Value" title="click for How To Get The Value" 
                                            domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToImageAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                                    src="/img/vendor/web-icons/information-white.png"></span>
		                    </c:if>
		                </div>
		                
		                
		            
		            </c:when>
		            
		            
		            <c:when test="${moduleAttr.type eq 'ModuleLinkAttribute'}">
		            
		                <div id="attrHrefValue_${moduleAttr.uuid}" class="attrHrefValue attrElement">
		                    <span class="title">Href: </span>
		                    <span class="value valueSection">${moduleAttr.href}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrHrefValue_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrHrefValue_v_${moduleAttr.uuid}" type="text" name="href" value="<c:out value='${moduleAttr.href}'/>" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrHrefValue_${moduleAttr.uuid}', 'valueName':'href', 'valueId':'attrHrefValue_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrHrefValue_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToLinkValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		                <div id="attrRelValue_${moduleAttr.uuid}" class="attrRelValue attrElement">
		                    <span class="title">Rel: </span>
		                    <span class="value valueSection">${moduleAttr.rel}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrRelValue_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone">
		                            <select name="rel" id="attrRelValue_v_${moduleAttr.uuid}">
		                                <option value="">select...</option>
		                                <c:forEach items="${moduleLinkAttr_linkRels}" var="linkRel" varStatus="linkRelIdx">
		                                    <option value="${linkRel.name()}" <c:if test="${moduleAttr.rel eq linkRel.name()}">selected="selected"</c:if>>${linkRel.name()}</option>
		                                </c:forEach>
		                            </select>
		                        </span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrRelValue_${moduleAttr.uuid}', 'valueName':'rel', 'valueId':'attrRelValue_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrRelValue_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToLinkValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		                <div id="attrTargetValue_${moduleAttr.uuid}" class="attrTargetValue attrElement">
		                    <span class="title">Target: </span>
		                    <span class="value valueSection">${moduleAttr.target}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrTargetValue_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone">
		                            <select name="target" id="attrTargetValue_v_${moduleAttr.uuid}">
		                                <option value="">select...</option>
		                                <c:forEach items="${moduleLinkAttr_linkTargets}" var="linkTarget" varStatus="linkTargetIndex">
		                                    <option value="${linkTarget.name()}" <c:if test="${moduleAttr.target eq linkTarget.name()}">selected="selected"</c:if>>${linkTarget.name()}</option>
		                                </c:forEach>
		                            </select>
		                        </span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrTargetValue_${moduleAttr.uuid}', 'valueName':'target', 'valueId':'attrTargetValue_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrTargetValue_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToLinkValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		                
		                <div id="attrLinkValue_${moduleAttr.uuid}" class="attrLinkValue attrElement">
		                    <span class="title">Link Value: </span>
		                    <span class="value valueSection">${moduleAttr.linkValue}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrLinkValue_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrLinkValue_v_${moduleAttr.uuid}" maxlength="50" type="text" name="linkValue" value="<c:out value='${moduleAttr.linkValue}'/>" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrLinkValue_${moduleAttr.uuid}', 'valueName':'linkValue', 'valueId':'attrLinkValue_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrLinkValue_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToLinkValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		            
		            
		            </c:when>
		            
		            <c:when test="${moduleAttr.type eq 'ModuleMoneyAttribute'}">
		            
		                <div id="attrCurrency_${moduleAttr.uuid}" class="attrCurrency attrElement">
		                    <span class="title">Currency: </span>
		                    <span class="value valueSection">${(!empty moduleAttr.money)?moduleAttr.money.currency.currencyCode:""}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrCurrency_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone">
		                            <select name="moneyCurrency" id="attrCurrency_v_${moduleAttr.uuid}">
		                                <option>select...</option>
		                                <c:forEach items="${availableCurrencies}" var="currency" varStatus="currencyIndex">
		                                    <option value="${currency.key}">${currency.value} [${currency.key}]</option>
		                                </c:forEach>
		                            
		                            </select>
		                        </span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrCurrency_${moduleAttr.uuid}', 'valueName':'moneyCurrency', 'valueId':'attrCurrency_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrCurrency_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToMoneyAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		                <div id="attrAmount_${moduleAttr.uuid}" class="attrAmount attrElement">
		                    <span class="title">Amount: </span>
		                    <span class="value valueSection">${(!empty moduleAttr.money)?moduleAttr.money.amount:""}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrAmount_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrAmount_v_${moduleAttr.uuid}" type="text" name="moneyAmount" value="${(!empty moduleAttr.money)?moduleAttr.money.amount:''}" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrAmount_${moduleAttr.uuid}', 'valueName':'moneyAmount', 'valueId':'attrAmount_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrAmount_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToMoneyAttrValue&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		            
		            
		            </c:when>           
		            <c:when test="${moduleAttr.type eq 'ModuleProductListAttribute'}">
		            
		                <div id="attrTnpip_${moduleAttr.uuid}" class="attrTnpip attrElement">
		                    <span class="title">Number of products per page: </span>
		                    <span class="value valueSection">${moduleAttr.totalNumProductsInPage}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrTnpip_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrTnpip_v_${moduleAttr.uuid}" type="text" name="totalNumProductsInPage" value="<c:out value='${moduleAttr.totalNumProductsInPage}'/>" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrTnpip_${moduleAttr.uuid}', 'valueName':'totalNumProductsInPage', 'valueId':'attrTnpip_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrTnpip_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
		                            domvalue="{'popupContent':'Maxinum number of products per page.'}" 
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		
		                <div id="attrHasPagi_${moduleAttr.uuid}" class="attrHasPagi attrElement">
		                    <span class="title">Need pagination: </span>
		                    <span class="value valueSection">${moduleAttr.hasPagination?"true":"false"}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrHasPagi_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input class="moduleCheckboxChangeVal" id="attrHasPagi_v_${moduleAttr.uuid}" type="checkbox" name="hasPagination" value="1" <c:if test="${moduleAttr.hasPagination}">checked="checked"</c:if> /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrHasPagi_${moduleAttr.uuid}', 'valueName':'hasPagination', 'valueId':'attrHasPagi_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrHasPagi_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
		                            domvalue="{'popupContent':'<strong>true:</strong> Pagination will be displayed on the page based on your total products need to show with maxinum number of products you like to show per page.<br/><strong>false:</strong> No pagination will be shown.'}" 
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		                
		                <div class="attrMprodPageUuid attrElement">
		                    <span>click <img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToProductList&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"> to know How To Get Product List in page.</span>
		                    
		                </div>
		            
		            </c:when>
		
		            <c:when test="${moduleAttr.type eq 'ModuleEntityCategoryListAttribute'}">
		            
		                <div id="attrCatType_${moduleAttr.uuid}" class="attrCatType attrElement">
		                    <span class="title">Type of category: </span>
		                    <span class="value valueSection">${moduleAttr.catType}</span>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
		                            domvalue="{'popupContent':'Type of category list.'}" 
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		            
		                <div id="attrSortby_${moduleAttr.uuid}" class="attrSortby attrElement">
		                    <span class="title">Sort by: </span>
		                    <span class="value valueSection">${moduleAttr.sortType}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrSortby_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone">
		                            <select name="sortType" id="attrSortby_v_${moduleAttr.uuid}">
		                                <c:forEach items="${entityCatSoryBy}" var="sortType" varStatus="sortTypeIdx">
		                                    <option value="${sortType.name()}" <c:if test="${sortType.name() eq moduleAttr.sortType}">selected="selected"</c:if>>${sortType.name()}</option>
		                                </c:forEach>
		                            </select>
		                            
		                        </span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrSortby_${moduleAttr.uuid}', 'valueName':'sortType', 'valueId':'attrSortby_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrSortby_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
		                            domvalue="{'popupContent':'Sort by selection.'}" 
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		            
		                <div id="attrCatLevel_${moduleAttr.uuid}" class="attrCatLevel attrElement">
		                    <span class="title">Level of category: </span>
		                    <span class="value valueSection">${moduleAttr.levelOfCategory}</span>
		                    <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		                        <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="attrCatLevel_${moduleAttr.uuid}" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
		                        <span class="valueEdit editSection displaynone"><input id="attrCatLevel_v_${moduleAttr.uuid}" type="text" name="levelOfCategory" value="${moduleAttr.levelOfCategory}" /></span>
		                        <span class="editActionIcons editSection displaynone">
		                            <img class="domReady_saveInstanceValue" 
		                                domvalue="{'typeToSave':'attrValue', 'webSectionId':'attrCatLevel_${moduleAttr.uuid}', 'valueName':'levelOfCategory', 'valueId':'attrCatLevel_v_${moduleAttr.uuid}', 'groupUuid':'${attrGroup.groupUuid}', 'attrUuid':'${moduleAttr.uuid}'}" 
		                                alt="save" src="/img/vendor/web-icons/tick.png"> 
		                            <img class="domReady_cancelToEditValue" 
		                                domvalue="attrCatLevel_${moduleAttr.uuid}" 
		                                alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
		                    
		                    </c:if>
		                    <span><img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
		                            domvalue="{'popupContent':'How many level of subCategories do you like category list hold.'}" 
		                            src="/img/vendor/web-icons/information-white.png"></span>
		                </div>
		            
		                <div class="attrMprodPageUuid attrElement">
		                    <span>click <img class="detailInfoPopupClick" alt="click for more information" title="click for more information"
                                    domvalue="{'width':'600', 'height':'300', 'topOffset':'-100', 'stay':'true', 'ajaxCall':{'url':'howToGetValueInView_v2', 'params':'howToDocName=howToCategoryList&moduleAttrGroupUuid=${attrGroup.moduleGroupUuid}&moduleAttrUuid=${moduleAttr.moduleAttrUuid}&moduleUuid=${moduleInstance.moduleuuid}'}}"
		                            src="/img/vendor/web-icons/information-white.png"> to know How To Get Category List in page.</span>
		                </div>
		            </c:when>
		            
		        </c:choose>
		        
		        <c:if test="${!empty moduleAttr.name && !isPreview && (moduleAttr.editable!=null && moduleAttr.editable)}">
		        
		            <div class="functionBtnsForAttr" style="background-color: white; height: 100%; right: 0; position: absolute; top: 0; width: 12px; border-left: 1px solid #4a4a4a; border-right: 1px solid #4a4a4a; border-bottom: 1px solid #4a4a4a; z-index: 9;">
		                <div class="handlerForFuncBtns domReady_openFuncBtnsForAttr" style="height: 12px; position: absolute; left: 0; width: 100%; z-index: 10; cursor: pointer;">
		                    
		                </div>
		                <div class="contentForFuncBtns" style="background-color: #d6d6d6; height: 100%; position: relative; float: left; width: 100%;">
		                    <div class="funcBtnsArea" style="margin: 20px 10px 10px; float: left; display: none;">
		                        <div class="coloring" style="float: left;">
		                            <img title="coloring" 
		                                class="coloringIconImg detailInfoPopupClick" 
		                                alt="coloring" 
		                                usingDefaultHexColor="${usingDefaultHexColor}" 
		                                domvalue="{'topOffset':'0', 'leftOffset':'-60', 'popupContentContainerId':'moduleColoringInput', 'sysDefaultHexColor':'${sysDefaultHexColor}', 'coloringAjax':'updateModuleMetaValue', 'currentColorContainerId':'attrName_${moduleAttr.uuid}', 'coStyleElementId':'attrName_${moduleAttr.uuid}'}"
		                                src="/img/vendor/web-icons/color-swatch.png">
		                        </div>
		                    </div>
		                </div>
		            </div>
		        
		        </c:if>
		    
		    </div>
		    
		    <div id="instanceValueSaveStatus_${moduleAttr.uuid}" class="attrStatus">
		        <div class="attrValidationStatus">
		            <c:if test="${empty moduleAttr.name}">
		                <div>
		                    The attribute defined in module is not duplicable, or the attribute could be deleted in module.
		                </div>
		            </c:if>
		            <c:if test="${!empty attributeUuidWithValidationInfos && !empty attributeUuidWithValidationInfos[moduleAttr.uuid]}">
		                <c:forEach items="${attributeUuidWithValidationInfos[moduleAttr.uuid]}" var="valiInfo" varStatus="valiInfoIndex">
		                    <div>
		                        ${valiInfo}
		                    </div>
		                </c:forEach>
		            </c:if>
		        </div>
		        
		        <div class="attrValidationStatus_temp" style="display: none;">
		        
		        </div>
		    </div>
		    
		    
		    <%-- instanceCharUsage info --%>
		    <c:if test="${!empty instanceCharUsageWith100Multipled_attrDupli}">
		        <input type="hidden" class="instanceCharUsageWith100Multipled_attrDupli" name="instanceCharUsageWith100Multipled_attrDupli" value="${instanceCharUsageWith100Multipled_attrDupli}"/>
		    </c:if>
		    
		    
		</div>
    
    </c:otherwise>
</c:choose>






