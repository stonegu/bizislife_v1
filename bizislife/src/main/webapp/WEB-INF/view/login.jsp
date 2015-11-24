<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<title>Home Page</title>

	<link rel="stylesheet" type="text/css" href="/js/brands/JQuery/jquery-ui-1.8.20/css/ui-lightness/jquery-ui-1.8.20.custom.css"/>

	<script type="text/javascript" src="/js/brands/JQuery/jquery-1.7.2.js" language="javascript"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/js/jquery-ui-1.8.20.custom.min.js"></script>
	
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.cookie.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/external/jquery.hotkeys.js"></script>
	<script type="text/javascript" src="/js/brands/JQuery/jquery-ui-1.8.20/development-bundle/ui/jquery.ui.position.js"></script>
	
	<link rel="stylesheet" href="/js/brands/colorbox-master/customizedcss/colorbox.css" type="text/css" media="screen" />
	<script type="text/javascript" src="/js/brands/colorbox-master/jquery.colorbox.js"></script>

    <link rel="stylesheet" type="text/css" href="/css/bizGlobal.css"/>
	
	<style type="text/css">
	
		.pagemiddle {
			text-align: center;
			position: relative;
			z-index: 60;
		}
		
		.pagemiddle .paragraph {
			padding: 30px 0 0 0;
		}

		.pagemiddle .paragraph .image{
			padding: 30px 0 0 0;
		}
		
		.pagemiddle .paragraph .image img{
			opacity:0.8;
			filter:alpha(opacity=80); /* For IE8 and earlier */
		}
		
		.keyWords{
			color: #154D86;
		}
		
		
		
		
		.skrollable {
			/*
			 * First-level skrollables are positioned relative to window
			 */
			position:fixed;
		
			/*
			 * Skrollables by default have a z-index of 100 in order to make it easy to position elements in front/back without changing each skrollable
			 */
			z-index:100;
		}
		
		.skrollr-mobile .skrollable {
			/*
				May cause issues on Android default browser (see #331 on GitHub).
			*/
			position:absolute;
		}
		
		.skrollable .skrollable {
			/*
			 * Second-level skrollables are positioned relative their parent skrollable
			 */
			position:absolute;
		}
		
		.skrollable .skrollable .skrollable {
			/*
			 * Third-level (and below) skrollables are positioned static
			 */
			position:static;
		}

	body {
		background: url("/img/basic/bg1.png") repeat scroll 0 0 rgba(0, 0, 0, 0);
/* 		background: -webkit-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -moz-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -o-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -ms-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: linear-gradient(top, #eeeeee, #cccccc 100%);
		background-attachment:fixed;
 */	
 		color: #373737;
 	}	
	
	.signupForFree {
	   background: url("/img/basic/bg5.png") repeat scroll 0 0 rgba(0, 0, 0, 0);
	}
	#progress {
		height:2px;
		background:#444;
		bottom:15px;
		z-index:200;
	}
	
	#scrollbar {
		position:fixed;
		right:2px;
		height:50px;
		width:6px;
		background:#444;
		background:rgba(0,0,0,0.6);
		border:1px solid rgba(255,255,255,0.6);
		z-index:300;
		border-radius:3px;
	}
	
	.skrollr-desktop #scrollbar {display:none;}

	#bg1, #bg2, #bg3 {
		z-index:50;
		top:0;
		left:0;
		width:100%;
		height:100%;
		background:url(/img/basic/background_cloud_3.png) repeat 0 0;
	}
	
	#bg2 {
		z-index:49;
		background-image:url(/img/basic/background_cloud_2.png);
	}
	
	#bg3 {
		z-index:48;
		background-image:url(/img/basic/background_cloud_4.png);
	}

	#transform {
		width:70%;
		left:50%;
		top:20%;
		margin-left:-35%;
		text-align:center;
		font-size:150%;
	
		.transform-origin(50%, 50%);
	}
	
	#properties {
		width:100%;
		height:100%;
		padding-top:10%;
		text-align:center;
	
		-webkit-box-sizing: border-box;
		-moz-box-sizing: border-box;
		box-sizing: border-box;
	}
	
	#easing_wrapper {
		width:100%;
		height:100%;
	}
	
	#easing {
		top:10%;
		width:50%;
		z-index:101;
	}
	
	.drop {
		background:#09f;
		font-weight:bold;
		padding:1em;
	}
	
	#download {
		width:80%;
		left:10%;
		height:80%;
		padding:3em;
		border:0 solid #222;
	
		-webkit-box-sizing: border-box;
		-moz-box-sizing: border-box;
		box-sizing: border-box;
	}

	</style>

</head>
<body>
	<div id="bg1" data-0="background-position:0px 0px;" data-end="background-position:-500px -10000px;"></div>
	<div id="bg2" data-0="background-position:0px 0px;" data-end="background-position:-500px -8000px;"></div>
	<div id="bg3" data-0="background-position:0px 0px;" data-end="background-position:-500px -6000px;"></div>

	<div id="progress" data-0="width:0%;background:hsl(200, 100%, 50%);" data-end="width:100%;background:hsl(920, 100%, 50%);"></div>


	<div class="pagetop">
		<c:import url="generalTop.jsp"/>
	</div>
	
	<div class="pagemiddle">
	
		<div class="summary paragraph" style="font-size: 3em; width: 70%; margin: 0px auto; font-weight: bold;">
			A platform help you to create, manage, share and post your product information.
		</div>
		
		<div class="moduleCreate paragraph">
			<h2>Create <span class="keyWords">customized product template</span> <img src="/img/vendor/web-icons/puzzle_green_16x16.png" alt="product module" /> with less input</h2>
			<p>You can design and create your customized product templates <img src="/img/vendor/web-icons/puzzle_green_16x16.png" alt="product module" /> by drag and drop. You also can config <img src="/img/vendor/web-icons/gear--pencil.png" alt="config" /> the template to minimize input mistakes.</p>
			<div class="image">
				<img width="60%" src="/img/pageContent/moduleCreate1.png" alt="module create" />
			</div>
		</div>
	
		<div class="customizedView paragraph">
			<h2>Write your customized <span class="keyWords">views</span> <img src="/img/vendor/web-icons/edit-image-right.png" alt="view" /></h2>
			<p>Bizislife gives you 100% control to create views <img src="/img/vendor/web-icons/edit-image-right.png" alt="view" /> by using data retriving helper <img src="/img/vendor/web-icons/information-white.png" alt="howto" /></p>
			<div class="image">
				<img width="60%" src="/img/pageContent/customizedView.png" alt="create customized views" />
			</div>
		</div>
	
		<div class="permissionSetup paragraph">
			<h2>Setup <span class="keyWords">permission</span> <img src="/img/vendor/web-icons/lock.png" alt="permission" /> on account and group for data management & sharing</h2>
			<p>Organization's admins can setup permissions <img src="/img/vendor/web-icons/lock.png" alt="permission" /> on accounts and groups for data management and sharing. 
			   Permission type includes: preview <img src="/img/vendor/web-icons/eye.png" alt="preview" />, 
			   read detail <img src="/img/vendor/web-icons/magnifier-zoom-actual-equal.png" alt="detail" />, 
			   modify <img src="/img/vendor/web-icons/gear.png" alt="modify" /> & copy <img src="/img/vendor/web-icons/copy_16x16.png" alt="copy" /></p>
			<div class="image">
				<img width="60%" src="/img/pageContent/permissionSetup1.png" alt="permission setup for account or group" />
			</div>
		</div>
	
		<div class="sharingData paragraph">
			<h2>Get <span class="keyWords">share</span> data from other organizations</h2>
			<p>Product data <img src="/img/vendor/web-icons/present.png" alt="product" />, 
			   web page <img src="/img/vendor/web-icons/layout-hf-3-mix.png" alt="page" /> and 
			   all templates (module) <img src="/img/vendor/web-icons/puzzle_blue_16x16.png" alt="module" /> <img src="/img/vendor/web-icons/puzzle_green_16x16.png" alt="module" /> can be cloned if data's owner give you the permission</p>
			<div class="image">
				<img width="60%" src="/img/pageContent/sharingData1.png" alt="sharing data by clone" />
			</div>
		</div>
		
        <div class="design paragraph">
            <h2>Design & manage <span class="keyWords">page layout</span> <img src="/img/vendor/web-icons/layout-select-content_transparency.png" alt="container" /> by drawing</h2>
            <p>Bizislife provides the tool to create & manage your page layout by mouse click & drawing</p>
            <div class="image">
                <img width="60%" src="/img/pageContent/pageLayoutDesign1.png" alt="page layout design" />
            </div>
            
        </div>
        
        <div class="schedule paragraph">
            <h2><span class="keyWords">Schedule</span> <img src="/img/vendor/web-icons/calendar_module.png" alt="schedule" /> content & view for display</h2>
            <p>Bizislife provides scheduling tool to schedule page's content and view based on date & priority</p>
            <div class="image">
                <img width="60%" src="/img/pageContent/scheduleContentView1.png" alt="schedule content and view for display" />
            </div>
        </div>
        
		<div class="signupForFree paragraph" style="padding: 30px 0; margin: 30px 0; border-bottom: 1px dashed #FFFFFF; border-top: 1px dashed #FFFFFF;">
			<h2>Sign up for <span class="keyWords">free</span></h2>
			<p>
				System is under testing! You can use below form to ask a free account to take a try.
			</p>
			<p>
				<form action="#" id="applicationForm">
					Contact Email: <input name="contactEmail" type="text" />
					<button type="button" class="domReady_appliSubmit">I like to try!</button>
				</form>
			</p>
		</div>
	
	</div>
	
	<div class="pagebottom">
		<c:import url="generalBottom.jsp"/>
	</div>
	
	<div id="scrollbar" data-0="top:0%;margin-top:2px;" data-end="top:100%;margin-top:-52px;"></div>
	
	
	
	
    <script src="/js/brands/skrollr/skrollr.min.js" type="text/javascript" language="javascript"></script>
    <script src="/javasecript/bgl.min.js" type="text/javascript" language="javascript"></script>
    
	<script type="text/javascript">
	var s = skrollr.init({
		edgeStrategy: 'set',
		easing: {
			WTF: Math.random,
			inverted: function(p) {
				return 1-p;
			}
		}
	});
	</script>    

</body>
</html>
