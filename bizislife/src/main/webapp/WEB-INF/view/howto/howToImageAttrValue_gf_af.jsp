Group's array and attribute's array are FALSE, which means only 
one group "<<groupname>>" and one attribute "<<attrname>>". You can 
just copy next lines into the jsp view to get the image:

<img 
   src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}&size=-1' 
   title='${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}' >

Note: size options: "-1" for original photo, 
      "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, 
      "200" for thumbnail photo width 200, "600" for thumbnail photo width 600. 