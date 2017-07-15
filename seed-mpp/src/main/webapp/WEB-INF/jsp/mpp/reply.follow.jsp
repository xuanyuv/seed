<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
var id = 0;
$(function(){
    id = "${empty replyInfo.id ? 0 : replyInfo.id}";
});
function saveOrUpdate(){
    if(isEmpty($("#content").val())){
        $.promptBox("回复内容不能为空", "#ffb848");
        return;
    }
    if($("#content").val().length > 1024){
        $.promptBox("回复的文本内容不能超过1024", "#ffb848");
        return;
    }
    $.post("${pageContext.request.contextPath}/mpp/reply/follow/upsert",
        {id:id, content:$("#content").val()},
        function(data){
            if(0 == data.code){
                id = data.data.id;
                $.promptBox("更新成功", "green");
            }else{
                $.promptBox(data.msg, "#ffb848");
            }
        }
    );
}
</script>

<div class="c_nav">
    <div class="ti">关注后回复</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table input-->
    <table class="tab_in2" width="100%">
        <tr class="ti"><th colspan="2"><span class="fl">编辑消息</span></th></tr>
        <tr>
            <th>回复消息：</th>
            <td><textarea id="content" name="content" style="height:300px;">${replyInfo.content}</textarea></td>
        </tr>
        <tr>
            <th>&nbsp;</th>
            <td><a class="btn_g mr_20" href="javascript:saveOrUpdate();">保&nbsp;存</a></td>
        </tr>
    </table>
    <!--/Table input-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>