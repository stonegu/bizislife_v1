$(function() {
	
	
	
    $.contextMenu({
        selector: '.clickForContainer', 
        trigger: 'right',
        zIndex:2000,
        //autoHide: true,
        // global callback
        callback: function(key, options) {
        	var x = Math.round(options.$menu.position().left-this.offset().left);
        	var y = Math.round(options.$menu.position().top-this.offset().top);
        	var containerUuid = $(this).attr("id");
        	
        	if(key=="addContainer"){
        		
    			$.ajax({
    				type: "GET",
    				url: "/newPageBuilderElement",
    				cache: false,
    				data: "type=container&parentId="+containerUuid+"&x="+x+"&y="+y,
    				dataType:"json",
    				success: function(data, textStatus){
    					// re-select current page if response is success
    					if(data.success){
							$("#generalPageTree").jstree("deselect_all");
    						$("#generalPageTree").jstree("select_node", "#"+data.response2);
    						// coloring the tree's node for container
    						var totalStyleSheets = document.styleSheets.length; 
    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-leaf a.hex_'+data.response1.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+data.response1.hexColor+'; height: 15px;}', 0);
    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-open a.hex_'+data.response1.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+data.response1.hexColor+'; height: 15px;}', 0);		
    						document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-closed a.hex_'+data.response1.hexColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+data.response1.hexColor+';height: 15px;}', 0);		
    								
    								
    					}
    				},
    				error: function(XMLHttpRequest, textStatus, errorThrown){
    					alert("failed");
    				
    				}
    			});
        		
        	}        	
        	
        	
        	
        },

        build: function($trigger, e) {
            // this callback is executed every time the menu is to be shown
            // its results are destroyed every time the menu is hidden
            // e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)
        	
        	if(!$trigger.hasClass("scheduledContainer") && !$trigger.hasClass("preview")){
                return {
    		        items: {
    		        	
    		        	"addContainer" : {
    		        		name: "Add Container", 
    		        		icon: "pbContainer"
    		        	}
    		        	
    		        }
                };
        		
        	}else{
        		
        		if($trigger.hasClass("scheduledContainer")){
            		
            		var info = "This <span style='color:#"+$trigger.attr("colorvalue")+"'>container</span> can't put sub-container in it, because it is scheduled"
            					+" <img src='/img/vendor/web-icons/calendar-medium.png'>."
            		showPagebuildInfo($(".actionInfoContent"), info, "error");
            		return false;
        			
        		}else if($trigger.hasClass("preview")){
            		var info = "This <span style='color:#"+$trigger.attr("colorvalue")+"'>container</span> can't put sub-container in it, because you don't have permission"
								+" <img src='/img/vendor/web-icons/lock.png'>."
					showPagebuildInfo($(".actionInfoContent"), info, "error");
            		return false;
        			
        		}
        		
        	}
        	
        	
        	
        }
    });	
	
	
	
	
	
	$(".newContainerModuleScheduleSubmitBtn").live("click", function(){
//		console.log("newContainerModuleScheduleSubmitBtn");
		
        var theForm = $("form[name='newContainerModuleSchedule']");
        var serializedData = theForm.serialize();        	

		
		$.ajax({
			type: "POST",
			url: "/newContainerModuleSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
                    escKey: false,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false
				});
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("new container module schedule failed!");
			
			}
		});
		
	});
	
	$(".scheduleEdit").live("click", function(){
    	
    	var scheduleUuid = $(this).attr("domvalue");
    	
		$(".edit_scheduleLine").hide();
		$(".scheduleLine").show();
		
		$("#"+scheduleUuid).hide();
		$("#edit_"+scheduleUuid).show();
		
	});
	
	$(".editContainerModuleScheduleSubmitBtn").live("click", function(){
        var scheduleUuid = $(this).attr("domvalue");
        var theForm = $("form[name='edit_"+scheduleUuid+"']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/editContainerModuleSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
                    escKey: false,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false
				});
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("edit container module schedule failed!");
			
			}
		});
        
		
	});
	
	$(".delContainerModuleScheduleSubmitBtn").live("click", function(){
        var scheduleUuid = $(this).attr("domvalue");
        var theForm = $("form[name='edit_"+scheduleUuid+"']");
        var serializedData = theForm.serialize();
        
		var retVal = confirm("Do you want to delete?");
		if( retVal == true ){
			$.ajax({
				type: "POST",
				url: "/delContainerModuleSchedule",
				cache: false,
				data: serializedData,
				dataType:"html",
				success: function(data, textStatus){
					
					$.colorbox({
						width : 700,
						height : 500,
						html: data,
                        escKey: false,
						//inline: true, 
						//href: mediaUploadContainerContent,
						opacity: 0.4,
						overlayClose: false
					});
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("delete container module schedule failed!");
				
				}
			});
		}        
		
	});
	
	$(".moduleInstanceScheduleSetup").live("click", function(){
		
		var containerModuleScheduleUuid = $(this).attr("domvalue");
		
		$.ajax({
			type: "GET",
			url: "/moduleInstanceSchedule",
			cache: false,
			data: "containerModuleScheduleId="+containerModuleScheduleUuid,
			dataType:"html",
			success: function(data, textStatus){
				
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
                    escKey: false,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false,
					onOpen:function(){},
					onLoad:function(){},
					onComplete:function(){},
					onCleanup:function(){},
					onClosed:function(){
						
		    			$.ajax({
		    				type: "GET",
		    				url: "/containerModuleSchedule",
		    				cache: false,
		    				data: "containerId="+containerUuid,
		    				dataType:"html",
		    				success: function(data, textStatus){
		    					
								$.colorbox({
									width : 700,
									height : 500,
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
					}
					
				});
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			
			}
		});

	});
	
	$(".newModuleInstanceScheduleSubmitBtn").live("click", function(){
        var theForm = $("form[name='newModuleInstanceSchedule']");
        var serializedData = theForm.serialize();        	
		
		$.ajax({
			type: "POST",
			url: "/newModuleInstanceSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
                    escKey: false,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false,
					onOpen:function(){},
					onLoad:function(){},
					onComplete:function(){},
					onCleanup:function(){},
					onClosed:function(){
						
		    			$.ajax({
		    				type: "GET",
		    				url: "/containerModuleSchedule",
		    				cache: false,
		    				data: "containerId="+containerUuid,
		    				dataType:"html",
		    				success: function(data, textStatus){
		    					
								$.colorbox({
									width : 700,
									height : 500,
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
					}
				});
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("new module instance schedule failed!");
			
			}
		});
		
	});
	
	$(".editModuleInstanceScheduleSubmitBtn").live("click", function(){
        var scheduleUuid = $(this).attr("domvalue");
        var theForm = $("form[name='edit_"+scheduleUuid+"']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/editModuleInstanceSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
                    escKey: false,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false,
					onOpen:function(){},
					onLoad:function(){},
					onComplete:function(){},
					onCleanup:function(){},
					onClosed:function(){
						
		    			$.ajax({
		    				type: "GET",
		    				url: "/containerModuleSchedule",
		    				cache: false,
		    				data: "containerId="+containerUuid,
		    				dataType:"html",
		    				success: function(data, textStatus){
		    					
								$.colorbox({
									width : 700,
									height : 500,
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
					}
				});
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("edit module instance schedule failed!");
			
			}
		});
        
		
	});
	
	$(".delModuleInstanceScheduleSubmitBtn").live("click", function(){
        var scheduleUuid = $(this).attr("domvalue");
        var theForm = $("form[name='edit_"+scheduleUuid+"']");
        var serializedData = theForm.serialize();
        
		var retVal = confirm("Do you want to delete?");
		if( retVal == true ){
			$.ajax({
				type: "POST",
				url: "/delModuleInstanceSchedule",
				cache: false,
				data: serializedData,
				dataType:"html",
				success: function(data, textStatus){
					
					$.colorbox({
						width : 700,
						height : 500,
						html: data,
                        escKey: false,
						//inline: true, 
						//href: mediaUploadContainerContent,
						opacity: 0.4,
						overlayClose: false,
						onOpen:function(){},
						onLoad:function(){},
						onComplete:function(){},
						onCleanup:function(){},
						onClosed:function(){
							
			    			$.ajax({
			    				type: "GET",
			    				url: "/containerModuleSchedule",
			    				cache: false,
			    				data: "containerId="+containerUuid,
			    				dataType:"html",
			    				success: function(data, textStatus){
			    					
									$.colorbox({
										width : 700,
										height : 500,
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
						}
					});
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("delete module instance schedule failed!");
				
				}
			});
		}        
		
	});

})



function showPagebuildInfo(where, info, type){ // type: error, info, 
	if($(where).length>0){
		var now = new Date(); 
		var datetime = now.getFullYear()+'/'+(now.getMonth()+1)+'/'+now.getDay(); 
		datetime += ' '+now.getHours()+':'+now.getMinutes()+':'+now.getSeconds();
		
		var content = "<div class='pagebuildinfo "+type+"'>"
			+"<div class='datetime'><strong>"+datetime+"</strong></div>"
			+"<div class='infoContent' style='padding-left:20px; padding-right:30px;'>"+info+"</div>"
			+"</div>"
		
		$(where).prepend(content);
	}
}

