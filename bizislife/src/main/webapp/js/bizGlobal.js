// permissionType: permissionBtn_p, permissionBtn_r, permissionBtn_c, permissionBtn_m
// permissionValue : checkboxYes, checkboxNo, checkboxEmpty, checkboxYesFollow, checkboxNoFollow
function recursivePermissionDetect(jsTreeNode, permissionType, permissionValue){ 
	//console.log($(jsTreeNode).attr("id")+"; ");
	var nodeObj = $(jsTreeNode);
	var nodeObjId = nodeObj.attr("id");
	var permissionSetContainer = $("#"+nodeObjId+" > a > span.permissionSets");
	
	if(permissionSetContainer.length>0){
		var nodePermissionBtn = permissionSetContainer.children("button."+permissionType);
		if(nodePermissionBtn.length>0 && (!nodePermissionBtn.hasClass("checkboxYes") && !nodePermissionBtn.hasClass("checkboxNo"))){
			
			if(permissionValue=="checkboxYes" || permissionValue=="checkboxYesFollow"){
				nodePermissionBtn.removeClass("checkboxEmpty checkboxNoFollow").addClass("checkboxYesFollow");
				permissionValue = "checkboxYesFollow";
			}else if(permissionValue=="checkboxNo" || permissionValue=="checkboxNoFollow"){
				nodePermissionBtn.removeClass("checkboxEmpty checkboxYesFollow").addClass("checkboxNoFollow");
				permissionValue = "checkboxNoFollow";
			}else if(permissionValue=="checkboxEmpty"){
				nodePermissionBtn.removeClass("checkboxNoFollow checkboxYesFollow").addClass("checkboxEmpty");
			}
			
			// recursive for the children
			// find direct childrens to setup the permission icon
			$("#"+nodeObjId+" > ul").children("li").each(function(i){
				//console.log($(this).attr("id")+", ");
				recursivePermissionDetect(this, permissionType, permissionValue);
			});
			
		}
	}
	
}

// uuid generator
function generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x7|0x8)).toString(16);
    });
    return uuid;
};


/**
* HtmlEscape in JavaScript, which is compatible with utf-8
* @author Ulrich Jensen, http://www.htmlescape.net
* Feel free to get inspired, use or steal this code and use it in your
* own projects.
* License:
* You have the right to use this code in your own project or publish it
* on your own website.
* If you are going to use this code, please include the author lines.
* Use this code at your own risk. The author does not warrent or assume any
* legal liability or responsibility for the accuracy, completeness or usefullness of
* this program code.
*/

  var hex=new Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f');
  
  
  function escapeHtml(originalText)
  {
   var preescape="" + originalText;
   var escaped="";
    
   var i=0;
   for(i=0;i<preescape.length;i++)
   {
    var p=preescape.charAt(i);
     
    p=""+escapeCharOther(p);
    p=""+escapeTags(p);
    p=""+escapeBR(p);
   
    escaped=escaped+p;
   }
   return escaped;
  }

  function escapeHtmlTextArea(originalText)
  {
   
   
   var preescape="" + originalText;
   var escaped="";
   
   var i=0;
   for(i=0;i<preescape.length;i++)
   {
    var p=preescape.charAt(i);
    
    p=""+escapeCharOther(p);
    p=""+escapeTags(p);
   
    escaped=escaped+p;
   }
   
   return escaped;
  }
   
   
  function escapeBR(original)
  {
     var thechar=original.charCodeAt(0);
   
   switch(thechar) {
     case 10: return "<br/>"; break; //newline
     case '\r': break;
   }
   return original; 
  } 

  function escapeNBSP(original)
  {
     var thechar=original.charCodeAt(0);
   switch(thechar) {
     case 32: return "&nbsp;"; break; //space
   }
   return original; 
  } 


  function escapeTags(original)
  {
     var thechar=original.charCodeAt(0);
   switch(thechar) {
     case 60:return "&lt;"; break; //<
     case 62:return "&gt;"; break; //>
     case 34:return "&quot;"; break; //"
   }
   return original;
  
  }
   
  function escapeCharOther(original)
  {
     var found=true;
     var thechar=original.charCodeAt(0);
   switch(thechar) {
     case 38:return "&amp;"; break; //&
     case 198:return "&AElig;"; break; //Æ
     case 193:return "&Aacute;"; break; //Á
     case 194:return "&Acirc;"; break; //Â
     case 192:return "&Agrave;"; break; //À
     case 197:return "&Aring;"; break; //Å
     case 195:return "&Atilde;"; break; //Ã
     case 196:return "&Auml;"; break; //Ä
     case 199:return "&Ccedil;"; break; //Ç
     case 208:return "&ETH;"; break; //Ð
     case 201:return "&Eacute;"; break; //É
     case 202:return "&Ecirc;"; break; //Ê
     case 200:return "&Egrave;"; break; //È
     case 203:return "&Euml;"; break; //Ë
     case 205:return "&Iacute;"; break; //Í
     case 206:return "&Icirc;"; break; //Î
     case 204:return "&Igrave;"; break; //Ì
     case 207:return "&Iuml;"; break; //Ï
     case 209:return "&Ntilde;"; break; //Ñ
     case 211:return "&Oacute;"; break; //Ó
     case 212:return "&Ocirc;"; break; //Ô
     case 210:return "&Ograve;"; break; //Ò
     case 216:return "&Oslash;"; break; //Ø
     case 213:return "&Otilde;"; break; //Õ
     case 214:return "&Ouml;"; break; //Ö
     case 222:return "&THORN;"; break; //Þ
     case 218:return "&Uacute;"; break; //Ú
     case 219:return "&Ucirc;"; break; //Û
     case 217:return "&Ugrave;"; break; //Ù
     case 220:return "&Uuml;"; break; //Ü
     case 221:return "&Yacute;"; break; //Ý
     case 225:return "&aacute;"; break; //á
     case 226:return "&acirc;"; break; //â
     case 230:return "&aelig;"; break; //æ
     case 224:return "&agrave;"; break; //à
     case 229:return "&aring;"; break; //å
     case 227:return "&atilde;"; break; //ã
     case 228:return "&auml;"; break; //ä
     case 231:return "&ccedil;"; break; //ç
     case 233:return "&eacute;"; break; //é
     case 234:return "&ecirc;"; break; //ê
     case 232:return "&egrave;"; break; //è
     case 240:return "&eth;"; break; //ð
     case 235:return "&euml;"; break; //ë
     case 237:return "&iacute;"; break; //í
     case 238:return "&icirc;"; break; //î
     case 236:return "&igrave;"; break; //ì
     case 239:return "&iuml;"; break; //ï
     case 241:return "&ntilde;"; break; //ñ
     case 243:return "&oacute;"; break; //ó
     case 244:return "&ocirc;"; break; //ô
     case 242:return "&ograve;"; break; //ò
     case 248:return "&oslash;"; break; //ø
     case 245:return "&otilde;"; break; //õ
     case 246:return "&ouml;"; break; //ö
     case 223:return "&szlig;"; break; //ß
     case 254:return "&thorn;"; break; //þ
     case 250:return "&uacute;"; break; //ú
     case 251:return "&ucirc;"; break; //û
     case 249:return "&ugrave;"; break; //ù
     case 252:return "&uuml;"; break; //ü
     case 253:return "&yacute;"; break; //ý
     case 255:return "&yuml;"; break; //ÿ
     case 162:return "&cent;"; break; //¢
     default:
      found=false;
      break;
    }
    if(!found)
    {
     if(thechar>127) {
      var c=thechar;
      var a4=c%16;
      c=Math.floor(c/16);
      var a3=c%16;
      c=Math.floor(c/16);
      var a2=c%16;
      c=Math.floor(c/16);
      var a1=c%16;
      return "&#x"+hex[a1]+hex[a2]+hex[a3]+hex[a4]+";";  
     }
     else
     {
      return original;
     }
    }

   
  }



/************************************************************************************
*
*   Begin DomReady's all functions
*
*************************************************************************************/

var domReadyEventFunctionsCollections = {
    doTest1: function(e, c){
//	        $("#debugArea").append("current class: "+c+" | event: "+e.target.className+"<br/>");
        alert("i am here");

        return false;
    },
    doTest2: function(e, c){
        var keyunicode=e.charCode || e.keyCode;
        if(keyunicode == 13){
            $("#debugArea").append("keyunicode"+keyunicode+"current class: "+c+" | event: "+e.target.className+"<br/>");
        }else{
            $("#debugArea").append("keyunicode"+keyunicode+"current class: "+c+" | event: "+e.target.className+"<br/>");
        }
        return false;
    }, 
    
    industrySelectionForSubCats: function(e, c){
    	var currentObj = $(e.target);
    	var naicsCode = currentObj.val();
    	
    	// ajax call to get subcat
		 $.ajax({
		     type: "GET",
		     url: "/getSubCatsByIndustryIdAjax",
		     cache: false,
		     data: "naicsCode="+naicsCode,
		     dataType:"html",
		     success: function(data, textStatus){
		     	
		         $("#subcategories").html(data);
		     },
		     error: function(XMLHttpRequest, textStatus, errorThrown){
		         alert("failed");
		
		     }
		 });
    	
    },
    
    topicNodeInfo: function(e, c){
    	var currentObj = $(e.target);
        var jsonObj = eval("("+utils.domValueReader(currentObj,2)+")");
    	
//    	console.log("..... click topic node info now ....");
		 $.ajax({
		     type: "GET",
		     url: "/getTopicInfo",
		     cache: false,
		     data: jsonObj,
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
    },
    
    viewSchedSubmit: function(e, c){
    	var currentObj = $(e.target);
        var scheduleUuid = currentObj.attr("domvalue");
        var theForm = $("form[name='edit_"+scheduleUuid+"']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/editInstanceViewSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				
				$(".instanceViewManagerContainer").html(data);
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("edit instance view schedule failed!");
			
			}
		});
    	
    },
    
    attrDetailInfo: function(e, c){
    	var currentObj = $(e.target);
        var attrJsonObj = eval("("+currentObj.attr("domvalue")+")");
    	var attrid = attrJsonObj.uuid;
    	var productid = $("#productidInNodeDetail").val();
		 $.ajax({
		     type: "GET",
		     url: "/attrDetailInfo",
		     cache: false,
		     data: "productid="+productid+"&attrid="+attrid,
		     dataType:"html",
		     success: function(data, textStatus){
					$.colorbox({
//						width : 500,
//						height : 300,
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
    },
    
    attrDetailInfoImg: function(e, c){
    	var currentObj = $(e.target);
        var attrJsonObj = eval("("+currentObj.attr("domvalue")+")");
    	var productid = $("#productidInNodeDetail").val();
    	
		$("#productIdInSaveAttrForm").val(productid);
		$("#attrIdInSaveAttrForm").val(attrJsonObj.uuid);
		$("#attrNameInSaveAttrForm").val(attrJsonObj.title);
		$("#attrValueInSaveAttrForm").val(attrJsonObj.value);
		$(".imgAttrForm .attrImgShowArea").html("<img src='/getphoto?id="+attrJsonObj.value+"&size=200'/>");
		
//		var imageAttrDetailContent = $("#imgAttrDetail");
		
		$.colorbox({
			width : 800,
			height : 300,
			inline: true, 
            escKey: false,
			href: "#imgAttrDetail",
			opacity: 0.4,
			overlayClose: false,
			onOpen:function(){},
			onLoad:function(){},
			onComplete:function(){
			},
			onCleanup:function(){
			},
			onClosed:function(){}
			
		});
		
    },
    
    attrUpdate: function(e, c){
    	
		var attrDatas = $("form[name='saveAttrForm']").serialize();
		
		$.ajax({
			type: "POST",
			url: "/updateAttribute",
			cache: false,
			data: attrDatas,
			dataType:"json",
			success: function(data, textStatus){
				var title = $("form[name='saveAttrForm'] input[name='attrName']").val();
				var content = $("form[name='saveAttrForm'] input[name='attrValue']").val();
				
				$.colorbox.close();
				
				$("#"+data.response1).find(".attrTitle").html(title);
				$("#"+data.response1).find(".attrValue").html(content);
				
//				else{ // new
//					// create a new grid in attribute grids list
//					var x = $('<li id="'+data.response1+'" class="attrgrid"><div><div class="gridTitle"><span class="attrTitle">'+title+'</span><span class="actionIcons"><img class="domReady_attrDetailInfo" domvalue="'+data.response1+'" src="/img/vendor/web-icons/external.png" /><img class="moveHandler" src="/img/vendor/web-icons/arrow-move.png" /></span></div><div class="attrValue">'+content+'</div></div></li>');
//					x.fadeTo(600, 0.5).appendTo('#sortable')
//				}
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			
			}
		});
    	
    },
    
    newAttr: function(e, c) {
		var attrDatas = $("form[name='saveAttrForm']").serialize();
		
		$.ajax({
			type: "POST",
			url: "/newAttribute",
			cache: false,
			data: attrDatas,
			dataType:"html",
			success: function(data, textStatus){
				$.colorbox.close();
				$(data).fadeTo(600, 0.5).appendTo('#sortable');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    	
    },
    
    deleteAttrGroupSet: function(e, c){
    	
    	if(confirm("Delete module-groupset?")){
        	//console.log("del attr group set");
        	var currentObj = $(e.target);
    		var moduleId = $("#moduleId").val();
    		var attrGroupUuid = currentObj.attr("domvalue");
    		$.ajax({
    			type: "GET",
    			url: "/deleteAttrGroupSet",
    			cache: false,
    			data: "moduleid="+moduleId+"&attrGroupId="+attrGroupUuid,
    			dataType:"json",
    			success: function(data, textStatus){
    				if(data.success){
    					// data.response1
    					currentObj.parents("fieldset.groupset").remove();
    					
    					// update usage info:
    					$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
    					
    				}else{
    					alert("Not deleted, try to refresh page and delete it again!");
    				}
    			},
    			error: function(XMLHttpRequest, textStatus, errorThrown){
    				alert("failed");
    			}
    		});
    		
    	}
    	
    	
    },
    
    deleteinstanceGroupSet: function(e, c){
    	if(confirm("Delete instance-groupset?")){
        	//console.log("del attr group set");
        	var currentObj = $(e.target);
    		var instanceId = $("#instanceId").val();
    		var attrGroupUuid = currentObj.attr("domvalue");
    		$.ajax({
    			type: "GET",
    			url: "/deleteInstanceAttrGroupSet",
    			cache: false,
    			data: "instanceUuid="+instanceId+"&attrGroupUuid="+attrGroupUuid,
    			dataType:"json",
    			success: function(data, textStatus){
    				if(data.success){
    					// data.response1
    					currentObj.parents("fieldset.groupset").remove();
    					
    					// update usage info:
    					$(".moduleInstanceDataStruct .instanceUsedSpace").html(data.response3+"/100");
    					$(".entityUsedSpace").html(data.response3+"/100");
   					
    					
    					
    				}else{
    					alert("Not deleted, try to refresh page and delete it again!");
    				}
    			},
    			error: function(XMLHttpRequest, textStatus, errorThrown){
    				alert("failed");
    			}
    		});
        	
    		
    	}
    	
    },
    
    
    deleteModuleAttr: function(e, c){
    	
    	
    	if(confirm("Delete module-attribute?")){
    		
        	var currentObj = $(e.target);
    		var moduleId = $("#moduleId").val();
        	var domvalue = currentObj.attr("domvalue");
            var jsonObj = eval("("+domvalue+")");
        	//console.log("groupid: "+jsonObj.groupid+" | attrid: "+jsonObj.attrid);
    		$.ajax({
    			type: "GET",
    			url: "/deleteModuleAttr",
    			cache: false,
    			data: "moduleId="+moduleId+"&attrGroupId="+jsonObj.groupid+"&attrId="+jsonObj.attrid,
    			dataType:"json",
    			success: function(data, textStatus){
    				if(data.success){
    					
    					// update usage info:
    					$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
    					
    					// data.response1
    					$("#"+data.response1).remove();
    				}else{
    					alert("Not deleted, try to refresh page and delete it again!");
    				}
    			},
    			error: function(XMLHttpRequest, textStatus, errorThrown){
    				alert("failed");
    			}
    		});
    	}
        
        
    },
    
    deleteInstanceAttr: function(e, c){
    	if(confirm("Delete instance-attribute?")){
        	var currentObj = $(e.target);
    		var instanceId = $("#instanceId").val();
        	var domvalue = currentObj.attr("domvalue");
            var jsonObj = eval("("+domvalue+")");
        	//console.log("groupid: "+jsonObj.groupid+" | attrid: "+jsonObj.attrid);
    		$.ajax({
    			type: "GET",
    			url: "/deleteInstanceAttr",
    			cache: false,
    			data: "instanceId="+instanceId+"&attrGroupId="+jsonObj.groupid+"&attrId="+jsonObj.attrid,
    			dataType:"json",
    			success: function(data, textStatus){
    				if(data.success){
    					// data.response1
    					$("#"+data.response1).remove();
    					
    					// update charUsage info
    					$(".moduleInstanceDataStruct .instanceUsedSpace").html(data.response3+"/100");
    					$(".entityUsedSpace").html(data.response3+"/100");
    					
    				}else{
    					alert("Not deleted, try to refresh page and delete it again!");
    				}
    			},
    			error: function(XMLHttpRequest, textStatus, errorThrown){
    				alert("failed");
    			}
    		});
            
    		
    	}
    	
    },
    
    newAttrGroupSet: function(e, c){
    	
		var moduleId = $("#moduleId").val();

    	//console.log("new attr group set");
		$.ajax({
		     type: "GET",
		     url: "/newAttrGroupSet",
		     cache: false,
		     data: "moduleId="+moduleId,
		     dataType:"html",
		     success: function(data, textStatus){
		    	 
		    	 //$("div.moduleDataStruct").append(data);
		    	 //$(data).insertBefore(".newGroupSetBtn");
		    	 
		    	 var moduleUsage = $(data).insertBefore(".newGroupSetBtn").find(".moduleDetailUsageWith100Multipled_newAttrGroupSet");
		    	 if(moduleUsage.length>0){
		    		 $("#tabs-3 .moduleUsedSpace").html(moduleUsage.val()+"/100");
		    	 }
		    	 
		    	 $( ".attrListContainer" ).droppable( "destroy" )
		    	 // bind droppable event to attrListContainer div.
		    	 $(".attrListContainer").droppable({
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
                                        txtMirror.on("blur", function(instance){
                                            //console.log("onBlur");
                                            instance.save();
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
		    	 
		     },
		     error: function(XMLHttpRequest, textStatus, errorThrown){
		         alert("failed");
		
		     }
		});
    	
    },
    
    newAttrImg: function(e, c) {
		var attrDatas = $("form[name='saveAttrForm_img']").serialize();
		
		$.ajax({
			type: "POST",
			url: "/newAttribute",
			cache: false,
			data: attrDatas,
			dataType:"html",
			success: function(data, textStatus){
				$.colorbox.close();
				$(data).fadeTo(600, 0.5).appendTo('#sortable');
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    	
    },
    
    pagePublish: function(e, c){
        var theForm = $("form[name='pagePublish']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/pagepublish",
			cache: false,
			data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					// close popup
					$.colorbox.close();
					// clean page msg
					$(".pageBriefInfoContainer").html("");
					$(".pageChangedinfo_msg_content").html("");
				}else{
					$(".pageChangedinfo_msg_content").html("System can't publish page changes, this could be system issues. You can refresh page and try again!");
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("page publish failed!");
			
			}
		});
        
    	
    	
    },
    
    pbTooltip: function(e, c){
    	var currentObj = $(e.target);
    	
//    	console.log("for domClick : pageX: "+e.pageX+" - currentObj.offset.left: "+currentObj.offset().left+" = "+(e.pageX-currentObj.offset().left));
//    	console.log("for domClick : pageY: "+e.pageY+" - currentObj.offset.top: "+currentObj.offset().top+" = "+(e.pageY-currentObj.offset().top));
    	
    	
    },
    
    xProductDetail: function(e, c){
    	$("#productDetailSection").css({"width":"0"}).hide().html("");
    	
    	$(".pageleft").show().animate({
    		'width': '25%'
    	}, 600, function(){
    		
    	});
    	
    	$(".pageright").show().animate({
    		'width': '25%'
    	}, 600, function(){
    		
    	});
    	
    	$(".pagecenter").animate({
    		'width': '50%'
    	}, 500, function(){
    		$("#productTreeSction").css({"width":"100%"})
    	});
    	
    }, 
    
    xPageDetail: function(e, c){
    	$("#pageDetailSection").css({"width":"0"}).hide().html("");
    	
    	$(".pageleft").show().animate({
    		'width': '25%'
    	}, 600, function(){
    		
    	});
    	
    	$(".pageright").show().animate({
    		'width': '25%'
    	}, 600, function(){
    		
    	});
    	
    	$(".pagecenter").animate({
    		'width': '50%'
    	}, 500, function(){
    		$("#pageTreeSction").css({"width":"100%"})
    		// remove all container sub-trees from page tree
    		$("#"+openedPageId).children("ul").remove();
    		if($("li.mb")){
        		$("li.mb").removeClass("jstree-closed jstree-open").addClass("jstree-leaf");
    		}
    		if($("li.dk")){
        		$("li.dk").removeClass("jstree-closed jstree-open").addClass("jstree-leaf");
    		}
    		// the pageId has been opened.
    		openedPageId = "";
    	});
    	
    	
    	// for ps style detail container
    	if((typeof detailContainer != "undefined") && detailContainer){
    		detailContainer.hideContainer();
    	}
    	
    	
    },
    
    attrDelete: function(e, c){
    	var currentObj = $(e.target);
    	var productid = $("#productidInNodeDetail").val();
    	var attrid = currentObj.attr("domvalue");
    	var title = $("#"+attrid).find(".attrTitle").html();
    	
		var r=confirm("Delete "+title+"?");
		if (r==true) {
			// ajax call to del

			$.ajax({
				type: "POST",
				url: "/delAttribute",
				cache: false,
			     data: "productid="+productid+"&attrid="+attrid,
				dataType:"html",
				success: function(data, textStatus){
					$("#"+attrid).remove();
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
				}
			});
			
		} else {
//			console.log("You pressed Cancel!");
		}
    },
    
    datePicker: function(e, c){
    	var currentObj = $(e.target);
    	var currentValue = currentObj.val();
    	if(!currentObj.hasClass("hasDatepicker")){
        	currentObj.datepicker();
        	currentObj.datepicker("option", "dateFormat", "yy-mm-dd");
    	}
    	if(currentValue){
    		currentObj.datepicker("setDate", currentValue);
    	}
    	currentObj.datepicker("show");
    	
    },
    
    dupliModuleElemt: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
    	var paramGroupIndex = currentObj.attr("paramGroupIndex");
    	// +1 for totalModuleInstanceGroupInAjax
    	if(currentObj.hasClass("groupDuplicate")){
        	totalModuleInstanceGroupInAjax = totalModuleInstanceGroupInAjax + 1;
    	}
    	
    	
//    	moduleUuid is global scope which is setup during the module selection.
//    	console.log(domvalue);
		$.ajax({
			type: "GET",
			url: "/dupliModuleElemt",
			cache: false,
		    data: "moduleId="+moduleUuid+"&elementPath="+domvalue+"&paramGroupIndex="+paramGroupIndex+"&totalModuleInstanceGroupInAjax="+totalModuleInstanceGroupInAjax,
			dataType:"html",
			success: function(data, textStatus){
				if(currentObj.hasClass("groupDuplicate")){
					//currentObj.parents("fieldset.moduleParamGroup").after(data);
					$("#"+domvalue+"_newAjaxGroupHere").before(data);
				}else if(currentObj.hasClass("paramDuplicate")){
					currentObj.parent(".moduleParam").after(data);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    deleteModuleElemt: function(e, c){
    	var currentObj = $(e.target);
    	if(currentObj.hasClass("groupRemove")){
    		currentObj.parents("fieldset.moduleParamGroup").remove();
    	}else if(currentObj.hasClass("paramRemove")){
    		currentObj.parent(".moduleParam").remove();
    	}
    	
    	
    },
    
    toEditValue: function(e, c){
    	//console.log("edit module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
    	$("#"+domvalue).find(".valueSection").addClass("displaynone");
    	$("#"+domvalue).find(".editSection").removeClass("displaynone");
    },
    
    toEditAttrTxtValue: function(e, c){
    	var currentObj = $(e.target);
    	var currentDomvalue = currentObj.attr("domvalue");
    	var currentCodeMirror = currentObj.data("txtMirror");
    	
    	var originalTxtValue = currentCodeMirror.getValue();
    	$("#attrDefaultTxtValue_"+currentDomvalue+" .domReady_toCancelAttrTxtValue").data("originalTxtValue", originalTxtValue);
    	currentCodeMirror.setOption("readOnly", false);
    	
    	//attrDefaultTxtValue_7996e4d5-84f1-409c-b52a-f326c7761d0b
    	$("#attrDefaultTxtValue_"+currentDomvalue).find(".valueSection").addClass("displaynone");
    	$("#attrDefaultTxtValue_"+currentDomvalue).find(".editSection").removeClass("displaynone");
    	
    },
    
    toCancelAttrTxtValue: function(e, c){
    	var currentObj = $(e.target);
    	var currentDomvalue = currentObj.attr("domvalue");
    	var currentCodeMirror = currentObj.data("txtMirror");
    	
    	var originalTxtValue = currentObj.data("originalTxtValue");
    	
    	currentCodeMirror.setValue(originalTxtValue);
    	currentCodeMirror.setOption("readOnly", true);
    	
    	//attrDefaultTxtValue_7996e4d5-84f1-409c-b52a-f326c7761d0b
    	$("#attrDefaultTxtValue_"+currentDomvalue).find(".valueSection").removeClass("displaynone");
    	$("#attrDefaultTxtValue_"+currentDomvalue).find(".editSection").addClass("displaynone");
    	
//    	$("#"+attrId+" .attrValidationStatus").show();
//    	$("#"+attrId+" .attrValidationStatus_temp").html("").hide();
    	
		$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus").show();
		$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus_temp").html("").hide();
    	
    	
    	
    },
    
    toSaveModuleAttrTxtValue: function(e, c){
    	
    	var currentObj = $(e.target);
    	var currentDomvalue = currentObj.attr("domvalue");

    	//attrDefaultTxtUpdateForm_25ec6ac9-aed3-4669-b000-011361096d32
        var theForm = $("form[id='attrDefaultTxtUpdateForm_"+currentDomvalue+"']");
        var serializedData = theForm.serialize();

		$("#moduleValueSaveStatus_"+currentDomvalue).html("");
		
		$.ajax({
			type: "POST",
			url: "/updateModuleValue",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					// update moduleUsage info:
					$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
					
			    	$("#attrDefaultTxtValue_"+currentDomvalue+" .domReady_toCancelAttrTxtValue").data("originalTxtValue", data.response1);
					
					$("#attrDefaultTxtValue_"+currentDomvalue).find("img.domReady_toCancelAttrTxtValue").click();
					
					// do extra task for change module type.
					$("#moduleValueSaveStatus_"+currentDomvalue).html("");
				}else{
					$("#moduleValueSaveStatus_"+currentDomvalue).html(data.response1);
					
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    toSaveAttrTxtValue: function(e, c){
    	
    	var currentObj = $(e.target);
    	var currentDomvalue = currentObj.attr("domvalue");

    	//attrDefaultTxtUpdateForm_25ec6ac9-aed3-4669-b000-011361096d32
        var theForm = $("form[id='attrDefaultTxtUpdateForm_"+currentDomvalue+"']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/updateInstanceValue",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
			    	$("#attrDefaultTxtValue_"+currentDomvalue+" .domReady_toCancelAttrTxtValue").data("originalTxtValue", data.response1);
			    	
					$("#attrDefaultTxtValue_"+currentDomvalue).find("img.domReady_toCancelAttrTxtValue").click();
					
					
					
					// ajax call to validate attribute again!
			        $.ajax({
			            type: "GET",
			            url: "/getAttributeValidation",
			            cache: false,
			            data: serializedData,
			            dataType:"json",
			            success: function(avData, textStatus){
			            	if(avData.success){
								$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus").html("").show();
								$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus_temp").html("").hide();
			            	}else{
								$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus").html(avData.response1).show();
								$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus_temp").html("").hide();
			            	}
			            	
			            },
			            error: function(XMLHttpRequest, textStatus, errorThrown){
			                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

			            }
			        });
					
				}else{
					$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus").hide();
					$("#instanceValueSaveStatus_"+currentDomvalue+" .attrValidationStatus_temp").html(data.response1).show();
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    saveModuleValue: function(e, c){

    	//console.log("save module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        
        
        var canUpdate = true;
        if(jsonObj.valueName=="name" || jsonObj.valueName=="groupName" || jsonObj.valueName=="array"){
            if(!confirm("You are trying to update the "+jsonObj.valueName+", YOU NEED TO UPDATE MODULE'S JSP AND INSTANCES' JSP ALSO!!\nAre you sure you like contine?")){
            	canUpdate = false;
            }
        }
        
        if(canUpdate){
    		var moduleId = $("#moduleId").val();
    		//$('input[value="Hot Fuzz"]')
    		var updateValue = $("#"+jsonObj.valueId).val();
    		
//    		if($("#"+jsonObj.valueId).prop('tagName')=="SELECT"){
//    			updateValue = 
//    		}
    		

    		if(jsonObj.attrUuid){
        		$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html("");
        	}else{
        		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html("");
        	}
    		
    		var postData = {"moduleId" : moduleId, "updateType" : jsonObj.typeToSave, "groupUuid" : jsonObj.groupUuid, "attrUuid" : jsonObj.attrUuid, "updateValue" : updateValue, "valueName" : jsonObj.valueName};
    		
			$.ajax({
    			type: "POST",
    			url: "/updateModuleValue",
    			cache: false,
    		    data: postData,
    			dataType:"json",
    			success: function(data, textStatus){
    				if(data.success){
    					// update moduleUsage info:
    					if(jsonObj.typeToSave=="groupValue"){
    						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
    					}else if(jsonObj.typeToSave=="attrValue"){
    						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
    					}else if(jsonObj.typeToSave=="moduledetailValue"){
    						if(jsonObj.valueName=="jsp"){
        						$("#tabs-4 .moduleJspUsedSpace").html(data.response3+"/100");
    						}else if(jsonObj.valueName=="css"){
        						$("#tabs-5 .moduleCssUsedSpace").html(data.response3+"/100");
    						}else{
        						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
    						}
    					}
    					
    					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
    					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
    					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
    					
    					// do extra task for change module type.
    					if(jsonObj.valueName=="type"){
    						if(data.response1=="true"){
    							$("#"+moduleId).removeClass("toInstance module").addClass("productModule").attr("rel", "productModule");
    							
    						}else if(data.response1=="false"){
    							$("#"+moduleId).removeClass("productModule").addClass("toInstance module").attr("rel", "module");
    						}
    					}
    					
    					$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html("");
    				}else{
    					if(jsonObj.attrUuid){
    						$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html(data.response1);
    			    	}else if(jsonObj.groupUuid){
    			    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
    			    	}else {
    			    		alert(data.response1);
    			    	}
    					
    				}
    			},
    			error: function(XMLHttpRequest, textStatus, errorThrown){
    				alert("failed");
    			}
    		});
        	
        }
    	
    },
    
    saveViewSchedValue: function(e, c){
    	//console.log("save module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var scheduleId = $("#scheduleId").val();
		//$('input[value="Hot Fuzz"]')
		var updateValue = $("#"+jsonObj.valueId).val();
		
		$.ajax({
			type: "GET",
			url: "/updateViewSchedValue",
			cache: false,
		    data: "scheduleId="+scheduleId+"&updateType="+jsonObj.typeToSave+"&updateValue="+updateValue+"&valueName="+jsonObj.valueName,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
					
					// refresh all activated view and scheds flag
					$("li.schedActivated").removeClass("schedActivated");
					if(data.response2){
						$(data.response2).each(function(){
							$("#"+this).addClass("schedActivated");
						})
					}
					
					
				}else{
					$(".instanceViewSchedule_info").html(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    saveViewValue: function(e, c){

    	//console.log("save module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var viewId = $("#instanceViewId").val();
		//$('input[value="Hot Fuzz"]')
		var updateValue = $("#"+jsonObj.valueId).val();
		
		$.ajax({
			type: "GET",
			url: "/updateViewValue",
			cache: false,
		    data: "viewId="+viewId+"&updateType="+jsonObj.typeToSave+"&updateValue="+updateValue+"&valueName="+jsonObj.valueName,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
					// refresh all activated view and scheds flag
					if(data.response2){
						$("li.schedActivated").removeClass("schedActivated");
						$(data.response2).each(function(){
							$("#"+this).addClass("schedActivated");
						})
					}
					
					// update jsp/css charusage data
					if(data.response3!=null){
						if(jsonObj.valueName=='jsp'){
							$(".viewJspUsedSpace").html(data.response3+"/100");
						}else if(jsonObj.valueName=='css'){
							$(".viewCssUsedSpace").html(data.response3+"/100");
						}
					}
					
				}else{
					alert(data.response1);
/*					
					if(jsonObj.attrUuid){
						$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html(data.response1);
			    	}else{
			    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
			    	}
*/					
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    	
    	
    },
    
    scheduleEdit: function(e, c){
		
		$(".schedDataToView").hide();
		$(".schedDataToEdit").show();
    	
    },
    
    
    cancelToEditValue: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
    	$("#"+domvalue).find(".valueSection").removeClass("displaynone");
    	$("#"+domvalue).find(".editSection").addClass("displaynone");
    	
    	// for instance update:
    	var attrId = domvalue.substring(domvalue.indexOf("_")+1);
    	$("#"+attrId+" .attrValidationStatus").show();
    	$("#"+attrId+" .attrValidationStatus_temp").html("").hide();
    	
    	// for moduledetail update:
    	$("#moduleValueSaveStatus_"+attrId).html("");
    	
    	
    },
    
    editModuleImg: function(e, c){
    	var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
    	
    	if(currentObj.hasClass("insideColorbox")){
            $("#"+jsonObj.webSectionId).find(".valueSection").addClass("displaynone");
            $("#"+jsonObj.webSectionId).find(".editSection").removeClass("displaynone");
            
            // open media list 
            $(".medialistForModuleConfig").removeClass("displaynone");
            $(".medialistForModuleConfig").find(".domReady_saveModuleImg").attr("domvalue", domvalue);
            $(".medialistForModuleConfig").find(".domReady_saveInstanceImg").attr("domvalue", domvalue);
            
            $(".medialistForModuleConfig").find(".domReady_cancelEditModuleImg").attr("domvalue", domvalue);
    	}else{
    	    
    	}
    	
    },
    
    cancelEditModuleImg: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
    	$("#"+jsonObj.webSectionId).find(".valueSection").removeClass("displaynone");
    	$("#"+jsonObj.webSectionId).find(".editSection").addClass("displaynone");
    	
    	// close media list 
    	$(".medialistForModuleConfig").addClass("displaynone");
    },
    
    selectMediaForModule: function(e, c){
        
        var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
        if(currentObj.hasClass("selected")){
            $("#mediaSelectForm_defaultValue").val(null);
        }else{
            $("#mediaSelectForm_defaultValue").val(domvalue);
        }
        var defaultValue_orig = $("#mediaSelectForm_defaultValue").val(),
            instanceId_orig = $("#mediaSelectForm_instanceId").val(),
            groutUuid_orig = $("#mediaSelectForm_groupUuid").val(),
            attrUuid_orig = $("#mediaSelectForm_attrUuid").val(); 
        
        var theForm = $("form[id='mediaSelectForm']");
        var serializedData = theForm.serialize(),
            formAction = theForm.attr("action");
        
        $.ajax({
            type: "POST",
            url: formAction,
            cache: false,
            //data: "instanceId="+instanceId+"&updateType="+jsonObj.typeToSave+"&groupUuid="+jsonObj.groupUuid+"&attrUuid="+jsonObj.attrUuid+"&updateValue="+imgFileSysName+"&valueName="+jsonObj.valueName,
            data: serializedData,
            dataType:"json",
            success: function(smData, textStatus){
                if(smData.success){
                    
                    // mediaSelectForm
                    if($("#mediaSelectForm").length>0){
                        $("img.domReady_selectMediaForModule").removeClass("selected").attr("src", "/img/vendor/web-icons/tick-box.png")
                        if(!!smData.response1){
                            $("img[domvalue='"+smData.response1+"']").addClass("selected").attr("src", "/img/vendor/web-icons/tick-button.png");
                        }
                    }
                    
                    if(formAction==="/updateInstanceValue"){
                        // upate instance's usage info
                        $(".moduleInstanceDataStruct .instanceUsedSpace").html(smData.response3+"/100");
                        $(".entityUsedSpace").html(smData.response3+"/100");
                        $("#instanceValueSaveStatus_"+attrUuid_orig+" .attrValidationStatus").html("");
                        $("#instanceValueSaveStatus_"+attrUuid_orig+" .attrValidationStatus_temp").html("").hide();
                    }else if(formAction==="/updateModuleValue"){
                        $("#tabs-3 .moduleUsedSpace").html(smData.response3+"/100");
                        $("#moduleValueSaveStatus_"+attrUuid_orig).html("");
                    }
                    
                    // replace the image
                    $("#attrImgFile_"+attrUuid_orig).find(".value").html("<img src='/getphoto?id="+smData.response1+"&size=50'/>")
                    // replace the domvalue
                    var newDomvalue = "{'popupContentContainerAppendId':'medialistForModuleConfigId', 'stay':true, 'width':'600px', 'height':'400px', 'topOffset': '-100', 'replacement': [{'objId':'mediaSelectForm_groupUuid', 'objVal':'"+groutUuid_orig+"'},{'objId':'mediaSelectForm_attrUuid', 'objVal':'"+attrUuid_orig+"'},{'objId':'mediaSelectForm_valueName', 'objVal':'fileSystemName'},{'objId':'mediaSelectForm_defaultValue', 'objVal':'"+defaultValue_orig+"'}]}"
                    $("#attrImgFile_"+attrUuid_orig).find("img.medialistForModuleConfigIcon").attr("domvalue", newDomvalue);
                    
                }else{
                    if(formAction==="/updateInstanceValue"){
                        $("#instanceValueSaveStatus_"+attrUuid_orig+" .attrValidationStatus").hide();
                        $("#instanceValueSaveStatus_"+attrUuid_orig+" .attrValidationStatus_temp").html(smData.response1).show();
                        $("#mediaSelectForm_defaultValue").val(defaultValue_orig);
                    }else if(formAction==="/updateModuleValue"){
                        $("#moduleValueSaveStatus_"+attrUuid_orig).html(smData.response1);
                    }
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $("#mediaSelectForm_defaultValue").val(defaultValue_orig);
                alert("failed");
            }
        });
        
        
    },
    
    saveInstanceImg: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        var imgFileSysName = $("#selectedImgSysNameForModuleConfig").val();
		var instanceId = $("#instanceId").val();

//		if(jsonObj.attrUuid){
//			$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html("");
//    	}else{
//    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html("");
//    	}
		
		if(jsonObj.attrUuid){
    		$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html("");
    	}else{
    		$("#instanceValueSaveStatus_"+jsonObj.groupUuid).html("");
    	}
        
		$.ajax({
			type: "GET",
			url: "/updateInstanceValue",
			cache: false,
		    data: "instanceId="+instanceId+"&updateType="+jsonObj.typeToSave+"&groupUuid="+jsonObj.groupUuid+"&attrUuid="+jsonObj.attrUuid+"&updateValue="+imgFileSysName+"&valueName="+jsonObj.valueName,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					// upate instance's usage info
					$(".moduleInstanceDataStruct .instanceUsedSpace").html(data.response3+"/100");
					$(".entityUsedSpace").html(data.response3+"/100");
					
					
					
			    	$("#"+jsonObj.webSectionId).find(".valueSection").removeClass("displaynone");
			    	$("#"+jsonObj.webSectionId).find(".editSection").addClass("displaynone");
			    	
			    	// close media list 
			    	$(".medialistForModuleConfig").addClass("displaynone");
			    	// replace the image
			    	$("#"+jsonObj.webSectionId).find(".value").html("<img src='/getphoto?id="+data.response1+"&size=50'/>")
			    	
					$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus").html("");
					$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html("").hide();
			    	
				}else{
					
					if(jsonObj.attrUuid){
						//console.log("aaaaaaaaaaaaa");
						$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus").hide();
						$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html(data.response1).show();
			    	}else{
			    		$("#instanceValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
			    	}
					
					
//					if(jsonObj.attrUuid){
//						$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html(data.response1);
//			    	}else{
//			    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
//			    	}
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    saveContainerValue: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var updateValue = $("#"+jsonObj.valueId).val();

        
		$.ajax({
			type: "GET",
			url: "/updateContainerDetailValue",
			cache: false,
		    data: "targetId="+jsonObj.containerUuid+"&updateValue="+updateValue+"&valueName="+jsonObj.valueName,
			dataType:"json",
			success: function(adata, textStatus){
				if(adata.success){
					
					
					
					$("#"+jsonObj.webSectionId).find("span.value").html(adata.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(adata.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
					// do extra task for change module type.
					if(jsonObj.valueName=="prettyname"){
						
						// update the containerTree for container's name
//						$("#generalPageTree").jstree('rename_node', ["#"+adata.response2 , adata.response1] );
//						console.log("resp1: "+adata.response1 + " | resp2: "+adata.response2);
						$("#generalPageTree").jstree('rename_node', "#"+adata.response2 , adata.response1);
						
					}else if(jsonObj.valueName=="classnames"){
						if(adata.response3){
							var str = 'This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="'+adata.response3+'">Click here</span> for further processing.'
							$(".pageBriefInfoContainer").html(str);
						}
						
					}
					
					$(".containerUpdateStatusInfo").html("");					
					
				}else{
					$(".containerUpdateStatusInfo").html(adata.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    },
    
    saveModuleImg: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        var imgFileSysName = $("#selectedImgSysNameForModuleConfig").val();
		var moduleId = $("#moduleId").val();

		if(jsonObj.attrUuid){
			$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html("");
    	}else{
    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html("");
    	}
        
		$.ajax({
			type: "GET",
			url: "/updateModuleValue",
			cache: false,
		    data: "moduleId="+moduleId+"&updateType="+jsonObj.typeToSave+"&groupUuid="+jsonObj.groupUuid+"&attrUuid="+jsonObj.attrUuid+"&updateValue="+imgFileSysName+"&valueName="+jsonObj.valueName,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					
					// update moduleUsage info:
					if(jsonObj.typeToSave=="groupValue"){
						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
					}else if(jsonObj.typeToSave=="attrValue"){
						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
					}else if(jsonObj.typeToSave=="moduledetailValue"){
						if(jsonObj.valueName=="jsp"){
    						$("#tabs-4 .moduleJspUsedSpace").html(data.response3+"/100");
						}else if(jsonObj.valueName=="css"){
    						$("#tabs-5 .moduleCssUsedSpace").html(data.response3+"/100");
						}else{
    						$("#tabs-3 .moduleUsedSpace").html(data.response3+"/100");
						}
					}
					
					
			    	$("#"+jsonObj.webSectionId).find(".valueSection").removeClass("displaynone");
			    	$("#"+jsonObj.webSectionId).find(".editSection").addClass("displaynone");
			    	
			    	// close media list 
			    	$(".medialistForModuleConfig").addClass("displaynone");
			    	// replace the image
			    	$("#"+jsonObj.webSectionId).find(".value").html("<img src='/getphoto?id="+data.response1+"&size=50'/>")
			    	
					
				}else{
					if(jsonObj.attrUuid){
						$("#moduleValueSaveStatus_"+jsonObj.attrUuid).html(data.response1);
			    	}else if(jsonObj.groupUuid){
			    		$("#moduleValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
			    	}else{
			    		alert(data.response1);
			    	}
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    saveInstanceValue: function(e, c){

    	//console.log("save module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var instanceId = $("#instanceId").val();
		//$('input[value="Hot Fuzz"]')
		var updateValue = $("#"+jsonObj.valueId).val();

		if(jsonObj.attrUuid){
    		$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html("");
    	}else{
    		$("#instanceValueSaveStatus_"+jsonObj.groupUuid).html("");
    	}
		
		var postData = {"instanceId" : instanceId, "updateType" : jsonObj.typeToSave, "groupUuid" : jsonObj.groupUuid, "attrUuid" : jsonObj.attrUuid, "updateValue" : updateValue, "valueName" : jsonObj.valueName};
		
		$.ajax({
			type: "POST",
			url: "/updateInstanceValue",
			cache: false,
		    data: postData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					
					// upate instance's usage info
					$(".moduleInstanceDataStruct .instanceUsedSpace").html(data.response3+"/100");
					$(".entityUsedSpace").html(data.response3+"/100");
					
					
					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
					// ajax call to validate attribute again!
			        $.ajax({
			            type: "GET",
			            url: "/getAttributeValidation",
			            cache: false,
			            data: "instanceId="+instanceId+"&groupUuid="+jsonObj.groupUuid+"&attrUuid="+jsonObj.attrUuid,
			            dataType:"json",
			            success: function(avData, textStatus){
			            	if(avData.success){
								$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus").html("").show();
								$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html("").hide();
			            	}else{
								$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus").html(avData.response1).show();
								$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html("").hide();
			            	}
			            	
			            },
			            error: function(XMLHttpRequest, textStatus, errorThrown){
			                //$("#beginPoint").after("<p style='color:red;'>XMLHttpRequest: "+XMLHttpRequest+" | textStatus: "+textStatus+" | errorThrown: "+errorThrown+"</p>");

			            }
			        });
					
				}else{
					if(jsonObj.attrUuid){
						//console.log("aaaaaaaaaaaaa");
						$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus").hide();
						$("#instanceValueSaveStatus_"+jsonObj.attrUuid+" .attrValidationStatus_temp").html(data.response1).show();
			    	}else{
			    		$("#instanceValueSaveStatus_"+jsonObj.groupUuid).html(data.response1);
			    	}
					
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    	
    	
    },
    
    duplicateAttrGroupSet: function(e, c){
    	var currentObj = $(e.target);
    	//{'groupid':'8f75ee6e-c34e-4448-8f9e-c8971c8ea55f', 'attrid':'ad1db725-d9e1-4dee-8cfb-77eaaf11fd13'}
    	var domvalue = currentObj.attr("domvalue");
//        var jsonObj = eval("("+domvalue+")");
		var instanceId = $("#instanceId").val();
        
		$.ajax({
			type: "GET",
			url: "/duplicateAttrGroupSet",
			cache: false,
		    data: "instanceUuid="+instanceId+"&groupUuid="+domvalue,
			dataType:"html",
			success: function(data, textStatus){
				
				// remove all old usage info
				$(".instanceCharUsageWith100Multipled_attrGroupDupli").remove();
				
				
                // *** add uuid to each textarea for codeMirror
                var madataDom = $(data);
                // for all text attribute edit
                var newTextAreaIds = new Array();
                madataDom.find(".attrDefaultTxtValueEditor").each(function(idx){
                    var newTextAreaId = generateUUID()
                    newTextAreaIds.push(newTextAreaId);
                    
                    $(this).attr('id', newTextAreaId);
                    
                });
				
				currentObj.parents("fieldset.groupset").after(madataDom);
				
                // constructor codeMirror based on newTextAreaIds
                for (var j=0; j<newTextAreaIds.length; j++) {
                   var newTextAreaId = newTextAreaIds[j];
                    
                   var attrId = $("#"+newTextAreaIds[j]).attr("domvalue");
                   var txtMirror = CodeMirror.fromTextArea(document.getElementById(newTextAreaId),
                       {
                           lineNumbers: true,
                           mode: "application/x-ejs",
                           indentUnit: 4,
                           indentWithTabs: true,
                           enterMode: "keep",
                           autoCloseTags: true,
                           readOnly: true,
                           //theme: "aa bb",
                           tabMode: "shift"
                       });
                   txtMirror.setSize(460, 100);
                   // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
                   // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
                   // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
                   txtMirror.on("blur", function(instance){
                       //console.log("onBlur");
                       instance.save();
                       //$('#'+newTextAreaId).parent('form').children('.CodeMirror')[0].CodeMirror.save();
                   });
//                   CodeMirror.on(txtMirror, 'blur', function(){
//                       console.log(this);
//                       txtMirror.save();
//                   })
                   
                   $("#attrDefaultTxtValue_"+attrId+" .txtAttrBtn").each(function(idx){
                       $(this).data("txtMirror", txtMirror);
                   });
                    
                }
				
				
				
				
				
				// update the instanceCharUsage info
		    	 var moduleUsage = $(".instanceCharUsageWith100Multipled_attrGroupDupli");
		    	 if(moduleUsage.length>0){
		    		 $(".moduleInstanceDataStruct .instanceUsedSpace").html(moduleUsage.val()+"/100");
		    		 $(".entityUsedSpace").html(moduleUsage.val()+"/100");
		    	 }
				
				
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
        
    },
    
    duplicateModuleAttr: function(e, c){
    	var currentObj = $(e.target);
    	//{'groupid':'8f75ee6e-c34e-4448-8f9e-c8971c8ea55f', 'attrid':'ad1db725-d9e1-4dee-8cfb-77eaaf11fd13'}
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var instanceId = $("#instanceId").val();
        
		$.ajax({
			type: "GET",
			url: "/duplicateInstanceAttr",
			cache: false,
		    data: "instanceUuid="+instanceId+"&groupUuid="+jsonObj.groupid+"&attrUuid="+jsonObj.attrid,
			dataType:"html",
			success: function(data, textStatus){
				
				// remove all instanceCharUsage info
				$(".instanceCharUsageWith100Multipled_attrDupli").remove();
				
				
                // *** add uuid to each textarea for codeMirror
                var madataDom = $(data);
                // for all text attribute edit
                var newTextAreaIds = new Array();
                madataDom.find(".attrDefaultTxtValueEditor").each(function(idx){
                    var newTextAreaId = generateUUID()
                    newTextAreaIds.push(newTextAreaId);
                    
                    $(this).attr('id', newTextAreaId);
                    
                });
				
//                dock.prepend(madataDom);
                $("#"+jsonObj.attrid).after(madataDom);
				
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
                   txtMirror.on("blur", function(instance){
                       //console.log("onBlur");
                       instance.save();
                   });
                   
                   $("#attrDefaultTxtValue_"+attrId+" .txtAttrBtn").each(function(idx){
                       $(this).data("txtMirror", txtMirror);
                   });
                    
                }
				
				// update the instanceCharUsage info
		    	 var moduleUsage = $("#"+jsonObj.groupid).find(".instanceCharUsageWith100Multipled_attrDupli");
		    	 if(moduleUsage.length>0){
		    		 $(".moduleInstanceDataStruct .instanceUsedSpace").html(moduleUsage.val()+"/100");
		    		 $(".entityUsedSpace").html(moduleUsage.val()+"/100");
		    	 }
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    newProductInstance: function(e, c){
    	
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var instanceId = $("#instanceId").val();
		
//		console.log("moduledetailUuid="+jsonObj.moduleDetailUuid);
//		console.log("entityDetailUuid="+instanceId);
		
		// ajax call to create a new instance
		var toNewIns = false;
		if(jsonObj.warning=="yes"){
			var r=confirm(jsonObj.warningInfo);
			if(r==true){
				toNewIns = true;
			}
		}else{
			toNewIns = true;
		}
		
		if(toNewIns==true){
			
			$.ajax({
				type: "GET",
				url: "/newProductInstance",
				cache: false,
			    data: "moduledetailUuid="+jsonObj.moduleDetailUuid+"&entityDetailUuid="+instanceId,
				dataType:"html",
				success: function(data, textStatus){
					$("#"+instanceId+" > a").click();
					
//					$("#productDetailSection .productAttrsContainer").html(data);
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
				}
			});
			
			
			$.colorbox.close();
		}
    },
    
    closeFuncBtnsForAttr: function(e, c){
    	//console.log("closeFuncBtnsForAttr");
    	var currentObj = $(e.target);
    	
		currentObj.parent(".functionBtnsForAttr").animate({
				"width": "12px"
			}, 1000, function() {
				currentObj.removeClass("domReady_closeFuncBtnsForAttr").addClass("domReady_openFuncBtnsForAttr");
				currentObj.css("background", "url('/img/pagebuilder/two_arrow_left.png') no-repeat scroll left center #4A4A4A");
				$(currentObj).next().children(".funcBtnsArea").hide();
			}
		);
    },
    
    openFuncBtnsForAttr: function(e, c){
    	var currentObj = $(e.target);
		$(currentObj).next().children(".funcBtnsArea").show();
    	
		currentObj.parent(".functionBtnsForAttr").animate({
			"width": "60px"
		}, 1000, function() {
			currentObj.removeClass("domReady_openFuncBtnsForAttr").addClass("domReady_closeFuncBtnsForAttr");
			currentObj.css("background", "url('/img/pagebuilder/two_arrow_right.png') no-repeat scroll left center #4A4A4A");
		});
    	
    },
    
    clickForPsDetail: function(e, c){
    	var currentObj = $(e.target);
        var jsonObj = eval("("+utils.domValueReader(currentObj,2)+")");
        
//        var content = "";
//        if(jsonObj.contentid){
//            content = $("#"+jsonObj.contentid).html();
//        }else if(jsonObj.content){
//        	content = jsonObj.content;
//        }
    	
    	detailContainer = new psExpandDetail.detailContainer(currentObj, {
    		leftOffset: jsonObj.leftOffset,
    		topOffset: jsonObj.topOffset,
    		height: jsonObj.height,
    		width: jsonObj.width,
    		tabSelected: jsonObj.tabSelected,
    		totalTabs: jsonObj.totalTabs,
//    		content: content,
    		title1: jsonObj.title1,
    		title2: jsonObj.title2,
    		title3: jsonObj.title3,
    		title4: jsonObj.title4,
    		containerBeforeOpen: function(){
    		},
    		containerAfterOpen: function(){
    			
    			// set content for tab 1
    			if(jsonObj.content1){
    				detailContainer.setContent(1, jsonObj.content1);
    			}else if(jsonObj.contentFromAjax1){
					$.get(jsonObj.contentFromAjax1, function(data) {
						detailContainer.setContent(1, data);
						
						
						// some special bindings: very customized binding!!!
		    			// for codeMirror binding
		    			if($(".psExpDetail .codeMirrorTextArea").length>0){

		    				var cssEditor = null;
		    				var htmlHeadEditor = null;
		    				// for html css
		    				if($("#codeMirrorCss").length>0){
		    					
							    cssEditor = CodeMirror.fromTextArea(document.getElementById("codeMirrorCss"), {
							        lineNumbers: true,
								    readOnly: true,
							    	mode: "css"
							    });

							    // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
							    // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
							    // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
							    cssEditor.on("blur", function(){
									//console.log("onBlur");
									cssEditor.save();
								});
		    					
		    				}
		    				
		    				
		    				// for html head
		    				if($("#codeMirrorHead").length>0){
		    					
							    htmlHeadEditor = CodeMirror.fromTextArea(document.getElementById("codeMirrorHead"), {
							        lineNumbers: true,
								    readOnly: true,
							    	mode: "text/html"
							    });

							    // Call .save() on the CodeMirror instance before triggering your jQuery ajax magic. It is not constantly updating the textarea as you type, for performance reasons. It'll notice submit events on the form, and save, but no such events are triggered when you do an ajax submit.
							    // Bind it using .on() as described in http://codemirror.net/doc/manual.html#events
							    // events: change", beforeChange, cursorActivity, beforeSelectionChange, viewportChange, gutterClick, focus, blur, scroll, update, renderLine, delete, clear, hide, unhide, redraw,  ... 
							    htmlHeadEditor.on("blur", function(){
									//console.log("onBlur");
							    	htmlHeadEditor.save();
								});
		    					
		    				}
		    				
		    				
		    				// for edit and cancel edit btn
							var headOriginalValue = "";
							var cssOriginalValue = "";

		    				$(".editTextAreaValue").click(function(){
		    					
								//console.log("editModuleJspCssValue")
						    	var domvalue = $(this).attr("domvalue");
								if($("#codeMirrorHead").length>0){
									headOriginalValue = htmlHeadEditor.getValue();
									htmlHeadEditor.setOption("readOnly", false);
								}else if($("#codeMirrorCss").length>0){
									cssOriginalValue = cssEditor.getValue();
									cssEditor.setOption("readOnly", false);
								}
						    	$("#"+domvalue).find(".valueSection").addClass("displaynone");
						    	$("#"+domvalue).find(".editSection").removeClass("displaynone");
		    					
		    					
		    				})
		    				
		    				$(".cancelEditTextAreaValue").click(function(){
		    					
						    	var domvalue = $(this).attr("domvalue");
								if($("#codeMirrorHead").length>0){
									htmlHeadEditor.setValue(headOriginalValue);
									htmlHeadEditor.setOption("readOnly", true);
								}else if($("#codeMirrorCss").length>0){
									cssEditor.setValue(cssOriginalValue);
									cssEditor.setOption("readOnly", true);
								}
						    	
						    	$("#"+domvalue).find(".valueSection").removeClass("displaynone");
						    	$("#"+domvalue).find(".editSection").addClass("displaynone");
		    					
		    					
		    				})
		    				
		    				if($(".savePageCssHead").length>0){
		    					$(".savePageCssHead").click(function(){
		    				    	var domvalue = $(this).attr("domvalue");
		    				        var jsonObj = eval("("+domvalue+")");

		    				        var theForm = $("form[id='"+jsonObj.formId+"']");
		    				        var serializedData = theForm.serialize();
		    				        
		    						$.ajax({
		    							type: "POST",
		    							url: "/updatePageDetailValue",
		    							cache: false,
		    						    data: serializedData,
		    							dataType:"json",
		    							success: function(data, textStatus){
		    								if(data.success){
		    									if($("#codeMirrorHead").length>0){
		    										headOriginalValue = data.response1;
		    										$("#"+jsonObj.webSectionId).find("img.cancelEditTextAreaValue").click();
		    									}else if($("#codeMirrorCss").length>0){
		    										cssOriginalValue = data.response1;
		    										$("#"+jsonObj.webSectionId).find("img.cancelEditTextAreaValue").click();
		    									}
		    									
		    								}else{
		    									alert(data.response1);
		    								}
		    							},
		    							error: function(XMLHttpRequest, textStatus, errorThrown){
		    								alert("failed");
		    							}
		    						});
		    				    	
		    				    	
		    					})
		    				}
		    				
		    			}
						
					});
    			}
    			
    			// set content for tab 2
    			if(jsonObj.content2){
    				detailContainer.setContent(2, jsonObj.content2);
    			}else if(jsonObj.contentFromAjax2){
					$.get(jsonObj.contentFromAjax2, function(data) {
						detailContainer.setContent(2, data);
						
					});
    			}
    			
    			// set content for tab 3
    			if(jsonObj.content3){
    				detailContainer.setContent(3, jsonObj.content1);
    			}else if(jsonObj.contentFromAjax3){
					$.get(jsonObj.contentFromAjax3, function(data) {
						detailContainer.setContent(3, data);
					});
    			}
    			
    			// set content for tab 4
    			if(jsonObj.content4){
    				detailContainer.setContent(4, jsonObj.content1);
    			}else if(jsonObj.contentFromAjax4){
					$.get(jsonObj.contentFromAjax4, function(data) {
						detailContainer.setContent(4, data);
					});
    			}
    			
    		},
    		
    		containerClose: function(){
    			//console.log("removing *****");
    		}
    	});
    	
    	detailContainer.drawContainer();
    },
    
    savePageValue: function(e, c){
    	
    	//console.log("save module value");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        
        var serializedData = "";
        if(jsonObj.formId){
        	var theForm = $("form[id='"+jsonObj.formId+"']");
        	serializedData = theForm.serialize();
        }else if(jsonObj.valueId){
    		var pageId = jsonObj.pageuuid;
    		var updateValue = $("#"+jsonObj.valueId).val();
        	serializedData = {"pageId" : pageId, "updateValue" : updateValue, "valueName" : jsonObj.valueName}
        }
		
		$.ajax({
			type: "POST",
			url: "/updatePageDetailValue",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
					// for update url : to change link for "click here to open the page"
					if(jsonObj.valueId && jsonObj.valueName && jsonObj.valueName=="url"){
						if(data.response2!=null){
							$(".closePsDetailIconImg").click();
							$("div.pageUrl").click();
							//$(".clickHereToOpenPage").attr("href", data.response2)
							
						}
					}
					
					
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    	
    },
    updateAccountProfile: function(e, c){
    	//console.log("saveAccountValue");
    	var currentObj = $(e.target);
    	var domValue = currentObj.attr("domvalue");
    	var theForm = $("form[id='"+domValue+"_form']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/updateAccountProfile",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+domValue).find("span.value").html(data.response1);
					//$("#"+domValue).find("div.value").html(data.response1);
					$("#"+domValue).find("img.domReady_cancelToEditValue").click();
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    },
    updateAccountPwd: function(e, c){
    	var currentObj = $(e.target);
    	var domValue = currentObj.attr("domvalue");
    	var theForm = $("form[id='"+domValue+"_form']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/updateAccountPwd",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+domValue).find("span.value").html("******");
					//$("#"+domValue).find("div.value").html(data.response1);
					$("#"+domValue).find("img.domReady_cancelToEditValue").click();
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    updateAccountContact: function(e, c){
    	var currentObj = $(e.target);
    	var domValue = currentObj.attr("domvalue");
    	var theForm = $("form[id='"+domValue+"_form']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/updateAccountContact",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+domValue).find("span.value").html(data.response1);
					//$("#"+domValue).find("div.value").html(data.response1);
					$("#"+domValue).find("img.domReady_cancelToEditValue").click();
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    updateOrgInfo: function(e, c){
    	var currentObj = $(e.target);
    	var domValue = currentObj.attr("domvalue");
    	var theForm = $("form[id='"+domValue+"_form']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/updateOrgInfo",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
				    
				    
					//$("#"+domValue).find("span.value").html(data.response1);
					//$("#"+domValue).find("div.value").html(data.response1);
					
				    if(data.response2==="orgname"){
				        $("#orgname_value").html(data.response1.orgname);
				    }else if(data.response2==="address"){
                        $("#apt_unit_number_value").html(data.response1.apt_unit_number);
                        $("#street_number_value").html(data.response1.street_number+" "+data.response1.address);
                        $("#city_value").html(data.response1.city);
                        $("#state_value").html(data.response1.state);
                        $("#country_value").html(data.response1.country);
                        $("#zip_value").html(data.response1.zip);
				    }else if(data.response2==="phone"){
                        $("#dayphone_country_value").html(data.response1.dayphone_country);
                        $("#dayphone_area_value").html(data.response1.dayphone_area);
                        $("#dayphone_value").html(data.response1.dayphone);
				    }else if(data.response2==="desc"){
				        $("#description_value").val(data.response1.description)
				    }
					
					$("#"+domValue).find("img.domReady_cancelToEditValue").click();
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    permissionSet: function(e, c){
    	//console.log("permission set");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.parent("span.permissionSets").attr("domvalue");
    	var permissionValue = "";
    	var permissionType = currentObj.hasClass("permissionBtn_p")?"permissionBtn_p":currentObj.hasClass("permissionBtn_r")?"permissionBtn_r":currentObj.hasClass("permissionBtn_c")?"permissionBtn_c":currentObj.hasClass("permissionBtn_m")?"permissionBtn_m":"";
    	
    	var parentTreeNode = currentObj.closest("ul")?currentObj.closest("ul").closest("li"):null;
    	var parentPermissionValue = null
    	if(parentTreeNode!=null && parentTreeNode.length>0){
    		//console.log("parent: "+parentTreeNode.attr("id"));
        	var parentPermissionSetContainer = $("#"+parentTreeNode.attr("id")+" > a > span.permissionSets");
        	var parentNodePermissionBtn = parentPermissionSetContainer.children("button."+permissionType);
        	parentPermissionValue =  parentNodePermissionBtn.hasClass("checkboxEmpty")?"checkboxEmpty":parentNodePermissionBtn.hasClass("checkboxYes")?"checkboxYes":parentNodePermissionBtn.hasClass("checkboxNo")?"checkboxNo":parentNodePermissionBtn.hasClass("checkboxYesFollow")?"checkboxYesFollow":parentNodePermissionBtn.hasClass("checkboxNoFollow")?"checkboxNoFollow":null;
    	}
    	
    	// situation 1: empty - yes - no - empty
    	// situation 2: checkboxYesFollow - no - checkboxYesFollow
    	// situation 3: checkboxNoFollow - yes - checkboxNoFollow
    	if(currentObj.hasClass("checkboxEmpty")){
    		currentObj.removeClass("checkboxEmpty").addClass("checkboxYes");
    		permissionValue = "checkboxYes";
    	}else if(currentObj.hasClass("checkboxYes")){
    		
    		if(parentPermissionValue!=null){
    			if(parentPermissionValue=="checkboxEmpty"){
            		currentObj.removeClass("checkboxYes").addClass("checkboxNo");
            		permissionValue = "checkboxNo";
    			}else if(parentPermissionValue=="checkboxYes" || parentPermissionValue=="checkboxYesFollow"){
            		currentObj.removeClass("checkboxYes").addClass("checkboxYesFollow");
            		permissionValue = "checkboxYesFollow";
    			}else if(parentPermissionValue=="checkboxNo" || parentPermissionValue=="checkboxNoFollow"){
            		currentObj.removeClass("checkboxYes").addClass("checkboxNoFollow");
            		permissionValue = "checkboxNoFollow";
    			}
    			
    		}else{ // currentNode is the toppest node
        		currentObj.removeClass("checkboxYes").addClass("checkboxNo");
        		permissionValue = "checkboxNo";
    		}
    		
    		
    	}else if(currentObj.hasClass("checkboxNo")){
    		
    		if(parentPermissionValue!=null){
    			if(parentPermissionValue=="checkboxEmpty"){
            		currentObj.removeClass("checkboxNo").addClass("checkboxEmpty");
            		permissionValue = "checkboxEmpty";
    			}else if(parentPermissionValue=="checkboxYes" || parentPermissionValue=="checkboxYesFollow"){
            		currentObj.removeClass("checkboxNo").addClass("checkboxYesFollow");
            		permissionValue = "checkboxYesFollow";
    			}else if(parentPermissionValue=="checkboxNo" || parentPermissionValue=="checkboxNoFollow"){
            		currentObj.removeClass("checkboxNo").addClass("checkboxNoFollow");
            		permissionValue = "checkboxNoFollow";
    			}
    			
    		}else{
        		currentObj.removeClass("checkboxNo").addClass("checkboxEmpty");
        		permissionValue = "checkboxEmpty";
    		}
    		
    	}else if(currentObj.hasClass("checkboxYesFollow")){
    		currentObj.removeClass("checkboxYesFollow").addClass("checkboxNo");
    		permissionValue = "checkboxNo";
    	}else if(currentObj.hasClass("checkboxNoFollow")){
    		currentObj.removeClass("checkboxNoFollow").addClass("checkboxYes");
    		permissionValue = "checkboxYes";
    	}
    	
    	// AJAX CALL TO UPDATE THE DB
		$.ajax({
			type: "GET",
			url: "/permissionStuffUpdate",
			cache: false,
			data: "permissionUuid="+$("#selectedPermissionUuid").val()+"&permissionType="+permissionType+"&permissionValue="+permissionValue+"&stuffUuid="+domvalue,
			dataType:"json",
			success: function(aData, textStatus){
				
				if(aData.success){
					
					// get extra info for copy permission for entity & page
					if(permissionType=="permissionBtn_c" ){
						$.ajax({
							type: "GET",
							url: "/extraInfoForPermissionStuffUpdate",
							cache: false,
							data: "permissionUuid="+$("#selectedPermissionUuid").val()+"&permissionType="+permissionType+"&permissionValue="+permissionValue+"&stuffUuid="+domvalue,
							dataType:"json",
							success: function(extraData, textStatus){
								if(extraData.success && extraData.response1){
									$(extraData.response1).each(function(i){
										if(this.selected){
											$("#"+this.key+" > a").find("span.detailInfoPopup").remove();
										}else{
											$("#"+this.key+" > a").find("span.detailInfoPopup").remove();
											$("#"+this.key+" > a").append(this.value);
										}
									})
								}
							},
							error: function(XMLHttpRequest, textStatus, errorThrown){
								alert("system error, you may need to refresh the page and try again!");
							}
						});
						
					}
				}
				
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("system error, you may need to refresh the page and try again!");
			}
		});
		
    	
    	
    	
    	// find direct childrens to setup the permission icon
    	$("#"+domvalue+" > ul").children("li").each(function(i){
    		//console.log($(this).attr("id")+", ");
    		recursivePermissionDetect(this, permissionType, permissionValue);
    	});
    	
    },
    
    savePermissionValue: function(e, c){
    	//console.log("savePermissionValue");
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
		var permissionUuid = $("#selectedPermissionUuid").val();
		//$('input[value="Hot Fuzz"]')
		var updateValue = $("#"+jsonObj.valueId).val();
		
		var postValue = {"permissionUuid" : permissionUuid, "updateValue" : updateValue, "valueName" : jsonObj.valueName};
		$.ajax({
			type: "POST",
			url: "/updatePermissionValue",
			cache: false,
		    data: postValue,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#"+jsonObj.webSectionId).find("span.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("div.value").html(data.response1);
					$("#"+jsonObj.webSectionId).find("img.domReady_cancelToEditValue").click();
					
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    editPageSchedule: function(e, c){
    	var currentObj = $(e.target);
    	var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        
    	var entityId = jsonObj.entityId;
    	
    	//console.log("editPageSchedule...");
    	$.colorbox({
    		width : 800,
    		height : 600,
            escKey: false,
    		//iframe : true,
    		href: "/getPageScheduleForEntitySetup?entityid="+entityId+"&pagetype="+jsonObj.pagetype,
    		//title: "asdfasdf",
    		opacity: 0.4,
    		overlayClose: false,
    		
    		onComplete:function(){
    			
					$(function() {
						$( "#pageScheduleForEntityTabs" ).tabs();
					});

				$("input:radio.defaultPageForEntity").change(function () {
					var radio_pageid=$(this).val();
					var radio_domvalue = $(this).attr("domvalue");
					var radio_jsonObj = eval("("+radio_domvalue+")");
					//alert("Radio button selection changed. Selected: "+selection);
//					console.log("Radio button selection changed. Selected: "+selection);
					
					
					$.ajax({
						type: "GET",
						url: "/updateEntityDefaultPage",
						cache: false,
					    data: "entityId="+radio_jsonObj.entityId+"&pagetype="+radio_jsonObj.pagetype+"&pageid="+radio_pageid+"&sitetype="+radio_jsonObj.sitetype,
						dataType:"json",
						success: function(data, textStatus){
							
						},
						error: function(XMLHttpRequest, textStatus, errorThrown){
							alert("failed");
						}
					});
					
				});
    			
    		}
    	})
    },
    
    domainValifyForm: function(e, c){
    	var currentObj = $(e.target);
    	var domValue = currentObj.attr("domvalue");
    	var theForm = $("form[id='"+domValue+"']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/domainValidateForOrg",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					currentObj.parents(".domainSubmitStatus").html("[passed]").css("color", "green");
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed");
			}
		});
    	
    },
    
    bugReportSubmit: function(e, c){
    	
    	var theForm = $("form[id='bugReportForm']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/bugReportSubmit",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
			    	$.colorbox({html:"<span style='font-size: 20px; font-weight: bold;'>Thank you for your report, we will try to fix this bug as soon as possible.</span>"});    	
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed to report form, you may need to refresh the page and try again!");
			}
		});
    	
    	
    },
    
    appliSubmit: function(e, c){
    	var theForm = $("form[id='applicationForm']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/applicationSubmit",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
			    	$.colorbox({html:"<span style='font-size: 20px; font-weight: bold;'>Thank you for your interest, your application is in processing, and you will receive an email within a day.</span>"});    	
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed to report form, you may need to refresh the page and try again!");
			}
		});
    	
    },
    
    contactInfoSubmit: function(e, c){
    	
    	var theForm = $("form[id='contactInfoForm']");
    	var serializedData = theForm.serialize();
    	
		$.ajax({
			type: "POST",
			url: "/contactInfoSubmit",
			cache: false,
		    data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
			    	$.colorbox({html:"<span style='font-size: 20px; font-weight: bold;'>Thank you for your interest, we will contact you as soon as possible.</span>"});    	
				}else{
					alert(data.response1);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("failed to report form, you may need to refresh the page and try again!");
			}
		});
    },
    
    colorboxMaxSize: function(e, c){
    	$.colorbox.resize({width:'100%', height:'100%'});
    	$("#colorbox #cboxTitle").html('<button class="domReady_colorboxOriginalSize">Original size</button>');
    	
    },
    
    colorboxOriginalSize: function(e, c){
    	$.colorbox.resize({width:'600', height:'500'});
    	$("#colorbox #cboxTitle").html('<button class="domReady_colorboxMaxSize">Max size</button>');
    },
    
    productEditAreaMax: function(e, c){
        
        // add special class to domReady_editModuleImg, let this listener knows that it opens media select is from colorbox
        $(".domReady_editModuleImg").addClass("insideColorbox");
    	
    	
    	var productEditArea = $("#productDetailSection .productAttrsContainer");
    	$.colorbox(
    		{
    			inline:true, 
    			href:productEditArea, 
    			width: '100%', 
    			height: '100%',
                escKey: false,
    			onOpen: function(){
    				$("span.productEditAreaMax").hide();
    			},
    			onCleanup: function() {
                    $(".domReady_cancelEditModuleImg").click();
    			},
    			onClosed: function(){
    				$("span.productEditAreaMax").show();
    				$(".domReady_editModuleImg").removeClass("insideColorbox");
    				//$(".medialistForModuleConfig").addClass("displaynone");
    			}
    		});
    	
    },
    
    newInstanceAttr: function(e, c){
        var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
        var jsonObj = eval("("+domvalue+")");
        
        $.ajax({
            type: "POST",
            url: "/instanceNewAttr",
            cache: false,
            data: "moduleUuid="+jsonObj.moduleUuid+"&moduleGroupUuid="+jsonObj.moduleGroupUuid+"&moduleAttrUuid="+jsonObj.moduleAttrUuid+"&instanceUuid="+jsonObj.instanceUuid,
            dataType:"json",
            success: function(data, textStatus){
                if(data.success){
                    $("#"+jsonObj.moduleAttrUuid+" .attrValidationStatus").html("<div>New attribute is created, you can see the update after you close and open this popup again.</div>")
                }else{
                    alert(data.response1);
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                alert("failed to create new attribute for instance, you may need to refresh the page and try again!");
            }
        });

        e.preventDefault();

    },
    
    jspPreview: function(e, c){
        
        var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
        
        var theForm = $("form[id='"+domvalue+"']");
        var serializedData = theForm.serialize();
        
        var identifier = 4;
        // create window immediately so it won't be blocked by popup blocker
//        var w = window.open("http://localhost:8080/jspPreview");
        var w = window.open();
        //get the history
        $.post("jspPreview", serializedData,
        //ajax query 
        function(data) {
            
            console.log("data: "+data);
            //response is here
            $(w.document.body).html(data);

        });//end ajax                
        
    },
    
    instancePreview: function(e, c){
    	
        var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
    	
        $.colorbox({
            width : 800,
            height : 600,
            href: "/jspPreview?objuuid="+domvalue+"&type=instance",
            escKey: false,
            opacity: 0.4,
            overlayClose: false
        });
    	
    },
    
    instanceAnnoncement: function(e, c){
        var currentObj = $(e.target);
        var domvalue = currentObj.attr("domvalue");
    	
        $.ajax({
            type: "GET",
            url: "/productInfoAnnouncement",
            cache: false,
            data: "objuuid="+domvalue,
            dataType:"json",
            success: function(data, textStatus){
            	if(data.success){
            		alert("Your announcement is sending, thank you!");
            	}else{
            		alert("System issue, you may need to refresh the page and try again!");
            	}
            	
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                alert("failed to annouce the product information, you may need to refresh the page and try again!");
            }
        });

    }
    
    
}

$(function(){

    var obj = $("body");

    // Possible event values: blur, focus, load, resize, scroll, unload, beforeunload, click,
    //      dblclick,  mousedown, mouseup, mousemove, mouseover, mouseout, mouseenter, mouseleave,
    //      change, select,  submit, keydown, keypress, keyup, error, ready
    // Note: globalModel is namespace to ground events together, which will support bind/unbind a group of event.
    //      check jquery: namespaced event (http://docs.jquery.com/Namespaced_Events)
    obj.bind('click.globalModel keypress.globalModel', domReadyListener.handler);

    //*************************************************************************
    //  All other events binds are list below
    //*************************************************************************
    
    // find subCategories based on industry
    $(".industrySelectionForSubCats").bind("change", domReadyEventFunctionsCollections.industrySelectionForSubCats);
    
    // find all input with type=text, and bind with blur to trim the value
    $("input[type='text']").bind("blur", function(){
    	$(this).val($.trim($(this).val()));
    });
    
    // ******* for tooltip : version - detail info popup 
    // How to: any html element with css class name : "detailInfoPopup" will have a tooltip when mouse over. Or,
    //         any html element with css class name : "detailInfoPopupClick" will have a tooltip when mouse click.
    var tooltipJsonObj = "";
    detailInfoPopupSkeleton = '<div id="detailInfoPopupContainer" style="z-index:99999;">'
	      + '<table width="" border="0" cellspacing="0" cellpadding="0" align="center" class="detailInfoPopupTable">'
	      + '<tr>'
	      + '   <td class="corner topLeft"></td>'
	      + '   <td class="top"></td>'
	      + '   <td class="corner topRight"></td>'
	      + '</tr>'
	      + '<tr>'
	      + '   <td class="left">&nbsp;</td>'
	      + '   <td style="background-color:white;"><div class="detailInfoPopupContainerCloseBtn" style="position: absolute;top:0;right:0;display:none;"><img class="detailInfoPopupContainerCloseBtnImg" src="/img/vendor/web-icons/cross-circle.png"></div><div id="detailInfoContent"></div></td>'
	      + '   <td class="right">&nbsp;</td>'
	      + '</tr>'
	      + '<tr>'
	      + '   <td class="corner bottomLeft">&nbsp;</td>'
	      + '   <td class="bottom">&nbsp;</td>'
	      + '   <td class="corner bottomRight"></td>'
	      + '</tr>'
	      + '</table>'
	      + '</div>';
    
    tooltipPopupFunc = function(thisObj, detailInfoPopupContainerObj){
        
        $('#detailInfoContent').html('&nbsp;');
        $('#detailInfoContent').removeAttr('style');
		var topOffset = new Number(0);
		var leftOffset = new Number(0);
        if($(thisObj).attr("domvalue")){
        	tooltipJsonObj = eval("("+utils.domValueReader($(thisObj),2)+")");
	        if(!!tooltipJsonObj.ajaxCall){ // ************** ajax call to get content
	        	
		        $.ajax({
		            type: "GET",
		            url: tooltipJsonObj.ajaxCall.url,
		            cache: false,
		            data: tooltipJsonObj.ajaxCall.params,
		            dataType:"html",
		            success: function(data, textStatus){
		            	$('#detailInfoContent').html(data); 
		            	
		            	// disable all input for topicInfo
		            	//$('#detailInfoContent .topicInfoFragment').find("input").attr('disabled','disabled');
		            },
		            error: function(XMLHttpRequest, textStatus, errorThrown){
		                alert("failed");

		            }
		        });
	        }else if(!!tooltipJsonObj.popupContent){ // ************** pass content to tooltip
	        	$("#detailInfoContent").html(tooltipJsonObj.popupContent);
	        }else if(!!tooltipJsonObj.popupContentContainerId){ // ************** pass id to tooltip, the content will be everything inside id
	        	$("#detailInfoContent").html($("#"+tooltipJsonObj.popupContentContainerId).html());
	        	
	        	// for some special actions
	        	// check or uncheck the default color checkbox based on ...
	        	var usingDefaultHexColor = $(thisObj).attr("usingDefaultHexColor");
	        	if((typeof usingDefaultHexColor!="undefined" && usingDefaultHexColor!=null) && usingDefaultHexColor.length>0 && usingDefaultHexColor=="true"){
	        		$("#detailInfoContent input.defaultMetaColor").attr("checked", true);
	        	}
	        	
	        }else if(!!tooltipJsonObj.popupContentContainerAppendId){ // ************** pass id to tooltip, the content will be everything inside id by using append
	            // use append to move content into 'detailInfoContent'
	        	$("#detailInfoContent").append($("#"+tooltipJsonObj.popupContentContainerAppendId).children());
	        }
	        
	        if(tooltipJsonObj.topOffset){ // ************** tooltip top offset
	        	topOffset = new Number(tooltipJsonObj.topOffset);
	        }
	        if(tooltipJsonObj.leftOffset){ // ************** tooltip left offset
	        	leftOffset = new Number(tooltipJsonObj.leftOffset);
	        }
	        
            if(tooltipJsonObj.width){ // ************** tooltip width
                $("#detailInfoContent").css("width", tooltipJsonObj.width);
            }
            if(tooltipJsonObj.height){ // ************** toltip height
                $("#detailInfoContent").css({
                    height: tooltipJsonObj.height,
                    overflow: 'auto'
                });
            }
            
            // ************** replace info
            if(!!tooltipJsonObj.replacement){
                $(tooltipJsonObj.replacement).each(function(i){
                    if(!this.objType || this.objType==='input'){
                        $("#"+this.objId).val(this.objVal);
                    }else{
                        $("#+this.objId").html(this.objVal);
                    }
                })
            }
	        
        	// ****** some special action binding for different purpose
            // mediaSelectForm
            if($("#mediaSelectForm").length>0){
                $("img.domReady_selectMediaForModule").removeClass("selected").attr("src", "/img/vendor/web-icons/tick-box.png")
                if(!!($("#mediaSelectForm_defaultValue").val())){
                    $("img[domvalue='"+$("#mediaSelectForm_defaultValue").val()+"']").addClass("selected").attr("src", "/img/vendor/web-icons/tick-button.png");
                }
            }
            
        	// jsColor binding
        	if($("#detailInfoContent>input.jsColor").length>0){
        		var currentColor = "";
        		if(tooltipJsonObj.currentColor){
        			currentColor = tooltipJsonObj.currentColor;
        		}else{
        			currentColor = $("#"+tooltipJsonObj.currentColorContainerId).attr("colorvalue");
        		}
        		
        		var myPicker = new jscolor.color(document.getElementById("detailInfoContent").getElementsByTagName("input")[0], 
        				{
        					pickerZIndex:"100000",
        					colorMainPosition:"relative",
        					onImmediateChange:function(){
        						$("#"+tooltipJsonObj.coStyleElementId).css("background-color", "#"+this.toString());
        					},
        					boxMouseUp: function(){
        						$("#detailInfoContent input.defaultMetaColor").attr("checked",false);
        						$(thisObj).attr("usingDefaultHexColor", "false");
        					}
        				})

//        		if(currentColor && ...){
//	        		myPicker.fromString(currentColor);
//        		}
        		
	        	var usingDefaultHexColor = $(thisObj).attr("usingDefaultHexColor");
	        	if((typeof usingDefaultHexColor!="undefined" && usingDefaultHexColor!=null) && usingDefaultHexColor.length>0 && usingDefaultHexColor=="true"){
	        		myPicker.fromString(tooltipJsonObj.sysDefaultHexColor);
	        	}else if(currentColor){
	        		myPicker.fromString(currentColor);
	        	}
        		
        		$("#detailInfoContent input.defaultMetaColor").click(function(){
        			
        			if($(this).is(':checked')){
//        				myPicker.disableJsColor(true);
        				
            			// ajax call to remove the customized color
        		        $.ajax({
        		            type: "GET",
        		            url: "/removeModuleMetaValue",
        		            cache: false,
        		            data: "targetId="+tooltipJsonObj.coStyleElementId+"&type=hexcolor",
        		            dataType:"json",
        		            success: function(rdata, textStatus){
        		            	$(thisObj).attr("usingDefaultHexColor", "true");
        		            	myPicker.fromString(tooltipJsonObj.sysDefaultHexColor);
        		            	$("#"+tooltipJsonObj.coStyleElementId).css("background-color", "#"+tooltipJsonObj.sysDefaultHexColor);
        		            },
        		            error: function(XMLHttpRequest, textStatus, errorThrown){
        		                alert("failed");
        		            }
        		        });
        				
        				
        			}else{
//        				myPicker.disableJsColor(false);
		            	$(thisObj).attr("usingDefaultHexColor", "false");
        			}
        			
        		})
        	}
	        
        }
        
        // set position
    	var pos = $(thisObj).offset();
        var width = $(thisObj).width();
        detailInfoPopupContainerObj.css({
            left: (pos.left + width + leftOffset) + 'px',
            top: (pos.top - 5 + topOffset) + 'px'
        });

        detailInfoPopupContainerObj.css('display', 'block');
    }
    
    tooltipCloseFunc = function(detailInfoPopupContainerObj){
        
        if(!!detailInfoPopupContainerObj){
            // return content back to original place if tooltipJsonObj.popupContentContainerId
            if(!!tooltipJsonObj && !!tooltipJsonObj.popupContentContainerAppendId){
                $("#"+tooltipJsonObj.popupContentContainerAppendId).append($("#detailInfoContent").children());
            }
            
            // do something before close:
            if((typeof tooltipJsonObj!="undefined" && tooltipJsonObj!=null) && tooltipJsonObj.coloringAjax){
                
                // check if defult color
                if($("#detailInfoContent input.defaultMetaColor") && !$("#detailInfoContent input.defaultMetaColor").is(':checked')){
                    var currentJsColor = detailInfoPopupContainerObj.find("input.jsColor").val();

                    var originalJsColor = "";
                    if(tooltipJsonObj.currentColor){
                        originalJsColor = tooltipJsonObj.currentColor;
                    }else{
                        originalJsColor = $("#"+tooltipJsonObj.currentColorContainerId).attr("colorvalue");
                    }
                    
                    
                    $.ajax({
                        type: "GET",
                        url: "/"+tooltipJsonObj.coloringAjax,
                        cache: false,
                        data: "targetId="+tooltipJsonObj.coStyleElementId+"&updateValue="+currentJsColor+"&valueName=hexcolor",
                        dataType:"json",
                        success: function(cdata, textStatus){
                            if(cdata.success){
                                $("#"+tooltipJsonObj.coStyleElementId).attr("colorvalue", cdata.response1);
                                $("#"+cdata.response2).children("a").removeClass().addClass("hex_"+currentJsColor);
                                //$("#"+cdata.response2).children("a").children("ins.jstree-icon").css({'background': 'url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+currentJsColor, 'height':'15px'});
                                var totalStyleSheets = document.styleSheets.length; 
                                document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-leaf a.hex_'+currentJsColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+currentJsColor+'; height: 15px;}', 0);
                                document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-open a.hex_'+currentJsColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+currentJsColor+'; height: 15px;}', 0);       
                                document.styleSheets[totalStyleSheets-1].insertRule('#generalPageTree .jstree-closed a.hex_'+currentJsColor+' > ins.jstree-icon {background: url("/img/vendor/web-icons/layout-select-content_transparency.png") no-repeat scroll 0 0 #'+currentJsColor+';height: 15px;}', 0);      
                                
                                tooltipJsonObj = null;
                                
                                if(cdata.response3){
                                    var str = 'This page has been changed! <span style="cursor: pointer;" class="pageChangedForProcess" domvalue="'+cdata.response3+'">Click here</span> for further processing.'
                                    $(".pageBriefInfoContainer").html(str);
                                }
                                
                                
                                
                            }else{
                                $("#"+tooltipJsonObj.coStyleElementId).css("background-color", "#"+originalJsColor);

                                tooltipJsonObj = null;
                            }
                        },
                        error: function(XMLHttpRequest, textStatus, errorThrown){
                            $("#"+tooltipJsonObj.coStyleElementId).css("background-color", "#"+originalJsColor);
                            alert("failed");
                        }
                    });
                    
                }
                
            }
            
            detailInfoPopupContainerObj.css('display', 'none');
            
        }
    	
    }
    
    tooltipHideDelay = 500;  
    tooltipHideTimer = null;
    tooltipHideTimerDisable = false;
	detailInfoPopupContainer = $("#detailInfoPopupContainer");
	
	// for mouseover and mouseout
    $(".detailInfoPopup").live("mouseover", function(){
    	if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
//    	console.log("....... mouse over detail info popup");
    	if(detailInfoPopupContainer.length<=0){
    		detailInfoPopupContainer = $(detailInfoPopupSkeleton);
    		$('body').append(detailInfoPopupContainer);
    	}
    	
    	tooltipCloseFunc(detailInfoPopupContainer);
    	tooltipPopupFunc(this, detailInfoPopupContainer);
        
    });
    
    $('.detailInfoPopup').live('mouseout', function() {
    	if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
    	if(!tooltipHideTimerDisable){
            tooltipHideTimer = setTimeout(function() {
                tooltipCloseFunc(detailInfoPopupContainer);
                //detailInfoPopupContainer.css('display', 'none');
                }, tooltipHideDelay);
    	}
    	
	});

    // for mouseClick
    $(".detailInfoPopupClick").live("click", function(){
    	if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
//    	console.log("....... mouse over detail info popup");
    	if(detailInfoPopupContainer.length<=0){
    		detailInfoPopupContainer = $(detailInfoPopupSkeleton);
    		$('body').append(detailInfoPopupContainer);
    	}
    	
        tooltipCloseFunc(detailInfoPopupContainer);
    	tooltipPopupFunc(this, detailInfoPopupContainer);
        
    });
    
    $('.detailInfoPopupClick').live('mouseout', function() {
    	if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
    	
    	if(!tooltipHideTimerDisable){
            tooltipHideTimer = setTimeout(function() {
                tooltipCloseFunc(detailInfoPopupContainer);
                //detailInfoPopupContainer.css('display', 'none');
                }, tooltipHideDelay);
    	    
    	}
	});
    
	
    
    $('#detailInfoPopupContainer').live("mouseover" ,function() {
    	if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
	});

    $('#detailInfoPopupContainer').live("mouseout", function() {
    	
		if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
		if(!tooltipHideTimerDisable){
	        tooltipHideTimer = setTimeout(function() {
	            tooltipCloseFunc(detailInfoPopupContainer);
	            //detailInfoPopupContainer.css('display', 'none');
	            }, tooltipHideDelay);
		}
		
	});
    
    $('#detailInfoContent').live("click", function(){
        if(!!tooltipJsonObj && !!tooltipJsonObj.stay){
            if (tooltipHideTimer) clearTimeout(tooltipHideTimer);
            tooltipHideTimerDisable = true;
            $(".detailInfoPopupContainerCloseBtn").css('display', 'block');
            
            // create a overlay to block other clickable funcitons
            if($("#detailInfoPopupOverlay").length<=0){
                var detailInfoPopupOverlay = "<div id='detailInfoPopupOverlay' style='display: block;opacity: 0.2;visibility: visible;height: 100%;position: fixed;width: 100%;left: 0;overflow: hidden;top: 0;z-index: 99998;background-color:gray;'></div>";
                $('body').append(detailInfoPopupOverlay);
            }
        }
        
    });
    
    $('.detailInfoPopupContainerCloseBtnImg').live("click", function(){
        if(!!tooltipJsonObj && !!tooltipJsonObj.stay){
            tooltipHideTimerDisable = false;
            $(".detailInfoPopupContainerCloseBtn").css('display', 'none');
            tooltipCloseFunc(detailInfoPopupContainer);
            
            // remove overlay
            if($("#detailInfoPopupOverlay").length>0){
                $("#detailInfoPopupOverlay").remove();
            }
        }
        
    })

    // ******* end for tooltip : version - detail info popup 
    
    
    // *** other global events:
	$(".moduleCheckboxChangeVal").live("click", function(){
		if($(this).is(':checked')){
			$(this).val("1");
		}else{
			$(this).val("0");
		}
	})
	
	// *** for footer auto hide ** //
    var footerTimeout = null;
    $('.pagebottom').mouseenter(function(){
       clearTimeout(footerTimeout);
       footerTimeout = setTimeout(function(){$('.pagebottom').animate({height: '30'}, 500); },500);
    });
    
    $('.pagebottom').mouseleave(function(){
       clearTimeout(footerTimeout);
       footerTimeout = setTimeout(function(){$('.pagebottom').animate({height: '5'}, 500); },500);
    });	
    
    // *** for bugReport ** //
    $(".bugReport").colorbox({
		width : 500,
		height : 300,
    	inline:true, 
        escKey: false,
    	width:"50%"});
    
    // *** for contact info ** //
    $(".domRd_contactInfo").colorbox({
		width : 500,
		height : 300,
    	inline:true, 
        escKey: false,
    	width:"50%"});

});

var domReadyListener = {
    handler: function(e){
        var currentElement = e.target;

//	        $("#debugArea").append(currentElement.tagName+" | "+currentElement.className+"<br/>");

        if(currentElement.className.split(" ").length>0){
            $.each(currentElement.className.split(" "), function(i, c){
                if(c.indexOf("domReady_")>-1){
                    if(c.indexOf("_keypress")>-1){// if class name include _keypress, do function only when key pressed
                        if(e.charCode || e.keyCode){
                            if(classNamesJSONObj!=null && classNamesJSONObj[c]!=null){
                                classNamesJSONObj[c](e, c);
                            }else if(globalClassNamesJSONObj!=null && globalClassNamesJSONObj[c]!=null){
                                globalClassNamesJSONObj[c](e, c);
                            }
                        }
                    }else if(e.type=="click"){
                        if(classNamesJSONObj!=null && classNamesJSONObj[c]!=null){
                            classNamesJSONObj[c](e, c);
                        }else if(globalClassNamesJSONObj!=null && globalClassNamesJSONObj[c]!=null){
                            globalClassNamesJSONObj[c](e, c);
                        }
//	                        return false;
                    }
                }

            })
        }
    }
};

/************************************************************************************
* globalClassNamesJSONObj is used to store all the class names with their functions used
*       in all pages.
*
* The name convension is :
*   for mouse click and keypress : domReady_xxxx
*   for keypress only: domReady_xxx_keypress
*
* classNamesJSONObj is used to store all class names with their functions used in
*   specific page.
*
*************************************************************************************/
var classNamesJSONObj = null;
var globalClassNamesJSONObj = {
    "domReady_test1":domReadyEventFunctionsCollections.doTest1,
    "domReady_test2_keypress":domReadyEventFunctionsCollections.doTest2,
    "domReady_instanceAnnoncement": domReadyEventFunctionsCollections.instanceAnnoncement,
    "domReady_appliSubmit":domReadyEventFunctionsCollections.appliSubmit,
    "domReady_attrDelete":domReadyEventFunctionsCollections.attrDelete,
    "domReady_attrDetailInfo":domReadyEventFunctionsCollections.attrDetailInfo,
    "domReady_attrDetailInfoImg":domReadyEventFunctionsCollections.attrDetailInfoImg,
    "domReady_attrUpdate":domReadyEventFunctionsCollections.attrUpdate,
    "domReady_bugReportSubmit":domReadyEventFunctionsCollections.bugReportSubmit,
    "domReady_cancelEditModuleImg":domReadyEventFunctionsCollections.cancelEditModuleImg,
    "domReady_cancelToEditValue":domReadyEventFunctionsCollections.cancelToEditValue,
    "domReady_clickForPsDetail":domReadyEventFunctionsCollections.clickForPsDetail,
    "domReady_closeFuncBtnsForAttr":domReadyEventFunctionsCollections.closeFuncBtnsForAttr,
    "domReady_colorboxMaxSize":domReadyEventFunctionsCollections.colorboxMaxSize,
    "domReady_colorboxOriginalSize":domReadyEventFunctionsCollections.colorboxOriginalSize,
    "domReady_contactInfoSubmit":domReadyEventFunctionsCollections.contactInfoSubmit,
    "domReady_datePicker":domReadyEventFunctionsCollections.datePicker,
    "domReady_deleteAttrGroupSet":domReadyEventFunctionsCollections.deleteAttrGroupSet,
    "domReady_deleteInstanceAttr":domReadyEventFunctionsCollections.deleteInstanceAttr,
    "domReady_deleteinstanceGroupSet":domReadyEventFunctionsCollections.deleteinstanceGroupSet,
    "domReady_deleteModuleAttr":domReadyEventFunctionsCollections.deleteModuleAttr,
    "domReady_deleteModuleElemt":domReadyEventFunctionsCollections.deleteModuleElemt,
    "domReady_domainValifyForm":domReadyEventFunctionsCollections.domainValifyForm,
    "domReady_duplicateAttrGroupSet":domReadyEventFunctionsCollections.duplicateAttrGroupSet,
    "domReady_duplicateModuleAttr":domReadyEventFunctionsCollections.duplicateModuleAttr,
    "domReady_dupliModuleElemt":domReadyEventFunctionsCollections.dupliModuleElemt,
    "domReady_editModuleImg":domReadyEventFunctionsCollections.editModuleImg,
    "domReady_editPageSchedule":domReadyEventFunctionsCollections.editPageSchedule,
    "domReady_instancePreview":domReadyEventFunctionsCollections.instancePreview,
    "domReady_jspPreview":domReadyEventFunctionsCollections.jspPreview,
    "domReady_newInstanceAttr":domReadyEventFunctionsCollections.newInstanceAttr,
    "domReady_newAttr":domReadyEventFunctionsCollections.newAttr,
    "domReady_newAttrGroupSet":domReadyEventFunctionsCollections.newAttrGroupSet,
    "domReady_newAttrImg":domReadyEventFunctionsCollections.newAttrImg,
    "domReady_newProductInstance":domReadyEventFunctionsCollections.newProductInstance,
    "domReady_openFuncBtnsForAttr":domReadyEventFunctionsCollections.openFuncBtnsForAttr,
    "domReady_pagePublish":domReadyEventFunctionsCollections.pagePublish,
    "domReady_pbTooltip":domReadyEventFunctionsCollections.pbTooltip,
    "domReady_permissionSet":domReadyEventFunctionsCollections.permissionSet,
    "domReady_productEditAreaMax":domReadyEventFunctionsCollections.productEditAreaMax,
    "domReady_saveContainerValue":domReadyEventFunctionsCollections.saveContainerValue,
    "domReady_saveInstanceValue":domReadyEventFunctionsCollections.saveInstanceValue,
    "domReady_saveInstanceImg":domReadyEventFunctionsCollections.saveInstanceImg, // this will need to be removed
    "domReady_saveModuleImg":domReadyEventFunctionsCollections.saveModuleImg, // this will need to be removed
    "domReady_saveModuleValue":domReadyEventFunctionsCollections.saveModuleValue,
    "domReady_savePageValue":domReadyEventFunctionsCollections.savePageValue,
    "domReady_savePermissionValue":domReadyEventFunctionsCollections.savePermissionValue,
    "domReady_saveViewSchedValue":domReadyEventFunctionsCollections.saveViewSchedValue,
    "domReady_saveViewValue":domReadyEventFunctionsCollections.saveViewValue,
    "domReady_selectMediaForModule": domReadyEventFunctionsCollections.selectMediaForModule,
    "domReady_scheduleEdit":domReadyEventFunctionsCollections.scheduleEdit,
    "domReady_toCancelAttrTxtValue":domReadyEventFunctionsCollections.toCancelAttrTxtValue,
    "domReady_toEditAttrTxtValue":domReadyEventFunctionsCollections.toEditAttrTxtValue,
    "domReady_toEditValue":domReadyEventFunctionsCollections.toEditValue,
    "domReady_topicNodeInfo":domReadyEventFunctionsCollections.topicNodeInfo,
    "domReady_toSaveAttrTxtValue":domReadyEventFunctionsCollections.toSaveAttrTxtValue, // for instance save
    "domReady_toSaveModuleAttrTxtValue":domReadyEventFunctionsCollections.toSaveModuleAttrTxtValue, // for module save
    "domReady_updateAccountContact":domReadyEventFunctionsCollections.updateAccountContact,
//    "domReady_updateAccountGroup":domReadyEventFunctionsCollections.updateAccountGroup,
    "domReady_updateAccountProfile":domReadyEventFunctionsCollections.updateAccountProfile,
    "domReady_updateAccountPwd":domReadyEventFunctionsCollections.updateAccountPwd,
    "domReady_updateOrgInfo":domReadyEventFunctionsCollections.updateOrgInfo,
    "domReady_viewSchedSubmit":domReadyEventFunctionsCollections.viewSchedSubmit,
    "domReady_xPageDetail":domReadyEventFunctionsCollections.xPageDetail,
    "domReady_xProductDetail":domReadyEventFunctionsCollections.xProductDetail


};


/************************************************************************************
*
*   All tools utilities are list here
*
*************************************************************************************/
var utils = {
	/*
    *  o:       current obj,
    *  next:    0 - next is false, which means to find domValue within o structure, ie. <o><script type="domValue">...</script></o>
    *           1 - next is true, which means to find domValue outside o structure (1st sibling), ie. <o/><script type="domValue">...</script>
    * */
    domValueReader: function(o,next){

        if(next==1){ // only the very next sibling and its tag must be Script tag with type of domValue will be accepted.
            var jThis = o.next("script[type='domvalue']");
            return jThis.html();
        }else if(next==0){ // only the first Script tag with type of domValue will be accepted.
            var jThis = $(o.find("script[type='domvalue']")[0]);
            return jThis.html();
        }else if(next==2){
            var jThis = $(o).attr("domvalue");
            return jThis;
        }


//       $("#debugArea").append(domValue+"<br/>");
    }

};