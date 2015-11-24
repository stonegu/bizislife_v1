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
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true&libraries=places"></script>

<%--<script src="/js/jquery.ui.core.js" type="text/javascript" language="javascript"></script>--%>
<%--<script src="/js/jquery.ui.tabs.js" type="text/javascript" language="javascript"></script>--%>

	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>

    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>

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
			<c:import url="accountInfoFragment_edit.jsp" />
		</div>
		
		<div class="pageright" style="width: 25%; float: right; background-color: #EEEEEE;">
			<p>this is the right section</p>
		</div>
	
	</div>
	
	<div class="pagebottom">
		<c:import url="generalBottom.jsp"/>
	</div>

	

    <script src="/javasecript/bgl.min.js" type="text/javascript" language="javascript"></script>
    
    <script type="text/javascript">
    $(function () {
    	$(".toggleGroupSelect").live("click", function(){
        	var currentObj = $(this);
        	var domvalue = currentObj.attr("domvalue");
            var jsonObj = eval("("+domvalue+")");

    		//domvalue="{'account':'${account.accountuuid}', 'group':'${group.id}', 'type':'unjoinGroup_outside', 'websectionid':'account_groups'}"
    		var canProcess = false;
    		if(jsonObj.type=="unjoinGroup_outside"){
    			if(confirm("You are trying to unjoin the group that isn't belong to your organization, you will not be able to re-join the group without the invitation.\nAre you like to un-join?")){
    				canProcess = true;
    			}
    		}else {
    			canProcess = true;
    		}
    		
    		if(canProcess){
        		$.ajax({
        			type: "GET",
        			url: "/toggleGroupJoin",
        			cache: false,
        		    data: "type="+jsonObj.type+"&account="+jsonObj.account+"&group="+jsonObj.group,
        			dataType:"json",
        			success: function(tgjData, textStatus){
        				if(tgjData.success){
        					
        					if(jsonObj.type=="joinGroup"){
            					if(tgjData.response1.grouptype=="Everyone"){
                					currentObj.attr("src", "/img/vendor/web-icons/tick_gray.png");
                					currentObj.attr("alt", "joined everyone group");
                					currentObj.attr("title", "joined everyone group");
            					}else{
                					currentObj.attr("src", "/img/vendor/web-icons/tick.png");
                					currentObj.attr("alt", "click to un-join the group");
                					currentObj.attr("title", "click to un-join the group");
            					}
            					
            					var newDomvalue = "";
            					if(tgjData.response1.organization_id==${account.organization_id}){
                					newDomvalue = "{'account':'"+jsonObj.account+"', 'group':'"+jsonObj.group+"', 'type':'unjoinGroup', 'websectionid':'account_groups'}"
            					}else{
            						newDomvalue = "{'account':'"+jsonObj.account+"', 'group':'"+jsonObj.group+"', 'type':'unjoinGroup_outside', 'websectionid':'account_groups'}"
            					}
            					currentObj.attr("domvalue", newDomvalue);
        						
        					}else if(jsonObj.type=="unjoinGroup"){
        						if(tgjData.response1.organization_id==${account.organization_id}){
                					currentObj.attr("src", "/img/vendor/web-icons/question-white.png");
                					currentObj.attr("alt", "click to join the group");
                					currentObj.attr("title", "click to join the group");
                					var newDomvalue = "{'account':'"+jsonObj.account+"', 'group':'"+jsonObj.group+"', 'type':'joinGroup', 'websectionid':'account_groups'}"
                					currentObj.attr("domvalue", newDomvalue);
        							
        						}else{
        							// remove whole line
        							currentObj.parentsUntil("tbody").remove();
        						}
        						
        					}else if(jsonObj.type=="unjoinGroup_outside"){
        						// duplicated codes from else if(jsonObj.type=="unjoinGroup")
        						if(tgjData.response1.organization_id==${account.organization_id}){
                					currentObj.attr("src", "/img/vendor/web-icons/question-white.png");
                					currentObj.attr("alt", "click to join the group");
                					currentObj.attr("title", "click to join the group");
                					var newDomvalue = "{'account':'"+jsonObj.account+"', 'group':'"+jsonObj.group+"', 'type':'joinGroup', 'websectionid':'account_groups'}"
                					currentObj.attr("domvalue", newDomvalue);
        							
        						}else{
        							// remove whole line
        							currentObj.parentsUntil("tbody").remove();
        						}
        						
        					}
        					
        					// update the group value        					
        	        		$.ajax({
        	        			type: "GET",
        	        			url: "/getAccountGroupInfo",
        	        			cache: false,
        	        		    data: "account="+jsonObj.account,
        	        			dataType:"html",
        	        			success: function(agiData, textStatus){
        	        				// update the value
        	    					$("#"+jsonObj.websectionid).find("div.value").html(agiData);
        	        				
        	        			},
        	        			error: function(XMLHttpRequest, textStatus, errorThrown){
        	        				alert("failed");
        	        			}
        	        		});
        				}
        				
        			},
        			error: function(XMLHttpRequest, textStatus, errorThrown){
        				alert("failed");
        			}
        		});


    		}
    		
    	})
    	
    })
    </script>

</body>
</html>