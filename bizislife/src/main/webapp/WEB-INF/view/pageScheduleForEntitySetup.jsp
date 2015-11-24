<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="thePageType" value="${(pagetype eq 'categoryPage')?'category':'product'}" />

<div id="pageScheduleForEntityTabs">

	<ul>
		<li><a href="#pageScheduleForEntityTabs-1">Desktop</a></li>
<!-- 		
		<li><a href="#pageScheduleForEntityTabs-2">Mobile</a></li>
 -->		
	</ul>
	<div id="pageScheduleForEntityTabs-1">
	
		<div>
			You can setup desktop ${thePageType} page by: 
			<div class="defaultPageSetup">
				<p><strong>Method #1: select default ${thePageType} page</strong></p>
				<p>
					<c:if test="${!empty orgDeskPages}">
						<ul>
							<li>
								<input type="radio" name="dDefaultPage" class="${thePageType} dDefaultPage defaultPageForEntity"
									domvalue="{'entityId':'${entityUuid}', 'pagetype':'${thePageType}', 'sitetype':'desktop'}"
										
									<c:choose>
										<c:when test="${thePageType eq 'category'}">
											<c:if test="${empty defaultCategoryPage_desk}">
												checked="checked"												
											</c:if>
										</c:when>
										<c:when test="${thePageType eq 'product'}">
											<c:if test="${empty defaultProductPage_desk}">
												checked="checked"												
											</c:if>
										</c:when>
									</c:choose>
									
									value=""
								> None
							</li>
							<c:forEach items="${orgDeskPages}" var="dpage" varStatus="dpageIdx">
								<li>
									<input type="radio" name="dDefaultPage" class="${thePageType} dDefaultPage defaultPageForEntity"
										domvalue="{'entityId':'${entityUuid}', 'pagetype':'${thePageType}', 'sitetype':'desktop'}"
										
										<c:choose>
											<c:when test="${thePageType eq 'category'}">
												<c:if test="${dpage.key eq defaultCategoryPage_desk}">
													checked="checked"												
												</c:if>
											</c:when>
											<c:when test="${thePageType eq 'product'}">
												<c:if test="${dpage.key eq defaultProductPage_desk}">
													checked="checked"												
												</c:if>
											</c:when>
										</c:choose>
										
										value="${dpage.key}"> ${dpage.value}
								</li>
							</c:forEach>							
						</ul>
					</c:if>
				</p>
			</div>
			Or
			<div class="schedulePageSetup">
				<p>
					<strong>Method #2: setup schedule to display ${thePageType} page</strong>
					(schedule will overwrite default setup!)
				</p>
				<p>
					...
				</p>
			</div>
		
		</div>	
	
	</div>
	
<%-- 	
	<div id="pageScheduleForEntityTabs-2">
		<div>
			You can setup Mobile ${thePageType} page by: 
			<div class="defaultPageSetup">
				<p><strong>Method #1: select default ${thePageType} page</strong></p>
				<p>
					<c:if test="${!empty orgMobilePages}">
						<ul>
							<li>
								<input type="radio" name="mDefaultPage" class="${thePageType} mDefaultPage defaultPageForEntity"
									domvalue="{'entityId':'${entityUuid}', 'pagetype':'${thePageType}', 'sitetype':'mobile'}"
										
									<c:choose>
										<c:when test="${thePageType eq 'category'}">
											<c:if test="${empty defaultCategoryPage_mobile}">
												checked="checked"												
											</c:if>
										</c:when>
										<c:when test="${thePageType eq 'product'}">
											<c:if test="${empty defaultProductPage_mobile}">
												checked="checked"												
											</c:if>
										</c:when>
									</c:choose>
									
									value=""
								> None
							</li>
							<c:forEach items="${orgMobilePages}" var="mpage" varStatus="mpageIdx">
								<li>
									<input type="radio" name="mDefaultPage" class="${thePageType} mDefaultPage defaultPageForEntity"
										domvalue="{'entityId':'${entityUuid}', 'pagetype':'${thePageType}', 'sitetype':'mobile'}" 
										
										<c:choose>
											<c:when test="${thePageType eq 'category'}">
												<c:if test="${mpage.key eq defaultCategoryPage_mobile}">
													checked="checked"												
												</c:if>
											</c:when>
											<c:when test="${thePageType eq 'product'}">
												<c:if test="${mpage.key eq defaultProductPage_mobile}">
													checked="checked"												
												</c:if>
											</c:when>
										</c:choose>
										
										value="${mpage.key}"> ${mpage.value}
								</li>
							</c:forEach>							
						</ul>
					</c:if>
				</p>
			</div>
			Or
			<div class="schedulePageSetup">
				<p>
					<strong>Method #2: setup schedule to display ${thePageType} page</strong>
					(schedule will overwrite default setup!)
				</p>
				<p>
					...
				</p>
			</div>
		
		</div>	
	</div>
 --%>	
	
</div>






