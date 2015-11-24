<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="javax.jms.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ActiveMQ Test</title>
</head>
<body>
<p>Message received:</p>

<%
MapMessage message = (MapMessage) request.getAttribute("result");
String value = message.getString("msg");
%>

<%= value %>
</body>
</html>