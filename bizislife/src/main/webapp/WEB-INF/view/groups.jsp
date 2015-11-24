<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Home Page</title>

	<link rel="stylesheet" type="text/css" href="/js/brands/JQuery/jquery-ui-1.8.20/css/ui-lightness/jquery-ui-1.8.20.custom.css"/>

	<script src="/js/brands/JQuery/jquery-1.7.2.js" type="text/javascript" language="javascript"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/js/jquery-ui-1.8.20.custom.min.js"></script>
	
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.cookie.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.hotkeys.js"></script>
	<script type="text/javascript" src="/js/brands/jsTree/jquery.jstree.js"></script>

	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>

    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>


<%--<script src="/js/jquery.ui.core.js" type="text/javascript" language="javascript"></script>--%>
<%--<script src="/js/jquery.ui.tabs.js" type="text/javascript" language="javascript"></script>--%>

	<style type="text/css">
	
		#permissionTree .jstree-closed[rel="permission"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/lock.png);
			background-position: 0 0;
		}		
		#permissionTree .jstree-open[rel="permission"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/lock.png);
			background-position: 0 0;
		}		
		#permissionTree .jstree-leaf[rel="permission"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/lock.png);
			background-position: 0 0;
		}
		
		
		#moduleTreeForPermissionSet .jstree-closed[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForPermissionSet .jstree-open[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForPermissionSet .jstree-leaf[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
	
		#moduleTreeForPermissionSet .jstree-leaf[rel="productModule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_green_16x16.png);
			background-position: 0 0;
		}
		
	
		#productTreeForPermissionSet .jstree-closed[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTreeForPermissionSet .jstree-open[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTreeForPermissionSet .jstree-leaf[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTreeForPermissionSet .jstree-closed[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#productTreeForPermissionSet .jstree-open[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#productTreeForPermissionSet .jstree-leaf[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		
		#pageTreeForPermissionSet .jstree-leaf[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#pageTreeForPermissionSet .jstree-open[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#pageTreeForPermissionSet .jstree-closed[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		

	
	
		.nodeFunctionIcons img {
			margin-bottom: -3px;
		}
		
		#productTreeForPermissionSet.jstree a{
			width: 100%;
		}
		#moduleTreeForPermissionSet.jstree a{
			width: 100%;
		}



		.permissionIcons button{
			width:16px;
			height: 16px;
			border: 0;
			margin: 0 2px;
			
		}
		
		.permissionIcons .permissionIconPreview{
			background: no-repeat url("/img/vendor/web-icons/eye.png") 0 0;
		}
		.permissionIcons .permissionIconRead{
			background: no-repeat url("/img/vendor/web-icons/magnifier-zoom-actual-equal.png") 0 0;
		}
		.permissionIcons .permissionIconCopy{
			background: no-repeat url("/img/vendor/web-icons/copy_16x16.png") 0 0;
		}
		.permissionIcons .permissionIconModify{
			background: no-repeat url("/img/vendor/web-icons/gear.png") 0 0;
		}



		.jstree .permissionSets{
			position: absolute;
			right: 30px;
		}
		
		.jstree .permissionBtn{
			width:16px;
			height: 16px;
			border: 0;
			margin: 0 2px;
		}
		
		.jstree .permissionBtn.checkboxEmpty{
			background: no-repeat url("/img/vendor/web-icons/checkbox_roundcorner_empty.png") 0 0;
		}
		.jstree .permissionBtn.checkboxYes{
			background: no-repeat url("/img/vendor/web-icons/checkbox_roundcorner_yes.png") 0 0;
		}
		.jstree .permissionBtn.checkboxNo{
			background: no-repeat url("/img/vendor/web-icons/checkbox_roundcorner_no.png") 0 0;
		}
		.jstree .permissionBtn.checkboxYesFollow{
			background: no-repeat url("/img/vendor/web-icons/checkbox_roundcorner_yes_light.png") 0 0;
		}
		.jstree .permissionBtn.checkboxNoFollow{
			background: no-repeat url("/img/vendor/web-icons/checkbox_roundcorner_no_light.png") 0 0;
		}
	
	</style>


</head>
<body>

	<div class="pagetop">
		<c:import url="generalTop.jsp"/>
	</div>
	
	<div class="pagemiddle">
	
		<div class="pageleft" style="width: 25%; float: left; background-color: #EEEEEE;">
			<div class="accountSection">
				<c:choose>
					<c:when test="${!empty loginAccount}">
						<c:import url="accountNav.jsp"/>
					</c:when>				
					<c:otherwise>

						<div>
							<p>Login Form:</p>
							<form action="/login" method="post" name="loginForm">
								<p><input type="text" name="username" placeholder="type your email here." title="type your email here."/></p>
								<p><input type="password" name="password" placeholder="type your password here." title="type your password here." /></p>
								<p><a href="/forgotPassword">Forgot password?</a><input type="submit" name="loginFormSubmit" value="Login" /></p>
							</form>
						</div>
						
						<div>
							<p>Forgot your password?</p>
							<p>Type your email address to retrive your new password</p>
							<p>Email: <input type="text" name="emailForPwd" /></p>
							<p><input type="submit" name="retrievePwd" value="Retrieve"/></p>
						</div>
					
					</c:otherwise>
				</c:choose>
			</div>
		
			<div class="mainLeftNav">
				<c:import url="leftNav.jsp"/>
			</div>
		
		</div>
		
		<div class="pagecenter" style="width: 50%; float: left; ">
		
			<c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount()}">
				<a href="/addGroup?org=${org.orguuid}">+Add Group</a>
			</c:if>
		
			<table style="width: 96%;">
				<thead style="background-color: gray; color: white;">
					<tr>
						<td style="text-align: center;">Name</td>
						<td style="text-align: center;">Usage</td>
						<td style="text-align: center;">Type</td>
						<td style="text-align: center;">Permission</td>
						<td style="text-align: center; width: 100px;">Actions</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${groups}" var="group" varStatus="groupIndex">
					<tr class="groupBrief briefDesc">
						<td>
							<c:choose>
								<c:when test="${(!empty loginAccount) && loginAccount.isSystemDefaultAccount() && (group.organization_id eq org.id)}">
									<span class="groupName groupEdit" domvalue="${group.id}" style="cursor: pointer;">${group.groupname}</span>
								</c:when>
								<c:otherwise>
									<span class="groupName">${group.groupname}</span>
								</c:otherwise>
							</c:choose>
						</td>
						<td style="text-align: center;">
							<c:choose>
								<c:when test="${group.accesslevel eq 1}">
									<img src="/img/vendor/web-icons/globe.png" alt="global usage" title="global usage">
								</c:when>
								<c:otherwise>
									<img src="/img/vendor/web-icons/home.png" alt="organization usage only" title="organization usage only">
								</c:otherwise>
							</c:choose>
						</td>
						<td>${group.grouptype}</td>
						<td>
							<c:choose>
								<c:when test="${group.grouptype eq 'SystemDefault'}">
									full privilege
								</c:when>
								<c:otherwise>
									...
								</c:otherwise>
							</c:choose>
						
						</td>
						<td style="text-align: center;">
						
							<c:if test="${(!empty loginAccount) && loginAccount.isSystemDefaultAccount()}">
							
								<c:choose>
									<c:when test="${group.organization_id eq org.id}">
										<img src="/img/vendor/web-icons/gear.png" alt="modify" class="groupEdit" title="modify">
									</c:when>
									<c:otherwise>
										<img src="/img/vendor/web-icons/trans_16x16.png">
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${group.grouptype!='SystemDefault'}">
										<img src="/img/vendor/web-icons/lock.png" alt="permission setup" title="permission setup" class="groupPermissionSet" domvalue="{'group':'${group.id}', 'targetOrg':'${org.id}'}">
									</c:when>
									<c:otherwise>
										<img src="/img/vendor/web-icons/trans_16x16.png">
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${(group.accesslevel eq 1) && (group.organization_id eq org.id) && (group.grouptype!='Everyone')}">
										<img src="/img/vendor/web-icons/arrow-join.png" alt="set who can join the group" class="groupJoin" domvalue="{'group':'${group.id}', 'targetOrg':'${org.id}'}" title="set who can join the group">
									</c:when>
									<c:otherwise>
										<img src="/img/vendor/web-icons/trans_16x16.png">
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${group.grouptype!='SystemDefault' && group.grouptype!='Everyone' && group.organization_id==org.id}">
										<form action="/delGroup" name="delGroup_${group.id}" id="delGroup_${group.id}" method="post" style="float: left;">
											<input type="hidden" name="org" value="${org.orguuid}" />
											<input type="hidden" name="groupId" value="${group.id}" />
										</form>
										<img src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" class="groupDelete" domvalue="${group.id}" title="delete"> 
									</c:when>
									<c:otherwise>
										<img src="/img/vendor/web-icons/trans_16x16.png">
									</c:otherwise>
								</c:choose>
							</c:if>
						
						</td>
					</tr>
					<c:if test="${(!empty loginAccount) && loginAccount.isSystemDefaultAccount() && (group.organization_id eq org.id)}">
						<tr class="groupDetail detailDesc" style="display: none;">
							<td colspan="5" style="background-color: #E6E6E6; padding: 10px;">
								<form action="/editGroup" method="post" name="editGroup">
									<input type="hidden" name="groupid" value="${group.id}" />
									<c:set var="groupid" value="${group.id}" scope="request"/>
									<c:set var="groupname" value="${group.groupname}" scope="request"/>
									<c:set var="description" value="${group.description}" scope="request"/>
									<c:set var="accesslevel" value="${group.accesslevel}" scope="request"/>
									<c:set var="targetOrg" value="${org}" scope="request"/>
									<c:import url="groupInfoFragment.jsp" />
									
									<input type="submit" />
									
								</form>
							</td>
						</tr>
					</c:if>
				</c:forEach>
				</tbody>
			</table>
		
		</div>
		
		<div class="pageright" style="width: 25%; float: right; background-color: #EEEEEE;">
			<p>this is the right section</p>
		</div>
	
	</div>
	
	<div class="pagebottom">
		<c:import url="generalBottom.jsp"/>
	</div>
	
	<%-- remove error msg from session --%>
	<%
		session.removeAttribute("errorMsgs");
	%>
	

    <script src="/javasecript/bgl.min.js" type="text/javascript" language="javascript"></script>
    <script src="/javasecript/permin.min.js" type="text/javascript" language="javascript"></script>
    
    <script type="text/javascript">
    $(function () {
    	var treeNodeIconsTimeout;

    	$(".groupEdit").click(function(){
    		$("tr.detailDesc").hide();
    		//console.log("groupEdit");
    		$(this).parents("tr.briefDesc").next("tr.detailDesc").show();
    		
    	})
    	
    	$(".groupDelete").click(function(){
    		
    		alert("TODO: take care all the group relationship...");
    		
/*     		
    		if(confirm("Are you like to delete the group?")){
    			var groupId = $(this).attr("domvalue");
    	        var theForm = $("#delGroup_"+groupId);
        		theForm.submit();
    		}
 */    		
    		
    	})
    	
    	$(".groupJoin").click(function(){
    		var domvalue = $(this).attr("domvalue");
    		var domJson = eval("("+domvalue+")");
    		
	        $.ajax({
	            type: "GET",
	            url: "/getWhoCanJoinGroupSegment",
	            cache: false,
	            data: "target=group&targetUuid="+domJson.group+"&targetOrg="+domJson.targetOrg,
	            dataType:"html",
	            success: function(jdata, textStatus){
	    			$.colorbox({
	    				width : 800,
	    				height : 600,
	    				html: jdata,
                        escKey: false,
	    				//inline: true, 
	    				//href: mediaUploadContainerContent,
	    				opacity: 0.4,
	    				overlayClose: false,
	    				onOpen:function(){},
	    				onLoad:function(){},
	    				onComplete:function(){
	    					
	    				},
	    				onCleanup:function(){
	    				},
	    				onClosed:function(){}
	    			})
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){
					alert(textStatus);
	            }
	        });
    		
    		
    		
    	})
    	
    	
         $(".toggleJoinAllowDeny").live("click", function(){
             var toggleIcon = $(this);
             var tjaDomvalue = toggleIcon.attr("domvalue");
             var tjaDomJson = eval("("+tjaDomvalue+")");
             
             //{'type':'allow', 'group':'6', 'targetOrg':'3'}
             
             $.ajax({
                 type: "GET",
                 url: "/toggleJoinAllowDeny",
                 cache: false,
                 data: "type="+tjaDomJson.type+"&group="+tjaDomJson.group+"&targetOrg="+tjaDomJson.targetOrg,
                 dataType:"html",
                 success: function(jtaData, textStatus){
                     
                     toggleIcon.parentsUntil("tbody").replaceWith(jtaData);
                     
                 },
                 error: function(XMLHttpRequest, textStatus, errorThrown){
                     alert("failed");
                 }
             });
             
         })
         
         $(".groupJoinKeyGenerate").live("click", function(){
             
             if(confirm("Note: refresh the key will cause old join request links unvalid for anyone hasn't join the group yet. \nAre you like to REFRESH KEY?")){
                 var jkGenerateIcon = $(this);
                 var jkgDomvalue = jkGenerateIcon.attr("domvalue");
                 var jkgDomJson = eval("("+jkgDomvalue+")");
                 
                 $.ajax({
                     type: "GET",
                     url: "/orgCanJoinModify",
                     cache: false,
                     data: "type=refreshJoinKey&group="+jkgDomJson.group+"&targetOrg="+jkgDomJson.targetOrg,
                     dataType:"json",
                     success: function(jkgData, textStatus){
                         if(jkgData.success){
                             jkGenerateIcon.siblings("span.joinKey").html(jkgData.response1);
                         }else{
                             alert(jkgData.response1);
                         }
                         
                     },
                     error: function(XMLHttpRequest, textStatus, errorThrown){
                         alert("failed");
                     }
                 });
                 
             }

         })
         
         $(".updateOrgCanJoinValue").live("click", function(){
             //{'webSectionId':'numAcctJoin_${org.id}', 
             //  'valueName':'totalaccountcanjoin', 
             //  'valueId':'numAcctJoin_v_${org.id}', 'group':'${group.id}', 'targetOrg':'${org.orguuid}'}
             var updateValueIcon = $(this);
             var uvDomvalue = updateValueIcon.attr("domvalue");
             var uvDomJson = eval("("+uvDomvalue+")");
             
             var updateValue = $("#"+uvDomJson.valueId).val();
             
             $.ajax({
                 type: "GET",
                 url: "/orgCanJoinModify",
                 cache: false,
                 data: "type=valueUpdate&group="+uvDomJson.group+"&targetOrg="+uvDomJson.targetOrg+"&valueName="+uvDomJson.valueName+"&value="+escape(updateValue),
                 dataType:"json",
                 success: function(ocjudata, textStatus){
                     if(ocjudata.success){
                         $("#"+uvDomJson.webSectionId).find("span.value").html(ocjudata.response1);
                         $("#"+uvDomJson.webSectionId).find("div.value").html(ocjudata.response1);
                         $("#"+uvDomJson.webSectionId).find("img.domReady_cancelToEditValue").click();
                         
                     }else{
                         alert(ocjudata.response1);
                     }
                 },
                 error: function(XMLHttpRequest, textStatus, errorThrown){
                     alert("failed");
                 }
             });
             
         })
                            
    	
    	
    	
    	
    	$(".groupPermissionSet").click(function(){
    		//console.log("groupPermissionSet");
    		var domvalue = $(this).attr("domvalue");
    		var domJson = eval("("+domvalue+")");
    		
    		// ajax call to get permissionSetSegment
	        $.ajax({
	            type: "GET",
	            url: "/getPermissionSetSegment",
	            cache: false,
	            data: "target=group&targetUuid="+domJson.group+"&targetOrg="+domJson.targetOrg,
	            dataType:"html",
	            success: function(pdata, textStatus){
	            	
	    			$.colorbox({
	    				width : 400,
	    				height : 600,
	    				html: pdata,
                        escKey: false,
	    				//inline: true, 
	    				//href: mediaUploadContainerContent,
	    				opacity: 0.4,
	    				overlayClose: false,
	    				onOpen:function(){},
	    				onLoad:function(){},
	    				onComplete:function(){
	    					// for tabs
/* 	    					
							$("#permissionTabs").tabs({
								select: function( event, ui ) {
									//console.log("tab select");
								},
								add: function( event, ui ) {
									//console.log("tab add");
								},
								create: function( event, ui ) {
									//console.log("tab create");
								},
								disable: function( event, ui ) {
									//console.log("tab disable");
								},
								enable: function( event, ui ) {
									//console.log("tab enable");
								},
								load: function( event, ui ) {
									//console.log("tab load");
								},
								remove: function( event, ui ) {
									//console.log("tab remove");
								},
								show: function( event, ui ) {
									//console.log("tab show");
									//xmlEditor.refresh();
									//jspEditor.refresh();
								}
								
							});
 */								
	    					
	    					
	    					
	    					// for permission tree:
	    					$("#permissionTree").jstree({ 
	    						"plugins" : [ "themes", "html_data", "ui", "crrm", "types" ],
								"themes" : {
									"theme" : "classic",
									"dots" : true,
									"icons" : true
								},
								"types" : {
									"types" : {
										
										"folder" :{
												"select_node" : function(){
													return false;
												}
										},
										"permission":{
												"select_node" : function(){
													return true;
												} 
										}
										
									}
								}
	    						
	    					})
							.bind("loaded.jstree", function (event, data) {
								$(this).jstree("open_all");
							})      
							.bind("create.jstree", function (event, data) {
								var parentNodeDomvalue = data.rslt.parent.attr("domvalue");
								var domJson = eval("("+parentNodeDomvalue+")");
								
								$.ajax({
									type: "GET",
									url: "/permissionNodeCreate",
									cache: false,
									data: "target="+domJson.target+"&targetId="+domJson.targetId+"&nodeName="+escape(data.rslt.name)+"&targetOrg=${org.orguuid}",
									dataType:"json",
									success: function(aData, textStatus){
										if(aData.success){
											$(data.rslt.obj).attr("id", aData.response1);		
											
										}else{
											$.jstree.rollback(data.rlbk);
											alert(aData.response1);
										}
									},
									error: function(XMLHttpRequest, textStatus, errorThrown){
										alert("system error, your result will be rollback!");
										$.jstree.rollback(data.rlbk);
									}
								});
								
							})
							.bind("remove.jstree", function (event, data) {
					            var nodeUuid = data.rslt.obj.attr("id");
					            // ajax call to delete the node
					            $.ajax({
					                type: "POST",
					                url: "/permissionNodeDelete",
					                cache: false,
					                data: "nodeId="+nodeUuid,
					                dataType:"json",
					                success: function(aData, textStatus){
					                    if(aData.success){
					                        $.colorbox.close();
					                    }else{
					                        $.jstree.rollback(data.rlbk);
					                    }
					                },
					                error: function(XMLHttpRequest, textStatus, errorThrown){
					                    alert("system error, your result will be rollback!");
					                    $.jstree.rollback(data.rlbk);
					                }
					            });
								
							})
							.bind("rename.jstree", function (event, data) {
							    
					            var currentNodeId = data.rslt.obj.attr("id");
					            var newName = data.rslt.new_name;
					            
                                $.ajax({
                                    type: "POST",
                                    url: "/updatePermissionValue",
                                    cache: false,
                                    data: "permissionUuid="+currentNodeId+"&updateValue="+newName+"&valueName=prettyname",
                                    dataType:"json",
                                    success: function(data, textStatus){
                                        if(data.success){
                                        }else{
                                            $.jstree.rollback(data.rlbk);
                                        }
                                    },
                                    error: function(XMLHttpRequest, textStatus, errorThrown){
                                        alert("name update failed, refresh the page and try again.");
                                        $.jstree.rollback(data.rlbk);
                                    }
                                });
							    
							})
							.bind("select_node.jstree", function(event, data){
								var permissionId = data.rslt.obj.attr("id");
								//console.log("select permission");
								//expand the colorbox, and append module & product tree for the group
								$.colorbox.resize({width:800, height:600});
								setPermissionSetEntityTree("${org.orguuid}", permissionId);
								
								
							})
							.bind("hover_node.jstree", function(event, data) {
								var nodeUuid = data.rslt.obj.attr("id");

								clearTimeout(treeNodeIconsTimeout);
								$("#permissionTree .nodeFunctionIcons").remove();
								
								data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
								$("#permissionTree .nodeFunctionIcons").bind("mouseenter", function(){
									clearTimeout(treeNodeIconsTimeout);
								})
								$("#permissionTree .nodeFunctionIcons").bind("mouseleave", function(){
									$(this).remove();
								})			

								
								// hide all icons first:
								$("#permissionTree .nodeFunctionIcons img").hide();
								
								// show icons and bind actions based on class name
								if(data.rslt.obj.hasClass("create")){
									$("#permissionTree .nodeFunctionIcons .permissionCreateIconImg").click(function(){
										//$("#permissionTree").jstree("rename", data.rslt.obj);
										$("#permissionTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"permission", "class":"rename delete config"}});
									})
									$("#permissionTree .nodeFunctionIcons .permissionCreateIconImg").show();
									
								}
								
								if(data.rslt.obj.hasClass("rename")){
									$("#permissionTree .nodeFunctionIcons .renameIconImg").click(function(){
										$("#permissionTree").jstree("rename", data.rslt.obj);
									})
									$("#permissionTree .nodeFunctionIcons .renameIconImg").show();
								}
								
                                if(data.rslt.obj.hasClass("delete")){
                                    $("#permissionTree .nodeFunctionIcons .deleteIconImg").click(function(){
                                        if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
                                            $("#permissionTree").jstree("remove", data.rslt.obj);
                                        }
                                    })
                                    $("#permissionTree .nodeFunctionIcons .deleteIconImg").show();
                                }
								
								
							})
							.bind("dehover_node.jstree", function(event, data) {
								treeNodeIconsTimeout=setTimeout(function(){
									data.rslt.obj.find(".nodeFunctionIcons").remove();
								},1000)
								
							})
	    					;	    						
	    					
	    				},
	    				onCleanup:function(){
	    				},
	    				onClosed:function(){}
	    				
	    			});
	    			
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){

	            }
	        });
    		
    		
    	})
    	
    })
    </script>


</body>
</html>
