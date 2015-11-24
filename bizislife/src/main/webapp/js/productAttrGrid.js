
$(function() {

//    $( "#sortable" ).sortable({
//    	handle: ".moveHandler" 
//    });
//    $( "#sortable" ).disableSelection();
    
    var timerForMouseEnter;
    
    $(".attrgrid").live("mouseenter", function(){
    	var currentObj = $(this);
    	timerForMouseEnter = setTimeout(function(){
    		$(".attrgrid").fadeTo("fast", 0.5);
    		currentObj.fadeTo(100, 1);
    	}, 300);
    }).live("mouseleave", function(){
    	clearTimeout(timerForMouseEnter);
    	$(this).fadeTo(100, 0.5);
    });
    
    
    // context menu for add new attribute:
    // jquery contextMenu doc : http://medialize.github.com/jQuery-contextMenu/docs.html
//    $.contextMenu({
//        selector: '.clickForNewAttr', 
//        trigger: 'left',
//        callback: function(key, options) {
//        	if(key=="ImgAttribute"){
//        		
//				$("#productIdInSaveAttrForm").val($("#productidInNodeDetail").val());
//        		$("#attrIdInSaveAttrForm").val("");
//        		$("#attrNameInSaveAttrForm").val("");
//        		$("#attrValueInSaveAttrForm").val("");
//        		$(".imgAttrForm .attrImgShowArea").html("");
//
//				$.colorbox({
//					width : 800,
//					height : 300,
//					inline: true, 
//					href: "#imgAttrDetail",
//					opacity: 0.4,
//					overlayClose: false,
//					onOpen:function(){},
//					onLoad:function(){},
//					onComplete:function(){
//					},
//					onCleanup:function(){
//					},
//					onClosed:function(){}
//					
//				});
//        		
//        	}else{
//            	var productid = $("#productidInNodeDetail").val();
//    			$.ajax({
//    				type: "GET",
//    				url: "/newAttribute",
//    				cache: false,
//    				data: "productid="+productid+"&attrType="+key,
//    				dataType:"html",
//    				success: function(data, textStatus){
//    					
//    					$.colorbox({
//    						html: data,
//    						opacity: 0.4,
//    						overlayClose: false
//    					});
//
//    				},
//    				error: function(XMLHttpRequest, textStatus, errorThrown){
//    					alert("failed");
//    				
//    				}
//    			});
//                
//        	}
//        },
//        items: {
//        	"RealAttribute" : {
//        		name: "Add Number", 
//        		icon: "number"
//        	},
//        	"StringAttribute" : {
//        		name: "Add Text", 
//        		icon: "text"
//        	},
//        	"ImgAttribute" : {
//        		name: "Add Images",
//        		icon: "img"
//        	}
//        	
//        }
//    });
//    
    
    
    
    
	
});