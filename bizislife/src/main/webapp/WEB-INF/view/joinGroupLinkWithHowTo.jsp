<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="joinGroupLinkWithHowTo">


<!-- 
    				mv.addObject("groupid", group.getId());
    				mv.addObject("orguuid", org.getOrguuid());
    				mv.addObject("joinkey", ocj.getJoinkey());
 -->
	You can send below link to users in ${org.orgsysname} for the join: <br/>
	http://${url}/joinGroup/g/${groupid}/to/${org.orguuid}/jk/${joinkey}


</div>