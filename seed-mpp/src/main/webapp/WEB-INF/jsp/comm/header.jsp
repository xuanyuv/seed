<%@ page pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" scope="session"/>

<!DOCTYPE HTML>
<html>
<head>
    <title>半步多管理平台</title>
    <meta charset="UTF-8">
    <link rel="icon" href="${ctx}/img/logo.ico" type="image/x-icon">
    <link rel="stylesheet" href="${ctx}/css/basic.css"/>
    <link rel="stylesheet" href="${ctx}/css/main.css"/>
    <script src="${ctx}/js/jquery-1.11.3.min.js"></script>
    <script src="${ctx}/js/pubs.js"></script>
    <script src="${ctx}/js/common.js"></script>
</head>
<body>
<div class="c_main_l">
    <div class="c_logo"><img height="35" src="${ctx}/img/logo.png"/></div>
    <ul class="c_menu">
        <li ${currentMenu eq 'menu_sys' ? 'class="on"' : ''}>
            <a href="#"><span>业务管理</span><i></i></a>
            <div>
                <a href="javascript:alert('暂未开放');"><span>一站到底</span></a>
                <a href="javascript:alert('暂未开放');"><span>员工之家</span></a>
                <a href="javascript:alert('暂未开放');"><span>幸运刮奖</span></a>
            </div>
        </li>
        <li ${currentMenu eq 'menu_fans' ? 'class="on"' : ''}>
            <a href="${ctx}/fans/list"><span>粉丝管理</span><i></i></a>
        </li>
        <li ${currentMenu eq 'menu_mpp' ? 'class="on"' : ''}>
            <a href="#"><span>微信设置</span><i></i></a>
            <div>
                <a href="${ctx}/view?url=mpp/mpmenu"><span>公众菜单</span></a>
                <a href="${ctx}/mpp/user/info"><span>公众号资料</span></a>
                <a href="${ctx}/mpp/reply/common/get"><span>通用的回复</span></a>
                <a href="${ctx}/mpp/reply/follow/get"><span>关注后回复</span></a>
                <a href="${ctx}/mpp/reply/keyword/list"><span>关键字回复</span></a>
            </div>
        </li>
    </ul>
</div>
<div class="c_main_r">
    <div class="c_topBar">
        <p class="p1"><i></i><span class="fm2">您好：${user.username}，欢迎访问半步多平台，现在是：<fmt:formatDate value="<%=new Date()%>" pattern="yyyy年MM月dd日 E"/></span></p>
        <p class="p2 fm2">
            <a href="${ctx}/view?url=password" class="mr_20"><i class="i_man"></i><span class="va_m">修改密码</span></a>
            <a href="${ctx}/user/logout"><i class="i_sw"></i><span class="va_m">退出</span></a>
        </p>
    </div>