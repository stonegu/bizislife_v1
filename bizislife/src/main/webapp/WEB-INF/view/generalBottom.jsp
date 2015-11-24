<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div style="margin: 0 0 0 10px;">
	<div class="copyright" style="width: 30%; float: left; margin: 0 0 0 10px;">&copy; 2014, Bizislife. All Rights Reserved.</div>
	<div class="contactInfo" style="width: 50%; float: right; text-align: right; margin: 0 10px 0 0;">
		<a href="#domRd_contactInfo" class="domRd_contactInfo"><img alt="Contact" src="/img/vendor/web-icons/mail--plus.png"></a>
		<a href="#bugRepot_content" class="bugReport"><img alt="bug report" src="/img/vendor/web-icons/bug--plus.png"></a>
	</div>
</div>

<div style="display: none;">
	<div id="bugRepot_content" style="padding:10px; background:#fff;">
		<h3>Bug report:</h3>
		<form action="#" id="bugReportForm">
			<input type="hidden" name="pageName" value="${currentPageId}">
			<textarea rows="9" style="width: 90%;" name="issue"></textarea>
			<button type="button" class="domReady_bugReportSubmit">submit</button>
		</form>
	</div>

    <div id="domRd_contactInfo" style="padding:10px; background:#fff;">
        <h3>Contact form</h3>
        <form action="#" id="contactInfoForm">
            <textarea rows="9" style="width: 90%;" name="contactInfo"></textarea>
            <button type="button" class="domReady_contactInfoSubmit">submit</button>
        </form>
    </div>

</div>