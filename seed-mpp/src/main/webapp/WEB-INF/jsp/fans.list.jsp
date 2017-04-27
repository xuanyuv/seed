<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">粉丝列表</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>性别</th>
            <th>国家</th>
            <th>省份</th>
            <th>城市</th>
            <th>关注</th>
            <th>关注时间</th>
        </tr>
        <c:forEach items="${page.content}" var="fans">
            <tr>
                <td><span><img alt="头像" src="${fans.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${fans.nickname}</span></td>
                <td><span>${fans.sex eq 1 ? '男' : fans.sex eq 2 ? '女' : '未知'}</span></td>
                <td><span>${fans.country}</span></td>
                <td><span>${fans.province}</span></td>
                <td><span>${fans.city}</span></td>
                <td>${fans.subscribe eq 0 ? '<span class="cf30 fw">未关注</span>' : '<span class="cgre fw">已关注</span>'}</td>
                <td><span>${fans.subscribeTime}</span></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="comm/page.jsp?requestURI=${ctx}/fans/list"/>
</div>
<!--/Content-->

<jsp:include page="comm/footer.jsp"/>