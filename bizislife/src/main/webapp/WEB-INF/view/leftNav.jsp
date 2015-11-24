<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="leftNav_main2">
</div>

<script type="text/javascript">

var currentPageId = "${currentPageId}";

$(function () {
	$("#leftNav_main2").jstree({ 
		"plugins" : [ "themes", "json_data"],
		"themes" : {
			"theme" : "classic",
			"dots" : true,
			"icons" : true
		},
		"json_data" : { 
			"ajax" : {
				"data" : function(node){
					if(node==-1){
						return "operation=get_leftTree";
					}else if(node.attr("lfc")){
						return node.attr("lfc");
					}else{
						return "operation=get_leftTree";
					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					return "/leftNavMain";
				},
				"success": function (data) {
                    //return new_data;
                    //console.log("success for loading tree!" );
                }
				
				
				
				
//				"url" : "/leftNavMain",
			}
		}
	})
	// 1) the loaded event fires as soon as data is parsed and inserted
	.bind("loaded.jstree", function (event, data) {
		// highlight the select node "jstree-clicked"
		if(currentPageId){
			var targetNode = $("#"+currentPageId);
			if(targetNode){
//				targetNode.attr("foldopenafterload", "true");
				targetNode.children("a").addClass("jstree-clicked");
			}
		}		
		
		var foldersToOpen = $("#leftNav_main2 li[foldopenafterload='true']");
		$(foldersToOpen).each(function(){
			data.inst.open_node(data.inst._get_node($("#"+$(this).attr("id"))));
		})
		//data.inst.open_all(-1);
		
		
	})
	.bind("open_node.jstree", function(event, data){
		//console.log("open: "+ data.rslt.obj.attr("id"));
		
		// highlight clicked node after async ajax call back
		if(currentPageId){
			var targetNode = $("#"+currentPageId);
			if(targetNode.length>0 && targetNode.children("a").length>0 && !targetNode.children("a").hasClass("jstree-clicked")){
				// add attr "foldopenafterload" to "true, and open node
				//.................
				//console.log("...... add click mark here! target node : " + targetNode.attr("id"));
				
				targetNode.children("a").addClass("jstree-clicked");
				
				// open current Node and it's parents (grand-parents, grand-grand-parents, ...)
				targetNode.parents("li[rel]").each(function(){
					$("#leftNav_main2").jstree("open_node", this);
				})
				$("#leftNav_main2").jstree("open_node", targetNode);
			}
		}		
		
	})
	.bind("close_node.jstree", function(event, data){
		//console.log("close: "+data.rslt.obj.attr("id"));
	})
/* 	.bind("select_node.jstree", function (event, data) { 
		// `data.rslt.obj` is the jquery extended node that was clicked
		alert(data.rslt.obj.attr("id"));
	})
 */	
	
	;
	
	
	
});

</script>

