<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<li id="${attr.uuid}" class="attrgrid">
	<div>
		<div class="gridTitle"><span class="attrTitle">${attr.title }</span><span class="actionIcons"><img title="click for detail information" class="${attr.type.typeCode=='image'?'domReady_attrDetailInfoImg':'domReady_attrDetailInfo' }" domvalue="${attr.toJson()}" src="/img/vendor/web-icons/external.png" /><img class="moveHandler" title="drag me there!" src="/img/vendor/web-icons/arrow-move.png" /></span></div>
		<c:choose>
			<c:when test="${attr.type.typeCode eq 'image'}">
				<div class="attrValue">
					<img src='/getphoto?id=${attr.value}&size=200'/>
				</div>
			</c:when>
			<c:otherwise>
				<div class="attrValue">${attr.value}</div>
			</c:otherwise>
		</c:choose>
		
		<div class="gridStatus"><span class="actionIcons"><img title="delete" class="domReady_attrDelete" domvalue="${attr.uuid}" src="/img/vendor/web-icons/bin.png" /></span></div>
	</div>
</li>
