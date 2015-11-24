<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="pageDetailInfo">
	<form action="/pageDetailUpdate" method="post" name="/ageDetailUpdate" id="pageDetailUpdate">

		<input type="hidden" name="pageId" id="pageId" value="${pageDetail.pageuuid}">
		<div id="tabs">
			<ul>
			<li><a href="#tabs-1">Page Detail</a></li>
			<li><a href="#tabs-2">CSS</a></li>
			</ul>
			<div id="tabs-1">
				<strong><p>Page Detail:</p></strong>
				<p>
					<span class="pagedetailTitle">Url</span>
					<input type="text" name="url" value="${pageDetail.url}">
				</p>
			</div>
			<div id="tabs-2">
				<strong><p>Page CSS:</p></strong>
				<textarea id="pagecssEditor" name="css">${pageMeta.css}</textarea>
			</div>
			
		</div>
		<input type="submit">

	
	
	</form>
</div>