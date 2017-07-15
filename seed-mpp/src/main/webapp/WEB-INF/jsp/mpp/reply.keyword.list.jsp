<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
function deleteKeyword(id){
    if(confirm("确定删除此关键字么？\r\n删除后其对应的文本回复或图文回复都将失效！！")){
        $.get("${ctx}/mpp/reply/keyword/delete/"+id,function(data){
            if(0 == data.code){
                location.reload();
            }else{
                $.promptBox(data.msg, "#ffb848");
            }
        });
    }
}
</script>

<div class="c_nav">
    <div class="ti">关键字回复</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Title-->
    <div class="title txt_r">
        <a class="bgre va_m" href="${ctx}/view?url=mpp/reply.keyword&o=add">+新增关键字</a>
    </div>
    <!--/Title-->
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>关键字</th>
            <th>回复类型</th>
            <th>回复内容</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="reply">
            <tr>
                <td><span>${reply.keyword}</span></td>
                <td><span>${reply.type eq 0 ? '文本' : reply.type eq 1?'图文' : reply.type eq 2?'图片' : reply.type eq 3?'活动' : reply.type eq 4?'转发到多客服':'未知'}</span></td>
                <td>
                    <span>
                        <c:if test="${fn:length(reply.content) gt 32}">
                            ${fn:substring(reply.content,0,32)}...
                        </c:if>
                        <c:if test="${fn:length(reply.content) le 32}">
                            ${reply.content}
                        </c:if>
                    </span>
                </td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=mpp/reply.keyword&id=${reply.id}">查看</a>
                    <a class="c09f mr_15" href="${ctx}/view?url=mpp/reply.keyword&o=update&id=${reply.id}">编辑</a>
                    <a class="c09f" href="javascript:deleteKeyword('${reply.id}');">删除</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/mpp/reply/keyword/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>