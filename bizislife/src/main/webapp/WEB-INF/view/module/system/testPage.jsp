<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

	<head>
		<title>Home Page</title>
	</head>
	
	<body>
		<div>
			<c:import url="/getContainerModuleContent?containerSchedule=12345${(!empty currentPageParams)?'&'+currentPageParams:''}"/>
		</div>
		<div>
			<c:import url="/getContainerModuleContent?containerSchedule=abcde${(!empty currentPageParams)?'&'+currentPageParams:''}"/>
		</div>
	</body>
</html>
