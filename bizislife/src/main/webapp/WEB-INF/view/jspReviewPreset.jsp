<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="jspReviewPreset">
    <div class="jsCssListContainer">
        <p style="text-align: center;"><strong>Preview setup</strong></p>
        <p>All JavaScript and Css files uploaded:</p>
        <table style="width: 98%; border: 1px solid;">
            <thead style="background-color: grey;">
                <tr>
                    <th style="width: 150px;">Name</th>
                    <th>Link</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${!empty jsDetails}">
	                <tr style="background-color: lightgray;">
	                    <td colspan="2">JavaScript:</td>
	                </tr>
	                <c:forEach items="${jsDetails}" var="js" varStatus="jsIdx">
	                   <tr>
	                       <td>${js.prettyname}</td>
	                       <td><a title="${js.prettyname}" target="_blank" href="${hostAddress}/getTxt?id=${js.mediauuid}">${hostAddress}/getTxt?id=${js.mediauuid}</a></td>
	                   </tr>
	                </c:forEach>
                </c:if>
                <c:if test="${!empty cssDetails}">
                    <tr style="background-color: lightgray;">
                        <td colspan="2">CSS:</td>
                    </tr>
                    <c:forEach items="${cssDetails}" var="css" varStatus="cssIdx">
                       <tr>
                           <td>${css.prettyname}</td>
                           <td><a title="${css.prettyname}" target="_blank" href="${hostAddress}/getTxt?id=${css.mediauuid}">${hostAddress}/getTxt?id=${css.mediauuid}</a></td>
                       </tr>
                    </c:forEach>
                </c:if>
            
            </tbody>
        
        </table>
    
    </div>

    <form action="#" id="jspPreviewForm" name="jspPreviewForm" method="post">
	    <div class="headSetup" style="margin-top: 10px;">
	        <p>You can add javascript & css files you like to use for the preview here: </p>
	            <input type="hidden" name="type" value="${type}">
	            <input type="hidden" name="objuuid" value="${objuuid}">
	            <input type="hidden" name="viewuuid" value="${viewuuid}">
	            <textarea name="jspPreviewHead" rows="8" cols="85" placeholder="type anything you like to be included in head section." style="width: 98%;"></textarea>
	    </div>
	    
	    <c:if test="${!empty selectableProductFolders}">
	        <div class="categories" style="margin-top: 10px;">
	            <p>You have productList attribute in your design, you can pick which category you like to use for the preview:</p>
	            <c:forEach items="${selectableProductFolders}" var="category" varStatus="categoryIdx">
	                [<input type="radio" name="categoryid" value="${category.systemName}"> ${category.prettyName}] 
	            </c:forEach>
	        </div>
	    </c:if>
    </form>
    
    <div class="previewNote" style="margin: 10px 0;">
        <strong>Note:</strong> The preview will need to popup a new window.
    </div>
    
    <button style="float: right; margin: 10px 10px 0pt 0pt;" class="domReady_jspPreview" domvalue="jspPreviewForm">Preview</button>
</div>
