<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Messages Page</title>

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
			<c:if test="${!empty msgs}">
				<table style="width: 100%;">
					<c:forEach items="${msgs}" var="msg" varStatus="msgIndex">
						<tr>
							<td>${msg.title}</td>
							<td style="width: 70px;"><a href="/delMsg?msgId=${msg.id}">Delete</a></td>
						</tr>
						<tr>
							<td style="text-align: right; color: gray;">
<%-- 							
								<fmt:formatDate value="${msg.createdate}" var="formattedDate" type="date" pattern="MM-dd-yyyy" />
								${formattedDate}
 --%>								
								<fmt:formatDate type="both" dateStyle="long" timeStyle="long" value="${msg.createdate}" />
							</td>
							<td></td>
						</tr>
					</c:forEach>
				</table>
				<div class="pagination">
					<c:if test="${!empty pagination && !empty pagination.paginationNodes}">
					    <p>pagination:</p>
					    <c:forEach items="${pagination.paginationNodes}" var="node" varStatus="nodeIdx">
					        <span class="page ${node.isCurrentNode()?'currentNode':''}">
					            <a title="${node.title}" href="${node.url}">${node.prettyName}</a>
					        </span>
					    </c:forEach>
					    <span>${pagination.extraInfo}</span>
					</c:if>				
				</div>
				
			</c:if>
		
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
		$(".ajaxProductPreview").colorbox({
            width : 800,
            height : 600,
            escKey: false,
            opacity: 0.4,
            overlayClose: false
		});
	})
</script>

</body>
</html>