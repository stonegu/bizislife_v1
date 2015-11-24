<div id="accountNav_main1">
</div>

<div>
	<a href="/logout">Logout</a>
</div>

<script type="text/javascript">

var currentAccountPageId = "${currentAccountPageId}";


$(function () {
	$("#accountNav_main1").jstree({ 
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
						return "operation=get_accountTree";
					}else if(node.attr("lfc")){
						return node.attr("lfc");
					}else{
						return "operation=get_accountTree";
					}
				}, 
				"url":function (node){
					//console.log("node: "+node);
					return "/accountNav";
				},
				"success": function (data) {
                    //return new_data;
                    //console.log("success");
                }
			}
		}
	})
	// 1) the loaded event fires as soon as data is parsed and inserted
	.bind("loaded.jstree", function (event, data) {
		if(currentAccountPageId){
			var targetNode = $("#"+currentAccountPageId);
			if(targetNode){
//				targetNode.attr("foldopenafterload", "true");
				targetNode.children("a").addClass("jstree-clicked");
			}
		}		
		
		var foldersToOpen = $("#accountNav_main1 li[foldopenafterload='true']");
		$(foldersToOpen).each(function(){
			data.inst.open_node(data.inst._get_node($("#"+$(this).attr("id"))));
		})
		//data.inst.open_all(-1);
		
		$(this).jstree("open_all");

	})
	.bind("open_node.jstree", function(event, data){
		//console.log("open: "+ data.rslt.obj.attr("id"));
		
		
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

