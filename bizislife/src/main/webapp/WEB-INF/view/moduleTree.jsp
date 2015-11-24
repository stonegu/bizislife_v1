<div class="moduleTree">

	<div id="moduleTree">
	
	</div>

</div>

<script type="text/javascript">


$(function () {
	
	// time value for treeNodeFunctionIcons
	var treeNodeIconsTimeout;

	
	$("#moduleTree").jstree({
		"plugins" : ["themes", "json_data", "ui", "types", "crrm", "dnd"],
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
							return true;
						} 
					},
					"productModule":{
						"select_node" : function(){
							return true;
						} 
					},
					"instance":{
						"select_node" : function(){
							return true;
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
//						if(node.hasClass("productInstance") || node.hasClass("productModule")){ // pins : 
//							return "operation=get_productTree&org=${orgUuid}&parentNodeId="+node.attr("id");
//						}else{
//							return "operation=get_moduleTree&org=${orgUuid}&parentNodeId="+node.attr("id");
//						}


						if(node.attr("rel")=="module" || node.attr("rel")=="folder"){
							return "operation=get_moduleTree&org=${orgUuid}&parentNodeId="+node.attr("id");
						}else if(node.attr("rel")=="instance"){
							return "operation=get_instanceViewTree&org=${orgUuid}&parentNodeId="+node.attr("id");
						}else if(node.attr("rel")=="instanceView"){
							return "operation=get_viewScheduleTree&org=${orgUuid}&parentNodeId="+node.attr("id");
						}
						




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
						if(m.o.attr("rel")=="folder" || m.o.attr("rel")=="module" || m.o.attr("rel")=="productModule"){
							if(m.np.attr("rel")=="folder"){
								return true;
							}
						}
					}else if(sourceTreeRootId!=targetTreeRootId){ // copy from another tree
						// m.o : moving obj, m.np : new parent
						if((m.o.attr("rel")=="folder" || m.o.attr("rel")=="module" || m.o.attr("rel")=="productModule") && m.o.hasClass("toShare")){
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
		$(this).jstree("open_all");
		
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
				url: "/moveModuleNode",
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
				url: "/copyModuleNodeToAnotherTree",
				cache: false,
				data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+targetFolderObj.attr("id"),
				dataType:"json",
				success: function(aData, textStatus){
					if(aData.success){
						// note: the target tree need to be refreshed, since trag-in tree structure could be changed!!
						// For example: moduledetail has copy permission, but direct parent-folder doesn't have copy permission, but grandparent-folder has copy permission.
						$("#moduleTree").jstree("refresh");
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
		
		if((data.rslt.obj.attr("rel")=="productModule" || data.rslt.obj.attr("rel")=="module") && data.rslt.obj.hasClass("toConfig")){
			var moduleUuid = data.rslt.obj.attr("id");

			// ajax call to get moduleConfigFragment jsp
			$.ajax({
			    type: "POST",
			    url: "/getModuleConfigFrag",
			    cache: false,
			    data: "moduleId="+moduleUuid,
			    dataType:"html",
			    success: function(mdata, textStatus){
			    	
					$.colorbox({
						width : 800,
						height : 650,
						html: mdata,
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
						    
					        // add special class to domReady_editModuleImg, let this listener knows that it opens media select is from colorbox
					        $(".domReady_editModuleImg").addClass("insideColorbox");
						    
							//console.log("beforeshow");
							// for codemirror
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
                            jspEditor.setSize(700, 450);

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
						    cssEditor.setSize(700, 450);

						    // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
						    // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
						    // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
						    cssEditor.on("blur", function(){
								//console.log("onBlur");
								cssEditor.save();
							});
                            
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
						    	
					    	// for drag and drop and sort
							$(".moduleAttrList li").draggable({
								appendTo: "body",
								//helper: "clone",
								helper: function( event ) {
									return $('<span domvalue="'+$(this).attr("domvalue")+'" style="white-space:nowrap;"/>').text($(this).text() + " helper");
								},
								cursor: "move",
								zIndex: 10000,
								cancel: ".noDraggable"
							});
					    	
							$(".attrListContainer").droppable({
							    accept: "li.draggable",
								drop: function(event, ui) {
									// $(this) is the dock, $(ui.draggable) is the draggable item
									//$(this).addClass( "ui-state-highlight" ).html( "Dropped!" );
									var dock = $(this);
									var dragedItem = $(ui.draggable);
									//console.log(dragedItem.attr("domvalue"));
									
									// ajax call to generate the attribute based on dragedItem.attr("domvalue")
									var dragedDomvalue = dragedItem.attr("domvalue");
									if(dragedDomvalue){
										var jsonObj = eval("("+dragedDomvalue+")");
										var attrsGroupId = $(this).attr("id");
										var moduleId = $("#moduleId").val();
										
								        $.ajax({
								            type: "POST",
								            url: "/moduleNewAttr",
								            cache: false,
								            data: "attrClassName="+jsonObj.classname+"&attrJspName="+jsonObj.jspname+"&attrsGroupId="+attrsGroupId+"&moduleId="+moduleId,
								            dataType:"html",
								            success: function(madata, textStatus){
								            	
								            	
                                                // *** add uuid to each textarea for codeMirror
								            	var madataDom = $(madata);
					                            // for all text attribute edit
					                            var newTextAreaIds = new Array();
					                            madataDom.find(".attrDefaultTxtValueEditor").each(function(idx){
					                            	var newTextAreaId = generateUUID()
					                            	newTextAreaIds.push(newTextAreaId);
					                                
					                                $(this).attr('id', newTextAreaId);
					                                
					                            });
								            	
								            	// remove all old moduleUsageInfo for new Attr
								            	$(".moduleDetailUsageWith100Multipled_newAttr").remove();
								            	
								            	dock.find(".attrTemp").remove();
								            	dock.prepend(madataDom);
								            	
										    	 var moduleUsage = dock.find(".moduleDetailUsageWith100Multipled_newAttr");
										    	 if(moduleUsage.length>0){
										    		 $("#tabs-3 .moduleUsedSpace").html(moduleUsage.val()+"/100");
										    	 }
										    	 
										    	 // constructor codeMirror based on newTextAreaIds
										    	 for (var j=0; j<newTextAreaIds.length; j++) {
										    		 
					                                var attrId = $("#"+newTextAreaIds[j]).attr("domvalue");
					                                var txtMirror = CodeMirror.fromTextArea(document.getElementById(newTextAreaIds[j]),
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
										    		 
										    	 }
										    	 
								            },
								            error: function(XMLHttpRequest, textStatus, errorThrown){
								                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

								            }
								        });
										
									}
									
								},
								over: function( event, ui ) {
									$(this).prepend($(".moduleAttrSimpleExample .moduleDataStructTemp").html());
								},
								
								out: function( event, ui ) {
									$(this).find(".attrTemp").remove();
								}
							});
							
							var startPosition, stopPosition;
							$(".attrListContainer").sortable({ 
					            handle: '.attrName',
					            items: ".attrSortable",
					            start: function( event, ui ) {
//                                    console.log('........ sort start');
//                                    console.log('........ start position:' + ui.item.index());
					                startPosition = ui.item.index();
					            },
					            stop: function( event, ui ) {
					                stopPosition = ui.item.index();
					                if(startPosition!==stopPosition){
	                                    
	                                    // ajax call to sort the attrs
	                                    var attrsGroupId = $(this).attr("id");
	                                    $.ajax({
	                                        type: "POST",
	                                        url: "/moduleDetailAttributesSort",
	                                        cache: false,
	                                        data: "moduleDetailId="+moduleUuid+"&groupId="+attrsGroupId+"&attrId="+ui.item[0].id+"&position="+stopPosition,
	                                        dataType:"json",
	                                        success: function(sortData, textStatus){
	                                            if(sortData.success){
	                                                
	                                            }else{
	                                                $( ".attrListContainer" ).sortable( "cancel" );
	                                            }
	                                            
	                                        },
	                                        error: function(XMLHttpRequest, textStatus, errorThrown){
	                                            alert("system error, your sort failed!");
	                                        }
	                                    });
					                    
					                }
 

					            },
					            update: function( event, ui ) {
//					                console.log('........ sort update');
					                
					            }
					        });

							
/* 											
							$(".attrListContainer").sortable({
								revert: true,
								placeholder: "ui-state-highlight",
								start: function(e, ui){
									ui.placeholder.height(ui.item.height());
								}
							});
*/
								// for media tree list
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
										"success": function (imgTdata) {
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
									//console.log("selectable!");
									
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
								
					    	
							// for editModuleJspCssValue && cancelEditModuleJspCssValue
							var jspOriginalValue = "";
							var cssOriginalValue = "";
							
							$(".editModuleJspCssValue").click(function(){
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
							
							$(".cancelEditModuleJspCssValue").click(function(){
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
							
							$(".saveModuleJspCssValue").click(function(){
								$(".moduleDataStructErrorSection").html("");

						    	//console.log("save module value");
						    	var domvalue = $(this).attr("domvalue");
						        var jsonObj = eval("("+domvalue+")");

						        var theForm = $("form[id='"+jsonObj.formId+"']");
						        var serializedData = theForm.serialize();
						        
								$.ajax({
									type: "POST",
									url: "/updateModuleValue",
									cache: false,
								    data: serializedData,
									dataType:"json",
									success: function(umdata, textStatus){
										if(umdata.success){
											
											//update jsp/css chars length validation info
				    						if(jsonObj.webSectionId.indexOf("Jsp")>-1){
				        						$("#tabs-4 .moduleJspUsedSpace").html(umdata.response3+"/100");
				    						}else if(jsonObj.webSectionId.indexOf("Css")>-1){
				        						$("#tabs-5 .moduleCssUsedSpace").html(umdata.response3+"/100");
				    						}
											
											if(jsonObj.webSectionId.indexOf("Jsp")>-1){
												jspOriginalValue = umdata.response1;
												$("#"+jsonObj.webSectionId).find("img.cancelEditModuleJspCssValue").click();
											}else if(jsonObj.webSectionId.indexOf("Css")>-1){
												cssOriginalValue = umdata.response1;
												$("#"+jsonObj.webSectionId).find("img.cancelEditModuleJspCssValue").click();
											}

											
											
										}else{
											//$(".moduleDataStructErrorSection").html(umdata.response1);
											alert(umdata.response1);
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
					alert("ajax call failed for getModuleConfigFrag!");							
			    }
			});
			
			
		}else if(data.rslt.obj.attr("rel")=="instance" && data.rslt.obj.hasClass("toInstanceEdit")){
			
			
			instanceUuid = data.rslt.obj.attr("id");
			var moduleUuid = $.jstree._reference('#moduleTree')._get_parent(data.rslt.obj).attr("id");
			
	        $.ajax({
	            type: "GET",
	            url: "/moduleInstance",
	            cache: false,
	            data: "moduleInstanceId="+instanceUuid,
	            dataType:"html",
	            success: function(midata, textStatus){
	            	// initialize group index beginning number for future ajax call to add new group
	            	totalModuleInstanceGroupInAjax = 100000;
	            	
					$.colorbox({
						width : 600,
						height : 550,
						title: '<button class="domReady_colorboxMaxSize">Max size</button>',
						html: midata,
                        escKey: false,
						opacity: 0.4,
						trapFocus: false,
						overlayClose: false,
						escKey: false,
						onComplete:function(){
						    
					        // add special class to domReady_editModuleImg, let this listener knows that it opens media select is from colorbox
					        $(".domReady_editModuleImg").addClass("insideColorbox");
						    
							// for text edit area codeMirror
							//$(".attrDefaultTxtValueEditor").codemirror({mode: 'javascript', lineNumbers: true});
/*                             var jspEditor = CodeMirror.fromTextArea(document.getElementById("jspEditor"), {
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
 */							
							
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
 
							
							// for media tree list
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
										"success": function (imgTdata) {
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
									//console.log("selectable!");
									
									// populate the data for the form
							        // $("#selectedImgSysNameForModuleConfig").val(nodeUuid);
									// $(".mediaShowArea .imgContainer").html("<img src='/getphoto?id="+nodeUuid+"&size=200'/>");
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
							
						}
					});

	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown){
	                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

	            }
	        });
			
			
		}else if(data.rslt.obj.attr("rel")=="instanceView" && data.rslt.obj.hasClass("editView")){
			
			var nodeUuid = data.rslt.obj.attr("id");
			
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
								//$(".moduleDataStructErrorSection").html("");

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
									success: function(uvdata, textStatus){
										if(uvdata.success){
											if(jsonObj.webSectionId.indexOf("Jsp")>-1){
												jspOriginalValue = uvdata.response1;
												
												// update jsp/css charusage data
												if(!!uvdata.response3){
													$(".viewJspUsedSpace").html(uvdata.response3+"/100");
												}
												
												$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
											}else if(jsonObj.webSectionId.indexOf("Css")>-1){
												cssOriginalValue = uvdata.response1;
												
												// update jsp/css charusage data
												if(!!uvdata.response3){
													$(".viewCssUsedSpace").html(uvdata.response3+"/100");
												}
												
												$("#"+jsonObj.webSectionId).find("img.cancelEditViewJspCssValue").click();
											}

											// refresh all activated view and scheds flag
											if(uvdata.response2){
												$("li.schedActivated").removeClass("schedActivated");
												$(uvdata.response2).each(function(){
													$("#"+this).addClass("schedActivated");
												})
											}
											
											
											
											
										}else{
											alert(uvdata.response1);
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
					alert("system error, you can try to refresh the page and select it again!");
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
						width : 400,
						height : 400,
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
					url: "/moduleNodeCreate",
					cache: false,
					data: "newNodetype=fd&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name),
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
						$.jstree.rollback(data.rlbk);
						alert("system error, your result will be rollback!");
					}
				});
			}
			
		}else if(data.rslt.obj.attr("rel")=="module" || data.rslt.obj.attr("rel")=="productModule"){
			if(data.rslt.obj.hasClass("clone")){
				//console.log("create a product!!!");
				var newNodeType = "md";
	 			
	 			if(data.rslt.obj.attr("rel")=="productModule"){
	 				newNodeType = "pm";
	 			}
	 
				$.ajax({
					type: "GET",
					url: "/moduleNodeCreate",
					cache: false,
					data: "newNodetype="+newNodeType+"&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name)+"&cloneFrom="+data.rslt.obj.attr("domvalue"),
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
						$.jstree.rollback(data.rlbk);
						alert("system error, your result will be rollback!");
					}
				});
				
			}else{
				
				//console.log("create a product!!!");
				var newNodeType = "md";
	 			
	 			if(data.rslt.obj.attr("rel")=="productModule"){
	 				newNodeType = "pm";
	 			}
	 
				$.ajax({
					type: "GET",
					url: "/moduleNodeCreate",
					cache: false,
					data: "newNodetype="+newNodeType+"&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name),
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
			
			
			
			
			
			
			
		}else if(data.rslt.obj.attr("rel")=="instance"){
			
			$.ajax({
				type: "GET",
				url: "/newModuleInstance",
				cache: false,
				data: "newNodetype=ins&parentNodeUuid="+parentNodeUuid+"&nodeName="+escape(data.rslt.name),
				dataType:"json",
				success: function(aData, textStatus){
					
					if(aData.success){
						$(data.rslt.obj).attr("id", aData.response1);		
						
					}else{
						$.jstree.rollback(data.rlbk);
						// the rollback will open the nodeFunctionIcons again, this will make sure that nodeFunctionIcons are closed!!
						$("#"+parentNodeUuid).find(".nodeFunctionIcons").remove();			
						
						alert(aData.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("system error, your result will be rollback!");
					$.jstree.rollback(data.rlbk);
				}
			});
			

		}else if(data.rslt.obj.attr('rel')=="instanceView"){
			$.ajax({
				type: "POST",
				url: "/instanceViewConfig",
				cache: false,
				data: "instanceId="+parentNodeUuid+"&instanceViewType=n&instanceViewId=newView&viewname="+escape(data.rslt.name),
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
						data.rslt.obj.attr("id", adata.response2.key);
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
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
				data: "priority=0&instanceViewId="+parentNodeUuid+"&scheduleName="+escape(data.rslt.name),
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){
						data.rslt.obj.attr("id", adata.response1);
					}else{
						$.jstree.rollback(data.rlbk);
						alert(adata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("new instance view schedule creation failed!");
					$.jstree.rollback(data.rlbk);
				}
			});
		
		}
		
		
	})
	
	.bind("remove.jstree", function (event, data) {
		
		
		var ancestorsIds = data.inst.get_path("#" + data.rslt.parent.attr("id"), true);
		var parentid = data.rslt.parent.attr("id");
		var currentNodeId = data.rslt.obj.attr("id");
		
		if(data.rslt.obj.attr("rel")=="folder"){
			if(confirm("You are try to delete a folder, all modules under this folder and all instances from the module will be deleted!!!!")){
				$.ajax({
					type: "GET",
					url: "/moduleNodeDelete",
					cache: false,
					data: "nodeId="+currentNodeId,
					dataType:"json",
					success: function(aData, textStatus){
						if(aData.success){
								
						}else{
							$.jstree.rollback(data.rlbk);
							alert(aData.response1);
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						$.jstree.rollback(data.rlbk);
						alert("system error, your result will be rollback!");
					}
				});
				
			}else{
				$.jstree.rollback(data.rlbk);
			}
		}else if(data.rslt.obj.attr("rel")=="module"){
			
			if(confirm("You are try to delete a module, all instances & schedules from the module will be deleted!!!!")){
				$.ajax({
					type: "GET",
					url: "/moduleNodeDelete",
					cache: false,
					data: "nodeId="+currentNodeId,
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
			
		}else if(data.rslt.obj.attr("rel")=="productModule"){
			if(confirm("You are try to delete a product module, all products using this module will be emptied!!!!")){
				$.ajax({
					type: "GET",
					url: "/moduleNodeDelete",
					cache: false,
					data: "nodeId="+currentNodeId,
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
		}else if(data.rslt.obj.attr("rel")=="instance"){
			
			if(confirm("You are try to delete a module instance, all views and jsp & css files and schedules will be deleted!!!!")){
				$.ajax({
					type: "GET",
					url: "/delModuleInstance",
					cache: false,
					data: "parentid="+parentid+"&currentNodeId="+currentNodeId,
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
				
				
			}else{
				$.jstree.rollback(data.rlbk);
				
			}
			
		}else if(data.rslt.obj.attr("rel")=="instanceView"){
			if(confirm("You are try to delete a instanceView, all jsp, css and schedules related to the view will be deleted!!!!")){
				$.ajax({
					type: "GET",
					url: "/instanceViewDelete",
					cache: false,
					data: "nodeId="+currentNodeId,
					dataType:"json",
					success: function(aData, textStatus){
						if(aData.success){
							alert("InstanceView is deleted!");
							
							// refresh all activated view and scheds flag
/* 							
							if(aData.response1){
								$("li.schedActivated").removeClass("schedActivated");
								$(aData.response1).each(function(){
									$("#"+this).addClass("schedActivated");
								})
							}
 */							
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
/* 						
						if(aData.response1){
							$("li.schedActivated").removeClass("schedActivated");
							$(aData.response1).each(function(){
								$("#"+this).addClass("schedActivated");
							})
						}
 */												
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
	
	.bind("rename.jstree", function (event, data) {
		//var ancestorsIds = data.inst.get_path("#" + data.rslt.obj.attr("id"), true);
		
		var currentNodeId = data.rslt.obj.attr("id");
		var newName = escape(data.rslt.new_name);
		
		if(data.rslt.obj.attr("rel")=="folder" || data.rslt.obj.attr("rel")=="module" || data.rslt.obj.attr("rel")=="productModule"){
			
			$.ajax({
				type: "GET",
				url: "/updateModuleValue",
				cache: false,
			    data: "moduleId="+currentNodeId+"&updateType=moduledetailValue&updateValue="+newName+"&valueName=prettyname",
				dataType:"json",
				success: function(adata, textStatus){
					if(adata.success){

						// update moduleUsage info:
						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
						
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
		
		}else if(data.rslt.obj.attr("rel")=="instance"){
			$.ajax({
				type: "GET",
				url: "/updateInstanceValue",
				cache: false,
			    data: "instanceId="+currentNodeId+"&updateType=moduleInstanceValue&updateValue="+newName+"&valueName=name",
				dataType:"json",
				success: function(rdata, textStatus){
					if(rdata.success){
						
						// upate instance's usage info
						$(".moduleInstanceDataStruct .instanceUsedSpace").html(rdata.response3+"/100");
						$(".entityUsedSpace").html(data.response3+"/100");
						
					}else{
						$.jstree.rollback(data.rlbk);
						alert(rdata.response1);
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
				success: function(rdata, textStatus){
					if(rdata.success){
					}else{
						$.jstree.rollback(data.rlbk);
						alert(rdata.response1);
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
				success: function(rdata, textStatus){
					if(rdata.success){
					}else{
						$.jstree.rollback(data.rlbk);
						alert(rdata.response1);
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
					$.jstree.rollback(data.rlbk);
				}
			});
			
		}
		
//		console.log("rename: "+currentNodeId + " | "+newName);	
	})
	

	.bind("hover_node.jstree", function(event, data) {
		var nodeUuid = data.rslt.obj.attr("id");

		clearTimeout(treeNodeIconsTimeout);
		$("#moduleTree .nodeFunctionIcons").remove();

		data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
		$("#moduleTree .nodeFunctionIcons").bind("mouseenter", function(){
			clearTimeout(treeNodeIconsTimeout);
		})
		$("#moduleTree .nodeFunctionIcons").bind("mouseleave", function(){
			$(this).remove();
		})			

		
		// hide all icons first:
		$("#moduleTree .nodeFunctionIcons img").hide();
		
		// show icons and bind actions based on class name
		if(data.rslt.obj.hasClass("toCreate")){
			$("#moduleTree .nodeFunctionIcons .folderCreateIconImg").click(function(){
				$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"folder", "class":"toCreate toDelete toRename"}});
			})
			$("#moduleTree .nodeFunctionIcons .folderCreateIconImg").show();
			
			$("#moduleTree .nodeFunctionIcons .newModuleIconImg").click(function(){
				
				var rel="module";
				if(confirm("Do you like this new module be used as product template?\nNote:\nclick Cancel to create a Regular Module, click Ok to create a product template.")){
					rel = "productModule";
				}
				
				if(rel=="productModule"){
					$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":rel, "class":"toDelete toConfig toRename productModule"}});
				}else{
					$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":rel, "class":"toDelete toConfig toInstance toRename module"}});
				}
				
			})
			$("#moduleTree .nodeFunctionIcons .newModuleIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("toRename")){
			$("#moduleTree .nodeFunctionIcons .renameIconImg").click(function(){
				$("#moduleTree").jstree("rename", data.rslt.obj);
			})
			$("#moduleTree .nodeFunctionIcons .renameIconImg").show();
			
		}
		
		if(data.rslt.obj.hasClass("toCopy")){
			$("#moduleTree .nodeFunctionIcons .duplicateIconImg").click(function(){
				//console.log("duplicateIconImg");
				// get parent node
				var parentNode = $.jstree._reference('#moduleTree')._get_parent(data.rslt.obj);
				
				// get the current node
				var currentNode = data.rslt.obj;
				if(currentNode.attr("rel")=="folder"){
					if(currentNode.find("li")){
						$("#moduleTree").jstree("create", parentNode, "last", {"state":"closed", "data":{"title":"New Copy"}, "attr" : {"rel":"folder", "domvalue":nodeUuid, "class":"toCreate toDelete toRename clone"}});
					}else{
						$("#moduleTree").jstree("create", parentNode, "last", {"data":{"title":"New Copy"}, "attr" : {"rel":"folder", "domvalue":nodeUuid, "class":"toCreate toDelete toRename clone"}});
					}
					
				}else if(currentNode.attr("rel")=="productModule"){
					$("#moduleTree").jstree("create", parentNode, "last", {"data":{"title":"New Copy"}, "attr" : {"rel":"productModule", "domvalue":nodeUuid, "class":"toDelete toConfig toInstance toRename module clone"}});
				}else if(currentNode.attr("rel")=="module"){
					$("#moduleTree").jstree("create", parentNode, "last", {"data":{"title":"New Copy"}, "attr" : {"rel":"module", "domvalue":nodeUuid, "class":"toDelete toConfig toInstance toRename module clone"}});
				}
				
			})
			$("#moduleTree .nodeFunctionIcons .duplicateIconImg").show();
				
		}
		
		if(data.rslt.obj.hasClass("toDelete")){
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").click(function(){
				var nodeName = data.inst.get_text(data.rslt.obj);
				var spanLocation = nodeName.indexOf("<span");
				if(spanLocation>-1){
					nodeName = nodeName.substring(0, nodeName.indexOf("<span"));
				}
				if(confirm("Confirm to delete '"+nodeName+"'?")){
					$("#moduleTree").jstree("remove", data.rslt.obj);
				}

			})
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").show();
		}
		if(data.rslt.obj.hasClass("toInstance")){

			$("#moduleTree .nodeFunctionIcons .newModuleInstanceIconImg").click(function(){
				// deselect all notes
				$("#moduleTree").jstree("deselect_all");
				
				// check if module has design
				$.ajax({
					type: "GET",
					url: "/hasModuleDesigned",
					cache: false,
					data: "moduleUuid="+data.rslt.obj.attr("id"),
					dataType:"json",
					success: function(cData, textStatus){
						if(cData.success){
							$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"instance", "class":"toInstanceEdit toInstanceDel newView toRename instance"}});
						}else{
							alert("You haven't designed module yet! You can click the module and goto design tab to do the design.");
						}
					},
					error: function(XMLHttpRequest, textStatus, errorThrown){
						alert("system error, you may need to refresh the page and tr it again!");
					}
				});
				
			})
			$("#moduleTree .nodeFunctionIcons .newModuleInstanceIconImg").show();
			
		}
		if(data.rslt.obj.hasClass("toConfig")){
			$("#moduleTree .nodeFunctionIcons .configIconImg").click(function(){
				
				$("#moduleTree").jstree("deselect_all");
				$("#moduleTree").jstree("select_node", data.rslt.obj);
				
			})
			$("#moduleTree .nodeFunctionIcons .configIconImg").show();
			
		}
		
		if(data.rslt.obj.hasClass("toInstanceEdit")){
			$("#moduleTree .nodeFunctionIcons .configIconImg").click(function(){
				
				$("#moduleTree").jstree("deselect_all");
				$("#moduleTree").jstree("select_node", data.rslt.obj);
				
			})
			$("#moduleTree .nodeFunctionIcons .configIconImg").show();
			
		}
		if(data.rslt.obj.hasClass("toInstanceDel")){
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").click(function(){
				var nodeName = data.inst.get_text(data.rslt.obj);
				var spanLocation = nodeName.indexOf("<span");
				if(spanLocation>-1){
					nodeName = nodeName.substring(0, nodeName.indexOf("<span"));
				}
				
				if(confirm("Confirm to delete '"+nodeName+"''?")){
					$("#moduleTree").jstree("remove", data.rslt.obj);
				}

			})
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").show();
			
		}
		
		if(data.rslt.obj.hasClass("newView")){
			$("#moduleTree .nodeFunctionIcons .viewCreateIconImg").click(function(){
				$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"instanceView", "class":"newSchedule delView editView toRename"}});
			})
			$("#moduleTree .nodeFunctionIcons .viewCreateIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("newSchedule")){
			$("#moduleTree .nodeFunctionIcons .newScheduleIconImg").click(function(){
				$("#moduleTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"schedule", "class":"editSchedule delSchedule toRename"}});
			})
			$("#moduleTree .nodeFunctionIcons .newScheduleIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("editView")){
			$("#moduleTree .nodeFunctionIcons .configIconImg").click(function(){
				$("#moduleTree").jstree("deselect_all");
				$("#moduleTree").jstree("select_node", data.rslt.obj);
			})
			$("#moduleTree .nodeFunctionIcons .configIconImg").show();
		}

		if(data.rslt.obj.hasClass("delView")){
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").click(function(){
				if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
					$("#moduleTree").jstree("remove", data.rslt.obj);
				}
			})
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("editSchedule")){
			$("#moduleTree .nodeFunctionIcons .configIconImg").click(function(){
				$("#moduleTree").jstree("deselect_all");
				$("#moduleTree").jstree("select_node", data.rslt.obj);
			})
			$("#moduleTree .nodeFunctionIcons .configIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("delSchedule")){
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").click(function(){
				if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
					$("#moduleTree").jstree("remove", data.rslt.obj);
				}
			})
			$("#moduleTree .nodeFunctionIcons .deleteIconImg").show();
		}
		
		if(data.rslt.obj.hasClass("toViewManage")){
			$("#moduleTree .nodeFunctionIcons .viewManagerIconImg").click(function(){
				
				
				instanceUuid = data.rslt.obj.attr("id");
				var moduleUuid = $.jstree._reference('#moduleTree')._get_parent(data.rslt.obj).attr("id");
				
		        $.ajax({
		            type: "GET",
		            url: "/instanceViewManage",
		            cache: false,
		            data: "moduleInstanceId="+instanceUuid,
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
//								$("#newInstanceViewRadio").click();
								
								
								// instanceview tree popup when user click view icon in module tree *************
								$("#instanceViewsTree").jstree({ 
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
											data: "instanceId="+instanceUuid+"&instanceViewType=n&instanceViewId=newView&viewname="+data.rslt.name,
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
											data: "instanceId="+instanceUuid+"&priority=0&instanceViewId="+parentNodeUuid+"&scheduleName="+data.rslt.name,
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
														if(aData.response1){
															$("li.schedActivated").removeClass("schedActivated");
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
													if(aData.response1){
														$("li.schedActivated").removeClass("schedActivated");
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
								
								.bind("rename.jstree", function (event, data) {
									//var ancestorsIds = data.inst.get_path("#" + data.rslt.obj.attr("id"), true);
									
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
													//$(".moduleDataStructErrorSection").html("");

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
																	if(data.response3!=nul){
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

																// refresh all activated view and scheds flag
																if(data.response2){
																	$("li.schedActivated").removeClass("schedActivated");
																	$(data.response2).each(function(){
																		$("#"+this).addClass("schedActivated");
																	})
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
									var nodeUuid = data.rslt.obj.attr("id");
							
									clearTimeout(treeNodeIconsTimeout);
									$("#instanceViewsTree .nodeFunctionIcons").remove();
							
									data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
									$("#instanceViewsTree .nodeFunctionIcons").bind("mouseenter", function(){
										clearTimeout(treeNodeIconsTimeout);
									})
									$("#instanceViewsTree .nodeFunctionIcons").bind("mouseleave", function(){
										$(this).remove();
									})			
							
									
									// hide all icons first:
									$("#instanceViewsTree .nodeFunctionIcons img").hide();
									
									// show icons and bind actions based on class name
									if(data.rslt.obj.hasClass("newView")){
										$("#instanceViewsTree .nodeFunctionIcons .viewCreateIconImg").click(function(){
											$("#instanceViewsTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"instanceView", "class":"newSchedule delView editView toRename"}});
										})
										$("#instanceViewsTree .nodeFunctionIcons .viewCreateIconImg").show();
									}
									
									if(data.rslt.obj.hasClass("toRename")){
										$("#instanceViewsTree .nodeFunctionIcons .renameIconImg").click(function(){
											$("#instanceViewsTree").jstree("rename", data.rslt.obj);
										})
										$("#instanceViewsTree .nodeFunctionIcons .renameIconImg").show();
									}
							
									if(data.rslt.obj.hasClass("editView")){
										$("#instanceViewsTree .nodeFunctionIcons .configIconImg").click(function(){
											$("#instanceViewsTree").jstree("deselect_all");
											$("#instanceViewsTree").jstree("select_node", data.rslt.obj);
										})
										$("#instanceViewsTree .nodeFunctionIcons .configIconImg").show();
									}
							
									if(data.rslt.obj.hasClass("delView")){
										$("#instanceViewsTree .nodeFunctionIcons .deleteIconImg").click(function(){
											
											if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
												$("#instanceViewsTree").jstree("remove", data.rslt.obj);
											}
											
											
										})
										$("#instanceViewsTree .nodeFunctionIcons .deleteIconImg").show();
									}
							
									if(data.rslt.obj.hasClass("newSchedule")){
										$("#instanceViewsTree .nodeFunctionIcons .newScheduleIconImg").click(function(){
											$("#instanceViewsTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"schedule", "class":"editSchedule delSchedule toRename"}});
										})
										$("#instanceViewsTree .nodeFunctionIcons .newScheduleIconImg").show();
									}
									
									if(data.rslt.obj.hasClass("editSchedule")){
										$("#instanceViewsTree .nodeFunctionIcons .configIconImg").click(function(){
											$("#instanceViewsTree").jstree("deselect_all");
											$("#instanceViewsTree").jstree("select_node", data.rslt.obj);
										})
										$("#instanceViewsTree .nodeFunctionIcons .configIconImg").show();
									}
									
									if(data.rslt.obj.hasClass("delSchedule")){
										$("#instanceViewsTree .nodeFunctionIcons .deleteIconImg").click(function(){
											if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
												$("#instanceViewsTree").jstree("remove", data.rslt.obj);
											}
										})
										$("#instanceViewsTree .nodeFunctionIcons .deleteIconImg").show();
									}
									
								})
								.bind("dehover_node.jstree", function(event, data) {
									treeNodeIconsTimeout=setTimeout(function(){
										data.rslt.obj.find(".nodeFunctionIcons").remove();
									},1000)
								});
							},
							onCleanup:function(){  },
							onClosed:function(){  }
						});
		            	
		            },
		            error: function(XMLHttpRequest, textStatus, errorThrown){
		                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

		            }
		        });
				
				
				
				
				
				
			})
			$("#moduleTree .nodeFunctionIcons .viewManagerIconImg").show();
			
		}
		
		
		// for instance extra action
		if(data.rslt.obj.attr("rel")=="instance" && (!data.rslt.obj.hasClass("jstree-leaf"))){
			
			// highlight current view
			 $.ajax({
			     type: "GET",
			     url: "/getCurrentViewForInstance",
			     cache: false,
			     data: "instanceId="+nodeUuid,
			     dataType:"json",
			     success: function(cvdata, textStatus){
			    	 if(cvdata.success){
			    		 if(cvdata.response1){
			    			 if(cvdata.response1.instanceviewuuid){
								$("#"+cvdata.response1.instanceviewuuid).children("a").addClass("redBorder");
			    			 }
			    		 }
			    		 
			    		 if(cvdata.response2){
			    			 if(cvdata.response2.uuid){
			    				 $("#"+cvdata.response2.uuid).children("a").addClass("redBorder");
			    			 }
			    		 }
			    		 
			    	 }else{
			    		 
			    	 }
			     	
			     },
			     error: function(XMLHttpRequest, textStatus, errorThrown){
			
			     }
			 });
			
			
			
			
		}
		
	})
	.bind("dehover_node.jstree", function(event, data) {
		treeNodeIconsTimeout=setTimeout(function(){
			data.rslt.obj.find(".nodeFunctionIcons").remove();
		},1000)
		
		// remove currentSchedule hightlight;
		$("#moduleTree a.redBorder").removeClass("redBorder");
		
	})
	

	
})



</script>