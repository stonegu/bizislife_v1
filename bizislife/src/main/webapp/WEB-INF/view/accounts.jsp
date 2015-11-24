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
				<a href="/addAccount?org=${orguuid}">+Add Account</a>
			</c:if>
			
			<table style="width: 98%;">
				<thead style="background-color: gray; color: white;">
					<tr>
						<td style="width: 20%;">Login</td>
						<td style="width: 20%;">Name</td>
						<td style="width: 30%;">Group</td>
						<td style="width: 15%;">Permission</td>
						<td style="width: 15%;">Actions</td>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach items="${accounts}" var="account" varStatus="accountIndex">
					    <c:if test="${empty account.suspenddate}">
	                        <tr class="${accountIndex.count%2==0?'lineEven':'lineOdd'}">
	                            <c:choose>
	                                <c:when test="${(!empty loginAccount && loginAccount.isSystemDefaultAccount()) || (loginAccount.accountuuid eq account.accountuuid)}">
	                                    <td id="acctsloginname_${account.accountuuid}"><a href="/getAcctInfo?acct=${account.accountuuid}">${account.loginname}</a></td>
	                                </c:when>
	                                <c:otherwise>
	                                    <td id="acctsloginname_${account.accountuuid}">${account.loginname}</td>
	                                </c:otherwise>
	                            </c:choose>
	                            
	                            <td>${account.firstname} ${account.lastname}</td>
	                            <td>
	                                <c:if test="${!empty account.accountGroups}">
	                                    <c:forEach items="${account.accountGroups}" var="group" varStatus="groupIdx">
	                                        <c:if test="${groupIdx.count>1}">,&nbsp;</c:if>
	                                        ${group.groupname}
	                                    </c:forEach>
	                                </c:if>
	                            </td>
	                            <td>
	                                ...
	                            </td>
	                            <td id="acctsActions_${account.accountuuid}">
	                                <c:choose>
	                                    <c:when test="${!empty loginAccount && loginAccount.isSystemDefaultAccount()}">
	                                        <c:choose>
	                                            <c:when test="${!empty account.activatedate}">
	                                                <a href="/accountDeactivate?acct=${account.accountuuid}"><img src="/img/vendor/web-icons/light-bulb.png" alt="click to deactivate account" class="accountDeactivate" title="click to deactivate account"></a>
	                                            </c:when>
	                                            <c:otherwise>
	                                                <a href="/accountActivate?acct=${account.accountuuid}"><img src="/img/vendor/web-icons/light-bulb-off.png" alt="click to activate account" class="accountActivate" title="click to activate account"></a>
	                                            </c:otherwise>
	                                        </c:choose>
	                                        
	                                        <a href="/getAcctInfo?acct=${account.accountuuid}"><img src="/img/vendor/web-icons/gear.png" alt="modify" class="accountEdit" title="modify"></a>
	                                        <img src="/img/vendor/web-icons/lock.png" alt="permission setup" title="permission setup" class="accountPermissionSet" domvalue="${account.accountuuid}">
	                                        <img src="/img/vendor/web-icons/cross-circle-frame.png" alt="delete" class="accountDelete" domvalue="${account.accountuuid}" title="delete">
	                                    </c:when>
	                                    <c:otherwise>
	                                        <img src="/img/vendor/web-icons/trans_16x16.png">
	                                        <c:choose>
	                                            <c:when test="${loginAccount.accountuuid eq account.accountuuid}">
	                                                <a href="/getAcctInfo?acct=${account.accountuuid}"><img src="/img/vendor/web-icons/gear.png" alt="modify" class="accountEdit" title="modify"></a>
	                                            </c:when>
	                                            <c:otherwise>
	                                                <img src="/img/vendor/web-icons/trans_16x16.png">
	                                            </c:otherwise>
	                                        </c:choose>
	                                        <img src="/img/vendor/web-icons/trans_16x16.png">
	                                        <img src="/img/vendor/web-icons/trans_16x16.png">
	                                    </c:otherwise>
	                                </c:choose>
	                            
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
	// time value for treeNodeFunctionIcons
	var treeNodeIconsTimeout;

    $(function() {
    	$(".accountPermissionSet").click(function(){
    		var domvalue = $(this).attr("domvalue");
    		
    		
    		// ajax call to get permissionSetSegment
	        $.ajax({
	            type: "GET",
	            url: "/getPermissionSetSegment",
	            cache: false,
	            data: "target=account&targetUuid="+domvalue,
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
	    					//$( "#tabs" ).tabs();
	    					
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
								
								var postValue = {"target" : domJson.target, "targetId" : domJson.targetId, "nodeName" : data.rslt.name, "targetOrg" : "${orguuid}"};
								$.ajax({
									type: "POST",
									url: "/permissionNodeCreate",
									cache: false,
									data: postValue,
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
								//expand the colorbox, and append module & product tree for the account
								$.colorbox.resize({width:800, height:600});
								
								setPermissionSetEntityTree("${orguuid}", permissionId);
								
								
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
    		
    	});
    	
    	$(".accountDelete").click(function(){
    	    
    	    if(confirm("Are you sure you like to delete this account?")){
                
                var acctId = $(this).attr("domvalue");
                
                $.ajax({
                    type: "POST",
                    url: "/delAccount",
                    cache: false,
                    data: "acctId="+acctId,
                    dataType:"json",
                    success: function(data, textStatus){
                        if(data.success){
                            $("#acctsloginname_"+acctId).fadeTo( "slow", 0.5, function() {
                                $("#acctsloginname_"+acctId+" a").css( "text-decoration", "line-through" );
                            });
                            $("#acctsActions_"+acctId).fadeTo( "slow", 0.1, function() {
                                $(this).html("");
                            });
                        }else{
                            alert(data.response1);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        alert("failed");
               
                    }
                });
    	        
    	    }
    	    
    	});
    	
    })
    </script>


</body>
</html>
