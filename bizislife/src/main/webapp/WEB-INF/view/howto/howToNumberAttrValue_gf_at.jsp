Group's array is FALSE and attribute's array is TRUE, which means only
one group "<<groupname>>" and mutiple attributes "<<attrname>>".
You can just copy next lines into the jsp view to get the values: 

Way #1 to use JSTL's forEach loop:<br/>
==================================<br/>
Group[name: <<groupname>>]'s attributes:<br/>
<ul>
    <c:forEach items="${module.groups['<<groupname>>'][0].attrs['<<attrname>>']}" 
        var="attr" varStatus="attrIdx">
        <li>Attribute[name: <<attrname>>] #${attrIdx.index}
            <ul>
                <li>title: ${attr.title}</li>
                <li>value: ${attr.value}</li>
            </ul>
        </li>
    </c:forEach>
</ul>

Way #2 to use list's index#:<br/>
============================<br/>
title:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].title}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][2].title}<br/>
...<br/>
value:<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].value}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][1].value}<br/>
${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][2].value}<br/>
...<br/>
