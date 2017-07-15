<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
$(function(){
    if("update"=="${o}" || "add"=="${o}"){
        $("#getDiv").hide();
        $("#updateDiv").show();
    }
    if(""=="${o}" || "update"=="${o}"){
        $.get("${ctx}/mpp/reply/keyword/get/${id}",
            function(data){
                if(0 == data.code){
                    if("update" == "${o}"){
                        $("#type").find("option[value=" + data.data.type + "]").prop("selected", true);
                        $("#keyword").val(data.data.keyword);
                        $("#content").val(data.data.content);
                    }else{
                        $("#get_keyword").html(data.data.keyword);
                        $("#get_type").html(data.data.type==0 ? "文本" : data.data.type==1?"图文" : data.data.type==2?"图片" : data.data.type==3?"活动" : data.data.type==4?"转发到多客服":"未知");
                        $("#get_content").html(data.data.content);
                    }
                }else{
                    $.promptBox("系统繁忙或关键字id=${id}的信息不存在", "#ffb848");
                }
            }
        );
    }
});
function validateForm(){
    if(isEmpty($("#keyword").val())){
        $.promptBox("请输入关键字", "#ffb848");
    }else if($("#type").val() != 0){
        $("#type").attr("value", "0");
        $.promptBox("暂时只能回复文本", "#ffb848");
    }else if(isEmpty($("#content").val())){
        $.promptBox("请输入回复的文本内容", "#ffb848");
    }else if($("#content").val().length > 1024){
        $.promptBox("回复的文本内容不能超过1024", "#ffb848");
    }else{
        return true;
    }
}
function submit(){
    if(validateForm()){
        $.post("${ctx}/mpp/reply/keyword/upsert",
            $("#keywordForm").serialize(),
            function(data){
                if(0 == data.code){
                    location.href = "${ctx}/mpp/reply/keyword/list";
                }else{
                    $.promptBox("系统繁忙或重复的关键字", "#ffb848");
                }
            }
        );
    }
}
</script>

<div class="c_nav">
    <div class="ti">关键字回复</div>
</div>
<!--Content-->
<div id="getDiv" class="c_content">
    <!--Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="2">关键字信息</th></tr>
        <tr><th width="15%">关键字：</th><td id="get_keyword"></td></tr>
        <tr><th>回复类型：</th><td id="get_type"></td></tr>
        <tr><th>回复内容：</th><td id="get_content"></td></tr>
    </table>
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="3">操作</th></tr>
        <tr>
            <td class="txt_l"><a class="btn_r" href="javascript:history.back();">返回</a></td>
        </tr>
    </table>
    <!--/Table order list-->
</div>
<div id="updateDiv" class="c_content" style="display:none;">
    <!--Table order list-->
    <form id="keywordForm">
        <input type="hidden" name="category" value="2"/>
        <input type="hidden" name="id" value="${empty id ? 0 : id}"/>
        <table class="tab_in2" width="100%">
            <tr class="ti">
                <th colspan="2">关键字信息</th>
            </tr>
            <tr>
                <th width="15%">关键字：</th>
                <td><input class="inpte" type="text" id="keyword" name="keyword" maxlength="16"/></td>
            </tr>
            <tr>
                <th>回复类型：</th>
                <td>
                    <select id="type" name="type">
                        <option value="0">文本</option>
                        <option value="1">图文</option>
                        <option value="2">图片</option>
                        <option value="3">活动</option>
                        <option value="4">转发多客服</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>回复内容：</th>
                <td><textarea id="content" name="content" style="height:300px;"></textarea></td>
            </tr>
        </table>
        <table class="tab_head tab_in tab_list2" width="100%">
            <tr class="ti"><th colspan="3">操作</th></tr>
            <tr>
                <td class="txt_l"><a class="btn_g" href="javascript:submit();">保存</a></td>
            </tr>
        </table>
    </form>
    <!--/Table order list-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>