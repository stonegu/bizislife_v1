<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page contentType="text/html; charset=UTF-8" %>


<div class="howtoPage">
	<div>
		<h2><a id="howtoIndex" class="bookmark">Index</a></h2>
		<ul>
			<li>
				<a href="#howtoProdData">How to create product data</a>
				<ul>
					<li>
						<a href="#createProdDataStep1">Step one: new a product template (module)</a>
					</li>
					<li>
						<a href="#createProdDataStep2">Step two: design product template (module)</a>
					</li>
					<li>
						<a href="#createProdDataStep3">Step three: use product template</a>
					</li>
					<li>
						<a href="#createProdDataStep4">Step four: populate product data</a>
					</li>
					<li>
						<a href="#createProdDataStep5">Step five: create views for product</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#howtoPageContent">How to create page content</a>
				<ul>
					<li>
						<a href="#pageContentStep1">Step one: new a regular module</a>
					</li>
					<li>
						<a href="#pageContentStep2">Step two: design content module</a>
					</li>
					<li>
						<a href="#pageContentStep3">Step three: instance the module</a>
						
					</li>
				</ul>
			</li>
			<li>
				<a href="#howtoPageLayout">How to create page layout (structure)</a>
				<ul>
					<li>
						<a href="#howtoContentWay1">Way 1: Default module setup</a>
					</li>
					<li>
						<a href="#howtoContentWay2">Way 2: Schedule container's display</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#howtoPublish">How to publish page</a>
			</li>
			<li>
				<a href="#moreForPage">More things you can do for page</a>
			</li>
			<li>
				<a href="#howtoDomain">How to setup domain</a>
			</li>
			<li>
				<a href="#howtoPermission">How to setup permission</a>
				<ul>
					<li>
						<a href="#permissionWay1">Way 1: setup permission on 'Everyone' group</a>
					</li>
					<li>
						<a href="#permissionWay2">Way 2: setup permission on private group</a>
					</li>
					<li>
						<a href="#permissionWay3">Way 3: setup permission on public group</a>
					</li>
					<li>
						<a href="#permissionWay4">Way 4: setup permission on user</a>
					</li>
					<li>
						<a href="#howPermissionWork">How permission works</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#group">Group</a>
			</li>
			<li>
				<a href="#howtoSharing">How to Sharing</a>
			</li>
			<li>
				<a href="#api">API</a>
			</li>
			<li>
				<a href="#newsfeedMessage">Newsfeed and message</a>
			</li>
			<li>
				<a href="#examplePhotoGallery">Example to create photo gallery page</a>
			</li>
			<li>
				<a href="#howtoBugReport">How to report bug</a>
			</li>
		</ul>
	</div>
	<div class="howToCreateProductData paragraph">
		<h2><a id="howtoProdData" class="bookmark">How to create product data</a></h2>
		
		<div class="step1 steps paragraph">
			<h3><a id="createProdDataStep1" class="bookmark">Step one: new a product template (module)</a></h3>
			<p>
				You can new a product template in Entity/Modules page, you can create template (module) under any folder, even under root folder 'Modules'.<br/>
				Note: You need to select 'OK' when you see the popup. 'OK' to create a product template (module), 'Cancel' to create a regular module. <a href="#howtoPageContent">(Detail for Regular Modul)</a>
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_newProductTemplate1.png" title="new a product template">
				<img width="100%" alt="new a product template" src="/img/pageContent/howto_newProductTemplate1.png">
			</a>
		</div>
		<div class="step2 steps paragraph">
			<h3><a id="createProdDataStep2" class="bookmark">Step two: design product template (module)</a></h3>
			<p>
				You can design template by just dragging & clicking.<br/>
				note: template is also called module in the system.<br/>
				tips: you also can get product template from other company. check <a href="#howtoSharing">How to sharing</a><br/>
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_designProductTemplate1.png" title="sample to design product template">
				<img width="100%" alt="sample to design product template" src="/img/pageContent/howto_designProductTemplate1.png">
			</a>
			
			<p>
				Each template should have one default view related. The view is just JSP page where you can retrieve all your product data based on template designed.
				You can just click <img alt="info icon" src="/img/vendor/web-icons/information-white.png"> icon beside value to get detail information for how to.
			</p>
			<p>
				Note: you don't need to create default view if you just like to hold product data in Bizislife. You can use API to retrieve product data in JSON format.
			</p>
			
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_writeJspForProduct1.png" title="how to create default view">
				<img width="100%" alt="how to create default view" src="/img/pageContent/howto_writeJspForProduct1.png">
			</a>
		</div>
		<div class="step3 steps paragraph">
			<h3><a id="createProdDataStep3" class="bookmark">Step three: use product template</a></h3>
			<p>You can use product template on category level or on product level.</p>
			<h4>Category level</h4>
			<p>
				Category can have sub-categories, and sub-category can have its sub-categories. There has no limitation that how many levels categories you can have.
				You can set template on any level of category, all new products under (no need directly under) category will try to find closest category 
				that has template defined. If find result is yes, new product will use the template defined in category, otherwise, new product will ask to select a 
				product template.
			</p>
			<p>
				You also can populate some default information in the template where category used, like category image, which can be used in category view.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_selectProductModuleForCategory.png" title="set template for category">
				<img width="100%" alt="set template for category" src="/img/pageContent/howto_selectProductModuleForCategory.png">
			</a>
			
			<h4>Product level</h4>
			<p>
				You can select template for product, you also can overwrite the product template inherited from category.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_selectProductModuleForProduct.png" title="set template for product">
				<img width="100%" alt="set template for product" src="/img/pageContent/howto_selectProductModuleForProduct.png">
			</a>
		</div>
		<div class="step4 steps paragraph">
			<h3><a id="createProdDataStep4" class="bookmark">Step four: populate product data</a></h3>
			<p>
				Any product with template defined is actually an instance of the template. The product has all the data structures that defined in template.
				What product need to modify is just couple things, like value, title, image. <br/>
				Note: all fields with gear icon beside are editable.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_instanceProduct.png" title="instance the product template">
				<img width="100%" alt="instance the product template" src="/img/pageContent/howto_instanceProduct.png">
			</a>
		</div>
		<div class="step5 steps paragraph">
			<h3><a id="createProdDataStep5" class="bookmark">Step five: create views for product</a></h3>
			<p>
				Product will use the default view defined in template. But product also can have it's customized views, and you can schedule the views.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_writeJspForProductView1.png" title="create views for product">
				<img width="100%" alt="create views for product" src="/img/pageContent/howto_writeJspForProductView1.png">
			</a>
		</div>
		
	</div>
	
	<div class="howToCreatePageContent paragraph">
		<h2><a id="howtoPageContent" class="bookmark">How to create page content</a></h2>
		<p>
			Page can be separated in multiple sections, but if you like one section one page, it's also OK for the system. For example,
			page can have header section, footer section, left navigation section, and middle content section. Each section is actually a 
			container, different containers to hold different contents. What I am describing here is how to create page content for one section (or one container).
		</p>
		<p>
			Page contents can be different, but if you design properly, some different contents can share the same structure. For example, image for orgnization's logo, 
			and image for your personal profile, these are different images, but all belong to image attribute. Another example is photo gallery. You can design a photo gallery 
			structure (I call it module) with multiple attribures, like image attribute to hold photo, text attribute to hold description, link attribute to hold photo's detail.
			After you design this photo gallery module, you can use this module on different place (same page or different pages).
		</p>
		<div class="step1 steps paragraph">
			<h3><a id="pageContentStep1" class="bookmark">Step one: new a regular module</a></h3>
			<p>
				Almost same as design product template I described above. The differences are:
				<ul>
					<li>You create a regular module <img alt="regular module icon" src="/img/vendor/web-icons/puzzle_blue_16x16.png"> instead of product module <img alt="product module icon" src="/img/vendor/web-icons/puzzle_green_16x16.png"></li>
					<li>You have more element choices than you create a product module</li>
					<li>regular module can't be used for product template (module)</li>
				</ul>
			</p>
			<p>
				You can new a regular module in Entity/Modules page, you can create module under any folder, even under root folder 'Modules'.<br/>
				Note: You need to select 'Cancel' when you see the popup. 'OK' to create a product template (module) <a href="#howtoProdData">(Detail for product Modul)</a>, 'Cancel' to create a regular module.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_newProductTemplate1.png" title="create a regular module">
				<img width="100%" alt="create a regular module" src="/img/pageContent/howto_newProductTemplate1.png">
			</a>
		</div>
		<div class="step2 steps paragraph">
			<h3><a id="pageContentStep2" class="bookmark">Step two: design content module</a></h3>
			<p>
				Almost same as design product template.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_designRegularModule1.png" title="design regular module">
				<img width="100%" alt="design regular module" src="/img/pageContent/howto_designRegularModule1.png">
			</a>
			<p>
				Each module should have one default view related. The view is just JSP page where you can retrieve all your data based on module designed.
				You can just click 'info' button beside value to get detail information for how to.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_writeJspForRegularModule1.png" title="how to create default view">
				<img width="100%" alt="how to create default view" src="/img/pageContent/howto_writeJspForRegularModule1.png">
			</a>
		</div>
		<div class="step3 steps paragraph">
			<h3><a id="pageContentStep3" class="bookmark">Step three: instance the module (template)</a></h3>
			<p>
				Like products are instances for product module, you also can have multiple instances for regular module. 
				For example, you create an image module, which can hold one image. If you have a page that needs to display 10 different images in different 
				places, you don't need to create 10 image modules, you just need to generate 10 image instance from the image module. All you need to change is 
				just the images.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_instanceRegularModule.png" title="instance the module">
				<img width="100%" alt="instance the module" src="/img/pageContent/howto_instanceRegularModule.png">
			</a>
			<p>
				Each instance can have it's own views, if default view from module doesn't fit the instance need. You also can schedule the view based on time an priority.
			</p>
			<p>Next picture shows there have 2 views for instance 'photo gallery 2.</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_writeJspForInstance1.png" title="views for instance">
				<img width="100%" alt="views for instance" src="/img/pageContent/howto_writeJspForInstance1.png">
			</a>
			<p>
				Next picuture shows that you can set one instance view as a default view, 
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_instanceDefaultView.png" title="instance default view">
				<img width="100%" alt="instance default view" src="/img/pageContent/howto_instanceDefaultView.png">
			</a>
			<p>
				or even schedule the views based on time and priority.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_scheduleViewForInstance.png" title="schedule views for instance">
				<img width="100%" alt="schedule views for instance" src="/img/pageContent/howto_scheduleViewForInstance.png">
			</a>
			
		</div>
		
	</div>
	
	<div class="howToCreatePageLayout paragraph">
		<h2><a id="howtoPageLayout" class="bookmark">How to create page layout (structure)</a></h2>
		<p>
			Page can have multiple sections(containers), I will call container instead of section from now on.
			Container also can have multiple containers, and so on. <br/>
			For example: page has top container, and top container can have banner container and login container.<br/>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_designPageLayout1.png" title="container can have containers">
				<img width="100%" alt="container can have containers" src="/img/pageContent/howto_designPageLayout1.png">
			</a>
			<br/>
			As you can see from the above image, container can inside another container, just like container on top of another container. 
			The toppest containers are actually to hold the contents. 
		</p>
		<p>
			Next is a example to create a complex layout.
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_createComplexLayout1.png" title="complex Layout Example">
				<img width="100%" alt="complexLayoutExample" src="/img/pageContent/howto_createComplexLayout1.png">
			</a>
		</p>
	</div>
	
	<div class="howToScheduleContentForContainer paragraph">
		<h2><a id="howtoSetContent" class="bookmark">How to set content into container</a></h2>
		<p>
			Page layout is just a structure of the page, there has no any contents inside instead of empty 'div'.
			There have two ways to add contents into container, 'default module' setup or schedule the module and instance.
		</p>
		
		<div class="way1 ways paragraph">
			<h3><a id="howtoContentWay1" class="bookmark">Way 1: Default module setup</a></h3>
			<p>
				You can set the default module for container. The container will display the view from default module's setup.
				The problem of this approach is that all containers will display same content if multiple containers all use same default module. 
				But this is also very useful when you like to share you page design for other companies but you don't know what's the content these company like to put. 
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_setDefaultModuleForContainer.png" title="default module setup for container">
				<img width="100%" alt="default module setup for container" src="/img/pageContent/howto_setDefaultModuleForContainer.png">
			</a>
		</div>
		
		<div class="way2 ways paragraph">
			<h3><a id="howtoContentWay2" class="bookmark">Way 2: Schedule container's display</a></h3>
			<p>
				Company likes to display different contents in different time periods, like Christmas, Thanks giving, Mother's day.
				Since you can create n number of modules, and 1 module can have m number of instance, this mean you can schedule 
				what's module you like to display in one time period, and also can do more schedules on what's the instance you like to 
				display in one time period.<br/>
				Note: You only can schedule the display on top container.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_scheduleForContainer1.png" title="schedule the content for container">
				<img width="100%" alt="schedule the content for container" src="/img/pageContent/howto_scheduleForContainer1.png">
			</a>
			<p>
				There has one exception when you like container to show product information. You can select any product modules (templates) for the schedule, 
				there has no difference to select product module 1 or product module 2, system only needs to know that this container is for product information only.
			</p>
		</div>
	</div>
	
	<div class="howToPublishPage paragraph">
		<h2><a id="howtoPublish" class="bookmark">How to publish page</a></h2>
		<p>
			After you create page layout, you can publish page by just click.
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_publishPage.png" title="publish page">
			<img alt="publish page" src="/img/pageContent/howto_publishPage.png">
		</a>
	</div>
	
	<div class="moreThingsForPage paragraph">
		<h2><a id="moreForPage" class="bookmark">More things you can do for page</a></h2>
		<p>
			<ul>
				<li>You can write page CSS, or even upload CSS or JavaScript files for pages</li>
				<li>You can give page a special link name instead of system auto generated name</li>
				<li>You can set page head info and title</li>
			</ul>
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_pageMoreThings1.png" title="page more things">
			<img alt="page more things" src="/img/pageContent/howto_pageMoreThings1.png">
		</a>
	</div>
	
	<div class="setupDomainsForOrg paragraph">
		<h2><a id="howtoDomain" class="bookmark">How to setup domain</a></h2>
		<p>
			You can hook all you pages to your own down by setup domain information in Bizislife. You will be asked to 
			paste a system generated string into your domain's manage company.
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_domainSetup.png" title="setup domain for org">
			<img width="100%" alt="setup domain for org" src="/img/pageContent/howto_domainSetup.png">
		</a>
	</div>
	
	<div class="howtoPermission paragraph">
		<h2><a id="howtoPermission" class="bookmark">How to setup permission</a></h2>
		<p>
			Suppose your company have multiple department to handle different products, like shoes department handles shoes information, 
			cloth department handles cloth information. But shoes department can see cloth information, vise versa. 
			This situation need permission functions to step in.<br/>
			Another example, suppose your company is a web design company, you like to design and create pages for another company which is 
			also registered in Bizislife. With permission functions, you can create and test pages inside your company first, and set permissions 
			on pages to share with other companies.
		</p>
		<p>
			There have four ways to setup permissions, set permission on Everyone group, or, set permission on inner group, or, set permission on outside group.
		</p>
		
		<div class="way1 ways paragraph">
			<h3><a id="permissionWay1" class="bookmark">Way 1: setup permission on 'Everyone' group</a></h3>
			<p>
				Every organization can find one Everyone group, which is default group belongs to Bizislife, and can be used for every organization. 
				All user is belong to Everyone group by default. Any permission set on Everyone group will give permission to everyone (users inside your organization and 
				users outside organization).<br/>
				One use case is, you created a fancy page, you like share (read or copy) your design to everyone.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_permissionEveryone1.png" title="set permission on everyone group">
				<img width="100%" alt="set permission on everyone group" src="/img/pageContent/howto_permissionEveryone1.png">
			</a>
		</div>
		
		<div class="way2 ways paragraph">
			<h3><a id="permissionWay2" class="bookmark">Way 2: setup permission on private group</a></h3>
			<p>
				There have two group type, private group <img alt="private group icon" src="/img/vendor/web-icons/home.png"> and public group <img alt="private group icon" src="/img/vendor/web-icons/globe.png">. Private group is the group can be joined by
				users inside organization. Public group is the group can be joined by users outside organization (other organizations' users).<br/>
				You can add permissions on private group, and all users in that group will be affected. 
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_permissionPrivateGroup1.png" title="add permission for private group">
				<img width="100%" alt="add permission for private group" src="/img/pageContent/howto_permissionPrivateGroup1.png">
			</a>
		</div>
		
		<div class="way3 ways paragraph">
			<h3><a id="permissionWay3" class="bookmark">Way 3: setup permission on public group</a></h3>
			<p>
				You can set permissions on public groups too.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_permissionPublicGroup1.png" title="add permission for public groups">
				<img width="100%" alt="add permission for public groups" src="/img/pageContent/howto_permissionPublicGroup1.png">
			</a>
			<p>
				But how could you ask other person to join your group? When you click <img alt="set who can join the group" src="/img/vendor/web-icons/arrow-join.png"> icon,
				you will have a screen where you can set which organization can join the group, and how many accounts can join. System also generate a link, which you 
				can send to related person to join.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_askPersonJoinGroup1.png" title="set who can join public group">
				<img width="100%" alt="set who can join public group" src="/img/pageContent/howto_askPersonJoinGroup1.png">
			</a>
		</div>
		
		<div class="way4 ways paragraph">
			<h3><a id="permissionWay4" class="bookmark">Way 4: setup permission on user</a></h3>
			<p>
				As a admin user, you can set permissions for all users in your organization. See group section for the definition of admin user.
			</p>
			<a class="howtoPhotosColorbox" href="/img/pageContent/howto_permissionUser1.png" title="set permission for user">
				<img width="100%" alt="set permission for user" src="/img/pageContent/howto_permissionUser1.png">
			</a>
		</div>
		
		<div class="howPermissionWork paragraph">
			<h3><a id="howPermissionWork" class="bookmark">How permission works</a></h3>
			<p>
				Permissions can be merged, which mean you can setup multiple permissions on group and user, the final permission for user is the 
				merge result based on user's groups permissions and user's permissions.
			</p>
			<p>
				Merge rule:<br/> 
				rule 1: allow & allow = allow, allow & deney = deny. For example: group copy permission for page is allow, user's copy permission is deny, 
				the merge permission will be deny.<br/>
				rule 2: there have 4 permission types in the system: preview, read, copy, modify, and have 3 permission levels, 0, 1, 3. 
				Preview permission type is level 0, read permission type is level 1, copy and modify permission type is level 3. So the rule is:
				<ul>
					<li>
						higher level permission is allow or deny, but lower level permission not set (null), then all level permissions is allow or deny.<br/>
						For example:<br/> 
						Case 1: You only set copy allow (true) for product A, then preview and read permission for product A is allow (true) too.<br/>
						Case 2: You only set copy deny (false) for product A, then preview and read permission for product A is deny (false) also.
					</li>
					<li>
						higher level permission is allow, but lower level permission is deny, the all level of permissions is deny.<br/>
						For example:<br/>
						Product A's preview permission is deny, but product A's modify permission is allow, then product A's preview, read, modify, copy permission is deny too.
					</li>
					<li>
						higher level permission is deny, but lower level permission is allow, then the higher level permission is still deny, lower level permission is still allow.<br/>
						For example:<br/>
						Product A's copy permission is deny, product A's read permission is allow, then product A's copy permission is still deny, and product A's read permission is still allow.
					</li>
				</ul>
			</p>
		</div>
		
	</div>
	
	<div class="whatsGroup paragraph">
		<h2><a id="group" class="bookmark">Group</a></h2>
		<p>
			Any organization can have two type of group, private group <img alt="private group icon" src="/img/vendor/web-icons/home.png"> and public group <img alt="private group icon" src="/img/vendor/web-icons/globe.png">. <br/>
			Private group is the group can be joined by users inside organization. Public group is the group can be joined by users outside organization (other organizations' users).<br/>
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_joinPrivateGroup1.png" title="private group creation and join">
			<img width="100%" alt="private group creation and join" src="/img/pageContent/howto_joinPrivateGroup1.png">
		</a>
		<p>
			Create public group:
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_publicGroupCreate.png" title="public group creation">
			<img width="100%" alt="public group creation" src="/img/pageContent/howto_publicGroupCreate.png">
		</a>
		<p>
			Join public group:
		</p>
		<a class="howtoPhotosColorbox" href="/img/pageContent/howto_askPersonJoinGroup1.png" title="public group join">
			<img width="100%" alt="public group join" src="/img/pageContent/howto_askPersonJoinGroup1.png">
		</a>
		<p>
			Also, there have two default groups exist in every organization. SystemDefault group and Everyone group. <br/>
			SystemDefault group is the super admin group, any user join this group will get the power to setup anything by default. Be very careful to set users to join this group.<br/>
			Everyone group is the group belongs to Bizislife, every users will belong to this group by default. 
			You can set permission on this group, and let everyone have some kind of permission for your company's resources. Be careful! 
		</p>
	</div>
	
	<div class="howToSharing paragraph">
		<h2><a id="howtoSharing">How to Sharing</a></h2>
		<p>
			Detail is coming ...
		</p>
	</div>
	
	<div class="whatsApi paragraph">
		<h2><a id="api" class="bookmark">API</a></h2>
		<p>
			There have couple APIs that you can use, like API to retrieve product information in JSON, etc. More details is coming... 
		<p>
	</div>

	<div class="newsFeed paragraph">
		<h2><a id="newsfeedMessage" class="bookmark">Newsfeed and message</a></h2>
		<p>
			I am still working on that, the key idea is users can subscribe the topic they like to listen, and system will send newsfeed or message based on their subscription.
			Also, users can send message between them. 
		</p>
	</div>

	<div class="example1 paragraph">
		<h2><a id="examplePhotoGallery" class="bookmark">Example to create photo gallery page</a></h2>
		<p>
			I am still working on that, the key idea is users can subscribe the topic they like to listen, and system will send newsfeed or message based on their subscription.
			Also, users can send message between them. 
		</p>
		
		<div class="step1 paragraph">
			<h3>Step one: manage photos in gallery instance</h3>
			<a class="howtoPhotosColorbox" href="/img/pageContent/example_step1.png" title="create photo gallery step 1">
				<img width="100%" src="/img/pageContent/example_step1.png" alt="step 1" />
			</a>
		</div>
	
		<div class="step2 paragraph">
			<h3>Step two: write JSP & CSS to display photos in gallery instance</h3>
			<a class="howtoPhotosColorbox" href="/img/pageContent/example_step2.png" title="create photo gallery step 2">
				<img width="100%" src="/img/pageContent/example_step2.png" alt="step 2" />
			</a>
		</div>
	
		<div class="step3 paragraph">
			<h3>Step three: create page layout & schedule the content to display</h3>
			<a class="howtoPhotosColorbox" href="/img/pageContent/example_step3.png" title="create photo gallery step 3">
				<img width="100%" src="/img/pageContent/example_step3.png" alt="step 3" />
			</a>
		</div>
	
		<div class="step4 paragraph">
			<h3>Step four: define page CSS & JAVASCRIPT</h3>
			<a class="howtoPhotosColorbox" href="/img/pageContent/example_step4.png" title="create photo gallery step 4">
				<img width="100%" src="/img/pageContent/example_step4.png" alt="step 4" />
			</a>
		</div>
	
		<div class="step5 paragraph">
			<h3>Step five: publish the page & you will get</h3>
			<a class="howtoPhotosColorbox" href="/img/pageContent/example_step5.png" title="create photo gallery step 5">
				<img width="100%" src="/img/pageContent/example_step5.png" alt="step 5" />
			</a>
		</div>
		
	</div>

	<div class="newsFeed paragraph">
		<h2><a id="howtoBugReport" class="bookmark">How to report bug</a></h2>
		<p>
			You can click <img alt="bug report" src="/img/vendor/web-icons/bug--plus.png"> icon, which locates inside footer area to report bugs.
		</p>
	</div>

	
	<div style="width: 90%; text-align: right;">
		<a href="#howtoIndex" class="howToIndexBack">GoTop</a>
	</div>

</div>