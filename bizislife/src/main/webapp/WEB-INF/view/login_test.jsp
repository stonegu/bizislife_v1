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
		background: #eeeeee;
		background: -webkit-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -moz-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -o-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: -ms-linear-gradient(top, #eeeeee, #cccccc 100%);
		background: linear-gradient(top, #eeeeee, #cccccc 100%);
		background-attachment:fixed;
	}	
	
	#progress {
		height:2px;
		background:#444;
		bottom:30px;
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
		background:url(http://prinzhorn.github.io/skrollr/examples/images/bubbles.png) repeat 0 0;
	}
	
	#bg2 {
		z-index:49;
		background-image:url(http://prinzhorn.github.io/skrollr/examples/images/bubbles2.png);
	}
	
	#bg3 {
		z-index:48;
		background-image:url(http://prinzhorn.github.io/skrollr/examples/images/bubbles3.png);
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
	
		<div id="transform" data-100="transform:scale(1) rotate(0deg);opacity:1;" data-600="" data-700="transform:scale(5) rotate(3600deg);opacity:0;">
			<h2>transform</h2>
			<p>scale, skew and rotate the sh** out of any element</p>
		</div>
		
		
	
	
		<div id="properties" data-700="top:100%;" data-1200="top:0%;" data-2000="display:block;" data-2700="top:-100%;display:none;">
			<h2>all numeric properties</h2>
			<p>width, height, padding, font-size, z-index, blah blah blah</p>
		</div>
	
		<div id="easing_wrapper" data-0="display:none;" data-2900="display:block;" data-3900="background:rgba(0, 0, 0, 0);color[swing]:rgb(0,0,0);" data-4900="background:rgba(0,0,0,1);color:rgb(255,255,255);" data-9000="top:0%;" data-11000="top:-100%;">
			<div id="easing" data-2900="left:100%" data-3600="left:25%;">
				<h2>easing?</h2>
				<p>sure.</p>
				<p>let me dim the <span data-2900="" data-3900="color[swing]:rgb(0,0,0);" data-4900="color:rgb(255,255,0);">lights</span> for this one...</p>
				<p data-4900="opacity:0;font-size:100%;" data-5500="opacity:1;font-size:150%;">you can set easings for each property and define own easing functions</p>
			</div>
	
			<div class="drop" data-5500="left:15%;bottom:100%;" data-8500="bottom:0%;">linear</div>
			<div class="drop" data-5500="left:25%;bottom[quadratic]:100%;" data-8500="bottom:0%;">quadratic</div>
			<div class="drop" data-5500="left:35%;bottom[cubic]:100%;" data-8500="bottom:0%;">cubic</div>
			<div class="drop" data-5500="left:45%;bottom[swing]:100%;" data-8500="bottom:0%;">swing</div>
			<div class="drop" data-5500="left:55%;bottom[WTF]:100%;" data-8500="bottom:0%;">WTF</div>
			<div class="drop" data-5500="left:65%;bottom[inverted]:100%;" data-8500="bottom:0%;">inverted</div>
			<div class="drop" data-5500="left:75%;bottom[bounce]:100%;" data-8500="bottom:0%;">bounce</div>
		</div>
	
		<div id="download" data-8800="top[cubic]:100%;border-radius[cubic]:0em;background:rgb(0,50,100);border-width:0px;" data-11000="top:10%;border-radius:2em;background:rgb(190,230,255);border-width:10px;">
			<h2>the end</h2>
			<p>by the way, you can also animate colors (you did notice this, didn't you?)</p>
			<p><strong>Now get this thing on <a href="https://github.com/Prinzhorn/skrollr">GitHub</a> and spread the word, it's open source!</strong> <a href="https://twitter.com/share" class="twitter-share-button" data-url="http://prinzhorn.github.com/skrollr/" data-via="Prinzhorn">Tweet</a>
			<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script></p>
			<p>Check out more <a href="https://github.com/Prinzhorn/skrollr/tree/master/examples#examples">examples</a>.</p>
			<p>Handcrafted by <a href="https://twitter.com/Prinzhorn" class="twitter-follow-button" data-show-count="false">Follow @Prinzhorn</a>
			<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script></p>
		</div>
	
	
	</div>
	
	<div class="pagebottom">
		<c:import url="generalBottom.jsp"/>
	</div>
	
	<div id="scrollbar" data-0="top:0%;margin-top:2px;" data-end="top:100%;margin-top:-52px;"></div>
	
	<script type="text/javascript" src="/js/brands/skrollr/skrollr.min.js"></script>
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
    <script src="/javasecript/bgl.min.js" type="text/javascript" language="javascript"></script>

</body>
</html>
