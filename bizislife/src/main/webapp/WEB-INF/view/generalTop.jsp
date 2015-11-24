<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>
	<div class="logo" style="width: 30%; float: left; margin: 0 0 0 10px;">
		<a href="http://www.bizislife.com"><span style="color: white; font-size: 25px; font-style: italic; font-weight: bold; text-decoration: none;">BizIsLife</span></a>
	</div>
	
	<div class="generalTopFuncs" style="width: 50%; float: right; text-align: right; margin: 0 10px 0 0; line-height: 30px;">
	
		<c:choose>
			<c:when test="${!empty loginAccount}">
				<span>Welcome: ${loginAccount.firstname} ${loginAccount.lastname}</span>
				<a href="/logout" style="color: red;">Logout</a>
			</c:when>
			<c:otherwise>
				<form action="/login" method="post" name="loginForm">
					<span><input type="text" name="username" placeholder="type your login name" title="type your login name"/></span>
					<span><input type="password" name="password" placeholder="type your password" title="type your password" /></span>
					<input type="submit" name="loginFormSubmit" value="Login" /> <span style="display: none;"><a href="/forgotPassword">Forgot password?</a></span>
				</form>
				
				<div class="retrivePwdContainer" style="display: none;">
					<p>Forgot your password?</p>
					<p>Type your email address to retrive your new password</p>
					<p>Email: <input type="text" name="emailForPwd" /></p>
					<p><input type="submit" name="retrievePwd" value="Retrieve"/></p>
				</div>
				
			</c:otherwise>
		
		</c:choose>
	
	</div>
</div>
<c:import url="globalPageElement.jsp"/>