Group's array is TRUE and attribute's array is TRUE, which means multiple 
groups "<<groupname>>" and each group has multiple attributes "<<attrname>>".
You can just copy next lines into the jsp view to get the values: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>']}" var="group" varStatus="groupIdx">
        <li>Group #${groupIdx.index}[name: <<groupname>>]'s attributes
            <c:forEach items="${group.attrs['<<attrname>>']}" var="attr" varStatus="attrIdx">
                <ul>
                    <li><strong>name: ${attr.name} #${attrIdx.index}</strong></li>
                    <li>
                        <img src='<<hostname>>/getphoto?id=${attr.value}&size=-1' title='${attr.title}' >
                    </li>
                </ul>
            </c:forEach>
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
...
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].value}&size=-1' 
    title='${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].title}' >
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][1].value}&size=-1' 
    title='${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][1].title}' >
<br/>
...<br/>

Note: size options: "-1" for original photo, 
      "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, 
      "200" for thumbnail photo width 200, "600" for thumbnail photo width 600. 