<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jadyer" uri="http://www.jadyer.com/tag/jadyer"%>

当前时间：<jadyer:dateTime/>，过去时间：<jadyer:dateTime pattern="yyyy年MM月dd日 HH:mm:ss" date="<%=new java.util.Date(109,11,19)%>"/>