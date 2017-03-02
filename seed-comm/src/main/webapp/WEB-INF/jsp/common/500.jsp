<%@ page pageEncoding="UTF-8" isErrorPage="true"%>
<%@ page import="org.apache.commons.lang3.exception.ExceptionUtils"%>
<!DOCTYPE HTML>
<html>
<head>
    <title>INTERNAL-SERVER-ERROR</title>
    <meta charset="UTF-8">
</head>
<body>
<img src="${pageContext.request.contextPath}/img/500.jpg">
<!--
<%=ExceptionUtils.getStackTrace(null==exception ? new RuntimeException("no-exception") : exception)%>
-->
</body>
</html>