Group's array is FALSE and attribute's array is TRUE, which means only
one group "<<groupname>>" and mutiple attributes "<<attrname>>".
You can just copy next lines into the jsp view to get the images: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
Group[name: <<groupname>>]'s attributes:<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>'][0].attrs['<<attrname>>']}" 
        var="attr" varStatus="attrIdx">
        <li>Attribute[name: <<attrname>>] #${attrIdx.index}<br/>
            <img src='<<hostname>>/getphoto?id=${attr.value}&size=-1' title='${attr.title}' >
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}&size=-1' 
    title='${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}' >
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].value}&size=-1' 
    title='${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].title}' >
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][2].value}&size=-1' 
    title='${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][2].title}' >
<br/>
...<br/>

Note: size options: "-1" for original photo, 
      "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, 
      "200" for thumbnail photo width 200, "600" for thumbnail photo width 600. 