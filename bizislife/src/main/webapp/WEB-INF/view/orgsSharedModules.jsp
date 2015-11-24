<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="orgsSharedModules">

	<div class="info">
		<p>You can find all shared modules in different organizations!</p>
	
	</div>

	<div id="orgsSharedModulesTree">
	
	</div>



</div>


<script type="text/javascript">

$(function () {

	
	$("#orgsSharedModulesTree").jstree({
		"plugins" : ["themes", "json_data", "ui", "types", "crrm", "dnd"],
		"core" : {
			"html_titles" : true
		},
		"types" : {
				"types" : {
					
					"org" :{
						"select_node" : function(){
							return false;
						} 
					},
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
						return "operation=getSharedModulesForAllOrgs&targetOrgid=${orgUuid}";
					}else{

						if(node.attr("rel")=="folder"){
							return "operation=getSharedModulesForAllOrgs&targetOrgid=${orgUuid}&folderNodeId="+node.attr("id");
						}else if(node.attr("rel")=="org"){
							return "operation=getSharedModulesForAllOrgs&targetOrgid=${orgUuid}&orgNodeId="+node.attr("id");
						}

					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					return "/sharedModuleTreeMain";
				},
				"success": function (data) {
                    //return new_data;
                    //console.log("success");
                }
			}
		},
		"crrm" : {
			"move" : {
				"default_position" : "first",
				"check_move" : function (m) {
 					return false;
 				}
			}
		}
		
	})
	.bind("loaded.jstree", function (event, data) {
		
	})
	.bind("open_node.jstree", function(event, data){
		//console.log(data.rslt.obj.attr("id"));
		if(data.rslt.obj.attr("rel")=="org"){
/* 			
			$(data.rslt.obj).find('li').each(function (i){
				data.inst.open_node($(this));
			});			
 */			
 			$(this).jstree("open_all", data.rslt.obj);
// 			$.jstree.open_all(data.rslt.obj);
			//data.inst.open_all(-1);
		}
	})
	
	
	
	
	
	
})

</script>