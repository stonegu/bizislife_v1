<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="productTree">


	<input type="button" value="Collapse All" onclick="$('#productTree').jstree('close_all');">
	<input type="button" value="Expand All" onclick="$('#productTree').jstree('open_all');">

	<div id="productTree">
	
	</div>
	
	<script type="text/javascript">
	$(function () {
		
		// time value for treeNodeFunctionIcons
		var treeNodeIconsTimeout;
		
		$("#productTree").jstree({
			"plugins" : ["themes", "json_data", "ui", "crrm", "dnd"],
			"core" : {
				"html_titles" : true
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
							return "operation=get_productTree&org=${orgUuid}";
						}else{
							if(node.attr("rel")=="folder" || node.attr("rel")=="default"){
								return "operation=get_productTree&org=${orgUuid}&parentNodeId="+node.attr("id");
							}else if(node.attr("rel")=="instanceView"){
								return "operation=get_schedForView&org=${orgUuid}&parentNodeId="+node.attr("id");
							}
						}
					}, 
					"url":function (node){
						//console.log("node: "+node);
						return "/productTreeMain";
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
							if(m.o.attr("rel")=="folder" || m.o.attr("rel")=="default"){
								if(m.np.attr("rel")=="folder"){
									return true;
								}
							}
							
						}else if(sourceTreeRootId!=targetTreeRootId){ // copy from another tree
							// m.o : moving obj, m.np : new parent
							if((m.o.attr("rel")=="folder" || m.o.attr("rel")=="default") && m.o.hasClass("toShare")){
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
			//console.log(".... move node!!");
			
			// you need to check the data is from the same tree or different tree
			var draggedObj = data.rslt.o;
			var originalFolderObj = data.rslt.op;
			var targetFolderObj = data.rslt.np;
			
			//var movingNodeId = data.rslt.o.attr("id");
			var originalPosition = data.inst.get_path("#" + originalFolderObj.attr("id"), true);
			
			if(originalPosition){
				$.ajax({
					type: "POST",
					url: "/moveProductNode",
					cache: false,
					data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+targetFolderObj.attr("id"),
					dataType:"json",
					success: function(aData, textStatus){
						if(aData.success){
													
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
				
			}else{ // data is dragged from another tree
				$.ajax({
					type: "POST",
					url: "/copyProductNodeToAnotherTree",
					cache: false,
					data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+targetFolderObj.attr("id"),
					dataType:"json",
					success: function(aData, textStatus){
						if(aData.success){
							// note: the target tree need to be refreshed, since trag-in tree structure could be changed!!
							// For example: moduledetail has copy permission, but direct parent-folder doesn't have copy permission, but grandparent-folder has copy permission.
							$("#productTree").jstree("refresh");
						
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
			
			
			
			if((data.rslt.obj.attr("rel")=="folder" || data.rslt.obj.attr("rel")=="default") && data.rslt.obj.hasClass("toConfig")){
				
				// ajax call to get node info (folder, product, ...)
				$.ajax({
					type: "GET",
					url: "/productTreeNodeDetail",
					cache: false,
					data: "nodeUuid="+nodeUuid,
					dataType:"html",
					success: function(aData, textStatus){
						
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
							$("#productTreeSction").css({"width":"25%"})
							$("#productDetailSection").css({"width":"75%"}).show().html(aData);
							
							
							
							
                            // for all text attribute edit
                            $(".attrDefaultTxtValueEditor").each(function(idx){
                                var attrId = $(this).attr("domvalue");
                                
                                $(this).attr('id', 'attrDefaultTxtValueEditor_'+idx);
                                var txtMirror = CodeMirror.fromTextArea(document.getElementById("attrDefaultTxtValueEditor_"+idx),
                                    {
                                        lineNumbers: true,
                                        mode: "application/x-ejs",
                                        indentUnit: 4,
                                        indentWithTabs: true,
                                        enterMode: "keep",
                                        autoCloseTags: true,
                                        readOnly: true,
                                        tabMode: "shift"
                                    });
                                txtMirror.setSize(460, 100);
                                // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
                                // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
                                // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
                                txtMirror.on("blur", function(){
                                    //console.log("onBlur");
                                    txtMirror.save();
                                });
                                
                                $("#attrDefaultTxtValue_"+attrId+" .txtAttrBtn").each(function(idx){
                                    $(this).data("txtMirror", txtMirror);
                                });
                                
                            });
							
							
							
	 
	 						// preload media tree for product attr only
							$("#mediaTreeForModuleConfig").jstree({
								"plugins" : ["themes", "json_data", "ui"],
								"core" : {
									"html_titles" : true
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
												return "operation=get_imageTree&org=${orgUuid}";
											}else{
												return "operation=get_imageTree&folderOnly=true&org=${orgUuid}&parentNodeId="+node.attr("id");
											}
										}, 
										"url":function (node){
											//console.log("node: "+node);
											return "/mediaTreeMain";
										},
										"success": function (data) {
						                    //return new_data;
						                    //console.log("success");
						                }
									}
								},

							})
							.bind("loaded.jstree", function (event, data) {
								//console.log("loaded!");
							})
							.bind("select_node.jstree", function (event, data) {
								var noSelectable = data.rslt.obj.hasClass("noSelectable");
								var nodeUuid = data.rslt.obj.attr("id");
								
								if(noSelectable){
									//console.log("no selectable!");
								}else{
									// populate the data for the form
									//$("#selectedImgSysNameForModuleConfig").val(nodeUuid);
									//$(".mediaShowArea .imgContainer").html("<img src='/getphoto?id="+nodeUuid+"&size=200'/>");
									
									// ajax call to get image list for the folder
					                $.ajax({
					                    type: "GET",
					                    url: "/getMediaListForFold",
					                    cache: false,
					                    data: "mediaType=img&folderId="+nodeUuid,
					                    dataType:"html",
					                    success: function(mlData, textStatus){
					                        $('.mediaSelectionArea').html(mlData); 
					                        
					                        
					                        
					                        
					                        // mediaSelectForm
					                        if($("#mediaSelectForm").length>0){
					                            $("img.domReady_selectMediaForModule").removeClass("selected").attr("src", "/img/vendor/web-icons/tick-box.png")
					                            if(!!($("#mediaSelectForm_defaultValue").val())){
					                                $("img[domvalue='"+$("#mediaSelectForm_defaultValue").val()+"']").addClass("selected").attr("src", "/img/vendor/web-icons/tick-button.png");
					                            }
					                        }
					                        
					                    },
					                    error: function(XMLHttpRequest, textStatus, errorThrown){
					                        alert("failed to get medialist for folder: "+nodeUuid);
					                    }
					                });
									
								}
							})
							.bind("create.jstree", function(event, data){
								//console.log("create!");
							})
	 						
	 						
	 

	 						// display product module tree for product only
							$("#moduleProductTree").jstree({
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
											"productModule":{
												"select_node" : function(){
													var treeRoot = $("#moduleProductTree");
													if(treeRoot.hasClass("preview")){
														return false;
													}else{
														return true;
													}
												} 
											},
											
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
												return "operation=get_moduleTree&org=${orgUuid}&parentNodeId="+node.attr("id");
											}
										}, 
										"url":function (node){
											//console.log("node: "+node);
											return "/moduleTreeForProductMain";
										},
										"success": function (data) {
						                    //return new_data;
						                    //console.log("success");
						                }
									}
								}
							})
							.bind("loaded.jstree", function (event, data) {
								$(this).jstree("open_all");
								
							})
							.bind("select_node.jstree", function(event, data){
								var nodeUuid = data.rslt.obj.attr("id");
								var productInstanceUuid = $("#instanceId").val();
								
								
								var selectedModuleForEntity = $("#delMoSelected_"+nodeUuid);
								if(selectedModuleForEntity.length>0){
									alert("You already selected this templated(module)!");
								}else{
									// ajax call to display the module user selected
									 $.ajax({
									     type: "GET",
									     url: "/moduleInstancePreview",
									     cache: false,
									     data: "previewType=productModule&moduleId="+nodeUuid+"&moduleInstanceUuid="+productInstanceUuid,
									     dataType:"html",
									     success: function(data, textStatus){
									     	
											$.colorbox({
												width : 600,
												height : 500,
												html: data,
						                        escKey: false,
												//inline: true, 
												//href: mediaUploadContainerContent,
												opacity: 0.4,
												overlayClose: false,
												onComplete:function(){
													
												}
											});
									    	 
									    	 
									     },
									     error: function(XMLHttpRequest, textStatus, errorThrown){
									         alert("failed");
									
									     }
									 });
									
								}
								
							})
							
							
						});
						
						
						
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("system error, refresh page and try again!");
					}
				});
				
			}else if(data.rslt.obj.attr("rel")=="instanceView" && data.rslt.obj.hasClass("editView")){
				
				$.ajax({
					type: "GET",
					url: "/getInstanceViewForm",
					cache: false,
					data: "instanceViewUuid="+nodeUuid,
					dataType:"html",
					success: function(aData, textStatus){
						
						
						$.colorbox({
							width : 800,
							height : 600,
							html: aData,
	                        escKey: false,
							//inline: true, 
							//href: mediaUploadContainerContent,
							opacity: 0.4,
							overlayClose: false,
							trapFocus: false,
							
							onOpen:function(){
							},
							onLoad:function(){  },
							onComplete:function(){
								
								var jspEditor = CodeMirror.fromTextArea(document.getElementById("jspEditor"), {
									lineNumbers: true,
									mode: "application/x-ejs",
									indentUnit: 4,
									indentWithTabs: true,
									enterMode: "keep",
									autoCloseTags: true,
									readOnly: true,
									tabMode: "shift"
								});
								jspEditor.setSize(700, 400);
								// Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
								// Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
								// events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
								jspEditor.on("blur", function(){
									//console.log("onBlur");
									jspEditor.save();
								});

								var cssEditor = CodeMirror.fromTextArea(document.getElementById("cssEditor"), {
									lineNumbers: true,
									readOnly: true,
									mode: "css"
								});
								cssEditor.setSize(700, 400);
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
								
								
								// for editViewJspCssValue && cancelEditViewJspCssValue
								var jspOriginalValue = "";
								var cssOriginalValue = "";
								
								$(".editViewJspCssValue").click(function(){
									//console.log("editModuleJspCssValue")
									var domvalue = $(this).attr("domvalue");
									if(domvalue.indexOf("Jsp")>-1){
										jspOriginalValue = jspEditor.getValue();
										jspEditor.setOption("readOnly", false);
									}else if(domvalue.indexOf("Css")>-1){
										cssOriginalValue = cssEditor.getValue();
										cssEditor.setOption("readOnly", false);
									}
									$("#"+domvalue).find(".valueSection").addClass("displaynone");
									$("#"+domvalue).find(".editSection").removeClass("displaynone");
									
								});
								
								$(".cancelEditViewJspCssValue").click(function(){
									var domvalue = $(this).attr("domvalue");
									if(domvalue.indexOf("Jsp")>-1){
										jspEditor.setValue(jspOriginalValue);
										jspEditor.setOption("readOnly", true);
									}else if(domvalue.indexOf("Css")>-1){
										cssEditor.setValue(cssOriginalValue);
										cssEditor.setOption("readOnly", true);
									}
									
									$("#"+domvalue).find(".valueSection").removeClass("displaynone");
									$("#"+domvalue).find(".editSection").addClass("displaynone");
								});
								
								$(".saveViewJspCssValue").click(function(){
									
									//console.log("save module value");
									var domvalue = $(this).attr("domvalue");
									var jsonObj = eval("("+domvalue+")");

									var theForm = $("form[id='"+jsonObj.formId+"']");
									var serializedData = theForm.serialize();
									
									$.ajax({
										type: "POST",
										url: "/updateViewValue",
										cache: false,
										data: serializedData,
										dataType:"json",
										success: function(data, textStatus){
											if(data.success){
												if(jsonObj.webSectionId.indexOf("Jsp")>-1){
													jspOriginalValue = data.response1;
													
													// update jsp/css charusage data
													if(data.response3!=null){
														$(".viewJspUsedSpace").html(data.response3+"/100");
													}
													
													$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
												}else if(jsonObj.webSectionId.indexOf("Css")>-1){
													cssOriginalValue = data.response1;
													
													// update jsp/css charusage data
													if(data.response3!=null){
														$(".viewCssUsedSpace").html(data.response3+"/100");
													}
													
													$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
												}

												
												
											}else{
												alert(data.response1);
												//$(".moduleDataStructErrorSection").html(data.response1);
											}
										},
										error: function(XMLHttpRequest, textStatus, errorThrown){
											alert("failed");
										}
									});
									
									
								});
								
							},
							onCleanup:function(){  },
							onClosed:function(){  }
							
						});
						
						
				
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("system error, you may need to refresh the page to try it again!");
					}
				});
				
			}else if(data.rslt.obj.attr("rel")=="schedule" && data.rslt.obj.hasClass("editSchedule")){
				// ajax call to get the schedule
				$.ajax({
					type: "GET",
					url: "/instanceViewSchedule",
					cache: false,
					data: "schedId="+data.rslt.obj.attr("id"),
					dataType:"html",
					success: function(aData, textStatus){
						
						$.colorbox({
							width : 800,
							height : 700,
							html: aData,
	                        escKey: false,
							//inline: true, 
							//href: mediaUploadContainerContent,
							opacity: 0.4,
							overlayClose: false,
							trapFocus: false,
							
							onOpen:function(){
							},
							onLoad:function(){  },
							onComplete:function(){},
							onCleanup:function(){  },
							onClosed:function(){  }
							
						});
						
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						//$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

					}
				});
				
			}
			
			
			
		})
		.bind("create.jstree", function (event, data) {
			var parentNodeUuid = data.rslt.parent.attr("id");
			
			if(data.rslt.obj.attr("rel")=="folder"){
				//console.log("create a folder!!!");
				
				
				if(data.rslt.obj.hasClass("clone")){
					//console.log("cloning folder...: disabled for system rebuse!!");
				}else{
					$.ajax({
						type: "GET",
						url: "/productNodeCreate",
						cache: false,
						data: "newNodetype=fd&parentNodeUuid="+parentNodeUuid+"&nodeName="+data.rslt.name,
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
					
				}
				
			}else if(data.rslt.obj.attr("rel")=="default"){
				//console.log("create a product!!!");
				
				if(data.rslt.obj.hasClass("clone")){
					//console.log(".... cloning.. product!!");
					
					
					$.ajax({
						type: "GET",
						url: "/productNodeCreate",
						cache: false,
						data: "newNodetype=et&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name)+"&cloneFrom="+data.rslt.obj.attr("domvalue"),
						dataType:"json",
						success: function(cloneData, textStatus){
							if(cloneData.success){
								$(data.rslt.obj).attr("id", cloneData.response1);		
								
							}else{
								$.jstree.rollback(data.rlbk);
								alert(cloneData.response1);
							}
						},
						error: function(XMLHttpRequest, textStatus, errorThrown){
							$.jstree.rollback(data.rlbk);
							alert("system error, your result will be rollback!");
						}
					});
					
					
					
					
					
					
				}else{
					$.ajax({
						type: "GET",
						url: "/productNodeCreate",
						cache: false,
						data: "newNodetype=et&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name),
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
					
				}
				
				
			}else if(data.rslt.obj.attr("rel")=="instanceView"){
				
				$.ajax({
					type: "POST",
					url: "/instanceViewConfig",
					cache: false,
					data: "instanceId="+parentNodeUuid+"&instanceViewType=p&instanceViewId=newView&viewname="+data.rslt.name,
					dataType:"json",
					success: function(adata, textStatus){
						if(adata.success){
							data.rslt.obj.attr("id", adata.response2.key);
						}else{
							$.jstree.rollback(data.rlbk);
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("new instance view creation failed!");
						$.jstree.rollback(data.rlbk);
					}
				});
				
			}else if(data.rslt.obj.attr("rel")=="schedule"){

				$.ajax({
					type: "POST",
					url: "/newInstanceViewSchedule",
					cache: false,
					data: "priority=0&instanceViewId="+parentNodeUuid+"&scheduleName="+data.rslt.name,
					dataType:"json",
					success: function(adata, textStatus){
						if(adata.success){
							data.rslt.obj.attr("id", adata.response1);
						}else{
							$.jstree.rollback(data.rlbk);
							alert("new instance view schedule creation failed!");
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("new instance view schedule creation failed!");
						$.jstree.rollback(data.rlbk);
					}
				});
				
			}	
			
			
			
			
			
		})
		.bind("rename.jstree", function (event, data) {
			var currentNodeId = data.rslt.obj.attr("id");
			var newName = data.rslt.new_name;
			
			if(data.rslt.obj.attr("rel")=="folder" || data.rslt.obj.attr("rel")=="default"){
				$.ajax({
					type: "GET",
					url: "/updateEntityDetailValue",
					cache: false,
				    data: "entityId="+currentNodeId+"&updateValue="+newName+"&valueName=name",
					dataType:"json",
					success: function(uData, textStatus){
						if(uData.success){
						}else{
							$.jstree.rollback(data.rlbk);
							alert(uData.response1);
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("failed");
						$.jstree.rollback(data.rlbk);
					}
				});
				
			}else if(data.rslt.obj.attr("rel")=="instanceView"){
				
				$.ajax({
					type: "GET",
					url: "/updateViewValue",
					cache: false,
					data: "viewId="+currentNodeId+"&updateType=instanceViewValue&updateValue="+newName+"&valueName=viewname",
					dataType:"json",
					success: function(data, textStatus){
						if(data.success){
						}else{
							$.jstree.rollback(data.rlbk);
							alert("rename failed!");
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("failed");
						$.jstree.rollback(data.rlbk);
					}
				});
				
			}else if(data.rslt.obj.attr("rel")=="schedule"){
				$.ajax({
					type: "GET",
					url: "/renameInstanceViewSchedule",
					cache: false,
					data: "scheduleId="+currentNodeId+"&newName="+newName,
					dataType:"json",
					success: function(data, textStatus){
						if(data.success){
						}else{
							$.jstree.rollback(data.rlbk);
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("failed");
						$.jstree.rollback(data.rlbk);
					}
				});
				
			}
			
			
			
		})
		
		.bind("remove.jstree", function (event, data) {
			var nodeUuid = data.rslt.obj.attr("id");
			
			if(data.rslt.obj.attr("rel")=="folder" || data.rslt.obj.attr("rel")=="default"){
				if(confirm("You are trying to delete a category or product, all related views and schedules will be deleted also!!! ")){
					// ajax call to delete the node
					$.ajax({
						type: "GET",
						url: "/entityNodeDelete",
						cache: false,
						data: "nodeId="+nodeUuid,
						dataType:"json",
						success: function(aData, textStatus){
							if(aData.success){
														
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
					
				}else{
					$.jstree.rollback(data.rlbk);
				}
				
				
			}else if(data.rslt.obj.attr("rel")=="instanceView"){
				if(confirm("You are trying to delete a instanceView, all jsp, css and schedules related to the view will be deleted!!!!")){
					$.ajax({
						type: "GET",
						url: "/instanceViewDelete",
						cache: false,
						data: "nodeId="+nodeUuid,
						dataType:"json",
						success: function(aData, textStatus){
							if(aData.success){
								$(".instanceViewManagerContainer").html("InstanceView is deleted!");
								
								// refresh all activated view and scheds flag
								$("li.schedActivated").removeClass("schedActivated");
								if(aData.response1){
									$(aData.response1).each(function(){
										$("#"+this).addClass("schedActivated");
									})
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
					
				}else{
					$.jstree.rollback(data.rlbk);
				}
			}else if(data.rslt.obj.attr("rel")=="schedule"){
				
				$.ajax({
					type: "GET",
					url: "/instanceViewScheduleDelete",
					cache: false,
					data: "scheduleId="+nodeUuid,
					dataType:"json",
					success: function(aData, textStatus){
						if(aData.success){
							// refresh all activated view and scheds flag
							$("li.schedActivated").removeClass("schedActivated");
							if(aData.response1){
								$(aData.response1).each(function(){
									$("#"+this).addClass("schedActivated");
								})
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
				
			}	
			
		})
		
		.bind("hover_node.jstree", function(event, data) {
			
			var nodeUuid = data.rslt.obj.attr("id");

			clearTimeout(treeNodeIconsTimeout);
			$("#productTree .nodeFunctionIcons").remove();

			data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
			$(".nodeFunctionIcons").bind("mouseenter", function(){
				clearTimeout(treeNodeIconsTimeout);
			})
			$(".nodeFunctionIcons").bind("mouseleave", function(){
				$(this).remove();
			})			
			
			// hide all icons first:
			$("#productTree .nodeFunctionIcons img").hide();
			
 			// show icons and bind actions based on class name
 			if(data.rslt.obj.hasClass("create")){
 				
 				$("#productTree .nodeFunctionIcons .categoryCreateIconImg").click(function(){
 					$("#productTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"folder", "class":"create delete toRename toConfig newView"}});
 				})
 				$("#productTree .nodeFunctionIcons .categoryCreateIconImg").show();
 				
 				$("#productTree .nodeFunctionIcons .productCreateIconImg").click(function(){
 					$("#productTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"default", "class":"delete toRename toConfig newView"}});
 				})
 				$("#productTree .nodeFunctionIcons .productCreateIconImg").show();
 				
 			}

 			if(data.rslt.obj.hasClass("toRename")){
				$("#productTree .nodeFunctionIcons .renameIconImg").click(function(){
					$("#productTree").jstree("rename", data.rslt.obj);
				})
				$("#productTree .nodeFunctionIcons .renameIconImg").show();
 				
 			}
 			
 			if(data.rslt.obj.hasClass("toCopy")){
				$("#productTree .nodeFunctionIcons .duplicateIconImg").click(function(){
					//console.log("duplicateIconImg");
					
					// get parent node
					var parentNode = $.jstree._reference('#productTree')._get_parent(data.rslt.obj);
					
					// get the current node
					var currentNode = data.rslt.obj;
					if(currentNode.attr("rel")=="folder"){
						if(currentNode.find("li")){
		 					$("#productTree").jstree("create", parentNode, "last", {"state":"closed", "data":{"title":"New Copy"}, "attr" : {"rel":"folder", "domvalue":nodeUuid, "class":"create delete toRename toConfig newView clone"}});
						}else{
		 					$("#productTree").jstree("create", parentNode, "last", {"data":{"title":"New Copy"}, "attr" : {"rel":"folder", "domvalue":nodeUuid, "class":"create delete toRename toConfig newView clone"}});
						}
					}else if(currentNode.attr("rel")=="default"){
	 					$("#productTree").jstree("create", parentNode, "last", {"data":{"title":"New Copy"}, "attr" : {"rel":"default", "domvalue":nodeUuid, "class":"delete toRename toConfig newView pageLinks clone"}});
					}
				})
				$("#productTree .nodeFunctionIcons .duplicateIconImg").show();
 				
 			}

 			if(data.rslt.obj.hasClass("delete")){
 				$("#productTree .nodeFunctionIcons .deleteIconImg").click(function(){
					if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
	 					$("#productTree").jstree("remove", data.rslt.obj);
					}

 				})
 				$("#productTree .nodeFunctionIcons .deleteIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("toConfig")){
				$("#productTree .nodeFunctionIcons .configIconImg").click(function(){
					$("#productTree").jstree("deselect_all");
					$("#productTree").jstree("select_node", data.rslt.obj);
				})
				$("#productTree .nodeFunctionIcons .configIconImg").show();
 				
 			}
 			
 			if(data.rslt.obj.hasClass("newView")){
 				$("#productTree .nodeFunctionIcons .viewCreateIconImg").click(function(){
 					//console.log("view create!");
 					$("#productTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"instanceView", "class":"newSchedule delView editView toRename"}});
 				})
 				$("#productTree .nodeFunctionIcons .viewCreateIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("newSchedule")){
 				$("#productTree .nodeFunctionIcons .newScheduleIconImg").click(function(){
 					$("#productTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"schedule", "class":"editSchedule delSchedule toRename"}});
 				})
 				$("#productTree .nodeFunctionIcons .newScheduleIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("editView")){
 				$("#productTree .nodeFunctionIcons .configIconImg").click(function(){
 					$("#productTree").jstree("deselect_all");
 					$("#productTree").jstree("select_node", data.rslt.obj);
 					
 				})
 				$("#productTree .nodeFunctionIcons .configIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("delView")){
 				$("#productTree .nodeFunctionIcons .deleteIconImg").click(function(){
 					if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
 						$("#productTree").jstree("remove", data.rslt.obj);
 					}
 				})
 				$("#productTree .nodeFunctionIcons .deleteIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("editSchedule")){
 				$("#productTree .nodeFunctionIcons .configIconImg").click(function(){
 					$("#productTree").jstree("deselect_all");
 					$("#productTree").jstree("select_node", data.rslt.obj);
 				})
 				$("#productTree .nodeFunctionIcons .configIconImg").show();
 			}

 			if(data.rslt.obj.hasClass("delSchedule")){
 				$("#productTree .nodeFunctionIcons .deleteIconImg").click(function(){
 					if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
 						$("#productTree").jstree("remove", data.rslt.obj);
 					}
 				})
 				$("#productTree .nodeFunctionIcons .deleteIconImg").show();
 			}
 			
 			if(data.rslt.obj.hasClass("pageLinks")){
 				$("#productTree .nodeFunctionIcons .openLinks").click(function(){
 					var entityUuid = data.rslt.obj.attr("id");
 					
 					 $.ajax({
 					     type: "GET",
 					     url: "/getAllProductPageLinksForProduct",
 					     cache: false,
 					     data: "productUuid="+entityUuid,
 					     dataType:"json",
 					     success: function(pidsdata, textStatus){
 					    	 
							if(pidsdata!=null && pidsdata.success){
							 	var linksHtml = "";
								$(pidsdata.response1).each(function(i){
									linksHtml = linksHtml + "<a href='" + this.key + "' target='_blank'>" + this.value + "</a>" + ", ";
								})
								
								$("#detailInfoContent").html(linksHtml);
							}else {
								$("#detailInfoContent").html("No product page found!");
							}
 					    	 
 					     	//$("#detailInfoContent").html("aaaaa");
 					     },
 					     error: function(XMLHttpRequest, textStatus, errorThrown){
 					         alert("failed");
 					
 					     }
 					 });
 					
 				})
 				
 				
 				$("#productTree .nodeFunctionIcons .openLinks").show();
 			}

 			if(data.rslt.obj.hasClass("toViewManage")){
 				
 				$("#productTree .nodeFunctionIcons .viewManagerIconImg").click(function(){
 					
 					var entityUuid = data.rslt.obj.attr("id");
 					
 	 				// ajax call to determine if entityDetail (category or product) has module selected
 					$.ajax({
 						type: "GET",
 						url: "/entityHasModuleSelected",
 						cache: false,
 					    data: "entityUuid="+entityUuid,
 						dataType:"json",
 						success: function(data, textStatus){
 							if(data.success){


 			 					$.ajax({
 			 						type: "GET",
 			 						url: "/instanceViewManageForEntity",
 			 						cache: false,
 			 						data: "entityUuid="+entityUuid,
 			 						dataType:"html",
 			 						success: function(data, textStatus){
 			 							
 			 			            	// initialize group index beginning number for future ajax call to add new group
 			 			            	totalModuleInstanceGroupInAjax = 100000;
 			 			            	
 			 			            	
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
 			 									// instanceview tree popup when user click view icon in module tree *************
 			 									$("#instanceViewsTreeForEntity").jstree({ 
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
 			 												"instanceView":{
 			 	 												"select_node" : function(){
 			 	 													return true;
 			 	 												} 
 			 												},
 			 												"schedule":{
 			 	 												"select_node" : function(){
 			 	 													return true;
 			 	 												} 
 			 												},
 			 												
 			 											}
 			 										},
 			 										

 			 									})
 			 									.bind("loaded.jstree", function (event, data) {
 			 										$(this).jstree("open_all");
 			 									})
 												.bind("rename.jstree", function (event, data) {
 													
 													var currentNodeId = data.rslt.obj.attr("id");
 													var newName = data.rslt.new_name;
 													
 													if(data.rslt.obj.attr("rel")=="instanceView"){
 														
 														$.ajax({
 															type: "GET",
 															url: "/updateViewValue",
 															cache: false,
 														    data: "viewId="+currentNodeId+"&updateType=instanceViewValue&updateValue="+newName+"&valueName=viewname",
 															dataType:"json",
 															success: function(data, textStatus){
 																if(data.success){
 																}else{
 																	$.jstree.rollback(data.rlbk);
 																}
 															},
 															error: function(XMLHttpRequest, textStatus, errorThrown){
 																alert("failed");
 																$.jstree.rollback(data.rlbk);
 															}
 														});
 														
 													}else if(data.rslt.obj.attr("rel")=="schedule"){
 														$.ajax({
 															type: "GET",
 															url: "/renameInstanceViewSchedule",
 															cache: false,
 														    data: "scheduleId="+currentNodeId+"&newName="+newName,
 															dataType:"json",
 															success: function(data, textStatus){
 																if(data.success){
 																}else{
 																	$.jstree.rollback(data.rlbk);
 																}
 															},
 															error: function(XMLHttpRequest, textStatus, errorThrown){
 																alert("failed");
 																$.jstree.rollback(data.rlbk);
 															}
 														});
 														
 													}
 												})
 			 									
 												.bind("remove.jstree", function (event, data) {
 													
 													var ancestorsIds = data.inst.get_path("#" + data.rslt.parent.attr("id"), true);
 													var parentid = data.rslt.parent.attr("id");
 													var currentNodeId = data.rslt.obj.attr("id");
 													
 													if(data.rslt.obj.attr("rel")=="instanceView"){
 														if(confirm("You are try to delete a instanceView, all jsp, css and schedules related to the view will be deleted!!!!")){
 															$.ajax({
 																type: "GET",
 																url: "/instanceViewDelete",
 																cache: false,
 																data: "nodeId="+currentNodeId,
 																dataType:"json",
 																success: function(aData, textStatus){
 																	if(aData.success){
 																		$(".instanceViewManagerContainer").html("InstanceView is deleted!");
 																		
 																		// refresh all activated view and scheds flag
 																		$("li.schedActivated").removeClass("schedActivated");
 																		if(aData.response1){
 																			$(aData.response1).each(function(){
 																				$("#"+this).addClass("schedActivated");
 																			})
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
 															
 														}else{
 															$.jstree.rollback(data.rlbk);
 														}
 													}else if(data.rslt.obj.attr("rel")=="schedule"){
 														$.ajax({
 															type: "GET",
 															url: "/instanceViewScheduleDelete",
 															cache: false,
 															data: "scheduleId="+currentNodeId,
 															dataType:"json",
 															success: function(aData, textStatus){
 																if(aData.success){
 																	// refresh all activated view and scheds flag
 																	$("li.schedActivated").removeClass("schedActivated");
 																	if(aData.response1){
 																		$(aData.response1).each(function(){
 																			$("#"+this).addClass("schedActivated");
 																		})
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
 														
 													}	
 													
 												})
 												
 												.bind("create.jstree", function (event, data) {
 													var parentNodeUuid = data.rslt.parent.attr("id");
 				//									console.log("parentNodeUuid: "+parentNodeUuid);
 				//									console.log("data.rslt.obj.attr(rel)"+data.rslt.obj.attr("rel"));
 				//									console.log("name: "+data.rslt.name);
 													
 													if(data.rslt.obj.attr("rel")=="instanceView"){
 				//										console.log("create a new instance view!!!");
 														
 														$.ajax({
 															type: "POST",
 															url: "/instanceViewConfig",
 															cache: false,
 															data: "instanceId="+entityUuid+"&instanceViewType=p&instanceViewId=newView&viewname="+data.rslt.name,
 															dataType:"json",
 															success: function(adata, textStatus){
 																if(adata.success){
 																	data.rslt.obj.attr("id", adata.response2.key);
 																}else{
 																	$.jstree.rollback(data.rlbk);
 																}
 															},
 															error: function(XMLHttpRequest, textStatus, errorThrown){
 																alert("new instance view creation failed!");
 																$.jstree.rollback(data.rlbk);
 															}
 														});
 														
 													}else if(data.rslt.obj.attr("rel")=="schedule"){
 				
 														$.ajax({
 															type: "POST",
 															url: "/newInstanceViewSchedule",
 															cache: false,
 															data: "instanceId="+entityUuid+"&priority=0&instanceViewId="+parentNodeUuid+"&scheduleName="+data.rslt.name,
 															dataType:"json",
 															success: function(adata, textStatus){
 																if(adata.success){
 																	data.rslt.obj.attr("id", adata.response1);
 																}else{
 																	$.jstree.rollback(data.rlbk);
 																}
 															},
 															error: function(XMLHttpRequest, textStatus, errorThrown){
 																alert("new instance view schedule creation failed!");
 																$.jstree.rollback(data.rlbk);
 															}
 														});
 														
 													}	
 													
 												})
 												
 												.bind("select_node.jstree", function(event, data){
 													var nodeUuid = data.rslt.obj.attr("id");
 													
 													if(data.rslt.obj.attr("rel")=="instanceView"){
 														
 														$.ajax({
 															type: "GET",
 															url: "/getInstanceViewForm",
 															cache: false,
 															data: "instanceViewUuid="+nodeUuid,
 															dataType:"html",
 															success: function(aData, textStatus){
 																$(".instanceViewManagerContainer").html(aData);
 																
 															    var jspEditor = CodeMirror.fromTextArea(document.getElementById("jspEditor"), {
 															        lineNumbers: true,
 															        mode: "application/x-ejs",
 															        indentUnit: 4,
 															        indentWithTabs: true,
 															        enterMode: "keep",
 																    autoCloseTags: true,
 																    readOnly: true,
 															        tabMode: "shift"
 																});
 															    // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
 															    // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
 															    // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
 															    jspEditor.on("blur", function(){
 																	//console.log("onBlur");
 																	jspEditor.save();
 																});

 															    var cssEditor = CodeMirror.fromTextArea(document.getElementById("cssEditor"), {
 															        lineNumbers: true,
 																    readOnly: true,
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
 														    	
 																
 																// for editViewJspCssValue && cancelEditViewJspCssValue
 																var jspOriginalValue = "";
 																var cssOriginalValue = "";
 																
 																$(".editViewJspCssValue").click(function(){
 																	//console.log("editModuleJspCssValue")
 															    	var domvalue = $(this).attr("domvalue");
 																	if(domvalue.indexOf("Jsp")>-1){
 																		jspOriginalValue = jspEditor.getValue();
 																    	jspEditor.setOption("readOnly", false);
 																	}else if(domvalue.indexOf("Css")>-1){
 																		cssOriginalValue = cssEditor.getValue();
 																		cssEditor.setOption("readOnly", false);
 																	}
 															    	$("#"+domvalue).find(".valueSection").addClass("displaynone");
 															    	$("#"+domvalue).find(".editSection").removeClass("displaynone");
 																	
 																});
 																
 																$(".cancelEditViewJspCssValue").click(function(){
 															    	var domvalue = $(this).attr("domvalue");
 																	if(domvalue.indexOf("Jsp")>-1){
 																    	jspEditor.setValue(jspOriginalValue);
 																    	jspEditor.setOption("readOnly", true);
 																	}else if(domvalue.indexOf("Css")>-1){
 																		cssEditor.setValue(cssOriginalValue);
 																		cssEditor.setOption("readOnly", true);
 																	}
 															    	
 															    	$("#"+domvalue).find(".valueSection").removeClass("displaynone");
 															    	$("#"+domvalue).find(".editSection").addClass("displaynone");
 																});
 																
 																$(".saveViewJspCssValue").click(function(){
 																	
 															    	//console.log("save module value");
 															    	var domvalue = $(this).attr("domvalue");
 															        var jsonObj = eval("("+domvalue+")");

 															        var theForm = $("form[id='"+jsonObj.formId+"']");
 															        var serializedData = theForm.serialize();
 															        
 																	$.ajax({
 																		type: "POST",
 																		url: "/updateViewValue",
 																		cache: false,
 																	    data: serializedData,
 																		dataType:"json",
 																		success: function(data, textStatus){
 																			if(data.success){
 																				if(jsonObj.webSectionId.indexOf("Jsp")>-1){
 																					jspOriginalValue = data.response1;
 																					
 																					// update jsp/css charusage data
 																					if(data.response3!=null){
 																						$(".viewJspUsedSpace").html(data.response3+"/100");
 																					}
 																					
 																					$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
 																				}else if(jsonObj.webSectionId.indexOf("Css")>-1){
 																					cssOriginalValue = data.response1;
 																					
 																					// update jsp/css charusage data
 																					if(data.response3!=null){
 																						$(".viewCssUsedSpace").html(data.response3+"/100");
 																					}
 																					
 																					$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
 																				}

 																				
 																				
 																			}else{
 																				alert(data.response1);
 																				//$(".moduleDataStructErrorSection").html(data.response1);
 																			}
 																		},
 																		error: function(XMLHttpRequest, textStatus, errorThrown){
 																			alert("failed");
 																		}
 																	});
 																	
 																	
 																});
 																
 																
 														
 															},
 															error: function(XMLHttpRequest, textStatus, errorThrown){
 																alert("system error, your result will be rollback!");
 																$.jstree.rollback(data.rlbk);
 															}
 														});
 														
 													}else if(data.rslt.obj.attr("rel")=="schedule"){
 														// ajax call to get the schedule
 												        $.ajax({
 												            type: "GET",
 												            url: "/instanceViewSchedule",
 												            cache: false,
 												            data: "schedId="+data.rslt.obj.attr("id"),
 												            dataType:"html",
 												            success: function(aData, textStatus){
 																$(".instanceViewManagerContainer").html(aData);
 												            },
 												            error: function(XMLHttpRequest, textStatus, errorThrown){
 												                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

 												            }
 												        });
 														
 													}
 													
 													
 													
 												})
 												
 			 									
 												.bind("hover_node.jstree", function(event, data) {
 													
 													$("#instanceViewsTreeForEntity").css("z-index", "10");
 													
 													var nodeUuid = data.rslt.obj.attr("id");
 													
 													clearTimeout(treeNodeIconsTimeout);
 													$("#instanceViewsTreeForEntity .nodeFunctionIcons").remove();
 											
 													data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
 													$("#instanceViewsTreeForEntity .nodeFunctionIcons").bind("mouseenter", function(){
 														clearTimeout(treeNodeIconsTimeout);
 													})
 													$("#instanceViewsTreeForEntity .nodeFunctionIcons").bind("mouseleave", function(){
 														$("#instanceViewsTreeForEntity").css("z-index", "0");
 														$(this).remove();
 													})			
 											
 													
 													// hide all icons first:
 													$("#instanceViewsTreeForEntity .nodeFunctionIcons img").hide();
 													
 													// show icons and bind actions based on class name
 													if(data.rslt.obj.hasClass("newView")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .viewCreateIconImg").click(function(){
 															//console.log("view create!");
 															$("#instanceViewsTreeForEntity").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"instanceView", "class":"newSchedule delView editView toRename"}});
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .viewCreateIconImg").show();
 													}
 													
 													if(data.rslt.obj.hasClass("toRename")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .renameIconImg").click(function(){
 															$("#instanceViewsTreeForEntity").jstree("rename", data.rslt.obj);
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .renameIconImg").show();
 													}
 													
 													if(data.rslt.obj.hasClass("delView")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .deleteIconImg").click(function(){
 															
 															if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
 																$("#instanceViewsTreeForEntity").jstree("remove", data.rslt.obj);
 															}
 															
 															
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .deleteIconImg").show();
 													}
 													
 													if(data.rslt.obj.hasClass("editView")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .configIconImg").click(function(){
 															$("#instanceViewsTreeForEntity").jstree("deselect_all");
 															$("#instanceViewsTreeForEntity").jstree("select_node", data.rslt.obj);
 															
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .configIconImg").show();
 														
 													}
 													
 													if(data.rslt.obj.hasClass("newSchedule")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .newScheduleIconImg").click(function(){
 															$("#instanceViewsTreeForEntity").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"schedule", "class":"editSchedule delSchedule toRename"}});
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .newScheduleIconImg").show();
 													}
 													
 													if(data.rslt.obj.hasClass("editSchedule")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .configIconImg").click(function(){
 															$("#instanceViewsTreeForEntity").jstree("deselect_all");
 															$("#instanceViewsTreeForEntity").jstree("select_node", data.rslt.obj);
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .configIconImg").show();
 													}
 													
 													if(data.rslt.obj.hasClass("delSchedule")){
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .deleteIconImg").click(function(){
 															if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
 																$("#instanceViewsTreeForEntity").jstree("remove", data.rslt.obj);
 															}
 														})
 														$("#instanceViewsTreeForEntity .nodeFunctionIcons .deleteIconImg").show();
 													}
 													
 												})
 												.bind("dehover_node.jstree", function(event, data) {

 													treeNodeIconsTimeout=setTimeout(function(){
 														$("#instanceViewsTreeForEntity").css("z-index", "0");
 														data.rslt.obj.find(".nodeFunctionIcons").remove();
 													},1000)
 												});
 			 									
 			 								},
 			 								onCleanup:function(){  },
 			 								onClosed:function(){  }
 			 							});
 			 			            	
 			 			            },
 			 						error: function(XMLHttpRequest, textStatus, errorThrown){
 			 						}
 			 					});
 			 				
 								
 								
 								
 								
 								
 								
 								
 								
 							
 							
 							
 							
 							
 							}else{
 				 				alert("No template (module) selected for current category or product! Select template (module) first please.");
 							}
 						},
 						error: function(XMLHttpRequest, textStatus, errorThrown){
 							alert("System can't determine whether this category or product has template(module) selected.");
 						}
 					});
 					
 					
 					
 					
 					
 					
 					
 					
 					
 					
 				
 				
 				
 				
 				
 				})
 				
 				$("#productTree .nodeFunctionIcons .viewManagerIconImg").show();

 			}
 			// end if class "toViewManage"
 			
		})
		.bind("dehover_node.jstree", function(event, data) {

			treeNodeIconsTimeout=setTimeout(function(){
				data.rslt.obj.find(".nodeFunctionIcons").remove();
			},1000)
		})
		
		
		
		$(".deleteModuleSelectionForEntity").live("click", function(){
			
			if(confirm("Remove template for this category or product? \nNote: all views related will be remove too!!")){
				
				//console.log("deleteModuleSelection");
				var entityUuid = $(this).attr("domvalue");
				// ajax call to del module setup for entity
				$.ajax({
					type: "GET",
					url: "/deleteModuleSelectionForEntity",
					cache: false,
					data: "entityUuid="+entityUuid,
					dataType:"json",
					success: function(aData, textStatus){
						// select entity node again
						$("#"+entityUuid+" > a").click();

					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("system error, your module will not be deleted from entity");
					}
				});
			}
			
		})
		
	})

	
	</script>








</div>