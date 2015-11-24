<div class="mobilePageTree">

	<div id="mobilePageTree">
	
	</div>

</div>

<script type="text/javascript">

// the page is has been opened
openedPageId = "";

$(function () {
	
	
	$("#mobilePageTree").jstree({
		"plugins" : ["themes", "json_data", "ui", "types", "contextmenu", "crrm"],
		"core" : {
			"html_titles" : true
		},
		"themes" : {
			"theme" : "classic",
			"dots" : true,
			"icons" : false
		},
		"json_data" : { 
			"ajax" : {
				"data" : function(node){
					
					if(node==-1){ // get root
						return "operation=get_mobileTree&org=${orgUuid}";
					}else{
						if(node.attr("rel")=="default" && node.hasClass("mb")){ // get container tree
							return "operation=get_container&org=${orgUuid}&pageid="+node.attr("id");
						}else if(node.attr("rel")=="folder"){ // get page tree if open folder
							return "operation=get_mobileTree&org=${orgUuid}&parentNodeId="+node.attr("id"); 
						}else if(node.attr("rel")=="container"){
							return "operation=get_container&org=${orgUuid}&pageid="+node.attr("id")+"&parentNodeId="+node.attr("id");
						}
					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					
					if(node==-1){
						return "/mobileTreeMain";
					}else{
						if(node.attr("rel")=="default" && node.hasClass("mb")){ // get container tree
							return "/containerTreeMain";
						}else if(node.attr("rel")=="folder"){ // get page tree if open folder
							return "/mobileTreeMain";
						}else if(node.attr("rel")=="container"){
							return "/containerTreeMain"
						}
					}
					
				},
				"success": function (data) {
                    //return new_data;
                    //console.log("success");
                }
			}
		},
		"contextmenu": {
			"items": function(node){
				var hasCreate = false;
				var hasRemove = false;
				var hasPageDetail = false;
				if(node.hasClass("create")){
					hasCreate = true;
				}
				if(node.hasClass("delete")){
					hasRemove = true;
				}
				if(node.hasClass("pageDetail")){
					hasPageDetail = true;
				}
				
				return{
					"foldCreate":hasCreate?
						{
							// The item label
							"label"				: "create folder",
							// The function to execute upon a click
							"action"			: function(obj){
								this.create(obj, "last", {"attr" : {"rel":"folder", "class":"create delete"}});
							},
							// All below are optional 
							"_disabled"			: false,
							"_class"			: "createfolder",	// class is applied to the item LI node
							"separator_before"	: false,	// Insert a separator before the item
							"separator_after"	: true,		// Insert a separator after the item
							// false or string - if does not contain `/` - used as classname
							"icon"				: false,
						}								
						:false,
					"pageCreate":hasCreate?
						{
							// The item label
							"label"				: "new page",
							// The function to execute upon a click
							"action"			: function(obj){
								this.create(obj, "last", {"attr" : {"rel":"default", "class":"delete pageDetail mb"}});
							},
							// All below are optional 
							"_disabled"			: false,
							"_class"			: "newProduct",	// class is applied to the item LI node
							"separator_before"	: false,	// Insert a separator before the item
							"separator_after"	: true,		// Insert a separator after the item
							// false or string - if does not contain `/` - used as classname
							"icon"				: false,
						}								
						:false,
					"remove":hasRemove?
						{
							// The item label
							"label"				: "remove",
							// The function to execute upon a click
							"action"			: function(obj){
								var num = this._get_children(obj).length;
								//console.log("num: "+num);
								if(num>0){
									if(confirm('There have '+num+' folders or/and products inside, do you really like to remove it?')){
										alert("remove now!!!");
									}
								}else{
									alert("remove now!!!");
								}
							},
							// All below are optional 
							"_disabled"			: false,
							"_class"			: "remove",	// class is applied to the item LI node
							"separator_before"	: false,	// Insert a separator before the item
							"separator_after"	: true,		// Insert a separator after the item
							// false or string - if does not contain `/` - used as classname
							"icon"				: false,
						}								
						:false,
						"pageDetail":hasPageDetail?{
							// The item label
							"label"				: "page detail",
							// The function to execute upon a click
							"action"			: function(obj){
								//console.log("page detail");
								var pageUuid = obj.attr("id");
 								
								 $.ajax({
								     type: "GET",
								     url: "/getPageDetail",
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
											overlayClose: false,
											onComplete:function(){
												
											    var cssEditor = CodeMirror.fromTextArea(document.getElementById("pagecssEditor"), {
											        lineNumbers: true,
											    	mode: "css"
											    });
											    cssEditor.on("blur", function(){
													//console.log("onBlur");
													cssEditor.save();
												});

												// for tabs 
												$("#tabs").tabs({
													select: function( event, ui ) {
														//console.log("tab select");
													},
													add: function( event, ui ) {
														//console.log("tab add");
													},
													create: function( event, ui ) {
														//console.log("tab create");
													},
													disable: function( event, ui ) {
														//console.log("tab disable");
													},
													enable: function( event, ui ) {
														//console.log("tab enable");
													},
													load: function( event, ui ) {
														//console.log("tab load");
													},
													remove: function( event, ui ) {
														//console.log("tab remove");
													},
													show: function( event, ui ) {
														//console.log("tab show");
												    	//xmlEditor.refresh();
												    	//jspEditor.refresh();
													}
													
												});
												
											}
										});
								    	 
								     },
								     error: function(XMLHttpRequest, textStatus, errorThrown){
								         alert("failed");
								
								     }
								 });
 
							},
							// All below are optional 
							"_disabled"			: false,
							"_class"			: "remove",	// class is applied to the item LI node
							"separator_before"	: false,	// Insert a separator before the item
							"separator_after"	: true,		// Insert a separator after the item
							// false or string - if does not contain `/` - used as classname
							"icon"				: false,
							
						}
						:false,
					"ccp":false
				}
				
			}
		}
		
	})
	.bind("loaded.jstree", function (event, data) {
		//console.log("loaded!");
		
	})
	.bind("select_node.jstree", function(event, data){
		var nodeUuid = data.rslt.obj.attr("id");
		
		// ajax call to get node info (folder, product, ...)
		//console.log("select page tree node : "+nodeUuid);
		
		var ref = data.rslt.obj.attr("rel");
		
		// select page node: 
		if(ref=="default" && data.rslt.obj.hasClass("mb")){ // do ajax call if click page
 			
			$.ajax({
				type: "GET",
				url: "/pageTreeNodeDetail",
				cache: false,
				data: "nodeUuid="+nodeUuid,
				dataType:"html",
				success: function(aData, textStatus){
					
					// close previous container tree
					if(openedPageId.length>0){
						$("#"+openedPageId).children("ul").remove();
			    		if($("li.mb")){
			        		$("li.mb").removeClass("jstree-closed jstree-open").addClass("jstree-leaf");
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
						$("li.mb").removeClass("jstree-closed jstree-open").addClass("jstree-leaf"); // clean up
						$("#"+nodeUuid).removeClass("jstree-leaf").addClass("jstree-closed"); // set current page node to have open '+' sign
						// open the tree and set the pageid in the global var
						openedPageId = nodeUuid;
						$("#mobilePageTree").jstree("open_all", "#"+nodeUuid);
						
						// append resizable to the container
						$(".toplevel").each(function(){
							$(this).resizable(
									{
										containment:$(this).parent(".clickForContainer"),
										handles: $(this).hasClass("leftright")?'e, w':'n, s',
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
														
													}else{
														// refresh container tree
							    						if($("#mobilePageTree")){
							    							$("#mobilePageTree").jstree("deselect_all");
								    						$("#mobilePageTree").jstree("select_node", "#"+openedPageId);
							    						}else if($("#desktopPageTree")){
							    							$("#desktopPageTree").jstree("deselect_all");
								    						$("#desktopPageTree").jstree("select_node", "#"+openedPageId);
							    						}

													}
													
												},
												error: function(XMLHttpRequest, textStatus, errorThrown){
													alert("system error, your result will be rollback!");
						    						if($("#mobilePageTree")){
						    							$("#mobilePageTree").jstree("deselect_all");
							    						$("#mobilePageTree").jstree("select_node", "#"+openedPageId);
						    						}else if($("#desktopPageTree")){
						    							$("#desktopPageTree").jstree("deselect_all");
							    						$("#desktopPageTree").jstree("select_node", "#"+openedPageId);
						    						}
													
												}
											});
											
											
											
											
											
										}
									}
								);
						})
						
					});
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, refresh page and try again!");
				}
			});
			
 	
			
			
		}else if(ref=="folder"){ // select folder node
			$(".domReady_xPageDetail").click();
		}else if(ref=="container"){ // select container node
			//console.log("click the container");
		}
		
	})
	.bind("create.jstree", function (event, data) {
		//console.log("create node");
		
		
		var parentNodeUuid = data.rslt.parent.attr("id");
		
		if(data.rslt.obj.attr("rel")=="folder"){
			//console.log("create a folder!!!");
			
			$.ajax({
				type: "GET",
				url: "/pageNodeCreate",
				cache: false,
				data: "newNodetype=fd&parentNodeUuid="+parentNodeUuid+"&nodeName="+data.rslt.name+"&pageTreeLevelViewType=mb",
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						$(data.rslt.obj).attr("id", aData.response1);		
						
					}else{
						$.jstree.rollback(data.rlbk);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}else if(data.rslt.obj.attr("rel")=="default"){
			
			
			//console.log("create a page!!!");
			
			$.ajax({
				type: "GET",
				url: "/pageNodeCreate",
				cache: false,
				data: "newNodetype=mb&parentNodeUuid="+parentNodeUuid+"&nodeName="+data.rslt.name+"&pageTreeLevelViewType=mb",
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						$(data.rslt.obj).attr("id", aData.response1);		
						
					}else{
						$.jstree.rollback(data.rlbk);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}
		
		
		
		
		
		
	});
	
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

</script>