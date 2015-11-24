Group's array and attribute's array are FALSE, which means only 
one group "<<groupname>>" and one attribute "<<attrname>>". You can 
just copy next lines into the jsp view to get the value:

title: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}
value: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}