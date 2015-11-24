<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="howToGetValueInView">
	<c:if test="${!empty attrGroup && !empty attr}">
		<strong>Array: </strong> ${attrGroup.array?"<span style='font-style: italic;'>TRUE</span>":"<span style='font-style: italic;'>FALSE</span>"} for Group "${attrGroup.groupName}", ${attr.array?"<span style='font-style: italic;'>TRUE</span>":"<span style='font-style: italic;'>FALSE</span>"} for Attribute "${attr.name}".<br/>
		<strong>HowTo: </strong><br/>
		<c:choose>
			<c:when test="${!attrGroup.array && !attr.array}">
				<div style="margin-left: 10px;">Group's array and Attribute's array are <span style="font-style: italic;">FALSE</span>, which means only one Group "${attrGroup.groupName}" 
					and one Attribute "${attr.name}" in the "${moduleDetail.prettyname}". You can just copy next line into the jsp view to get the value:</div>
				
				<c:choose>
					<c:when test="${attr.type eq 'ModuleImageAttribute'}">
						<div style="margin-left: 10px; font-style: italic; color: blue;">&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;&#39; &#62;</div>
						<div>
							<span style="font-weight: bold;">Note: </span>size options: "-1" for original photo, "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, "200" for thumbnail photo width 200, "600" for thumbnail photo width 600.
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleLinkAttribute'}">
						<div style="margin-left: 10px; font-style: italic; color: blue;">
							<p style="color: blue;">Note: href's format should begin with "http://"</p>
							<strong>href:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].href&#125;<br/>
							<strong>rel:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].rel&#125;<br/>
							<strong>target:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].target&#125;<br/>
							<strong>title:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
							<strong>linkValue:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].linkValue&#125;
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleMoneyAttribute'}">
						<div style="margin-left: 10px; font-style: italic; color: blue;">
							<strong>currency:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.currency&#125;<br/>
							<strong>amount:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.amount&#125;<br/>
							<strong>title:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleProductListAttribute'}">
						<div style="margin-left: 10px; font-style: italic; color: blue;">
							<strong>title:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;&lt;br/&gt;<br/>
						
							&lt;c:if test=&quot;&#36;&#123;!empty productTree&#125;&quot;&gt;<br/>
								&lt;strong&gt;current category info&lt;/strong&gt;&lt;br/&gt;<br/>
								&lt;ul&gt;<br/>
									&lt;li&gt;current category name: &#36;&#123;productTree.prettyName&#125;&lt;/li&gt;<br/>
									&lt;li&gt;current category systemName: &#36;&#123;productTree.systemName&#125;&lt;/li&gt;<br/>
									&lt;li&gt;current category type: &#36;&#123;productTree.type.code&#125;&lt;/li&gt;<br/>
									&lt;li&gt;total subCategories under current category: &#36;&#123;productTree.totalSubfolder&#125;&lt;/li&gt;<br/>
									&lt;li&gt;total products under current category: &#36;&#123;productTree.totalProducts&#125;&lt;/li&gt;<br/>
								&lt;/ul&gt;<br/>
								
								&lt;strong&gt;one level down subCategories&lt;/strong&gt;&lt;br/&gt;<br/>
								&lt;c:if test=&quot;&#36;&#123;!empty productTree.subnodes&#125;&quot;&gt;<br/>
									&lt;table border=&quot;1&quot;&gt;<br/>
										&lt;tr&gt;<br/>
											&lt;th&gt;Name&lt;/th&gt;<br/>
											&lt;th&gt;SystemName&lt;/th&gt;<br/>
											&lt;th&gt;Type&lt;/th&gt;<br/>
											&lt;th&gt;SubCate #&lt;/th&gt;<br/>
											&lt;th&gt;Product #&lt;/th&gt;<br/>
											&lt;th&gt;image&lt;/th&gt;<br/>
										&lt;/tr&gt;<br/>
									&lt;c:forEach items=&quot;&#36;&#123;productTree.subnodes&#125;&quot; var=&quot;subCate&quot; varStatus=&quot;subCateIdx&quot;&gt;<br/>
										&lt;tr&gt;<br/>
											&lt;td&gt;<br/>
												
												&lt;a href=&quot;&#36;&#123;subCate.url&#125;&quot; &gt;&#36;&#123;subCate.prettyName&#125;&lt;/a&gt;
											
											
<%-- 											
												&lt;c:choose&gt;<br/>
													&lt;c:when test=&quot;&#36;&#123;subCate.type.code eq 'fd'&#125;&quot;&gt;<br/>
														&lt;a href=&quot;/getPage/org/&#36;&#123;org.orguuid&#125;/pageurl/&#36;&#123;pageid&#125;?categoryid=&#36;&#123;subCate.systemName&#125;&quot; target=&quot;_blank&quot;&gt;&#36;&#123;subCate.prettyName&#125;&lt;/a&gt;<br/>
													&lt;/c:when&gt;<br/>
													&lt;c:otherwise&gt;<br/>
														&lt;a href='/getPage/org/&#36;&#123;org.orguuid&#125;/pageurl/&#36;&#123;module.groups[&quot;${attrGroup.groupName}&quot;][0].attrs[&quot;${attr.name}&quot;][0].desktopProductPageUuid&#125;?entityid=&#36;&#123;subCate.systemName&#125;' target=&quot;_blank&quot;&gt;&#36;&#123;subCate.prettyName&#125;&lt;/a&gt;<br/>
													&lt;/c:otherwise&gt;<br/>
												&lt;/c:choose&gt;<br/>
 --%>												
												
												
											&lt;/td&gt;<br/>
											&lt;td&gt;&#36;&#123;subCate.systemName&#125;&lt;/td&gt;<br/>
											&lt;td&gt;&#36;&#123;subCate.type.code&#125;&lt;/td&gt;<br/>
											&lt;td&gt;&#36;&#123;subCate.totalSubfolder&#125;&lt;/td&gt;<br/>
											&lt;td&gt;&#36;&#123;subCate.totalProducts&#125;&lt;/td&gt;<br/>
											&lt;td&gt;&lt;img src=&quot;/getphoto?id=&#36;&#123;subCate.defaultImageSysName&#125;&amp;size=100&quot;&gt;&lt;/td&gt;<br/>
										&lt;/tr&gt;<br/>
									&lt;/c:forEach&gt;<br/>
									&lt;/table&gt;<br/>
								&lt;/c:if&gt;<br/>
								
							&lt;/c:if&gt;<br/>
							
							&lt;div&gt;<br/>
							&lt;c:if test=&quot;&#36;&#123;!empty pagination &amp;&amp; !empty pagination.paginationNodes&#125;&quot;&gt;<br/>
							&lt;p&gt;<br/>
							pagination:<br/>
							&lt;/p&gt;<br/>
							&lt;c:forEach items=&quot;&#36;&#123;pagination.paginationNodes&#125;&quot; var=&quot;node&quot; varStatus=&quot;nodeIdx&quot;&gt;<br/>
								
								&lt;span class=&quot;page &#36;&#123;node.isCurrentNode()?'currentNode':''&#125;&quot;&gt;<br/>
									&lt;a title=&quot;&#36;&#123;node.title&#125;&quot; href=&quot;&#36;&#123;node.url&#125;&quot;&gt;&#36;&#123;node.prettyName&#125;&lt;/a&gt;<br/>
								&lt;/span&gt;<br/>
							&lt;/c:forEach&gt;<br/>
								
								&lt;c:if test=&quot;&#36;&#123;!empty pagination.viewAllUrl&#125;&quot;&gt;<br/>
									&lt;span class=&quot;view all&quot;&gt;<br/>
										&lt;a href=&quot;&#36;&#123;pagination.viewAllUrl&#125;&quot;&gt;View All&lt;/a&gt;<br/>
									&lt;/span&gt;<br/>
								&lt;/c:if&gt;<br/>
								
								&lt;span&gt;&#36;&#123;pagination.extraInfo&#125;&lt;/span&gt;<br/>
								
							&lt;/c:if&gt;<br/>
							&lt;/div&gt;<br/>					
							
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleEntityCategoryListAttribute'}">
						<div style="margin-left: 10px; font-style: italic; color: blue;">
						
							&lt;div id=&quot;categoryTree1&quot;&gt;<br/>
								&lt;c:if test=&quot;&#36;{!empty categoryTree}&quot;&gt;<br/>
									&lt;%-- for first level --%&gt;<br/>
									&lt;ul&gt;<br/>
										&lt;li id=&quot;&#36;{categoryTree.systemName}&quot; rel=&quot;cat&quot; class=&quot;&#36;{categoryTree.isSelected()?'opened':''}&quot;&gt;<br/>
											&lt;a href=&quot;&#36;{categoryTree.url}&quot;&gt;&#36;{categoryTree.prettyName}&lt;/a&gt;<br/>
							
											&lt;%-- for second level --%&gt;<br/>
											&lt;c:if test=&quot;&#36;{!empty categoryTree.subNodes}&quot;&gt;<br/>
												&lt;ul&gt;<br/>
													&lt;c:forEach items=&quot;&#36;{categoryTree.subNodes}&quot; var=&quot;subL1&quot; varStatus=&quot;subL1Idx&quot;&gt;<br/>
														&lt;li id=&quot;&#36;{subL1.systemName}&quot; rel=&quot;cat&quot; class=&quot;&#36;{subL1.isSelected()?'opened':''}&quot;&gt;<br/>
															&lt;a href=&quot;&#36;{subL1.url}&quot;&gt;&#36;{subL1.prettyName}&lt;/a&gt;<br/>
															
															&lt;%-- for third level --%&gt;<br/>
															&lt;c:if test=&quot;&#36;{!empty subL1.subNodes}&quot;&gt;<br/>
																&lt;ul&gt;<br/>
																	&lt;c:forEach items=&quot;&#36;{subL1.subNodes}&quot; var=&quot;subL2&quot; varStatus=&quot;subL2Idx&quot;&gt;<br/>
																		&lt;li id=&quot;&#36;{subL2.systemName}&quot; rel=&quot;cat&quot; class=&quot;&#36;{subL2.isSelected()?'opened':''}&quot;&gt;<br/>
																			&lt;a href=&quot;&#36;{subL2.url}&quot;&gt;&#36;{subL2.prettyName}&lt;/a&gt;<br/>
																			
																			&lt;%-- for forth level --%&gt;<br/>
																			&lt;c:if test=&quot;&#36;{!empty subL2.subNodes}&quot;&gt;<br/>
																				&lt;ul&gt;<br/>
																					&lt;c:forEach items=&quot;&#36;{subL2.subNodes}&quot; var=&quot;subL3&quot; varStatus=&quot;subL3Idx&quot;&gt;<br/>
																						&lt;li id=&quot;&#36;{subL3.systemName}&quot; rel=&quot;cat&quot; class=&quot;&#36;{subL3.isSelected()?'opened':''}&quot;&gt;<br/>
																							&lt;a href=&quot;&#36;{subL3.url}&quot;&gt;&#36;{subL3.prettyName}&lt;/a&gt;<br/>
																						&lt;/li&gt;<br/>
																					&lt;/c:forEach&gt;<br/>
																				&lt;/ul&gt;<br/>
																			&lt;/c:if&gt;<br/>
																		&lt;/li&gt;<br/>
																	&lt;/c:forEach&gt;<br/>
																&lt;/ul&gt;<br/>
															&lt;/c:if&gt;<br/>
														&lt;/li&gt;<br/>
													&lt;/c:forEach&gt;<br/>
												&lt;/ul&gt;<br/>
											&lt;/c:if&gt;<br/>
										&lt;/li&gt;<br/>
									&lt;/ul&gt;<br/>
								&lt;/c:if&gt;<br/>
							&lt;/div&gt;<br/>
						
						</div>
					</c:when>
					<c:otherwise>
						<div style="margin-left: 10px; font-style: italic; color: blue;">
							<strong>title:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
							<strong>value:</strong> &#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;
						</div>
					</c:otherwise>
				</c:choose>
				
				
			</c:when>
			<c:when test="${attrGroup.array && !attr.array}">
				<div style="margin-left: 10px;">Group's array is <span style="font-style: italic;">TRUE</span> and Attribute's array is <span style="font-style: italic;">FALSE</span>, which means multiple Group "${attrGroup.groupName}" 
					and one Attribute "${attr.name}" in the "${moduleDetail.prettyname}". There have two ways to get the value:</div>
				<c:choose>
					<c:when test="${attr.type eq 'ModuleImageAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;attGroup.attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;attGroup.attrs["${attr.name}"][0].title&#125;&#39; &#62;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][2].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][2].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									...<br/>
								</div>
							</li>
						</ul>
						<div>
							<span style="font-weight: bold;">Note: </span>size options: "-1" for original photo, "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, "200" for thumbnail photo width 200, "600" for thumbnail photo width 600.
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleLinkAttribute'}">
						<p style="color: blue;">Note: href's format should begin with "http://"</p>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;<strong>href: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].href&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>rel: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].rel&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>target: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].target&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>title: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].title&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>linkValue: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].linkValue&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>href: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].href&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].href&#125;<br/>
									...<br/>
									<strong>rel: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].rel&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].rel&#125;<br/>
									...<br/>
									<strong>target: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].target&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].target&#125;<br/>
									...<br/>
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									...<br/>
									<strong>linkValue: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].linkValue&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].linkValue&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					</c:when>
					<c:when test="${attr.type eq 'ModuleMoneyAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;<strong>title: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].title&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>currency: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].money.currency&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>amount: </strong>&#36;&#123;attGroup.attrs["${attr.name}"][0].money.amount&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									...<br/>
									<strong>currency: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.currency&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].money.currency&#125;<br/>
									...<br/>
									<strong>amount: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.amount&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].money.amount&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					
					</c:when>
					
					<c:otherwise>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											<strong>title:</strong> &#60;li&#62;&#36;&#123;attGroup.attrs["${attr.name}"][0].title&#125;&#60;/li&#62;<br/>
											<strong>value:</strong> &#60;li&#62;&#36;&#123;attGroup.attrs["${attr.name}"][0].value&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>title:</strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][2].attrs["${attr.name}"][0].title&#125;<br/>
									...<br/>
									
									<strong>value:</strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][2].attrs["${attr.name}"][0].value&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					</c:otherwise>
				</c:choose>
				
			</c:when>
			<c:when test="${attrGroup.array && attr.array}">
				<div style="margin-left: 10px;">Group's array is <span style="font-style: italic;">TRUE</span> and Attribute's array is <span style="font-style: italic;">TRUE</span>, which means multiple Group "${attrGroup.groupName}" 
					and multiple Attribute "${attr.name}" in each group in the "${moduleDetail.prettyname}". There have two ways to get the value:</div>
				<c:choose>
					<c:when test="${attr.type eq 'ModuleImageAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;group name: &#36;&#123;attGroup.groupName&#125;<br/>
												&#60;ul&#62;<br/>
													&#60;c:forEach items="&#36;&#123;attGroup.attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
														&#60;li&#62;image: &#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;attr.value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;attr.title&#125;&#39; &#62;&#60;/li&#62;<br/>
													&#60;/c:forEach&#62;<br/>
												&#60;/ul&#62;<br/>
											&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;&#39; &#62;<br/>
									...<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].title&#125;&#39; &#62;<br/>
									...<br/>
								</div>
							</li>
						</ul>
						<div>
							<span style="font-weight: bold;">Note: </span>size options: "-1" for original photo, "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, "200" for thumbnail photo width 200, "600" for thumbnail photo width 600.
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleLinkAttribute'}">
						<p style="color: blue;">Note: href's format should begin with "http://"</p>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;group name: &#36;&#123;attGroup.groupName&#125;<br/>
												&#60;ul&#62;<br/>
													&#60;c:forEach items="&#36;&#123;attGroup.attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
														&#60;li&#62;<strong>href</strong>: &#36;&#123;attr.href&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>rel</strong>: &#36;&#123;attr.rel&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>target</strong>: &#36;&#123;attr.target&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>title</strong>: &#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>linkValue</strong>: &#36;&#123;attr.linkValue&#125;&#60;/li&#62;<br/>
													&#60;/c:forEach&#62;<br/>
												&#60;/ul&#62;<br/>
											&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>href: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].href&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].href&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].href&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].href&#125;<br/>
									...<br/>
									<strong>rel: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].rel&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].rel&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].rel&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].rel&#125;<br/>
									...<br/>
									<strong>target: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].target&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].target&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].target&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].target&#125;<br/>
									...<br/>
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									<strong>linkValue: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].linkValue&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].linkValue&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].linkValue&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].linkValue&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					</c:when>
					<c:when test="${attr.type eq 'ModuleMoneyAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;group name: &#36;&#123;attGroup.groupName&#125;<br/>
												&#60;ul&#62;<br/>
													&#60;c:forEach items="&#36;&#123;attGroup.attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
														&#60;li&#62;<strong>title</strong>: &#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>currency</strong>: &#36;&#123;attr.money.currency&#125;&#60;/li&#62;<br/>
														&#60;li&#62;<strong>amount</strong>: &#36;&#123;attr.money.amount&#125;&#60;/li&#62;<br/>
													&#60;/c:forEach&#62;<br/>
												&#60;/ul&#62;<br/>
											&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									<strong>currency: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.currency&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].money.currency&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].money.currency&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].money.currency&#125;<br/>
									...<br/>
									<strong>amount: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.amount&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].money.amount&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].money.amount&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].money.amount&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					
					</c:when>
					<c:otherwise>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}']&#125;" var="attGroup" varStatus="attGroupIdx"&#62;<br/>
											&#60;li&#62;group name: &#36;&#123;attGroup.groupName&#125;<br/>
												&#60;ul&#62;<br/>
													&#60;c:forEach items="&#36;&#123;attGroup.attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
														&#60;li&#62;attribute title: &#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
														&#60;li&#62;attribute value: &#36;&#123;attr.value&#125;&#60;/li&#62;<br/>
													&#60;/c:forEach&#62;<br/>
												&#60;/ul&#62;<br/>
											&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
								
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									
									<strong>value: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].value&#125;<br/>
									...<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][0].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][1].attrs["${attr.name}"][1].value&#125;<br/>
									...<br/>
									
								</div>
							</li>
						</ul>
					</c:otherwise>
				</c:choose>	
				
			</c:when>
			<c:when test="${!attrGroup.array && attr.array}">
			
			
				<div style="margin-left: 10px;">Group's array is <span style="font-style: italic;">FALSE</span> and Attribute's array is <span style="font-style: italic;">TRUE</span>, which means one Group "${attrGroup.groupName}" 
					and multiple Attributes "${attr.name}" in the "${moduleDetail.prettyname}". There have two ways to get the value:</div>
				<c:choose>
					<c:when test="${attr.type eq 'ModuleImageAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}'][0].attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
											&#60;li&#62;&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;attr.value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;attr.title&#125;&#39; &#62;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
									
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;&#39; &#62;<br/>
									&#60;img src&#61;&#39;&#47;getphoto&#63;id&#61;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][2].value&#125;&#38;size&#61;-1&#39; title&#61;&#39;&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][2].title&#125;&#39; &#62;<br/>
									...<br/>
								</div>
							</li>
						</ul>
						<div>
							<span style="font-weight: bold;">Note: </span>size options: "-1" for original photo, "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, "200" for thumbnail photo width 200, "600" for thumbnail photo width 600.
						</div>
					</c:when>
					<c:when test="${attr.type eq 'ModuleLinkAttribute'}">
						<p style="color: blue;">Note: href's format should begin with "http://"</p>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}'][0].attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
											&#60;li&#62;<strong>href: </strong>&#36;&#123;attr.href&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>rel: </strong>&#36;&#123;attr.rel&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>target: </strong>&#36;&#123;attr.target&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>title: </strong>&#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>linkValue: </strong>&#36;&#123;attr.linkValue&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
									
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>href: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].href&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].href&#125;<br/>
									...<br/>
									<strong>rel: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].rel&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].rel&#125;<br/>
									...<br/>
									<strong>target: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].target&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].target&#125;<br/>
									...<br/>
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									<strong>linkValue: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].linkValue&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].linkValue&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					
					</c:when>
					<c:when test="${attr.type eq 'ModuleMoneyAttribute'}">
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}'][0].attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
											&#60;li&#62;<strong>title: </strong>&#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>currency: </strong>&#36;&#123;attr.money.currency&#125;&#60;/li&#62;<br/>
											&#60;li&#62;<strong>amount: </strong>&#36;&#123;attr.money.amount&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
									
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									...<br/>
									<strong>currency: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.currency&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].money.currency&#125;<br/>
									...<br/>
									<strong>amount: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].money.amount&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].money.amount&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					
					</c:when>
					<c:otherwise>
						<ul>
							<li>
								<div style="margin-left: 10px;">Way #1 to use JSTL's <span style="font-style: italic;">forEach</span> loop:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									
									&#60;ul&#62;<br/>
										&#60;c:forEach items="&#36;&#123;module.groups['${attrGroup.groupName}'][0].attrs['${attr.name}']&#125;" var="attr" varStatus="attrIdx"&#62;<br/>
											<strong>title: </strong><br/>
											&#60;li&#62;&#36;&#123;attr.title&#125;&#60;/li&#62;<br/>
											<strong>value: </strong><br/>
											&#60;li&#62;&#36;&#123;attr.value&#125;&#60;/li&#62;<br/>
										&#60;/c:forEach&#62;<br/>
									&#60;/ul&#62;
									
								</div>
							</li>
							<li>
								<div style="margin-left: 10px;">Way #2 to use list's index#:</div>
								<div style="margin-left: 10px; font-style: italic; color: blue;">
									<strong>title: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].title&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][2].title&#125;<br/>
									...<br/>
									<strong>value: </strong><br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][0].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][1].value&#125;<br/>
									&#36;&#123;module.groups["${attrGroup.groupName}"][0].attrs["${attr.name}"][2].value&#125;<br/>
									...<br/>
								</div>
							</li>
						</ul>
					</c:otherwise>
				</c:choose>
			
			</c:when>
		</c:choose>
		
	</c:if>	
</div>
