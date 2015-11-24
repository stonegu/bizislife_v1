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
		
		
			<h1>File Upload with Jersey</h1>
		 
			<form action="http://localhost:8090/rest/file/upload" method="post" enctype="multipart/form-data">
		 
			   <p>
				Select a file : <input type="file" name="file" size="45" />
			   </p>
		 
			   <input type="submit" value="Upload It" />
			</form>
			
			
			<h1>File Upload with RestfulTemplate</h1>
			
			<form action="/uploadImg3" method="post" enctype="multipart/form-data" name="uploadImage">
				<p>name: <input type="text" name="imageName" value="adfasdfa" /></p>
				<p>
				Select a image file : <input type="file" name="img"/>
				</p>
			   <input type="submit" value="Upload It" />
				
			</form>
			
		
			<h1>File Upload with httpPost</h1>
			
			<form action="/uploadImgByHttpPost" method="post" enctype="multipart/form-data" name="uploadImage">
<!-- 				<p>name: <input type="text" name="imageName[0]" value="adfasdfa" /></p>
 -->
 				<p>
				Select a image file : <input type="file" name="files"/>
				</p>
				<p>
				Select a image file : <input type="file" name="files"/>
				</p>
				<p>
				Select a image file : <input type="file" name="files"/>
				</p>
				<p>
				Select a image file : <input type="file" name="files"/>
				</p>
			   <input type="submit" value="Upload It" />
				
			</form>
			
		
		
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
