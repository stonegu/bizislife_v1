<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>${previewTitle}</title>

${pageHead}


<!-- module customized css -->
<c:if test="${!empty contentInfoMap && !empty contentInfoMap['modulecss']}">
    <link rel="stylesheet" type="text/css" href="http://${bizhost}/css${contentInfoMap['modulecss']}"/>
</c:if>

<!-- instance view's customized css -->
<c:if test="${!empty contentInfoMap && !empty contentInfoMap['viewcss']}">
    <link rel="stylesheet" type="text/css" href="http://${bizhost}/css${contentInfoMap['viewcss']}"/>
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
            <div class="containerSection">
                <c:import url="page/desktopContainerDetailPreview.jsp" />       
            </div>
        </c:otherwise>
    </c:choose>

</body>

</html>