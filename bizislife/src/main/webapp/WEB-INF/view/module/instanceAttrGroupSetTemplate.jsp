<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<c:choose>
    <c:when test="${empty attrGroup.groupUuid}">
     <fieldset class="groupset" id="${attrGroup.moduleGroupUuid}">
         <legend>
             <span>
                 Group: <span class="value valueSection">${attrGroup.groupName}</span>
             </span>
         </legend>
         <div class="groupStatus">
            <c:choose>
                <c:when test="${!empty moduleDetailForProduct.value}">
                    New group is created in "${moduleDetailForProduct.value}"!
                </c:when>
                <c:when test="${!empty moduleDetail.prettyname}">
                    New group is created in "${moduleDetail.prettyname}"!
                </c:when>
                <c:otherwise>
                    New group is created in ModuleDetail!
                </c:otherwise>
            </c:choose>
         </div>
         
         <div class="attrListContainer">
             <c:if test="${!empty attrGroup.attrList}">
                 <c:forEach items="${attrGroup.attrsMapWithSameModuleAttrUuid}" var="attrs" varStatus="attrsIdx">
                     <c:if test="${!empty attrs.value}">
                        <div class="attrsInGroup ${attrsIdx.count%2==0?'lineEven':'lineOdd'}">
                             <c:forEach items="${attrs.value}" var="attr" varStatus="attrIdx">
                             
                                 <c:set var="moduleAttr" value="${attr}" scope="request"/>
                                 <c:set var="attrGroup" value="${attrGroup}" scope="request"/>
                                 <c:set var="moduleInstance" value="${moduleInstance}" scope="request"/>
                                 <c:set var="attrIdx" value="${attrIdx.index}" scope="request"/>
                                 <c:set var="isPreview" value="${isPreview}" scope="request"/>
                                 <c:import url="/WEB-INF/view/module/InstanceAttrGeneral.jsp"/>
                               
                             </c:forEach>
                        </div>
                     </c:if>
                 </c:forEach>
             </c:if>
         
         </div>
         
     </fieldset>
        
        
    </c:when>
    <c:otherwise>
     <fieldset class="groupset <c:if test='${attrGroupIdx>0}'>duplicated</c:if>">
   <legend>
       <span id="groupName_${attrGroup.groupUuid}">
           Group: <span class="value valueSection">${attrGroup.groupName}</span>
       </span>
       <span id="groupArray_${attrGroup.groupUuid}">
           [ <span class="title">isArray: </span><span class="value valueSection">${attrGroup.array?"true":"false"}</span> ]
       </span>
       
       <c:if test="${!isPreview}">
           <c:choose>
               <c:when test="${attrGroupIdx>0}">
                   <span class="groupDelete" style="float: right;">
                       <img src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" domvalue="${attrGroup.groupUuid}" class="groupDel deleteBtn domReady_deleteinstanceGroupSet" title="delete">
                   </span>
               </c:when>
               <c:otherwise>
                   <c:if test="${attrGroup.array}">
                       <span class="groupDuplicate" style="float: right;">
                           <img src="/img/vendor/web-icons/plus-circle-frame.png" alt="duplicate" domvalue="${attrGroup.groupUuid}" class="groupDupl duplicateBtn domReady_duplicateAttrGroupSet" title="duplicate">
                       </span>
                   </c:if>
               </c:otherwise>
           </c:choose>
       
       </c:if>
       
       
       
   </legend>
   
   <div id="instanceGroupStatus_${attrGroup.groupUuid}" class="groupStatus">
       <c:if test="${empty attrGroup.groupName}">
           The attrGroup defined in module is not duplicable.
       </c:if>
   </div>
   
   
   <div class="attrListContainer" id="${attrGroup.groupUuid}">
       <div id="moduleValueSaveStatus_${attrGroup.groupUuid}" class="attrGroupStatus"></div>
       <c:if test="${!empty attrGroup.attrList}">
           <c:forEach items="${attrGroup.attrsMapWithSameModuleAttrUuid}" var="attrs" varStatus="attrsIdx">
               <c:if test="${!empty attrs.value}">
                  <div class="attrsInGroup ${attrsIdx.count%2==0?'lineEven':'lineOdd'}">
                       <c:forEach items="${attrs.value}" var="attr" varStatus="attrIdx">
                       
                           <c:set var="moduleAttr" value="${attr}" scope="request"/>
                           <c:set var="attrGroup" value="${attrGroup}" scope="request"/>
                           <c:set var="moduleInstance" value="${moduleInstance}" scope="request"/>
                           <c:set var="attrIdx" value="${attrIdx.index}" scope="request"/>
                           <c:set var="isPreview" value="${isPreview}" scope="request"/>
                           <c:import url="/WEB-INF/view/module/InstanceAttrGeneral.jsp"/>
                         
                       </c:forEach>
                  </div>
               </c:if>
           </c:forEach>
       </c:if>
   
   </div>
   
   <%-- moduleCharUsage info --%>
   <c:if test="${!empty instanceCharUsageWith100Multipled_attrGroupDupli}">
       <input type="hidden" class="instanceCharUsageWith100Multipled_attrGroupDupli" name="instanceCharUsageWith100Multipled_attrGroupDupli" value="${instanceCharUsageWith100Multipled_attrGroupDupli}"/>
   </c:if>
     </fieldset>
    
    </c:otherwise>

</c:choose>
	
