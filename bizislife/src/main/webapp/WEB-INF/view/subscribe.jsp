<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Home Page</title>

	<script src="/js/brands/JQuery/jquery-1.7.2.js" type="text/javascript" language="javascript"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.cookie.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.hotkeys.js"></script>
	
	<script type="text/javascript" src="/js/brands/jsTree/jquery.jstree.js"></script>

	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>

    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>
    
	<style type="text/css">
		.nodeFunctionIcons img {
			margin-bottom: -3px;
		}
		
		.topictitle {
			font-weight: bold;
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
		
			<p>Note: click 'checkbox' to select current node and it's subnodes; click node name to select current node only.</p>
		
			<input type="button" value="Collapse All" onclick="$('#subscribeTree').jstree('close_all');">
			<input type="button" value="Expand All" onclick="$('#subscribeTree').jstree('open_all');">
		
			<div id="subscribeTree">
			</div>
		
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


	<script type="text/javascript">
	
	$(function () {
		var treeNodeIconsTimeout;
		
		$("#subscribeTree").jstree({
			"plugins" : ["themes", "json_data", "checkbox", "ui", "types"],
			"json_data" : { 
				"ajax" : {
					"data" : function(node){
						if(node==-1){
							return "operation=get_topicTree&org=${orgUuid}";
						}else{
							return "operation=get_topicTree&org=${orgUuid}&parentNodeId="+node.attr("id");
						}
					}, 
					"url":function (node){
						//console.log("node: "+node);
						return "/topicTreeMain";
					},
					"success": function (data) {
	                    //return new_data;
	                    //console.log("success");
	                }
				}
			},
			"core" : {
				"html_titles" : true
			},
			"types" : {
 				"types" : {
					"noCheckable" : {
						"check_node" : false,
						"uncheck_node" : false
					} 
 				}
			},
			"themes" : {
				"theme" : "classic",
				"dots" : true,
				"icons" : false
			},
			"checkbox" : {
				"override_ui" : false,
				"two_state" : true,
				"real_checkboxes" : true
			}
			
			
		})
		.bind("loaded.jstree", function (event, data) {
			//console.log("loaded!!!!!!!!!!!!");
			$("a.noCheckable ins.jstree-checkbox").hide();
		})
		.bind("open_node.jstree", function(event, data){
			//console.log("open_node!!!!!");
			$("a.noCheckable ins.jstree-checkbox").hide();
		})
		.bind("open_all.jstree", function(event, data){
//			console.log("open all .............: "+data.rslt.obj.attr("id"));

		})
		.bind("check_node.jstree", function(event, data){
			//console.log("check_node: ");
			//console.log('ID of parent node:'+(data.inst._get_parent(data.rslt.obj)==-1?'checked node is root!':data.inst._get_parent(data.rslt.obj).attr('id')));
			//console.log(data.inst.get_path(data.rslt.obj));
			//console.log(data.rslt.obj.parents("li[foldopenafterload]"));
			//console.log("check node: "+data.rslt.obj.attr("id"));
			
			// ajax call to reset serverside data
	        $.ajax({
	            type: "GET",
	            url: "/saveSubscribe",
	            cache: false,
	            data: "topicUuid="+data.rslt.obj.attr('id')+"&type=subscribe",
	            dataType:"json",
	            success: function(data, textStatus){
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){
	                alert("failed");

	            }
	        });
			
			if(data.rslt.obj.hasClass('jstree-closed')){
				$("#subscribeTree").jstree("open_node", 
					data.rslt.obj, function() {
						var childrentContainer = data.rslt.obj.children('ul');
						if(childrentContainer){
							// only check the first level child nodes
							childrentContainer.children('li').each(function(i){
								$.jstree._reference('#subscribeTree').check_node(this);
							});
						}					
					}, false);
			}else if(data.rslt.obj.hasClass('jstree-open')){
				var childrentContainer = data.rslt.obj.children('ul');
				if(childrentContainer){
					// only check the first level child nodes
					childrentContainer.children('li').each(function(i){
						//console.log( 'ID child('+i+'):'+$(this).attr('id'));
						$.jstree._reference('#subscribeTree').check_node(this);
					});
				}
			}
		})
		.bind("uncheck_node.jstree", function(event, data){
			//console.log("uncheck node: "+data.rslt.obj.attr("id"));
			// ajax call to reset serverside data
	        $.ajax({
	            type: "GET",
	            url: "/saveSubscribe",
	            cache: false,
	            data: "topicUuid="+data.rslt.obj.attr('id')+"&type=unsubscribe",
	            dataType:"json",
	            success: function(data, textStatus){
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){
	                alert("failed");

	            }
	        });
			
			if(data.rslt.obj.hasClass('jstree-closed')){
				$("#subscribeTree").jstree("open_node", 
					data.rslt.obj, function() {
						var childrentContainer = data.rslt.obj.children('ul');
						if(childrentContainer){
							// only check the first level child nodes
							childrentContainer.children('li').each(function(i){
								$.jstree._reference('#subscribeTree').uncheck_node(this);
							});
						}					
					}, false);
			}else if(data.rslt.obj.hasClass('jstree-open')){
				var childrentContainer = data.rslt.obj.children('ul');
				if(childrentContainer){
					// only check the first level child nodes
					childrentContainer.children('li').each(function(i){
						//console.log( 'ID child('+i+'):'+$(this).attr('id'));
						$.jstree._reference('#subscribeTree').uncheck_node(this);
					});
				}
			}
			
		})
		.bind("select_node.jstree", function(event, data){
			//console.log("select topic tree ...");
            var nodeUuid = data.rslt.obj.attr("id");
			
                $.colorbox({
                    width : 500,
                    height : 300,
                    href: "/getTopicInfo?targetId="+nodeUuid,
                    escKey: false,
                    opacity: 0.4,
                    overlayClose: false
                });
			
			
		})
		.bind("hover_node.jstree", function(event, data) {
			
			var nodeUuid = data.rslt.obj.attr("id");

			clearTimeout(treeNodeIconsTimeout);
			$("#subscribeTree .nodeFunctionIcons").remove();

			data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
			$("#subscribeTree .nodeFunctionIcons").bind("mouseenter", function(){
				clearTimeout(treeNodeIconsTimeout);
			})
			$("#subscribeTree .nodeFunctionIcons").bind("mouseleave", function(){
				$(this).remove();
			})

			
			// hide all icons first:
			$("#subscribeTree .nodeFunctionIcons img").hide();
			
			if(data.rslt.obj.hasClass("toConfig")){
				$("#subscribeTree .nodeFunctionIcons .configIconImgGear").click(function(){
					
					$("#subscribeTree").jstree("deselect_all");
					$("#subscribeTree").jstree("select_node", data.rslt.obj);
					
				})
				$("#subscribeTree .nodeFunctionIcons .configIconImgGear").show();
			}
			
		})
		.bind("dehover_node.jstree", function(event, data) {
			treeNodeIconsTimeout=setTimeout(function(){
				data.rslt.obj.find(".nodeFunctionIcons").remove();
			},1000)
			
		})

		
		$(".subscribeCommitBtn").click(function(){
			//var checked_ids = [];
			var checked_uuids = [];
			$("#subscribeTree").jstree("get_checked",null,true).each(function(){
				//checked_ids.push(this.id);
				checked_uuids.push($(this).attr("id"));
			})
			//console.log(checked_routes);
			// ajax call to save subscribes
	        $.ajax({
	            type: "GET",
	            url: "/saveSubscribes",
	            cache: false,
	            data: "topicUuids="+checked_uuids,
	            dataType:"json",
	            success: function(data, textStatus){
	            	var newSubscribedTopics = "";
	            	var deletedSubscribeTopics = "";
	            	if(data){
/* 	            		
	            		if(data.success){
		            		$(data.response1.saved).each(function(){
		            			newSubscribedTopics = newSubscribedTopics + "\n\t" + this;
		            		})
		            		$(data.response1.deleted).each(function(){
		            			deletedSubscribeTopics = deletedSubscribeTopics + "\n\t" + this;
		            		})
		            		alert("New subscribes: " + newSubscribedTopics + "\nDeleted subscribes: " +  deletedSubscribeTopics);
	            		}else{
	            			alert("Save failed")
	            		}
 */	            		
	            	}else{
	            		alert("No any changes");
	            	}
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){
	                alert("failed");

	            }
	        });
			
			
		})
		
	})
	
	</script>


</body>
</html>
