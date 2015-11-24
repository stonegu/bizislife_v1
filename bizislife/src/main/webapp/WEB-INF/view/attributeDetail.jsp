<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="attributeDetail">
	<div class="attrContent" id="attrContentId">
		<p><strong>
			<c:choose>
				<c:when test="${attrType eq 'RealAttribute'}">
					Number Attribute
				</c:when>
				<c:when test="${attrType eq 'StringAttribute'}">
					Text Attribute
				</c:when>
			</c:choose>
		</strong></p>
		<form action="#" name="saveAttrForm">
			<input type="hidden" name="productId" value="${productUuid}">
			<input type="hidden" name="attrType" value="${attrType}">
			<c:if test="${!empty attribute}">
				<input type="hidden" name="attrId" value="${attribute.uuid}">
			</c:if>
			
			<input type="text" name="attrName" value="${attribute!=null?attribute.title:''}">
			<br/>
			
			<c:choose>
				<c:when test="${attrType eq 'RealAttribute'}">
					<input type="text" name="attrValue" class="RealAttribute" value="${attribute!=null?attribute.value:''}">
				</c:when>
				<c:when test="${attrType eq 'StringAttribute'}">
					<input type="text" name="attrValue" class="StringAttribute" value="${attribute!=null?attribute.value:''}">
				</c:when>
			</c:choose>		
			<div>
				<c:choose>
					<c:when test="${!empty attribute}">
						<button type="button" class="domReady_attrUpdate">Save</button>
					</c:when>
					<c:otherwise>
						<button type="button" class="domReady_newAttr">Save</button>
					</c:otherwise>
				</c:choose>
				<button type="button" onclick="$.colorbox.close();">Cancel</button>
			</div>		
			
		</form>
	
	</div>
</div>
