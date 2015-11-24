<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="pageTreeNodeDetail" id="pg_${pageuuid}">
	<div style="float: left; width: 100%; text-align: center; margin: 10px 0;"><strong>Page Design:</strong></div>
	<div class="ptnd_topControlSection" style="float: left; width: 100%; margin-bottom: 10px; border-bottom: 1px solid; background-color: #f3f3f3;">
		<span class="pageBriefInfoContainer" style="float: left; width: 80%; color: red;">
			<c:if test="${!empty changeList}">
				This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="${pageuuid}">Click here</span> for further processing.
			</c:if>
		</span>
	
		<span style="float: right; padding: 2px;">
			<img src="/img/vendor/web-icons/cross-script.png" alt="close" class="domReady_xPageDetail deleteIconImg" title="close">
		</span>
	</div>
	<div class="ptnd_contentSection" style="float: left; width: 100%; position: relative;">
		
		<div class="pb_page clickForContainer domReady_pbTooltip <c:if test='${!pageModifyPermission}'>preview</c:if>" domvalue="${'pageid'}" style="width: ${pageDesignArea['width']}px; height: ${pageDesignArea['height']}px;">
			<!-- put pageContainer and loop through all the pageContainer's subnodes --> 
			<c:import url="containerDetail.jsp" />
		</div>
		
		<div class="pageDetailBtnAll" style="z-index:1; width: 30px; height: ${pageDesignArea['height']}px; position: absolute; right: 0; background-color: #4a4a4a;">
			<div title="setup page css" 
				class="pageCss domReady_clickForPsDetail" 
				domvalue="{'topOffset':'0', 'leftOffset':'-401', 'width':'400', 'height':'400', 'type':'css', 'totalTabs':'2', 'tabSelected':'1', 'title1':'customized css', 'title2':'system css', 'contentFromAjax1':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=css', 'contentFromAjax2':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=defaultcss'}"
				style="background: #dfdfdf url('/img/vendor/web-icons2/led-icons/css.png') no-repeat center center; border: 1px solid; height: 30px; width: 28px; margin-top: 10px;" >
			</div>
			
			<div title="setup page url" 
				class="pageUrl domReady_clickForPsDetail"
				domvalue="{'topOffset':'0', 'leftOffset':'-401', 'width':'400', 'height':'140', 'type':'url', 'title1':'url setup', 'contentFromAjax1':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=url'}"
				style="background: #dfdfdf url('/img/vendor/web-icons2/led-icons/link.png') no-repeat center center; height: 30px; width: 28px; border-bottom: 1px solid; border-left: 1px solid; border-right: 1px solid; "></div>
		
			<div title="setup page html" 
				class="pageHtml domReady_clickForPsDetail" 
				domvalue="{'topOffset':'0', 'leftOffset':'-401', 'width':'400', 'height':'300', 'type':'html', 'totalTabs':'2', 'tabSelected':'1', 'title1':'html head', 'title2':'page title', 'contentFromAjax1':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=pagehead', 'contentFromAjax2':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=pagetitle'}"
				style="background: #dfdfdf url('/img/vendor/web-icons2/famfamfam_silk_icons_v013/icons/html.png') no-repeat center center; height: 30px; width: 28px; border-bottom: 1px solid; border-left: 1px solid; border-right: 1px solid; " >
			</div>
		
		
<%-- 		
		
			<div title="setup page title" 
				class="pageTitle domReady_clickForPsDetail"
				domvalue="{'topOffset':'0', 'leftOffset':'-201', 'width':'200', 'height':'100', 'type':'pagetitle', 'title1':'title setup', 'contentFromAjax1':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=pagetitle'}"
				style="background-color: #dfdfdf; height: 30px; width: 28px; border-bottom: 1px solid black; border-left: 1px solid black; border-right black: 1px solid; font-size: 8px; line-height: 30px; text-align: center; color: blue; font-weight: bold;">&lt;title&gt;</div>
				
			<div title="setup page head" 
				class="pageHead domReady_clickForPsDetail"
				domvalue="{'topOffset':'0', 'leftOffset':'-401', 'width':'400', 'height':'400', 'type':'pagehead', 'title1':'head setup', 'contentFromAjax1':'ajaxPageDetailFieldValue?pageid=${pageuuid}&fieldName=pagehead'}"
				style="background-color: #dfdfdf; height: 30px; width: 28px; border-bottom: 1px solid black; border-left: 1px solid black; border-right: 1px solid black; font-size: 8px; line-height: 30px; text-align: center; color: blue; font-weight: bold;">&lt;head&gt;</div>
 --%>				
		</div>
		
		<div class="actionInfo" style="position: absolute; width: 100%; height: 500px; z-index: -1;">
		
			<div class="actionInfoArea" style="position: relative; padding-left: 510px; padding-right: 30px; height:100%; color: #3e5f78;">
				<div class="actionInfoContent" style="position: absolute; height: 100%; overflow-y: auto;">
				</div>
			</div>
		</div>
		
	</div>
	
	<div class="fakeContainer" style="display: none; position: absolute; width: 0; height: 0; top: 0; left: -100000px;"></div>
	
</div>



