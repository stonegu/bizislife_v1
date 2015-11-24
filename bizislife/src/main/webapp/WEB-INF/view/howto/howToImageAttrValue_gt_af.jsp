Group's array is TRUE and attribute's array is FALSE, which means multiple 
groups "<<groupname>>" and each group has one attribute "<<attrname>>".
You can just copy next lines into the jsp view to get the values: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>']}" var="group" varStatus="groupIdx">
        <li>Group #${groupIdx.index}[name: <<groupname>>]'s attribute<br/>
            <img src='<<hostname>>/getphoto?id=${group.attrs["<<attrname>>"][0].value}&size=-1' 
                title='${attGroup.attrs["<<attrname>>"][0].title}' >
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}&size=-1' 
    title='${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}' >
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].value}&size=-1' 
    title='${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].title}' >
<br/>
<img src='<<hostname>>/getphoto?id=${module.groups["<<groupname>>"][2].attrs["<<attrname>>"][0].value}&size=-1' 
    title='${module.groups["<<groupname>>"][2].attrs["<<attrname>>"][0].title}' >
<br/>

Note: size options: "-1" for original photo, 
      "50" for thumbnail photo width 50, "100" for thumbnail photo width 100, 
      "200" for thumbnail photo width 200, "600" for thumbnail photo width 600. 