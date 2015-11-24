<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Home Page</title>

	<script type="text/javascript" src="/js/brands/JQuery/jquery-1.7.2.js" language="javascript"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/js/jquery-ui-1.8.20.custom.min.js"></script>
	
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.cookie.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.hotkeys.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/ui/jquery.ui.position.js"></script>
	
	<script type="text/javascript" src="/js/brands/jsTree/jquery.jstree.js"></script>

	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>
 	
	<script type="text/javascript" src="/js/brands/jqueryContextMenu/src/jquery.contextMenu.js"></script>
	<link rel="stylesheet" href="/js/brands/jqueryContextMenu/src/jquery.contextMenu.css" type="text/css" media="screen" />
	
<!-- 	<script type="text/javascript" src="/js/brands/fineuploader.jquery-3.0/jquery.fineuploader-3.0.js"></script>
	<link rel="stylesheet" href="/js/brands/fineuploader.jquery-3.0/fineuploader.css" type="text/css" media="screen" />
 -->	
 
    <script type="text/javascript" src="/js/brands/dropzonejs/dropzone.js"></script>
    <link rel="stylesheet" type="text/css" href="/js/brands/dropzonejs/css/dropzone.css"/>
	
	
	
    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>

	<style type="text/css">
		.jstree-leaf[rel="default"] a> .jstree-icon{
			background-image:url(/js/brands/jsTree/file.png);
			background-position: 0 0;
		}		
	
		.jstree-leaf[rel="image"] a> .jstree-icon{
			background-image:url(/img/vendor/web-icons/image.png);
			background-position: 0 0;
		}		
	
		.jstree-leaf[rel="pdf"] a> .jstree-icon{
			background-image:url(/img/vendor/web-icons/document-pdf-text.png);
			background-position: 0 0;
		}





		.jstree-leaf[rel="css"] a> .jstree-icon{
			background-image:url(/img/vendor/web-icons2/led-icons/css.png);
			background-position: 0 0;
		}
		
		.jstree-leaf[rel="javascript"] a> .jstree-icon{
			background-image:url(/img/others/Jscript_icon_16x16.png);
			background-position: 0 0;
		}
		
		.jstree-leaf[rel="text"] a> .jstree-icon{
			background-image:url(/img/vendor/web-icons/document-text.png);
			background-position: 0 0;
		}
		






		
		.nodeFunctionIcons img {
			margin-bottom: -3px;
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
			<div id="mediaTreeSction" style="float: left;">
				<c:import url="mediaTree.jsp"/>
			</div>
<!-- 			
			<div id="productDetailSection" style="display: none; float: right;">
			</div>
 -->		
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

</body>
</html>
