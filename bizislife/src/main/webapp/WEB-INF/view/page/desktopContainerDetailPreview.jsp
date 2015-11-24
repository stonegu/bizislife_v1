<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="ct_fakeSystemName" class="container fakePrettyName">
    <c:import url="http://${bizhost}/getContainerModuleContent?pageid=fakeSystemName&moduleid=${contentInfoMap['moduleuuid']}&instanceid=${contentInfoMap['instanceuuid']}&viewid=${contentInfoMap['viewuuid']}&categoryid=${categoryid}&pageidx=${pageidx}&entityid=${entityid}&hostname=${hostname}"/>
</div>
