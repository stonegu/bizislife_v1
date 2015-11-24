Group's array and attribute's array are FALSE, which means only 
one group "<<groupname>>" and one attribute "<<attrname>>". You can 
just copy next lines into the jsp view to get the value:

href: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].href}<br/>
rel: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].rel}<br/>
target: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].target}<br/>
title: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}<br/>
linkValue: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].linkValue}<br/> 

Note: href's format should begin with "http://"