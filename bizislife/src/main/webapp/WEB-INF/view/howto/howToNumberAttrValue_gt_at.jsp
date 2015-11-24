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
                    <li>title: ${attr.title}</li>
                    <li>value: ${attr.value}</li>
                </ul>
            </c:forEach>
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
title:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].title}<br/>
...<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][1].title}<br/>
...<br/>
value:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].value}<br/>
...<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].value}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][1].value}<br/>
...<br/>