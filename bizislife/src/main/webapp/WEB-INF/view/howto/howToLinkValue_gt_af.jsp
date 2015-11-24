Group's array is TRUE and attribute's array is FALSE, which means multiple 
groups "<<groupname>>" and each group has one attribute "<<attrname>>".
You can just copy next lines into the jsp view to get the values: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>']}" var="group" varStatus="groupIdx">
        <li>Group #${groupIdx.index}[name: <<groupname>>]'s attribute
            <ul>
                <li>href: ${group.attrs["<<attrname>>"][0].href}</li>
                <li>rel: ${group.attrs["<<attrname>>"][0].rel}</li>
                <li>target: ${group.attrs["<<attrname>>"][0].target}</li>
                <li>title: ${group.attrs["<<attrname>>"][0].title}</li>
                <li>linkValue: ${group.attrs["<<attrname>>"][0].linkValue}</li>
            </ul>
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
href:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].href}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].href}<br/>
...<br/>
rel:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].rel}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].rel}<br/>
...<br/>
target:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].target}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].target}<br/>
...<br/>
title:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].title}<br/>
...<br/>
linkValue:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].linkValue}<br/>
${module.groups["<<groupname>>"][1].attrs["<<attrname>>"][0].linkValue}<br/>
...<br/>