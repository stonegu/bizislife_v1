<div class="pageTreeInfo">
	<div class="limitationInfo">
<%-- 	
		<span title="pages in organization">${pagesAlreadyHave}</span>:<span title="pages can create">${pagesCanHave}</span>
 --%>		
	</div>
</div>

<div class="generalPageTree">

	<input type="button" value="Collapse All" onclick="$('#generalPageTree').jstree('close_all');">
	<input type="button" value="Expand All" onclick="$('#generalPageTree').jstree('open_all');">
	<div id="generalPageTree">
	
	</div>

</div>

<script type="text/javascript">

// the page has been opened
openedPageId = "";

$(function () {
	
	// time value for treeNodeFunctionIcons
	var treeNodeIconsTimeout;
	
	$("#generalPageTree").jstree({
		"plugins" : ["themes", "json_data", "ui", "types", "crrm", "dnd"],
		"core" : {
			"html_titles" : true
		},
		"themes" : {
			"theme" : "classic",
			"dots" : true,
			"icons" : true
		},
		"types" : {
			"types" : {
				
				"folder" :{
					"select_node" : function(){
						return false;
					} 
				},
				"default":{
					"select_node" : function(){
						return false;
					} 
				},
				"container":{
					"select_node" : function(){
						return true;
					} 
				},
				"page":{
					"select_node" : function(){
						return true;
					} 
				},
				"cmSched":{
					"select_node" : function(){
						return true;
					} 
				},
				"miSched":{
					"select_node" : function(){
						return true;
					} 
				}
				
			}
		},
		"json_data" : { 
			"ajax" : {
				"data" : function(node){
					
					if(node==-1){ // get root
						return "operation=get_${pageType}Tree&org=${orgUuid}";
					}else{
						if(node.attr("rel")=="page" && (node.hasClass("mb") || node.hasClass("dk"))){ // get container tree
							return "operation=get_container&org=${orgUuid}&pageid="+node.attr("id");
						}else if(node.attr("rel")=="folder"){ // get page tree if open folder
							return "operation=get_${pageType}Tree&org=${orgUuid}&parentNodeId="+node.attr("id"); 
						}else if(node.attr("rel")=="container"){
							return "operation=get_container&org=${orgUuid}&parentNodeId="+node.attr("id");
						}else if(node.attr("rel")=="cmSched"){
							return "operation=get_miSched&org=${orgUuid}&parentNodeId="+node.attr("id");
						}
					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					
					if(node==-1){
						return "/pageTreeMain";
					}else{
						if(node.attr("rel")=="page" && (node.hasClass("mb") || node.hasClass("dk"))){ // get container tree
							return "/containerTreeMain";
						}else if(node.attr("rel")=="folder"){ // get page tree if open folder
							return "/pageTreeMain";
						}else if(node.attr("rel")=="container"){
							return "/containerTreeMain"
						}else if(node.attr("rel")=="cmSched"){
							return "/containerSchedTree"
						}
					}
					
				},
				"success": function (data) {
                    //return new_data;
                    //console.log("success");
                }
			}
		},
		"crrm" : {
			"move" : {
				"always_copy": "multitree", // If set to "multitree" only moves between trees will be forced to a copy.
				"default_position" : "first",
				"check_move" : function (m) {
					
					// check the root node id for moving obj
					//console.log(m.o.attr?m.o.parentsUntil(".jstree","li").last() : 0);
					var sourceTreeRootId = m.o.parentsUntil(".jstree","li").last().attr("id");
					var targetTreeRootId = m.np.parentsUntil(".jstree","li").last().attr("id");

					if(sourceTreeRootId==targetTreeRootId){ // move inside same tree
						// m.o : moving obj, m.np : new parent
						if(m.o.attr("rel")=="folder" || m.o.attr("rel")=="page"){
							if(m.np.attr("rel")=="folder"){
								return true;
							}
						}
					
					}else if(sourceTreeRootId!=targetTreeRootId){ // copy from another tree
						// m.o : moving obj, m.np : new parent
						if((m.o.attr("rel")=="folder" || m.o.attr("rel")=="page") && m.o.hasClass("toShare")){
							if(m.np.attr("rel")=="folder"){
								return true;
							}
						}
					}
					
					return false;
					
 				}
			}
		}
		
	})
	.bind("loaded.jstree", function (event, data) {
		//console.log("loaded!");
		
	})
	.bind("move_node.jstree", function (event, data) {
		// you need to check the data is from the same tree or different tree
		var draggedObj = data.rslt.o;
		var originalFolderObj = data.rslt.op;
		var targetFolderObj = data.rslt.np;
		
		//var movingNodeId = data.rslt.o.attr("id");
		var originalPosition = data.inst.get_path("#" + originalFolderObj.attr("id"), true);
//		var targetPosition = data.inst.get_path("#" + targetFolderObj.attr("id"), true);
		
		if(originalPosition){
			
			$.ajax({
				type: "POST",
				url: "/movePageNode",
				cache: false,
				data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+data.rslt.np.attr("id"),
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
												
					}else{
						$.jstree.rollback(data.rlbk);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else{ // data is dragged from another tree
			
			$.ajax({
				type: "POST",
				url: "/copyPageNodeToAnotherTree",
				cache: false,
				data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+targetFolderObj.attr("id"),
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						// note: the target tree need to be refreshed, since trag-in tree structure could be changed!!
						// For example: moduledetail has copy permission, but direct parent-folder doesn't have copy permission, but grandparent-folder has copy permission.
						$("#generalPageTree").jstree("refresh");
					
						// add new page containers' hexcolors into css
						if(aData.response2!=null){
    						var totalStyleSheets = document.styleSheets.length; 
							$(aData.response2).each(function(i){
	    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-leaf a.hex_'+this+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+this+'; height: 15px;}', 0);
	    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-open a.hex_'+this+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+this+'; height: 15px;}', 0);		
	    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-closed a.hex_'+this+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+this+';height: 15px;}', 0);		
								
							})
						}
					
					}else{
						$.jstree.rollback(data.rlbk);
						alert(aData.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
		}
		
	})
	.bind("select_node.jstree", function(event, data){
		var nodeUuid = data.rslt.obj.attr("id");
		var parentUuid = $.jstree._reference('#generalPageTree')._get_parent(data.rslt.obj).attr("id");
		
		// ajax call to get node info (folder, product, ...)
		//console.log("select page tree node : "+nodeUuid);
		
		var ref = data.rslt.obj.attr("rel");
		
		// select page node: 
		if(ref=="page" && (data.rslt.obj.hasClass("mb") || data.rslt.obj.hasClass("dk")) && data.rslt.obj.hasClass("toConfig")){ // do ajax call if click page
 			
			$.ajax({
				type: "GET",
				url: "/pageTreeNodeDetail",
				cache: false,
				data: "nodeUuid="+nodeUuid,
				dataType:"html",
				success: function(aData, textStatus){
					
			    	// for ps style detail container
			    	if((typeof detailContainer != "undefined") && detailContainer){
			    		detailContainer.hideContainer();
			    	}
					
					
					// close previous container tree
					if(openedPageId.length>0){
						$("#"+openedPageId).children("ul").remove();
			    		if($("li.${(pageType eq 'mobile')?'mb':'dk'}")){
			        		$("li.${(pageType eq 'mobile')?'mb':'dk'}").removeClass("jstree-closed jstree-open").addClass("jstree-leaf");
			    		}
						openedPageId = "";
					}
					
					$(".pageleft").animate({
						'width': '0'
					}, 500, function(){
						$(this).hide();
					});
					
					$(".pageright").animate({
						'width': '0'
					}, 500, function(){
						$(this).hide();
					});
					
					$(".pagecenter").animate({
						'width': '100%'
					}, 600, function(){
						$("#pageTreeSction").css({"width":"30%"})
						$("#pageDetailSection").css({"width":"70%"}).show().html(aData);
						
						// append container tree for the page
						$("li.${(pageType eq 'mobile')?'mb':'dk'}").removeClass("jstree-closed jstree-open").addClass("jstree-leaf"); // clean up
						$("#"+nodeUuid).removeClass("jstree-leaf").addClass("jstree-closed"); // set current page node to have open '+' sign
						// open the tree and set the pageid in the global var
						openedPageId = nodeUuid;
						$("#generalPageTree").jstree("open_all", "#"+nodeUuid);
						
						
						var mousemoveListener = {
							    handler: function(e){
									e.stopImmediatePropagation();
									//console.log("[x: " + e.pageX + " | y: " + e.pageY+"] [this.position.left: " + $(this).position().left + " | this.position.top: " + $(this).position().top + " | offset.left: " + $(this).offset().left + " | offset.top: " + $(this).offset().top + "]");
							    	var x = Math.round(e.pageX-$(this).offset().left);
							    	var y = Math.round(e.pageY-$(this).offset().top);
							    	//console.log("x: "+x+" | y: "+y);

							    	if(hideFakeContainerTimer!=null){
								    	clearTimeout(hideFakeContainerTimer);
							    	}
							    	
									$(".fakeContainer").css({
										top: y<460?e.pageY+2+"px":e.pageY-40+"px",
										left: x<400?e.pageX+15+"px":e.pageX-80+"px",
										width: "100px",
										display: "block",
										color: "white"
									}).html("x:"+x+" y:"+y+"<br/>right click...");
									
									hideFakeContainerTimer = setTimeout(function(){
										//console.log("timeout....");
										$(".fakeContainer").css({
											top: "0",
											left: "-100000px",
											width: "0",
											height: "0",
											display: "none"
										}).html("");
										
									},1000)
							    }
							};						
						
						
						
						// append resizable to the container
						$(".toplevel").each(function(){
							
							if(!$(this).hasClass("preview")){
								
								$(this).resizable(
										{
											containment:$(this).parent(".clickForContainer"),
											handles: $(this).hasClass("leftright")?'e, w':'n, s',
	/* 												
											start: function( event, ui ) {
												console.log("start!!");
												$(".clickForContainer").unbind("mousemove", mousemoveListener.handler);
											},
	 */										
											stop: function(event, ui){
												//console.log("resize stoped.....");
												var resizedContainerId = $(this).attr("id");
												var resizedLeftPosition = ui.position.left;
												var resizedTopPosition = ui.position.top;
												var resizedWidth = ui.size.width;
												var resizedHeight = ui.size.height;
												
												$.ajax({
													type: "GET",
													url: "/resizeContaier",
													cache: false,
													data: "containerId="+resizedContainerId+"&leftPosition="+resizedLeftPosition+"&topPosition="+resizedTopPosition+"&width="+resizedWidth+"&height="+resizedHeight,
													dataType:"json",
													success: function(aData, textStatus){
														if(aData.success){
															// reset resized container based on return value
															$("#ct_"+aData.response1.containeruuid).css({
																'top' : aData.response1.topposition+"px",
																'left' : aData.response1.leftposition+"px",
																'width' : aData.response1.width+"px",
																'height' : aData.response1.height+"px"
															})
															
															$(".clickForContainer").bind("mousemove", mousemoveListener.handler);
															
															if(aData.response2){
																var str = 'This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="'+aData.response2+'">Click here</span> for further processing.'
																$(".pageBriefInfoContainer").html(str);
															}
															
															
														}else{
															// refresh container tree
							    							$("#generalPageTree").jstree("deselect_all");
								    						$("#generalPageTree").jstree("select_node", "#"+openedPageId);

														}
														
													},
													error: function(XMLHttpRequest, textStatus, errorThrown){
														alert("system error, your result will be rollback!");
						    							$("#generalPageTree").jstree("deselect_all");
							    						$("#generalPageTree").jstree("select_node", "#"+openedPageId);
														
													}
												});
												
												
												
												
												
											}
										}
									);
							}
							
						})
						
						// append mousemove event to container for fake container display
						var hideFakeContainerTimer = null;
						
 						$(".clickForContainer").bind("mousemove", mousemoveListener.handler);
  						
 						$(".ui-resizable-handle").mousedown(function(e){
							//e.stopImmediatePropagation();
 							//console.log("unbind!");
 							$(".clickForContainer").unbind("mousemove", mousemoveListener.handler);	
 						})
 						
/*  						
 						$(".ui-resizable-handle").mouseenter(function(e){
 							console.log("unbind!");
 							$(".clickForContainer").unbind("mousemove");	
 						}).mouseleave(function(e){
 							console.log("bind!");
 							$(".clickForContainer").bind("mousemove", mousemoveListener.handler);
 						})
 */						
						
					});
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, refresh page and try again!");
				}
			});
			
 	
			
			
		}else if(ref=="cmSched"){
			
			ajaxForContentModuleSched(parentUuid, data.rslt.obj.attr("id"));
			
			
		}else if(ref=="miSched"){
			
			//console.log("you selected misched!!!!");
			ajaxForModuleInstanceSched(parentUuid, data.rslt.obj.attr("id"));
		}else if(ref=="container"){ // select container node
			//console.log("click the container");
		
			$.colorbox({
				width : 800,
				height : 600,
                escKey: false,
				href: "/containerDetailSetup?containerId="+nodeUuid,
				opacity: 0.4,
				overlayClose: false,
				onComplete:function(){
					
				}
				
			});
		
		
		
		
		
		
		
		
		
		
		}
		
	})
	.bind("create.jstree", function (event, data) {
		//console.log("create node");
		
		
		var parentNodeUuid = data.rslt.parent.attr("id");
		
		if(data.rslt.obj.attr("rel")=="folder"){
			//console.log("create a folder!!!");
			var postValue = {"newNodetype": "fd", "parentNodeUuid" : parentNodeUuid, "nodeName" : data.rslt.name, "pageTreeLevelViewType" : "${(pageType eq 'mobile')?'mb':'dk'}"};
			
			$.ajax({
				type: "POST",
				url: "/pageNodeCreate",
				cache: false,
				data: postValue,
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						$(data.rslt.obj).attr("id", aData.response1);		
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(aData.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="page"){
			
			
			//console.log("create a page!!!");
			var postValue = {"newNodetype" : "${(pageType eq 'mobile')?'mb':'dk'}", "parentNodeUuid" : parentNodeUuid, "nodeName" : data.rslt.name, "pageTreeLevelViewType" : "${(pageType eq 'mobile')?'mb':'dk'}"};
			$.ajax({
				type: "POST",
				url: "/pageNodeCreate",
				cache: false,
				data: postValue,
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						$(data.rslt.obj).attr("id", aData.response1);	
						
						// coloring the tree's node for container
						var totalStyleSheets = document.styleSheets.length; 
						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-leaf a.hex_'+aData.response2.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+aData.response2.hexColor+'; height: 15px;}', 0);
						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-open a.hex_'+aData.response2.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+aData.response2.hexColor+'; height: 15px;}', 0);		
						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-closed a.hex_'+aData.response2.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+aData.response2.hexColor+';height: 15px;}', 0);		
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(aData.response1)
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="cmSched"){
			var domvalue = data.rslt.obj.attr("domvalue");
			//console.log(domvalue);
			// ajax call to create a new schedule
			$.ajax({
				type: "POST",
				url: "/newContainerModuleSchedule",
				cache: false,
				data: domvalue+"&scheduleName="+escape(data.rslt.name),
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
						alert("New schedule is created, the new schedule name is: "+adata.response1.schedulename);
						
						// select the page again:
						$("#generalPageTree").jstree("deselect_all");
						$("#generalPageTree").jstree("select_node", $("#"+adata.response2.pageuuid));
						
						if(adata.response2.moduletype=="pm"){
							data.rslt.obj.removeClass("toModuleinstancesched");
						}
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("new container module schedule failed!");
					$.jstree.rollback(data.rlbk);
				
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="miSched"){
			var domvalue = data.rslt.obj.attr("domvalue");
			//console.log(domvalue);
			// ajax call to create a new schedule
			$.ajax({
				type: "POST",
				url: "/newModuleInstanceSchedule",
				cache: false,
				data: domvalue+"&scheduleName="+escape(data.rslt.name),
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
						alert("New schedule is created, the new schedule name is: "+adata.response1.schedulename);
						
						// select the page again:
						$("#generalPageTree").jstree("deselect_all");
						$("#generalPageTree").jstree("select_node", $("#"+adata.response2.pageuuid));
						
						
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("new module-instance schedule failed!");
					$.jstree.rollback(data.rlbk);
				
				}
			});
			
		}
		
	})
	.bind("remove.jstree", function (event, data) {
		var nodeUuid = data.rslt.obj.attr("id");
		var nodeRel = data.rslt.obj.attr("rel")
		
		// ajax call to delete the node
		$.ajax({
			type: "GET",
			url: "/pageNodeDelete",
			cache: false,
			data: "nodeId="+nodeUuid+"&relType="+nodeRel,
			dataType:"json",
			success: function(aData, textStatus){
				if(aData.success){
					if(nodeRel=="container"){
						$("#ct_"+aData.response1).remove();
						
						if(aData.response2){
							var str = 'This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="'+aData.response2+'">Click here</span> for further processing.'
							$(".pageBriefInfoContainer").html(str);
						}
						
					}else if(nodeRel=="page"){
						$("#pg_"+aData.response1).find(".ptnd_contentSection").remove();
						$("#pg_"+aData.response1).find(".pageBriefInfoContainer").html("");
					}else if(nodeRel=="cmSched"){
						$("#ct_"+aData.response2).removeClass("scheduledContainer").css("background-image", "none");
					}
				}else{
					$.jstree.rollback(data.rlbk);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("system error, your result will be rollback!");
				$.jstree.rollback(data.rlbk);
			}
		});
		
	})
	
	.bind("rename.jstree", function (event, data) {
		var currentNodeId = data.rslt.obj.attr("id");
		var newName = escape(data.rslt.new_name);
		//console.log("... rename");
		
		if(data.rslt.obj.attr("rel")=="folder" || data.rslt.obj.attr("rel")=="page"){
			$.ajax({
				type: "GET",
				url: "/updatePageDetailValue",
				cache: false,
			    data: "pageId="+currentNodeId+"&updateValue="+newName+"&valueName=prettyname",
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="container"){
			$.ajax({
				type: "GET",
				url: "/updateContainerDetailValue",
				cache: false,
			    data: "targetId="+currentNodeId+"&updateValue="+newName+"&valueName=prettyname",
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
						
                        if(adata.response3){
                            var str = 'This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="'+adata.response3+'">Click here</span> for further processing.'
                            $(".pageBriefInfoContainer").html(str);
                        }
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="cmSched"){
			
			$.ajax({
				type: "POST",
				url: "/editContainerModuleSchedule",
				cache: false,
				data: "updatetype=rename&schedUuid="+currentNodeId+"&scheduleName="+newName,
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("edit container-module schedule failed!");
					$.jstree.rollback(data.rlbk);
				
				}
			});
			
			
		}else if(data.rslt.obj.attr("rel")=="miSched"){
			
			$.ajax({
				type: "POST",
				url: "/editModuleInstanceSchedule",
				cache: false,
				data: "updatetype=rename&schedUuid="+currentNodeId+"&scheduleName="+newName,
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("edit module-instance schedule failed!");
					$.jstree.rollback(data.rlbk);
				
				}
			});
			
		}
		
	})
	
	
	.bind("hover_node.jstree", function(event, data) {

		var nodeUuid = data.rslt.obj.attr("id");

		clearTimeout(treeNodeIconsTimeout);
		$("#generalPageTree .nodeFunctionIcons").remove();

		data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
		$(".nodeFunctionIcons").bind("mouseenter", function(){
			clearTimeout(treeNodeIconsTimeout);
		})
		$(".nodeFunctionIcons").bind("mouseleave", function(){
			$(this).remove();
		})			
		
		// hide all icons first:
		$("#generalPageTree .nodeFunctionIcons img").hide();
		
			// show icons and bind actions based on class name
			if(data.rslt.obj.hasClass("create")){
				
				$("#generalPageTree .nodeFunctionIcons .folderCreateIconImg").click(function(){
					$("#generalPageTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"folder", "class":"create delete toRename"}});
					//this.create(obj, "last", {"attr" : {"rel":"folder", "class":"create delete"}});
				})
				$("#generalPageTree .nodeFunctionIcons .folderCreateIconImg").show();
				
				$("#generalPageTree .nodeFunctionIcons .newPageIconImg").click(function(){
					$("#generalPageTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"page", "class":"toConfig toCopy toShare toRename delete pageDetail${(pageType eq 'mobile')?' mb':' dk'}"}});
				})
				$("#generalPageTree .nodeFunctionIcons .newPageIconImg").show();
				
			}
			
 			if(data.rslt.obj.hasClass("toContainerModuleSched")){
				$("#generalPageTree .nodeFunctionIcons .newContainerModuleScheduleIconImg").click(function(){
					//$("#generalPageTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"cmSched", "class":"toRename delete toModuleinstancesched toConfig noMiSched", "domvalue":"{'moduleid':'111', 'fromdate':'2000', 'todate':'3000', 'priority':'9'}"}});
					ajaxForContentModuleSched(data.rslt.obj.attr("id"), "newSched");
					
				})
				$("#generalPageTree .nodeFunctionIcons .newContainerModuleScheduleIconImg").show();
 			}
			
 			if(data.rslt.obj.hasClass("toModuleinstancesched")){
				$("#generalPageTree .nodeFunctionIcons .newModuleInstanceScheduleIconImg").click(function(){
					//$("#generalPageTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"miSched", "class":"toRename delete"}});
					ajaxForModuleInstanceSched(data.rslt.obj.attr("id"), "newSched");
				})
				$("#generalPageTree .nodeFunctionIcons .newModuleInstanceScheduleIconImg").show();
 			}
			
 			if(data.rslt.obj.hasClass("delete")){
 				$("#generalPageTree .nodeFunctionIcons .deleteIconImg").click(function(){
					if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
	 					$("#generalPageTree").jstree("remove", data.rslt.obj);
					}

 				})
 				$("#generalPageTree .nodeFunctionIcons .deleteIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("pageDetail")){
				$("#generalPageTree .nodeFunctionIcons .configIconImg").click(function(){
					$("#generalPageTree").jstree("deselect_all");
					$("#generalPageTree").jstree("select_node", data.rslt.obj);
				})
				$("#generalPageTree .nodeFunctionIcons .configIconImg").show();
 				
 			}
 			
 			if(data.rslt.obj.hasClass("toConfig")){
				$("#generalPageTree .nodeFunctionIcons .configIconImg").click(function(){
					$("#generalPageTree").jstree("deselect_all");
					$("#generalPageTree").jstree("select_node", data.rslt.obj);
				})
				$("#generalPageTree .nodeFunctionIcons .configIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("toRename")){
				$("#generalPageTree .nodeFunctionIcons .renameIconImg").click(function(){
					$("#generalPageTree").jstree("rename", data.rslt.obj);
				})
				$("#generalPageTree .nodeFunctionIcons .renameIconImg").show();
 			}
 			
 			
 			
 			
 			
 			
 			
 			
 			if(data.rslt.obj.hasClass("toDefaultModule")){
				$("#generalPageTree .nodeFunctionIcons .newModuleIconImg").click(function(){
					ajaxForContainerDefaultModuleSetup(data.rslt.obj.attr("id"));
				})
				$("#generalPageTree .nodeFunctionIcons .newModuleIconImg").attr(
						{"title":"Add default module to container", 
						 "alt":"Add default module to container"}
				).show();
 			}
 			
 			if(data.rslt.obj.hasClass("toColoring")){
/* 
 				$("#generalPageTree .nodeFunctionIcons .coloringIconImg").click(function(){
					//console.log("coloring...");			    	
					
				})
 */				
				var popupContent = '<input class="jsColor">';
				var leftOffset = "-60";
				
				var containerColor = $("#ct_"+data.rslt.obj.attr("id")).attr("colorvalue");
				$("#generalPageTree .nodeFunctionIcons .coloringIconImg")
					.attr("domvalue", "{'topOffset':'0', 'leftOffset':'"+leftOffset+"', 'popupContent':'"+popupContent+"', 'coloringAjax':'updateContainerDetailValue', 'currentColor':'"+containerColor+"', 'coStyleElementId':'ct_"+data.rslt.obj.attr("id")+"'}")
					.show();
				
 			}
 			
 			
			
 			
 			// for container's extra action
 			if(data.rslt.obj.attr("rel")=="container"){
 	 			// for container highlight
 				$("#ct_"+nodeUuid).css("border", "1px solid red");

 	 			if(data.rslt.obj.hasClass("toContainerModuleSched") && (!data.rslt.obj.hasClass("jstree-leaf"))){
 	 	 			
 	 	 			// highlight current schedule
 	 				 $.ajax({
 	 				     type: "GET",
 	 				     url: "/getCurrentScheduleForContainer",
 	 				     cache: false,
 	 				     data: "containerId="+nodeUuid,
 	 				     dataType:"json",
 	 				     success: function(csdata, textStatus){
 	 				    	 if(csdata.success){
 	 				    		 if(csdata.response1){
 	 				    			 if(csdata.response1.currentModuleInstanceSchedule){
 	 	 				    			 $("#"+csdata.response1.currentModuleInstanceSchedule).children("a").addClass("redBorder");
 	 				    			 }
 	 				    			 if(csdata.response1.currentContainerModuleSchedule){
 	 	 				    			 $("#"+csdata.response1.currentContainerModuleSchedule).children("a").addClass("redBorder");
 	 				    			 }
 	 				    		 }
 	 				    		 
 	 				    		 if(csdata.response2){
 	 				    			 showPagebuildInfo($(".actionInfoContent"), csdata.response2, "info");
 	 				    		 }
 	 				    		 
 	 				    	 }else{
 	 				    		showPagebuildInfo($(".actionInfoContent"), csdata.response1);
 	 				    	 }
 	 				     	
 	 				     },
 	 				     error: function(XMLHttpRequest, textStatus, errorThrown){
 	 				
 	 				     }
 	 				 });
 	 			}
 	 			
 	 			
 	 			
 			}
			
			
		
		
	})
	.bind("dehover_node.jstree", function(event, data) {
		var nodeUuid = data.rslt.obj.attr("id");

		treeNodeIconsTimeout=setTimeout(function(){
			data.rslt.obj.find(".nodeFunctionIcons").remove();
		},1000)
		
		// remove container's hightlight
		$(".clickForContainer").css("border", "none");
		
		// remove currentSchedule hightlight;
		$("#generalPageTree a.redBorder").removeClass("redBorder");
		
	})
	;
	
	$(".pageChangedForProcess").live("click", function(){
		var pageUuid = $(this).attr("domvalue");
		 $.ajax({
		     type: "GET",
		     url: "/getPageChangedInfo",
		     cache: false,
		     data: "pageUuid="+pageUuid,
		     dataType:"html",
		     success: function(data, textStatus){
					$.colorbox({
						width : 500,
						height : 300,
						html: data,
                        escKey: false,
						//inline: true, 
						//href: mediaUploadContainerContent,
						opacity: 0.4,
						overlayClose: false
					});
		    	 
		    	 
		     },
		     error: function(XMLHttpRequest, textStatus, errorThrown){
		         alert("failed");
		
		     }
		 });
	});
	
	
	
})


function ajaxForModuleInstanceSched(containerModuleSchedId, schedId){
	$.ajax({
		type: "GET",
		url: "/getModuleInstanceScheduleConfig",
		cache: false,
	    data: "org=${orgUuid}&schedId="+schedId+"&containerModuleSchedId="+containerModuleSchedId,
		dataType:"html",
		success: function(data, textStatus){
			
			$.colorbox({
				width : 800,
				height : 600,
				html: data,
                escKey: false,
				//inline: true, 
				//href: mediaUploadContainerContent,
				opacity: 0.4,
				overlayClose: false,

				onOpen:function(){
				},
				onLoad:function(){  },
				onComplete:function(){

					// create a instanceTreeForSched
					$("#instanceTreeForSched").jstree({ 
						"plugins" : [ "themes", "html_data", "ui", "crrm", "types" ],
						"themes" : {
							"theme" : "classic",
							"dots" : true,
							"icons" : true
						},
						"types" : {
							"types" : {
								
								"folder" :{
										"select_node" : function(){
											return false;
										} 
								},
								"default":{
										"select_node" : function(){
											return false;
										} 
								},
								"module":{
										"select_node" : function(){
											return false;
										} 
								},
								"instance":{
										"select_node" : function(){
											if($("#instanceTreeForSched").hasClass("preview")){
												return false;
											}else{
												return true;
											}
										} 
								},
								
							}
						},
						
					})
					.bind("loaded.jstree", function (event, data) {
						$(this).jstree("open_all");
					}) 
					.bind("select_node.jstree", function(event, data){
						var nodeUuid = data.rslt.obj.attr("id");
						var parentUuid = $.jstree._reference('#instanceTreeForSched')._get_parent(data.rslt.obj).attr("id");
						var rel = data.rslt.obj.attr("rel");
						if(rel=="instance"){
							
							$.ajax({
								type: "GET",
								url: "/moduleInstancePreview",
								cache: false,
							    data: "previewType=module&moduleId="+parentUuid+"&moduleInstanceUuid="+nodeUuid,
								dataType:"html",
								success: function(mdata, textStatus){
									$(".schedInstanceSelected").html(mdata);
								},
								error: function(XMLHttpRequest, textStatus, errorThrown){
									alert("System error, can't get module for moduleUuid: "+nodeUuid);
								}
							});
						}
					})
					
					
					// bind click event to "newModuleInstanceSchedConfirm"
					$(".newModuleInstanceSchedConfirm").click(function(){
						
						// do a simple validation : user must select module
						if($("#moduleInstanceUuidForDisplayFragment") && $("#moduleInstanceUuidForDisplayFragment").val()){
					        var theForm = $("form[name='moduleInstanceSchedConfig']");
					        var serializedData = theForm.serialize();

							//console.log("serializedData: "+serializedData);
							$.colorbox.close();
							$("#generalPageTree").jstree("create", $("#"+containerModuleSchedId), "last", {"data":"newSchedule", "attr" : {"rel":"miSched", "class":"toRename delete toConfig", "domvalue":serializedData}});						
						}else{
							alert("You must select a instance on the left for the schedule.");
						}
						
					})

					
					// bind click event to "editModuleInstanceSchedConfirm"
					$(".editModuleInstanceSchedConfirm").click(function(){
						//console.log("editContainerModuleSchedConfirm");
				        var theForm = $("form[name='moduleInstanceSchedConfig']");
				        var serializedData = theForm.serialize();
						
						$.ajax({
							type: "POST",
							url: "/editModuleInstanceSchedule",
							cache: false,
							data: serializedData+"&updatetype=general",
							dataType:"json",
							success: function(adata, textStatus){
								if(adata.success){
									$.colorbox.close();
								}else{
									alert(adata.response1);
								}
							},
							error: function(XMLHttpRequest, textStatus, errorThrown){
								alert("edit module-instance schedule failed!");
							
							}
						});

					})
					
					
				},
				onCleanup:function(){  },
				onClosed:function(){  }
				
			})
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("System can't get moduletree for container's schedule.");
		}
	});
	
}

function ajaxForContentModuleSched(containerId, schedId){
	//schedId for new Schedule : "newSched"
	//console.log("......."+schedId);
	
	//console.log("container id: "+containerId);
	
	// select module, fromtime, totime, priority
	$.ajax({
		type: "GET",
		url: "/getContainerModuleScheduleConfig",
		cache: false,
	    data: "org=${orgUuid}&schedId="+schedId+"&containerId="+containerId,
		dataType:"html",
		success: function(data, textStatus){
			$.colorbox({
				width : 800,
				height : 600,
				html: data,
                escKey: false,
				//inline: true, 
				//href: mediaUploadContainerContent,
				opacity: 0.4,
				overlayClose: false,

				onOpen:function(){
				},
				onLoad:function(){  },
				onComplete:function(){
					
					// create a moduleTreeForSched
					$("#moduleTreeForSched").jstree({
						"plugins" : ["themes", "json_data", "ui", "types"],
						"core" : {
							"html_titles" : true
						},
						"types" : {
							"types" : {
								
								"folder" :{
									"select_node" : function(){
										return false;
									} 
								},
								"default":{
									"select_node" : function(){
										return false;
									} 
								},
								"module":{
									"select_node" : function(){
										
										if($("#moduleTreeForSched").hasClass("preview")){
											return false;
										}else {
											return true;
										}
										
									} 
								},
								"productModule":{
									"select_node" : function(){
										
										if($("#moduleTreeForSched").hasClass("preview")){
											return false;
										}else {
											return true;
										}
									} 
								}
								
							}
						},
						"themes" : {
							"theme" : "classic",
							"dots" : true,
							"icons" : true
						},
						"json_data" : { 
							"ajax" : {
								"data" : function(node){
									if(node==-1){
										return "operation=get_moduleTree&org=${orgUuid}";
									}else{
										return "operation=get_moduleTreeForSched&org=${orgUuid}&parentNodeId="+node.attr("id");
									}
								}, 
								"url":function (node){
									//console.log("node: "+node);
									return "/moduleTreeMain";
								},
								"success": function (data) {
				                    //return new_data;
				                    //console.log("success");
				                }
							}
						}
					})
					.bind("select_node.jstree", function(event, data){
						var resetModuleConfirm = true;
						if(schedId!="newSched"){
							resetModuleConfirm = confirm("You try to change module setup for the schedule, which could lost all your module-instance-schedules if you have!");
						}
						
						if(resetModuleConfirm){
							var nodeUuid = data.rslt.obj.attr("id");
							
							$.ajax({
								type: "GET",
								url: "/getModuleForContainerSchedule",
								cache: false,
							    data: "moduleid="+nodeUuid,
								dataType:"html",
								success: function(mdata, textStatus){
									$(".schedModuleSelected").html(mdata);
								},
								error: function(XMLHttpRequest, textStatus, errorThrown){
									alert("System error, can't get module for moduleUuid: "+nodeUuid);
								}
							});
							
						}
						
					})
					.bind("loaded.jstree", function (event, data) {
						
					})
					.bind("after_open.jstree", function(event, data){
						//highlight the selected module
						var selectedModuleId = $("#moduleUuidForDisplayFragment").val();
						$("#"+selectedModuleId).children("a").children("span.prettyname").addClass("redBorder");
					})
					
					
					
					// bind click event to "newContainerModuleSchedConfirm"
					$(".newContainerModuleSchedConfirm").click(function(){
						
						// do a simple validation : user must select module
						if($("#moduleUuidForDisplayFragment") && $("#moduleUuidForDisplayFragment").val()){
					        var theForm = $("form[name='containerModuleSchedConfig']");
					        var serializedData = theForm.serialize();

							//console.log("serializedData: "+serializedData);
							$.colorbox.close();
							$("#generalPageTree").jstree("create", $("#"+containerId), "last", {"data":"newSchedule", "attr" : {"rel":"cmSched", "class":"toRename delete toModuleinstancesched toConfig noMiSched", "domvalue":serializedData}});						
						}else{
							alert("You must select a module on the left for the schedule.");
						}
						
					})
					
					// bind click event to "editContainerModuleSchedConfirm"
					$(".editContainerModuleSchedConfirm").click(function(){
						//console.log("editContainerModuleSchedConfirm");
				        var theForm = $("form[name='containerModuleSchedConfig']");
				        var serializedData = theForm.serialize();
						
						$.ajax({
							type: "POST",
							url: "/editContainerModuleSchedule",
							cache: false,
							data: serializedData+"&updatetype=general",
							dataType:"json",
							success: function(adata, textStatus){
								if(adata.success){
									$.colorbox.close();
								}else{
									alert(adata.response1);
								}
							},
							error: function(XMLHttpRequest, textStatus, errorThrown){
								alert("edit container module schedule failed!");
							
							}
						});

					})
					
					
				},
				onCleanup:function(){  },
				onClosed:function(){  }
				
			})
				
			
			
			
			
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("System can't get moduletree for container's schedule.");
		}
	});
	
}

function ajaxForContainerDefaultModuleSetup(containerId){
	
	$.colorbox({
		width : 800,
		height : 700,
		//iframe : true,
		href: "/getContainerDefaultModuleSetup?containerId="+containerId,
		//title: "asdfasdf",
		opacity: 0.4,
		overlayClose: false,
        escKey: false,
		onComplete:function(){
			
			// create a moduleTreeForContainerDefaultModule
			$("#moduleTreeForContainerDefaultModule").jstree({
				"plugins" : ["themes", "json_data", "ui", "types"],
				"core" : {
					"html_titles" : true
				},
				"types" : {
					"types" : {
						
						"folder" :{
							"select_node" : function(){
								return false;
							} 
						},
						"default":{
							"select_node" : function(){
								return false;
							} 
						},
						"module":{
							"select_node" : function(){
								if($("#moduleTreeForContainerDefaultModule").hasClass("preview")){
									return false;
								}else{
									return true;
								}
							} 
						},
						"productModule":{
							"select_node" : function(){
								
								if($("#moduleTreeForContainerDefaultModule").hasClass("preview")){
									return false;
								}else{
									return true;
								}
							} 
						}
						
					}
				},
				"themes" : {
					"theme" : "classic",
					"dots" : true,
					"icons" : true
				},
				"json_data" : { 
					"ajax" : {
						"data" : function(node){
							if(node==-1){
								return "operation=get_moduleTree&org=${orgUuid}";
							}else{
								return "operation=get_moduleTreeForSched&org=${orgUuid}&parentNodeId="+node.attr("id");
							}
						}, 
						"url":function (node){
							//console.log("node: "+node);
							return "/moduleTreeMain";
						},
						"success": function (data) {
		                    //return new_data;
		                    //console.log("success");
		                }
					}
				}
			})
			.bind("select_node.jstree", function(event, data){
				
				var nodeUuid = data.rslt.obj.attr("id");
				
				$.ajax({
					type: "GET",
					url: "/getModuleForContainerSchedule",
					cache: false,
				    data: "moduleid="+nodeUuid,
					dataType:"html",
					success: function(mdata, textStatus){
						$(".defaultModuleSelected").html(mdata);
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("System error, can't get module for moduleUuid: "+nodeUuid);
					}
				});
				
			})
			.bind("loaded.jstree", function (event, data) {
				
			})
			.bind("after_open.jstree", function(event, data){
				//highlight the selected module
				var selectedModuleId = $("#moduleUuidForDisplayFragment").val();
				$("#"+selectedModuleId).children("a").children("span.prettyname").addClass("redBorder");
			})
			
			
			
			// bind click event to "newContainerModuleSchedConfirm"
			$(".defaultContainerModuleConfirm").click(function(){
				
				// do a simple validation : user must select module
				if($("#moduleUuidForDisplayFragment") && $("#moduleUuidForDisplayFragment").val()){
			        var theForm = $("form[name='containerDefaultModuleConfig']");
			        var serializedData = theForm.serialize();
			        
					$.ajax({
						type: "POST",
						url: "/editContainerDefaultModule",
						cache: false,
						data: serializedData,
						dataType:"json",
						success: function(adata, textStatus){
							if(adata.success){
								$.colorbox.close();
							}else{
								alert(adata.response1);
							}
						},
						error: function(XMLHttpRequest, textStatus, errorThrown){
							alert("edit container module schedule failed!");
						
						}
					});
			        
			        
			        
			        
			        
					
				}else{
					alert("You must select a module on the left for the schedule.");
				}
				
			})
			
			
		}
		
		
		
	});
	
}


</script>