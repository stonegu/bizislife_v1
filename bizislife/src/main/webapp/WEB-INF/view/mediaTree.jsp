<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="mediaTree">

    <div id="mediaTree">
    
    </div>
    
    <div id="mediaUploadContainer" class="mediaUploadContainer mediaUploadClass" style="display: none;">
        <p>
           You can upload image, text (includeing javascript, css) within 1MB size. 
           upload version 2 is available, click <a href="#" onclick="fileupload_v2();return false;">here</a> to have a try! 
        </p>
        
        <p id="support-notice">Your browser does not support Ajax uploads :-(<br/>The form will be submitted as normal.</p>
        <form action="/uploadFileByHttpPost" name="fileuploadform" method="post" enctype="multipart/form-data" id="mediaUploadForm-id" >
            <div class="fileList">
                <div class="fileInput">Select a file to upload : <input type="file" name="files"/> <button class="removeFileInput">Remove</button><br/></div>
            </div>
            <p><a href="#" class="moreFileUpload">More file to upload ...</a></p>
            <input type="hidden" name="targetNodeId" id="targetNodeId" value="" />
            <input type="submit" value="Upload" id="upload-button-id" disabled="disabled" />
        </form>
        <p id="upload-status"></p>
        <p id="upload-progress"></p>
        <pre id="upload-result"></pre>
    </div>
    
	<div id="dropzone_autoprocess" class="dropzone_autoprocess mediaUploadClass" style="display: none;">
	   <div style="padding: 10px 0;">
	       You can upload image, text (includeing javascript, css) within 1MB size.
	       You can goto <a href="#" onclick="fileupload_v1();return false;">"upload V1"</a> if this upload screen doesn't work for you.
	   </div>
	
		<form action="#" id="image-upload" class="dropzone">
            <input id="targetNodeId_dropzone" type="hidden" value="" name="targetNodeId">
		</form>
	</div>
	
	
    <script type="text/javascript">
    
    function fileupload_v1 () {
        $("#dropzone_autoprocess").hide();
        $.colorbox({
            width : 600,
            height : 600,
            escKey: false,
            //open: true,
            //html: mediaUploadContainerContent,
            //html: function(){
                //return $("#mediaUploadContainer");
            //},
            inline: true, 
            href: "#mediaUploadContainer",
//            rel: "mediaUpload",
            opacity: 0.4,
            overlayClose: false,
            onOpen:function(){},
            onLoad:function(){},
            onComplete:function(){
                $("#mediaUploadContainer").show();
            },
            onCleanup:function(){
                console.log('cleanup v1');
                $("#mediaUploadContainer").hide();
                $("#upload-status").html("");
                $("#upload-progress").html("");
                $("#upload-result").html("");
                
            },
            onClosed:function(){
                console.log('closed v1');
            }
            
        });
        
    }
    
    function fileupload_v2 () {
        $("#mediaUploadContainer").hide();
        $.colorbox({
            width : 600,
            height : 600,
            escKey: false,
            //open: true,
            inline: true, 
            title: 'FileUpload Version 2',
            href: "#dropzone_autoprocess",
//            rel: "mediaUpload",
            opacity: 0.4,
            overlayClose: false,
            onOpen:function(){},
            onLoad:function(){},
            onComplete:function(){
                $("#dropzone_autoprocess").show();
            },
            onCleanup:function(){
                console.log('cleanup v2');
                // for v2 upload: dropzone
                $("#dropzone_autoprocess").hide();
                // remove all dropzone previews:
                var myDropzone = Dropzone.forElement("#image-upload");
                myDropzone.removeAllFiles();
                
            },
            onClosed:function(){
                console.log('closed v2');
                $("#dropzone_autoprocess").hide();
            }
            
        });
        
    }
    
    function mediatreeCreate (jsonObj) {
        var resultInfoContainer_v1 = document.getElementById('upload-result');
        var resultString = "";
        
        if(jsonObj.response1){
            $(jsonObj.response1).each(function(i){
                
                console.log('........... generatedName: '+this.generatedName);
                
                // create nodes in the media tree
                //console.log(this.originalName);
                // {"response1":[{"extraInfo":null,"originalName":"Capture.JPG","generatedName":"c6499895-2945-4df8-808a-5a0b013652a7","contentType":"image\/jpeg","size":null},{"extraInfo":null,"originalName":"3.jpg","generatedName":"8df3617a-e221-4b99-afc2-555cb52b7407","contentType":"image\/jpeg","size":null}],"response2":"60ace2ae-6774-4c5f-88f1-0a205b6bc474","success":true}
                if(this.generatedName){
                    
                    if(this.contentType.indexOf("image")>-1){
                        $("#mediaTree").jstree(
                                "create",
                                "#"+jsonObj.response2,
                                "last",
                                {"attr" : 
                                    {"rel":this.contentType.indexOf("image")>-1?"image":this.contentType.indexOf("pdf")>-1?"pdf":this.contentType.indexOf("css")>-1?"css":this.contentType.indexOf("javascript")?"javascript":this.contentType.indexOf("text")?"text":"default", 
                                        "id":this.generatedName, 
                                        "class":"delete"},
                                 "data":
                                    {
                                        "title" : this.originalName,
                                        "attr" : {
                                            "class": "detailInfoPopup",
                                            "domvalue" : "{'topOffset':20, 'leftOffset':-30, 'popupContent':'<img src=/getphoto?id="+this.generatedName+"&size=100>'}"
                                        }
                                     }
                                }
                            );
                    }else{
                        $("#mediaTree").jstree(
                                "create",
                                "#"+jsonObj.response2,
                                "last",
                                {"attr" : 
                                    {"rel":this.contentType.indexOf("image")>-1?"image":this.contentType.indexOf("pdf")>-1?"pdf":this.contentType.indexOf("css")>-1?"css":this.contentType.indexOf("javascript")?"javascript":this.contentType.indexOf("text")?"text":"default", 
                                        "id":this.generatedName, 
                                        "class":"delete"},
                                 "data" : this.originalName
                                }
                            );
                        
                    }
                    
                }
                
                resultString = resultString + this.originalName + ": " + this.extraInfo + "\n";
                
            })
        }
        
        resultInfoContainer_v1.innerHTML = resultString;

    }
    
    $(function () {

        //Disable the auto init. So we can modify settings first. We will manually initialize it later.
        //Dropzone.autoDiscover = false;
        //imageUpload portion is the camelized version of our HTML elements ID. ie <div id="image-upload"> becomes imageUpload.
        Dropzone.options.imageUpload = {
            paramName: "files", // The name that will be used to transfer the file
            url: "/uploadFileByHttpPost",
            maxFilesize: 1, // MB
            parallelUploads: 5,
            maxFiles: 100,
//            addRemoveLinks: true,
            uploadMultiple: true,
//            thumbnailWidth: 150,
//            thumbnailHeight: 150,
            acceptedFiles: ".jpeg,.jpg,.png,.gif,.JPEG,.JPG,.PNG,.GIF,.txt,.js,.css",
            
            init: function() {
                
                this.on("sendingmultiple", function() {
                    // Gets triggered when the form is actually being sent.
                    // Hide the success button or the complete form.
                    console.log("sending.....");
                    
				});
				this.on("successmultiple", function(files, response) {
					// Gets triggered when the files have successfully been sent.
					// Redirect user or notify of success.
					//console.log("success...%j", response);
				    mediatreeCreate(response);
					
					
				});
				this.on("errormultiple", function(files, response) {
					// Gets triggered when there was an error sending the files.
					// Maybe show form again, and notify user of error
					  console.log("error...");
				});
				this.on("removedfile", function(file, response){
				    console.log('-------remove file: %j', file);
				})
				
            }
            
        };
        
        // Manually init dropzone on our element.
/*         var myDropzone = new Dropzone("#image-upload", {
            url: '/uploadFileByHttpPost'
        });        
 */        
        
        // time value for treeNodeFunctionIcons
        var treeNodeIconsTimeout;
        
        $("#mediaTree").jstree({
            "plugins" : ["themes", "json_data", "ui", "types", "crrm", "dnd"],
            "core" : {
                "html_titles" : true
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
                            return "operation=get_mediaTree&org=${orgUuid}";
                        }else{
                            return "operation=get_mediaTree&org=${orgUuid}&parentNodeId="+node.attr("id");
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
            },
            "crrm" : {
                "move" : {
                    "default_position" : "first",
                    "check_move" : function (m) {
                        // m.o : moving obj, m.np : new parent
                        if(m.np.attr("rel")=="folder"){
                            return true;
                        }
                        return false;
                    }
                }
            }

        })
        .bind("loaded.jstree", function (event, data) {
            //console.log("loaded!");
            
        })
        .bind("select_node.jstree", function(event, data){
            var nodeUuid = data.rslt.obj.attr("id");
            var rel = data.rslt.obj.attr("rel");
            if(rel=="image"){
                $.colorbox({
//                  width : 500,
//                  height : 300,
//                  html: "<img src='/getphoto?id="+nodeUuid+"&size=600' />",
                    href: "/getphoto?id="+nodeUuid+"&size=600",
                    photo: true,
                    title: "click to open the <a href='/getphoto?id="+nodeUuid+"&size=-1' target='_blank'>original size</a>"+" <a href='/getphoto?id="+nodeUuid+"&size=50' target='_blank'>50 pixs</a>"+" <a href='/getphoto?id="+nodeUuid+"&size=100' target='_blank'>100 pixs</a>"+" <a href='/getphoto?id="+nodeUuid+"&size=200' target='_blank'>200 pixs</a>"+" <a href='/getphoto?id="+nodeUuid+"&size=600' target='_blank'>600 pixs</a>",
                    escKey: false,
                    //inline: true, 
                    //href: mediaUploadContainerContent,
                    opacity: 0.4,
                    overlayClose: false
                });
            }else if(rel=="text" || rel=="css" || rel=="javascript"){
                $.colorbox({
                    width : 700,
                    height : 600,
                    iframe : true,
                    escKey: false,
                    href: "/getTxt?id="+nodeUuid+"&characters=100",
                    title: "click to get the <a href='/getTxt?id="+nodeUuid+"' target='_blank'>whole document</a>",
                    opacity: 0.4,
                    overlayClose: false
                });
                
            }else if(rel=="pdf"){
                $.colorbox({
                    width : 700,
                    height : 600,
                    iframe : true,
                    escKey: false,
                    href: "/getPdfInText?id="+nodeUuid,
                    title: "click to get the <a href='/getTxt?id="+nodeUuid+"' target='_blank'>whole document</a>",
                    opacity: 0.4,
                    overlayClose: false
                });
                
            }
            
            
        })
        
        .bind("create.jstree", function (event, data) {
            
            var parentNodeUuid = data.rslt.parent.attr("id");
            
            if(data.rslt.obj.attr("rel")=="folder"){
                //console.log("create a folder!!!");
                
                $.ajax({
                    type: "GET",
                    url: "/mediaNodeCreate",
                    cache: false,
                    data: "newNodetype=fd&parentNodeUuid="+parentNodeUuid+"&nodeName="+data.rslt.name,
                    dataType:"json",
                    success: function(aData, textStatus){
                        if(aData.success){
                            $(data.rslt.obj).attr("id", aData.response1);       
                            
                        }else{
                            $.jstree.rollback(data.rlbk);
                            alert(aData.response1);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        alert("system error, your result will be rollback!");
                        $.jstree.rollback(data.rlbk);
                    }
                });
                
            }else if(data.rslt.obj.attr("rel")=="default"){
                //console.log("create a image!!!");
/*              
                $.ajax({
                    type: "GET",
                    url: "/mediaNodeCreate",
                    cache: false,
                    data: "newNodetype=pd&parentNodeUuid="+parentNodeUuid+"&nodeName="+data.rslt.name,
                    dataType:"json",
                    success: function(aData, textStatus){
                        if(aData.success){
                            $(data.rslt.obj).attr("id", aData.response1);       
                            
                        }else{
                            $.jstree.rollback(data.rlbk);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        alert("system error, your result will be rollback!");
                        $.jstree.rollback(data.rlbk);
                    }
                });
 */             
            }
            
            
            
            
        })
        .bind("remove.jstree", function (event, data) {
            var nodeUuid = data.rslt.obj.attr("id");
            // ajax call to delete the node
            $.ajax({
                type: "GET",
                url: "/mediaNodeDelete",
                cache: false,
                data: "nodeId="+nodeUuid,
                dataType:"json",
                success: function(aData, textStatus){
                    if(aData.success){
                                                
                    }else{
                        $.jstree.rollback(data.rlbk);
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    alert("system error, your result will be rollback!");
                    $.jstree.rollback(data.rlbk);
                }
            });
            
            
            
            
            //console.log("..............."+nodeUuid);
        })
        
        .bind("rename.jstree", function (event, data) {
            var currentNodeId = data.rslt.obj.attr("id");
            var newName = data.rslt.new_name;
            
            if(data.rslt.obj.attr("rel")=="folder"){
                
                $.ajax({
                    type: "GET",
                    url: "/updateMediaDetailValue",
                    cache: false,
                    data: "mediaId="+currentNodeId+"&updateValue="+newName+"&valueName=prettyname",
                    dataType:"json",
                    success: function(data, textStatus){
                        if(data.success){
                        }else{
                            $.jstree.rollback(data.rlbk);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        alert("failed");
                        $.jstree.rollback(data.rlbk);
                    }
                });
            }
            
        })
        
        
        
        .bind("hover_node.jstree", function(event, data) {
            var nodeUuid = data.rslt.obj.attr("id");

            clearTimeout(treeNodeIconsTimeout);
            $("#mediaTree .nodeFunctionIcons").remove();

            data.rslt.obj.children("a").after($(".treeHoverNodeFunctionIcons").html());
            $(".nodeFunctionIcons").bind("mouseenter", function(){
                clearTimeout(treeNodeIconsTimeout);
            })
            $(".nodeFunctionIcons").bind("mouseleave", function(){
                $(this).remove();
            })          

            
            // hide all icons first:
            $("#mediaTree .nodeFunctionIcons img").hide();
            // show icons and bind actions based on class name
            if(data.rslt.obj.hasClass("create")){
                $("#mediaTree .nodeFunctionIcons .folderCreateIconImg").click(function(){
                    $("#mediaTree").jstree("create", data.rslt.obj, "last", {"attr" : {"rel":"folder", "class":"create delete toRename"}});
                })
                $("#mediaTree .nodeFunctionIcons .folderCreateIconImg").show();
                
                $("#mediaTree .nodeFunctionIcons .newMediaIconImg").click(function(){
                    // copy from contextMenu : image upload
                    $("#targetNodeId").val(data.rslt.obj.attr("id"));
                    $("#targetNodeId_dropzone").val(data.rslt.obj.attr("id"));
                    
                    // open selected node first, which will prevent some nodes created two times
                    $("#mediaTree").jstree("open_node", $('#'+nodeUuid));
                    
                    fileupload_v2 ();
                    
                })
                $("#mediaTree .nodeFunctionIcons .newMediaIconImg").show();
            }
            if(data.rslt.obj.hasClass("delete")){
                $("#mediaTree .nodeFunctionIcons .deleteIconImg").click(function(){
                    if(confirm("Confirm to delete '"+data.inst.get_text(data.rslt.obj)+"'?")){
                        $("#mediaTree").jstree("remove", data.rslt.obj);
                    }

                })
                $("#mediaTree .nodeFunctionIcons .deleteIconImg").show();
            }
            
            if(data.rslt.obj.hasClass("toRename")){
                $("#mediaTree .nodeFunctionIcons .renameIconImg").click(function(){
                    $("#mediaTree").jstree("rename", data.rslt.obj);
                })
                $("#mediaTree .nodeFunctionIcons .renameIconImg").show();
                
            }
            
        })
        .bind("dehover_node.jstree", function(event, data) {
            treeNodeIconsTimeout=setTimeout(function(){
                data.rslt.obj.find(".nodeFunctionIcons").remove();
            },1000)
        })
        .bind("move_node.jstree", function (event, data) {
            //console.log(".... move node!!");
            
            //var movingNodeId = data.rslt.o.attr("id");
/*          var originalPosition = data.inst.get_path("#" + data.rslt.op.attr("id"), true);
            var targetPosition = data.inst.get_path("#" + data.rslt.np.attr("id"), true);
 */         
//          console.log("originalPosition: "+originalPosition);
//          console.log("targetPosition: "+targetPosition);
//          console.log("moving node id: "+movingNodeId);

            $.ajax({
                type: "POST",
                url: "/moveMediaNode",
                cache: false,
                data: "nodeId="+data.rslt.o.attr("id")+"&targetUuid="+data.rslt.np.attr("id"),
                dataType:"json",
                success: function(aData, textStatus){
                    if(aData.success){
                                                
                    }else{
                        $.jstree.rollback(data.rlbk);
                        alert(aData.response1);
                    }
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    alert("system error, your result will be rollback!");
                    $.jstree.rollback(data.rlbk);
                }
            });

            
            
            
        })
        ;
        
        
        
        
        // for upload medias        
/*      var manualuploader = $('#manual-fine-uploader').fineUploader({
            request: {
                endpoint: 'uploadFileByQQ'
            },
            autoUpload: false,
            text: {
                uploadButton: '<i class="icon-plus icon-white"></i> Select Files'
            },
            debug: true
        });
          
        $('#triggerUpload').click(function() {
            manualuploader.fineUploader('uploadStoredFiles');
        });
 */     
        // end for upload medias        
        
        
        // more file to upload
        $(".moreFileUpload").click(function(e){
            $(".fileList").append("<div class='fileInput'>Select a file to upload : <input type='file' name='files'/> <button class='removeFileInput'>Remove</button><br/></div>")
            e.preventDefault();
        })
        // remove upload input
        $(".removeFileInput").live("click", function(){
            $(this).parents(".fileInput").remove();
        })
        
        
    })

    
    
    
    // for upload media
    function supportAjaxUploadWithProgress() {
        return supportFileAPI() && supportAjaxUploadProgressEvents() && supportFormData();
        
        // Is the File API supported?
        function supportFileAPI() {
            var fi = document.createElement('INPUT');
            fi.type = 'file';
            return 'files' in fi;
        };
        
        // Are progress events supported?
        function supportAjaxUploadProgressEvents() {
            var xhr = new XMLHttpRequest();
            return !! (xhr && ('upload' in xhr) && ('onprogress' in xhr.upload));
        };
        
        // Is FormData supported?
        function supportFormData() {
            return !! window.FormData;
        }
    }   
    
    // Actually confirm support
    if (supportAjaxUploadWithProgress()) {
        // Ajax uploads are supported!
        // Change the support message and enable the upload button
        var notice = document.getElementById('support-notice');
        var uploadBtn = document.getElementById('upload-button-id');
        notice.innerHTML = "Your browser supports HTML uploads. Go upload something!";
        uploadBtn.removeAttribute('disabled');
        
        // Init the Ajax form submission
        initFullFormAjaxUpload();
        
        // Init the single-field file upload
        //initFileOnlyAjaxUpload();
    }
    
    function initFullFormAjaxUpload() {
        var form = document.getElementById('mediaUploadForm-id');
        form.onsubmit = function() {
            // FormData receives the whole form
            var formData = new FormData(form);
            
            // We send the data where the form wanted
            var action = form.getAttribute('action');
            
            // Code common to both variants
            sendXHRequest(formData, action);
            
            // Avoid normal form submission
            return false;
        }
    }
    
    function initFileOnlyAjaxUpload() {
        var uploadBtn = document.getElementById('upload-button-id');
        uploadBtn.onclick = function (evt) {
            var formData = new FormData();
            
            // Since this is the file only, we send it to a specific location
            var action = '/upload';
            
            // FormData only has the file
            var fileInput = document.getElementById('file-id');
            var file = fileInput.files[0];
            formData.append('our-file', file);
            
            // Code common to both variants
            sendXHRequest(formData, action);
        }
    }   
    // Once the FormData instance is ready and we know
    // where to send the data, the code is the same
    // for both variants of this technique
    function sendXHRequest(formData, uri) {
        // Get an XMLHttpRequest instance
        var xhr = new XMLHttpRequest();
        
        // Set up events
        xhr.upload.addEventListener('loadstart', onloadstartHandler, false);
        xhr.upload.addEventListener('progress', onprogressHandler, false);
        xhr.upload.addEventListener('load', onloadHandler, false);
        
        // Set up request
        xhr.open('POST', uri, true);
        
        // Fire!
        xhr.send(formData);
        xhr.addEventListener('readystatechange', onreadystatechangeHandler, false);
    }
    
    // Handle the start of the transmission
    function onloadstartHandler(evt) {
        var div = document.getElementById('upload-status');
        div.innerHTML = 'Upload started!';
        // replace all input field with loader icon.
        $(".fileList").html("<img src='/img/basic/ajax-loader.gif'>");
    }
    
    // Handle the end of the transmission
    function onloadHandler(evt) {
        var div = document.getElementById('upload-status');
        div.innerHTML = 'Upload successful!';
        $("#upload-progress").html("");
        $(".fileList").html("<div class='fileInput'>Select a file to upload : <input type='file' name='files'/> <button class='removeFileInput'>Remove</button><br/></div>")

    }
    
    // Handle the progress
    function onprogressHandler(evt) {
        var div = document.getElementById('upload-progress');
//      var percent = evt.loaded/evt.total*100;
        
        if (evt.lengthComputable) {
            var percent = Math.round((evt.loaded / evt.total) * 100);
            div.innerHTML = 'Progress: ' + percent + '%';
            
            if(percent>0.8){
                var statusdiv = document.getElementById('upload-status');
                statusdiv.innerHTML = "Server process ...";
            }
        }
        
    }
    
    // Handle the response from the server
    function onreadystatechangeHandler(evt) {
        //console.log("enter onreadystatechangeHandler");
        //console.log("this.readyState: "+this.readyState);
        var status = null;
        
        try {
            status = evt.target.status;
        }
        catch(e) {
            return;
        }
        
        if (status == '200' && evt.target.responseText) {
            var result = document.getElementById('upload-result');
            //result.innerHTML = '<p>The server saw it as:</p><pre>' + evt.target.responseText + '</pre>';
            
            if(this.readyState==3){
                result.innerHTML = "Server exception happens, you can try to refresh the page and upload again!";
                
            }else if(this.readyState==4){
                var jsonObj = eval("("+evt.target.responseText+")");
                mediatreeCreate(jsonObj);
                
//                result.innerHTML = resultString;
            }
            
        }
    }   
    
    
    
    
    
    
    </script>








</div>