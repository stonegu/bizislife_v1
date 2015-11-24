<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>${pageMeta.title}</title>

${pageHead}

<!-- container css from page design:  -->
<style>

<c:choose>
	<c:when test="${(empty pageMeta.defaultcss) || (pageMeta.defaultcss eq '1')}">
		html { height:100%;} 
		body { 
		  min-height:100%; 
		  height:100%;
		  margin:0;
		  padding:0;
		}
		
		.containerSection {height: 100%;}
		
			#ct_${container.systemName}{
				background-color: #${container.hexColor};
				float: left;
				width: 100%;
				height: 100%;
			}
			<c:set var="cssContainer" value="${container}" scope="request"/>
			<c:set var="pageMeta" value="${pageMeta}" scope="request"/>
			<c:import url="mobileContainerCss.jsp" />
	
	</c:when>
	
	<c:when test="${pageMeta.defaultcss eq '2'}">
		html { height:100%;} 
		body { 
		  min-height:100%; 
		  height:100%;
		  margin:0;
		  padding:0;
		}
		
		.containerSection {height: 100%;}
		
			#ct_${container.systemName}{
				float: left;
				width: 100%;
				height: 100%;
			}
			<c:set var="cssContainer" value="${container}" scope="request"/>
			<c:set var="pageMeta" value="${pageMeta}" scope="request"/>
			<c:import url="mobileContainerCss.jsp" />
	
	</c:when>
</c:choose>
















</style>

<!-- module customized css -->
<c:if test="${!empty containerContentInfoMap}">
	<c:forEach items="${containerContentInfoMap}" var="containerContentInfo" varStatus="containerContentInfoIdx">
		<c:if test="${!empty containerContentInfo.value && !empty containerContentInfo.value['modulecss']}">
			<link rel="stylesheet" type="text/css" href="/css${containerContentInfo.value['modulecss']}"/>
		</c:if>
	</c:forEach>
</c:if>

<!-- instance view's customized css -->
<c:if test="${!empty containerContentInfoMap}">
	<c:forEach items="${containerContentInfoMap}" var="containerContentInfo" varStatus="containerContentInfoIdx">
		<c:if test="${!empty containerContentInfo.value && !empty containerContentInfo.value['viewcss']}">
			<link rel="stylesheet" type="text/css" href="/css${containerContentInfo.value['viewcss']}"/>
		</c:if>
	</c:forEach>
</c:if>

<!-- page customized css -->
<c:if test="${pageMeta!=null && !empty pageMeta.css}">
	<link rel="stylesheet" type="text/css" href="/css/org/${org.id}/${pageMeta.pageuuid}.css"/>
</c:if>



</head>
<body>
	<c:choose>
		<c:when test="${!empty errorMsgs}">
			<!-- error msg section -->
			<div class="errorMsgSection">
				<c:forEach items="${errorMsgs}" var="msg" varStatus="msgIndex">
					<span style="color: red;">${msg}</span><br/>
				</c:forEach>
			</div>
		</c:when>
		<c:otherwise>
			<c:if test="${container!=null}">
				<div class="containerSection">
					<c:import url="mobileContainerDetail.jsp" />		
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>

</body>

</html>