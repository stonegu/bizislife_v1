<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div>

	<fieldset>
        <legend>
            Add Account for ${targetOrg.orgname}
        </legend>
        
        <table style="width: 100%;">
            <tr>
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['personalEmail'])?'errorHighlight':''}">Email *: </span></td>
                <td class="addAcctValue"><input type="text" id="personalEmail" name="personalEmail" value="${personalEmail}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['personalEmail']}</span></td>
            </tr>
            <tr>
                <td class="addAcctTitle"><span>Use email as login name: </span></td>
                <td class="addAcctValue"><input type="checkbox" id="useEmailAsLoginName" name="useEmailAsLoginName" value="true" <c:if test="${!empty useEmailAsLoginName && useEmailAsLoginName}">checked="checked"</c:if> /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight"></span></td>
            </tr>
            <tr id="loginnamePart">
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['loginname'])?'errorHighlight':''}">Login Name *: </span></td>
                <td class="addAcctValue"><input id="loginname" type="text" name="loginname" value="${account.loginname}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['loginname']}</span></td>
            </tr>
            <tr>
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['firstname'])?'errorHighlight':''}">First Name *: </span></td>
                <td class="addAcctValue"><input type="text" name="firstname" value="${accountProfile.firstname}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['firstname']}</span></td>
            </tr>
            <tr>
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['lastname'])?'errorHighlight':''}">Last Name *: </span></td>
                <td class="addAcctValue"><input type="text" name="lastname" value="${accountProfile.lastname}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['lastname']}</span></td>
            </tr>
            <tr>
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['pwd'])?'errorHighlight':''}">Password *: </span></td>
                <td class="addAcctValue"><input type="password" name="pwd" value="${account.pwd}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['pwd']}</span></td>
            </tr>
            <tr>
                <td class="addAcctTitle"><span class="${(!empty errorMsgs['repwd'])?'errorHighlight':''}">Confirm *: </span></td>
                <td class="addAcctValue"><input type="password" name="repwd" value="${repwd}" /></td>
                <td class="addAcctErrorInfo"><span class="errorHighlight">${errorMsgs['repwd']}</span></td>
            </tr>
            <tr>
                <td colspan="3"><span>Belong in Group(s): </span></td>
            </tr>
            <c:forEach items="${accountGroups}" var="group" varStatus="groupIndex">
                <tr>
                    <td colspan="3">
		                <c:choose>
		                    <c:when test="${group.grouptype eq 'Everyone'}">
		                        <img src="/img/vendor/web-icons/tick.png" alt="selected" title="selected"> ${group.groupname} group from ${orgIdNameMap[group.organization_id]}
		                        <input type="hidden" name="accountGroup" value="${group.id}" />
		                    </c:when>
		                    <c:otherwise>
		                        <input type="checkbox" name="accountGroup" value="${group.id}" <c:if test="${!empty selectedGroups && selectedGroups[group.id]}">checked="checked"</c:if> /> ${group.groupname} group from ${orgIdNameMap[group.organization_id]} 
		                    </c:otherwise>
		                </c:choose>
                    </td>
                </tr>
            </c:forEach>
        
        </table>
		
		<input type="hidden" name="org" value="${targetOrg.orguuid}">
		
	</fieldset>

</div>

<script type="text/javascript">

	$(function(){
		$("#useEmailAsLoginName").click(function(){
			if($(this).attr("checked")){
				if($.trim($("#personalEmail").val())){
					$(this).next("span.errorHighlight").html("");
	 				//$("#loginnamePart").hide();
	 				$("#loginname").val($("#personalEmail").val());
				}else{
					$(this).attr("checked", false);
					$(this).next("span.errorHighlight").html("emil address is empty");
				}
			}else{
				//$("#loginnamePart").show();
				$("#loginname").val("");
			}
		})
		
	})



</script>