title: ${module.groups["<<groupname>>"][0].attrs["<<attrname>>"][0].title}
<br />
<c:if test="${!empty productTree}">
    <strong>current category info</strong>
    <br />
    <ul>
        <li>current category name: ${productTree.prettyName}</li>
        <li>current category systemName: ${productTree.systemName}</li>
        <li>current category type: ${productTree.type.code}</li>
        <li>total subCategories in current category:
            ${productTree.totalSubfolder}</li>
        <li>total products in current category:
            ${productTree.totalProducts}</li>
    </ul>
    <strong>one level down subCategories</strong>
    <br />
    <c:if test="${!empty productTree.subnodes}">
        <table border="1">
            <tr>
                <th>Name</th>
                <th>SystemName</th>
                <th>Type</th>
                <th>SubCate #</th>
                <th>Product #</th>
                <th>image</th>
            </tr>
            <c:forEach items="${productTree.subnodes}" var="subCate" varStatus="subCateIdx">
                <tr>
                    <td><a href="${subCate.url}">${subCate.prettyName}</a></td>
                    <td>${subCate.systemName}</td>
                    <td>${subCate.type.code}</td>
                    <td>${subCate.totalSubfolder}</td>
                    <td>${subCate.totalProducts}</td>
                    <td><img src="<<hostname>>/getphoto?id=${subCate.defaultImageSysName}&size=100"></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</c:if>
<div>
    <c:if test="${!empty pagination && !empty pagination.paginationNodes}">
        <p>pagination:</p>
        <c:forEach items="${pagination.paginationNodes}" var="node" varStatus="nodeIdx">
            <span class="page ${node.isCurrentNode()?'currentNode':''}">
                <a title="${node.title}" href="${node.url}">${node.prettyName}</a>
            </span>
        </c:forEach>
        <c:if test="${!empty pagination.viewAllUrl}">
            <span class="view all"> <a href="${pagination.viewAllUrl}">View All</a>
            </span>
        </c:if>
        <span>${pagination.extraInfo}</span>
    </c:if>
</div>