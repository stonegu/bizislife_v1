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
	
	<link rel="stylesheet" type="text/css" href="/css/bizPageBuider.css"/>
	
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
	
	<style type="text/css">
		.ui-resizable-handle { position: absolute;font-size: 0.1px;z-index: 98; display: block;}
		.ui-resizable-n { cursor: n-resize; height: 7px; width: 100%; top: 0px; left: 0px; background: transparent url("/img/pagebuilder/resizableHandler_w_n.gif") no-repeat center}
		.ui-resizable-s { cursor: s-resize; height: 7px; width: 100%; bottom: 0px; left: 0px; background: transparent url("/img/pagebuilder/resizableHandler_w_s.gif") no-repeat center}
		.ui-resizable-e { cursor: e-resize; width: 7px; right: 0px; top: 0px; height: 100%; background: transparent url("/img/pagebuilder/resizableHandler_w_e.gif") no-repeat center}
		.ui-resizable-w { cursor: w-resize; width: 7px; left: 0px; top: 0px; height: 100%; background: transparent url("/img/pagebuilder/resizableHandler_w_w.gif") no-repeat center}
		.ui-resizable-se { cursor: se-resize; width: 12px; height: 12px; right: 1px; bottom: 1px; }
		.ui-resizable-sw { cursor: sw-resize; width: 9px; height: 9px; left: -5px; bottom: -5px; }
		.ui-resizable-nw { cursor: nw-resize; width: 9px; height: 9px; left: -5px; top: -5px; }
		.ui-resizable-ne { cursor: ne-resize; width: 9px; height: 9px; right: -5px; top: -5px;}	
		
		.CodeMirror {
		    border-bottom: 1px solid black;
		    border-top: 1px solid black;
		}		
		
		.pstab1_content .CodeMirror {
			height: 200px;
			width: 380px;
		}
		
		.nodeFunctionIcons img {
			margin-bottom: -3px;
		}
		
		#generalPageTree .jstree-leaf[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#generalPageTree .jstree-open[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#generalPageTree .jstree-closed[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		
		
<c:choose>
	<c:when test="${!empty containerHexcolors}">
		<c:forEach items="${containerHexcolors}" var="containerHexcolor" varStatus="containerHexcolorIdx">
			#generalPageTree .jstree-leaf a.hex_${containerHexcolor} > ins.jstree-icon {
				background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #${containerHexcolor};
				height: 15px;
			}
			#generalPageTree .jstree-open a.hex_${containerHexcolor} > ins.jstree-icon {
				background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #${containerHexcolor};
				height: 15px;
			}
			#generalPageTree .jstree-closed a.hex_${containerHexcolor} > ins.jstree-icon {
				background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #${containerHexcolor};
				height: 15px;
			}
		</c:forEach>
	</c:when>
	<c:otherwise>
		#generalPageTree .jstree-leaf[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
				
		#generalPageTree .jstree-open[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
				
		#generalPageTree .jstree-closed[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
	</c:otherwise>
</c:choose>
		
/* 		
		#generalPageTree .jstree-leaf[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
				
		#generalPageTree .jstree-open[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
				
		#generalPageTree .jstree-closed[rel="container"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-select-content.png");
		    background-position: 0 0;
		}
 */		
		
		
		
		
		
		
		
		
		
		#generalPageTree .jstree-leaf[rel="cmSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_module.png");
		    background-position: 0 0;
		}
		#generalPageTree .jstree-open[rel="cmSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_module.png");
		    background-position: 0 0;
		}
		#generalPageTree .jstree-closed[rel="cmSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_module.png");
		    background-position: 0 0;
		}
		#generalPageTree .jstree-leaf[rel="miSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_instance.png");
		    background-position: 0 0;
		}
		#generalPageTree .jstree-open[rel="miSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_instance.png");
		    background-position: 0 0;
		}
		#generalPageTree .jstree-closed[rel="miSched"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/calendar_instance.png");
		    background-position: 0 0;
		}
		
		#moduleTreeForSched .jstree-closed[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForSched .jstree-open[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForSched .jstree-leaf[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
	
		#moduleTreeForSched .jstree-leaf[rel="productModule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_green_16x16.png);
			background-position: 0 0;
		}
		
		#instanceTreeForSched .jstree-closed[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#instanceTreeForSched .jstree-open[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#instanceTreeForSched .jstree-leaf[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#instanceTreeForSched .jstree-closed[rel="instance"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/document-binary.png);
			background-position: 0 0;
		}		
		#instanceTreeForSched .jstree-open[rel="instance"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/document-binary.png);
			background-position: 0 0;
		}		
		#instanceTreeForSched .jstree-leaf[rel="instance"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/document-binary.png);
			background-position: 0 0;
		}		
		
		
		
		
		#moduleTreeForContainerDefaultModule .jstree-closed[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForContainerDefaultModule .jstree-open[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
		#moduleTreeForContainerDefaultModule .jstree-leaf[rel="module"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_blue_16x16.png);
			background-position: 0 0;
		}		
	
		#moduleTreeForContainerDefaultModule .jstree-leaf[rel="productModule"] a> ins.jstree-icon{
			background-image:url(/img/vendor/web-icons/puzzle_green_16x16.png);
			background-position: 0 0;
		}
		
		
		
		
		#orgsSharedPagesTree .jstree-leaf[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		#orgsSharedPagesTree .jstree-open[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		#orgsSharedPagesTree .jstree-closed[rel="org"] > a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/building-hedge.png");
		    background-position: 0 0;
		}		
		
		#orgsSharedPagesTree .jstree-leaf[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#orgsSharedPagesTree .jstree-open[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
		}		
		#orgsSharedPagesTree .jstree-closed[rel="page"] a > ins.jstree-icon {
		    background-image: url("/img/vendor/web-icons/layout-hf-3-mix.png");
		    background-position: 0 0;
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
			<div id="pageTreeSction" style="float: left;">
				<c:import url="generalPageTree.jsp"/>
			</div>
			
			<div id="pageDetailSection" style="display: none; float: right;">
			</div>
		
		</div>
		
		<div class="pageright" style="width: 25%; float: right; background-color: #EEEEEE;">
			<c:import url="orgsSharedPages.jsp" />
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
    <script src="/javasecript/bizPb.min.js" type="text/javascript" language="javascript"></script>
    <script src="/javasecript/psed.min.js" type="text/javascript" language="javascript"></script>

</body>
</html>
