<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="mediasContainer">
    <c:choose>
        <c:when test="${!empty medias}">
		    <c:forEach items="${medias}" var="media" varStatus="mediaIdx">
		        <div style="float: left; width: 100px; border: 1px solid gray; margin: 5px; padding: 5px; min-height: 130px; max-height: 130px; overflow: hidden; position: relative;" class="mediaDetail">
		            <div class="mediaBtns" style="float: right;">
		                <img alt="" src="/img/vendor/web-icons/tick-box.png" class="domReady_selectMediaForModule" domvalue="${media.mediauuid}">
		            </div>
		            <div class="thumbnail">
		                <img alt="${media.prettyname}" title="${media.prettyname}" src="/getphoto?id=${media.mediauuid}&size=100">
		            </div>
		            <div class="mediaInfo" style="white-space: nowrap; overflow: hidden; font-size: 13px; position: absolute; bottom: 2px;">
		                <span class="name" title="${media.prettyname}">${media.prettyname}</span>
		            </div>
		            
		        </div>
		    </c:forEach>
        </c:when>
        <c:otherwise>
            <span>No medias in this folder.</span>
        </c:otherwise>
    </c:choose>

</div>