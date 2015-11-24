$(function() {
/*	
	$(".instanceViewConfigSubmit").live("click", function(){
		
        var theForm = $("form[name='instanceViewConfig']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/instanceViewConfig",
			cache: false,
			data: serializedData,
			dataType:"json",
			success: function(data, textStatus){
				if(data.success){
					$("#viewConfigInfoId").html(data.response1)
					if(data.response2){
						var newViewNode = "<div style='width: 100%;'><input type='radio' value='"+data.response2.key+"' name='instanceView' class='getInstanceViewForm'>"+data.response2.value+"<br></div>"
						$("#instanceViewManage .viewList").append(newViewNode);
					}
					
				}else{
					$("#viewConfigInfoId").html(data.response1)
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("new instance view creation failed!");
			
			}
		});
        
		
	});
*/	
	
/*	
	$(".newInstanceViewScheduleSubmitBtn").live("click", function(){
        var theForm = $("form[name='newInstanceViewSchedule']");
        var serializedData = theForm.serialize();
        
		$.ajax({
			type: "POST",
			url: "/newInstanceViewSchedule",
			cache: false,
			data: serializedData,
			dataType:"html",
			success: function(data, textStatus){
				$.colorbox({
					width : 700,
					height : 500,
					html: data,
					//inline: true, 
					//href: mediaUploadContainerContent,
					opacity: 0.4,
					overlayClose: false
				});
				
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
				alert("new instance view schedule creation failed!");
			
			}
		});
		
	});
*/	
	
//	$(".delInstanceViewScheduleSubmitBtn").live("click", function(){
//        var scheduleUuid = $(this).attr("domvalue");
//        var theForm = $("form[name='edit_"+scheduleUuid+"']");
//        var serializedData = theForm.serialize();
//        
//		var retVal = confirm("Do you want to delete?");
//		if( retVal == true ){
//			$.ajax({
//				type: "POST",
//				url: "/delInstanceViewSchedule",
//				cache: false,
//				data: serializedData,
//				dataType:"html",
//				success: function(data, textStatus){
//					
//					$.colorbox({
//						width : 700,
//						height : 500,
//						html: data,
//						//inline: true, 
//						//href: mediaUploadContainerContent,
//						opacity: 0.4,
//						overlayClose: false
//					});
//					
//				},
//				error: function(XMLHttpRequest, textStatus, errorThrown){
//					alert("delete instance view schedule failed!");
//				
//				}
//			});
//		}        
//		
//	})
	
	
});
