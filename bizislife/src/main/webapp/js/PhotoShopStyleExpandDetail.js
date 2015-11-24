var psExpandDetail = {

	detailContainer : function(target, prop){
		var THIS = this;
		
		// default value
		this.tabSelected = 1; // default selected tab is the first one;
		this.totalTabs = 1; // default total tabs is 1, max number of tabs is 4
		this.title1 = 'title1 here'; // default title for 
		this.title2 = 'title2 here'; // default title for 
		this.title3 = 'title3 here'; // default title for 
		this.title4 = 'title4 here'; // default title for 
		
		// content
		this.content1 = 'waiting ...';
		// content
		this.content2 = 'waiting ...';
		// content
		this.content3 = 'waiting ...';
		// content
		this.content4 = 'waiting ...';

		
		this.leftOffset = 0;
		this.topOffset = 0;
		this.position = 'absolute';
		this.height = 100;
		this.width = 100;
		
		
		// some call back
		this.containerBeforeOpen = null;
		this.containerAfterOpen = null; // call back when container open
		this.containerClose = null; // call back when container closed
		

		// value from prop to change the default value
		for(var p in prop) {
			if(prop.hasOwnProperty(p)) {
				this[p] = prop[p];
				//console.log("property: "+p+" | "+prop[p]);
			}
		}
		
		var targetPosition = psExpandDetail.getElementPos(target);
		var x = targetPosition.left+(new Number(THIS.leftOffset));
		var y = targetPosition.top+(new Number(THIS.topOffset));
		
		this.setContent = function(tabIndex, ct){
			if(tabIndex==1){
				$(".psExpDetail .psContentContainer .pstab1_content").html(ct);
			}else if(tabIndex==2){
				$(".psExpDetail .psContentContainer .pstab2_content").html(ct);
			}else if(tabIndex==3){
				$(".psExpDetail .psContentContainer .pstab3_content").html(ct);
			}else if(tabIndex==4){
				$(".psExpDetail .psContentContainer .pstab4_content").html(ct);
			}
		}
		
		this.drawContainer = function(){
			
			// only one container in the page: remove container before open.
			hideContainer();
			dispatcheContainerBeforeOpen();
			
			var tabs = "";
			var tabContents = "";
			if(!this.totalTabs){
				this.totalTabs = 1;
			}
			if(!this.tabSelected){
				this.tabSelected = 1;
			}
			if(!this.title1){
				this.title1 = "title1 here";
			}
			if(!this.title2){
				this.title2 = "title2 here";
			}
			if(!this.title3){
				this.title3 = "title3 here";
			}
			if(!this.title4){
				this.title4 = "title4 here";
			}
			for(var i=1; i<=this.totalTabs; i++){
				tabs = tabs + '<div class="psDetailTitle" id="pstab'+i+'" style="background-color: #'
							+ (i==new Number(this.tabSelected)?'e7e7e7':'a6a6a6') 
							+'; border-right: 1px solid #424242; cursor: pointer; position: relative; float: left; padding: 0 4px; height: '
							+ (i==new Number(this.tabSelected)?'18':'17') 
							+'px; line-height: '
							+ (i==new Number(this.tabSelected)?'17':'15') 
							+'px;">'
							+ (i==1?this.title1:(i==2?this.title2:(i==3?this.title3:(i==4?this.title4:this.title1))))
							+ '</div>';
				tabContents = tabContents + '<div class="tabContent pstab'+i+'_content'
								+ (i==new Number(this.tabSelected)?'':' displaynone')
								+ '">'
								+ (i==1?this.content1:(i==2?this.content2:(i==3?this.content3:(i==4?this.content4:"waiting ..."))))
								+'</div>'
			}
			
			
			
			var cd = '<div class="psExpDetail" '
						+'style="background-color: black;'
						+'position: '+THIS.position+';'
						+'border: 1px solid;'
						+'height: '+THIS.height+'px;'
						+'width: '+THIS.width+'px;'
						+'top: '+y+'px; left: '+x+'px; '
						+'">'
						
						+'<div class="psMainbody" style="background-color: #e7e7e7; position: relative; width: '+THIS.width+'px; margin-top: 4px; height: '+((new Number(THIS.height))-4)+'px;">'
						+'<div class="psContentContainer" style="margin: 20px 4px 3px; float: left; overflow-y: auto; width: '+((new Number(THIS.width))-4-4)+'px; height: '+((new Number(THIS.height))-4-20-3)+'px;">'+tabContents+'</div>'
						+'</div>'
						
						+'<div class="psDetailhead" style="background-color: #949494; border-bottom: 1px solid #424242; position: absolute; top: 4px; width: '+THIS.width+'px; height: 17px;">'
						+tabs
						+'<div class="psDetailClose" style="position: relative; float: right; "><img src="/img/vendor/web-icons/grid_stack16x16.png" alt="close" class="closePsDetailIconImg" title="close"></div>'
						+'</div>'
						
						+'</div>'; 
			
			$('body').append($(cd));
			
			dispatchContainerAfterOpen();
			
			// bind close btn with click event;
			$(".closePsDetailIconImg").click(function(){
				hideContainer();
			})
			
			// bind switch title tabs
			$(".psDetailTitle").click(function(){
				$(".psDetailTitle").css({
					"background-color":"#a6a6a6",
					"height":"17px",
					"line-height":"15px"
				})
				$(".tabContent").addClass("displaynone");
				
				$(this).css({
					"background-color":"#e7e7e7",
					"height":"18px",
					"line-height":"17px"
				})
				var thisId = $(this).attr("id");
				$("."+thisId+"_content").removeClass("displaynone");
			})
		}
		
		this.hideContainer = function(){
			hideContainer();
		}
		
		function hideContainer(){
			$(".psExpDetail").remove();
			dispatchContainerClose();
		}
		
		function dispatcheContainerBeforeOpen(){
			if(THIS.containerBeforeOpen){
				var callback;
				if(typeof THIS.containerBeforeOpen ==='string'){
					callback = new Function(THIS.containerBeforeOpen);
				}else{
					callback = THIS.containerBeforeOpen;
				}
				callback.call(THIS);	
			}
		}
		
		function dispatchContainerAfterOpen(){
			if(THIS.containerAfterOpen){
				var callback;
				if(typeof THIS.containerAfterOpen ==='string'){
					callback = new Function(THIS.containerAfterOpen);
				}else{
					callback = THIS.containerAfterOpen;
				}
				callback.call(THIS);
			}
		}
		
		function dispatchContainerClose() {
			if (THIS.containerClose) {
				var callback;
				if (typeof THIS.containerClose === 'string') {
					callback = new Function (THIS.containerClose);
				} else {
					callback = THIS.containerClose;
				}
				callback.call(THIS);
			}
		}
		
	},
	
	getElementPos : function(e) {
		return $(e).offset();
	},
	
	removeContainer: function(e) {
		$(".psExpDetail").remove();
		
		dispatchContainerClose();
	}
}

//$(".closePsDetailIconImg").live("click", function(){
//	psExpandDetail.removeContainer();
//})