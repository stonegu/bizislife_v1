<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Home Page</title>

	<link rel="stylesheet" type="text/css" href="/js/brands/JQuery/jquery-ui-1.8.20/css/ui-lightness/jquery-ui-1.8.20.custom.css"/>

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
	
	<script type="text/javascript" src="/js/brands/jscolor/jscolor.js"></script>
	
	
	<!-- js & css for codemirror -->
	<link rel="stylesheet" href="/js/brands/codemirror-3.1/lib/codemirror.css">
	<script src="/js/brands/codemirror-3.1/lib/codemirror.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/xml/xml.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/javascript/javascript.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/css/css.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/vbscript/vbscript.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/htmlmixed/htmlmixed.js"></script>
	<script src="/js/brands/codemirror-3.1/mode/htmlembedded/htmlembedded.js"></script>
	<script src="/js/brands/codemirror-3.1/addon/edit/closetag.js"></script>
	
    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>
    <link rel="stylesheet" type="text/css" href="/css/bizModule.css"/>
    <link rel="stylesheet" type="text/css" href="/css/productAttrGrid.css"/>

	<style type="text/css">
	
		.CodeMirror {
			border-top: 1px solid black; 
			border-bottom: 1px solid black;
		}
	
		/* begin css for image attr selection */
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
		


		#productTree .jstree-closed[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-open[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-leaf[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-closed[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#productTree .jstree-open[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#productTree .jstree-leaf[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}

		#productTree .jstree-closed[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-open[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-leaf[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}

		#productTree .jstree-closed[rel="schedule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/calendar.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-open[rel="schedule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/calendar.png);
			background-position: 0 0;
		}
		
		#productTree .jstree-leaf[rel="schedule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/calendar.png);
			background-position: 0 0;
		}
		
		#instanceViewsTreeForEntity .jstree-leaf[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#instanceViewsTreeForEntity .jstree-open[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#instanceViewsTreeForEntity .jstree-closed[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		
		#instanceViewsTreeForEntity .jstree-closed[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}
		#instanceViewsTreeForEntity .jstree-open[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}
		#instanceViewsTreeForEntity .jstree-leaf[rel="instanceView"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/edit-image-right.png);
			background-position: 0 0;
		}
		
		#instanceViewsTreeForEntity .jstree-leaf[rel="schedule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/calendar.png);
			background-position: 0 0;
		}

		.nodeFunctionIcons img {
			margin-bottom: -3px;
		}

		
		.jstree-leaf.noSelectable{
			-moz-opacity:.50; -ms-filter:alpha(opacity=50); filter:alpha(opacity=50); opacity:.50;
		}
		
		#imgAttrDetail{
			width: 100%;
		}
		#imageTree{
			width: 50%;
			float: left;
			background-color: #F9F9F9;
		}
		.imgAttrForm{
			width: 50%;
			float: right;
		}
				
		/* end css for image attr selection */
		
		
		#moduleProductTree .jstree-closed[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleProductTree .jstree-open[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleProductTree .jstree-leaf[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
	
		#moduleProductTree .jstree-leaf[rel="productModule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_green_16x16.png);
			background-position: 0 0;
		}
		
		
		
		
		
		/* for share product tree */
		
		#orgsSharedProductsTree .jstree-leaf[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		#orgsSharedProductsTree .jstree-open[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		#orgsSharedProductsTree .jstree-closed[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		
		#orgsSharedProductsTree .jstree-closed[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}		
		#orgsSharedProductsTree .jstree-open[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}		
		#orgsSharedProductsTree .jstree-leaf[rel="folder"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/category_16x16.png);
			background-position: 0 0;
		}		

		#orgsSharedProductsTree .jstree-closed[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#orgsSharedProductsTree .jstree-open[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}
		#orgsSharedProductsTree .jstree-leaf[rel="default"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/present.png);
			background-position: 0 0;
		}


		
		/* end for share product tree */
/* 		
		.pagecenter .infoSection {
		  background-color: gray;
		}
 */		
		
		
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
			<div class="infoSection">
				<c:import url="modulesInfo.jsp" />
			</div>
			
			<div id="productTreeSction" style="float: left;">
				<c:import url="productTree.jsp"/>
			</div>
			
			<div id="productDetailSection" style="display: none; float: right;">
			</div>
		
		</div>
		
		<div class="pageright" style="width: 25%; float: right; background-color: #EEEEEE;">
			<c:import url="orgsSharedProducts.jsp" />
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
	<script src="/js/productAttrGrid.js" type="text/javascript" language="javascript"></script>

</body>
</html>
