function setPermissionSetEntityTree(orguuid, permissionId){
	
	
	//ajax call to get tree
	$.ajax({
		type: "GET",
		url: "/permissionSetEntityTree",
		cache: false,
		data: "permissionId="+permissionId,
		dataType:"html",
		success: function(etData, textStatus){
			
			$(".permissionSetSegment .permissionTree").css({"float": "left", "width": "30%"})
			$(".permissionSetSegment .permissionContent").css({"float": "right", "width": "65%"}).html(etData);
			
			// for tabs
			$("#permissionEntitySetTabs").tabs();
			
			// for module tree
			$("#moduleTreeForPermissionSet").jstree({
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
							"module":{
								"select_node" : function(){
									return false;
								} 
							},
							"productModule":{
								"select_node" : function(){
									return false;
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
							// only the accounts in SystemDefault group can setup the permission for accounts and groups. The accounts in SystemDefault group have the full privilege for all modules, entities, pages.
							// so, in this case, isPermissionFilterNeeded = false
							if(node==-1){
								return "operation=get_moduleTree&org="+orguuid+"&isInstanceInclude=false&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
							}else{
								if(node.attr("rel")=="module" || node.attr("rel")=="folder"){
									return "operation=get_moduleTree&org="+orguuid+"&parentNodeId="+node.attr("id")+"&isInstanceInclude=false&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
								}
							}
						}, 
						"url":function (node){
							return "/moduleTreeMain";
						},
						"success": function (data) {
		                }
					}
				}
			})
			.bind("loaded.jstree", function (event, data) {
				$(this).jstree("open_all");
			});
			
			// for product tree
			$("#productTreeForPermissionSet").jstree({
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
							// only the accounts in SystemDefault group can setup the permission for accounts and groups. The accounts in SystemDefault group have the full privilege for all modules, entities, pages.
							// so, in this case, isPermissionFilterNeeded = false
							if(node==-1){
								return "operation=get_productTree&org="+orguuid+"&isViewInclude=false&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
							}else{
								if(node.attr("rel")=="folder" || node.attr("rel")=="default"){
									return "operation=get_productTree&org="+orguuid+"&parentNodeId="+node.attr("id")+"&isViewInclude=false&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
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
				}											
			})
			.bind("loaded.jstree", function (event, data) {
				$(this).jstree("open_all");
			})
			.bind("hover_node.jstree", function(event, data) {
			})
			.bind("dehover_node.jstree", function(event, data) {
			})
			;
			
			
			// for page tree
			$("#pageTreeForPermissionSet").jstree({
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
							// only the accounts in SystemDefault group can setup the permission for accounts and groups. The accounts in SystemDefault group have the full privilege for all modules, entities, pages.
							// so, in this case, isPermissionFilterNeeded = false
							if(node==-1){
								return "operation=getPageRoots&org="+orguuid+"&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
							}else{
								if(node.attr("rel")=="folder" && node.hasClass("dk")){
									return "operation=get_desktopTree&org="+orguuid+"&parentNodeId="+node.attr("id")+"&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
								}else if(node.attr("rel")=="folder" && node.hasClass("mb")){
									return "operation=get_mobileTree&org="+orguuid+"&parentNodeId="+node.attr("id")+"&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
								}
							}
							
						}, 
						"url":function (node){
							//console.log("node: "+node);
							return "/pageTreeMain";
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
			.bind("hover_node.jstree", function(event, data) {
			})
			.bind("dehover_node.jstree", function(event, data) {
			})
			;
			
			
			// for media tree
			$("#mediaTreeForPermissionSet").jstree({
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
							// only the accounts in SystemDefault group can setup the permission for accounts and groups. The accounts in SystemDefault group have the full privilege for all modules, entities, pages.
							// so, in this case, isPermissionFilterNeeded = false
							if(node==-1){
								return "operation=get_mediaTree&folderOnly=true&org="+orguuid+"&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
							}else{
								if(node.attr("rel")=="folder"){
									return "operation=get_mediaTree&folderOnly=true&org="+orguuid+"&parentNodeId="+node.attr("id")+"&isPermissionInclude=true&isPermissionFilterNeeded=false&permissionUuid="+$("#selectedPermissionUuid").val();
								}
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
				}											
			})
			.bind("loaded.jstree", function (event, data) {
				$(this).jstree("open_all");
			})
			.bind("hover_node.jstree", function(event, data) {
			})
			.bind("dehover_node.jstree", function(event, data) {
			})
			;
			
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("system error, you may need to refresh the page!");
		}
	});
	
	
}