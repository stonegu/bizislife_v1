Group's array is TRUE and attribute's array is FALSE, which means multiple 
groups "<<groupname>>" and each group has one attribute "<<attrname>>".
You can just copy next lines into the jsp view to get the values: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>']}" var="group" varStatus="groupIdx">
        <li>Group #${groupIdx.index}[name: <<groupname>>]'s attribute
            <ul>
                <li>name: ${group.attrs["<<attrname>>"][0].name}</li>
                <li>title: ${group.attrs["<<attrname>>"][0].title}</li>
                <li>value: ${group.attrs["<<attrname>>"][0].value}</li>
            </ul>
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
title:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][2].attrs["<<attrname>>"][0].title}<br/>
...<br/>
value:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].value}<br/>
${module.groups["<<groupname>>"][2].attrs["<<attrname>>"][0].value}<br/>
...<br/>