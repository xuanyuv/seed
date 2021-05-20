<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mt" uri="/WEB-INF/mytag"%>
<center>
    <h1>Mp3列表</h1>
    歌曲总数：<font color="green">${totalCount}</font>
    当前页数：<font color="red">${currentPage}</font>/<font color="blue">${totalPage}</font>
    <br/>
    <br/>
    <table width="666" border="0" cellspacing="1" bgcolor="#000000">
        <tr align="center">
            <th bgcolor="#F0F0F0">歌曲编号</th>
            <th bgcolor="#F0F0F0">歌曲名称</th>
            <th bgcolor="#F0F0F0">歌手</th>
            <th bgcolor="#F0F0F0">词作者</th>
            <th bgcolor="#F0F0F0">歌曲大小</th>
            <th bgcolor="#F0F0F0">压缩后大小</th>
        </tr>
        <c:forEach items="${list}" var="mp3">
            <tr align="center">
                <td bgcolor="#F0F0F0">${mp3.id}</td>
                <td bgcolor="#F0F0F0">${mp3.name}</td>
                <td bgcolor="#F0F0F0">${mp3.singer}</td>
                <td bgcolor="#F0F0F0">${mp3.author}</td>
                <td bgcolor="#F0F0F0"><del>${mp3.size}</del></td>
                <td bgcolor="#F0F0F0">${mp3.size*0.8}</td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <%-- 下面就是使用自定义标签实现的分页效果 --%>
    <mt:page currPage="${currentPage}" totalPage="${totalPage}"
        path="${pageContext.request.contextPath}/servlet/Mp3"
        param="currentPage" hasSelect="false" hasTextField="true"/>
</center>