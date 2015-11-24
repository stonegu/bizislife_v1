<div id="categoryTree1">
	<c:if test="${!empty categoryTree}">
		<%-- for first level --%>
		<ul>
			<li id="${categoryTree.systemName}" rel="cat" class="${categoryTree.isSelected()?'opened':''}">
				<a href="${categoryTree.url}">${categoryTree.prettyName}</a>
				<%-- for second level --%>
				<c:if test="${!empty categoryTree.subNodes}">
					<ul>
						<c:forEach items="${categoryTree.subNodes}" var="subL1" varStatus="subL1Idx">
							<li id="${subL1.systemName}" rel="cat" class="${subL1.isSelected()?'opened':''}">
								<a href="${subL1.url}">${subL1.prettyName}</a>
								<%-- for third level --%>
								<c:if test="${!empty subL1.subNodes}">
									<ul>
										<c:forEach items="${subL1.subNodes}" var="subL2" varStatus="subL2Idx">
											<li id="${subL2.systemName}" rel="cat" class="${subL2.isSelected()?'opened':''}">
												<a href="${subL2.url}">${subL2.prettyName}</a>
												<%-- for forth level --%>
												<c:if test="${!empty subL2.subNodes}">
													<ul>
														<c:forEach items="${subL2.subNodes}" var="subL3" varStatus="subL3Idx">
															<li id="${subL3.systemName}" rel="cat" class="${subL3.isSelected()?'opened':''}">
    															<a href="${subL3.url}">${subL3.prettyName}</a>
															</li>
														</c:forEach>
													</ul>
												</c:if>
                                                <%-- end forth level --%>
											</li>
										</c:forEach>
									</ul>
								</c:if>
                                <%-- end third level --%>
							</li>
						</c:forEach>
					</ul>
				</c:if>
                <%-- end second level --%>
			</li>
		</ul>
        <%-- end first level --%>
	</c:if>
</div>