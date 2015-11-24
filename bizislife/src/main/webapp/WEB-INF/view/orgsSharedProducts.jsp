<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="orgsSharedProducts">

	<div class="info">
		<p>You can find all shared products in different organizations!</p>
	
	</div>

	<div id="orgsSharedProductsTree">
	
	</div>



</div>


<script type="text/javascript">
$(function () {
	
	$("#orgsSharedProductsTree").jstree({
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
						return "operation=getSharedProductsForAllOrgs&targetOrgid=${orgUuid}";
					}else{

						if(node.attr("rel")=="folder"){
							return "operation=getSharedProductsForAllOrgs&targetOrgid=${orgUuid}&folderNodeId="+node.attr("id");
						}else if(node.attr("rel")=="org"){
							return "operation=getSharedProductsForAllOrgs&targetOrgid=${orgUuid}&orgNodeId="+node.attr("id");
						}

					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					return "/sharedProductTreeMain";
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
 			$(this).jstree("open_all", data.rslt.obj);
		}
	})
	
	
	
	
	
	
})
</script>