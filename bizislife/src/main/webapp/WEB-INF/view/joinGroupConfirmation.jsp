<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Join Group Confirmation Page</title>
	<script src="/js/brands/JQuery/jquery-1.7.2.js" type="text/javascript" language="javascript"></script>

	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>
</head>
<body>

	<div>
	
		You (${loginAccount.firstname} ${loginAccount.lastname}) are trying to join ${groupOrg.orgsysname}'s ${group.groupname}!<br/>
		Click <button type="button" class="groupJoinFinalConfirm" domvalue="{'group':'${group.id}', 'joinkey':'${joinkey}'}">Here</button> to confirm.
	</div>
	<div class="confirmInfoSection">
	
	</div>
	
	<div>
		Back to <a href="/">Home</a>
	</div>
	
	
    <script type="text/javascript">
    $(function () {
    	$(".groupJoinFinalConfirm").click(function(){
    		//alert("groupJoinFinalConfirm");
    		var domvalue = $(this).attr("domvalue");
    		var domJson = eval("("+domvalue+")");
    		
			$.ajax({
				type: "GET",
				url: "/groupJoinFinalConfirm",
				cache: false,
			    data: "groupToJoin="+domJson.group+"&joinkey="+domJson.joinkey,
				dataType:"json",
				success: function(jgcData, textStatus){
					
					//http://localhost/getAcctInfo?acct=27a86e41-9e19-4188-a1b4-22e270657fcc
							
					if(jgcData.success){
						$(".confirmInfoSection").html("Congradulation! You joined "+jgcData.response1.groupname+", you can click <a href='/getAcctInfo?acct=${loginAccount.accountuuid}'>here</a> to check your personal info!");
					}else{
						alert(jgcData.response1)
					}
					
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert("failed");
				}
			});
    		
    		
    	})
    })
    </script>

</body>