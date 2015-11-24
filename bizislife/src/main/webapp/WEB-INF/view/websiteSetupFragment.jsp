<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="websiteSetupFragment">
	<div class="domainNameSetup">
		<c:if test="${!empty orgDomains}">
			<c:forEach items="${orgDomains}" var="orgDomain" varStatus="orgDomainIdx">
				<c:choose>
					<c:when test="${!empty loginAccount && loginAccount.isSystemDefaultAccount()}">
						<form action="/domainRemoveSubmit" name="domainSubmitForm_${orgDomainIdx.count}" id="domainSubmitForm_${orgDomainIdx.count}" method="post" style="padding-top: 8px;">
							<div>
								<input type="hidden" name="orgid" value="${orgid}">
								<span>Domain: </span>
								<span>
									${orgDomain.value}
									<input type="hidden" name="domainName" value="${orgDomain.value}">
								</span>
								<c:choose>
									<c:when test="${orgDomain.selected}">
										<span class="domainSubmitStatus" style="color: green;">[passed]</span>
									</c:when>
									<c:otherwise>
										<span class="domainSubmitStatus" style="color: red;"><button type="button" class="domReady_domainValifyForm" domvalue="domainSubmitForm_${orgDomainIdx.count}">validate</button></span>
										<span class="detailInfoPopupClick"
											domvalue="{'popupContent':'You can add a TXT record: <br/><strong>bizislifeVali=${orgDomain.key}</strong><br/> to verify domain ownership. You add TXT records using the administration tools at your domain host.'}"
											style="color: blue; text-decoration: underline; cursor: pointer;"
											>HowTo?</span>
									</c:otherwise>
								</c:choose>
							</div>
							
							<input type="submit" name="submit" value="remove">
						</form>
					</c:when>
					<c:otherwise>
						<div>
							<span>Domain: </span>
							<span>
								${orgDomain.value}
							</span>
							<c:choose>
								<c:when test="${orgDomain.selected}">
									<span class="domainSubmitStatus" style="color: green;">[passed]</span>
								</c:when>
								<c:otherwise>
									<span class="domainSubmitStatus" style="color: red;"><button type="button" class="domReady_domainValifyForm" domvalue="domainSubmitForm_${orgDomainIdx.count}">validate</button></span>
									<span class="detailInfoPopupClick"
										domvalue="{'popupContent':'You can add a TXT record: <br/><strong>bizislifeVali=${orgDomain.key}</strong><br/> to verify domain ownership. You add TXT records using the administration tools at your domain host.'}"
										style="color: blue; text-decoration: underline; cursor: pointer;"
										>HowTo?</span>
								</c:otherwise>
							</c:choose>
						</div>
					</c:otherwise>
				</c:choose>
			
				
				
			</c:forEach>
		</c:if>
		
		<c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount()}">
			<form action="/domainSubmit" name="domainSubmitForm_0" id="domainSubmitForm_0" method="post" style="padding-top: 8px;">
				<div>
					<input type="hidden" name="orgid" value="${orgid}">
					<span>Domain: </span>
					<span><input type="text" name="domainName"></span> <span class="domainSubmitStatus"></span>
				</div>
				<input type="submit" name="submit" value="add">
				<p>
				    Note: <br/>
				    1. type in domain without subdomain or www.<br/>
				    2. your website's DNS need to point to ${proxyId}.
				</p>
				
			</form>
		</c:if>
		
	</div>

</div>