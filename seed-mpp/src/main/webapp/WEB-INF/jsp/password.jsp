<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="comm/header.jsp"/>

<script src="js/webtoolkit.md5.js"></script>
<script>
function submit(){
    if(isEmpty($("#oldPassword").val())){
        $.promptBox("原密码不能为空", "#ffb848");
        return;
    }
    if(isEmpty($("#newPassword").val())){
        $.promptBox("新密码不能为空", "#ffb848");
        return;
    }
    if($("#newPassword").val() == $("#oldPassword").val()){
        $.promptBox("新密码不能与原密码相同", "#ffb848");
        return;
    }
    if(isEmpty($("#newPasswordConfirm").val())){
        $.promptBox("确认密码不能为空", "#ffb848");
        return;
    }
    if($("#newPassword").val() != $("#newPasswordConfirm").val()){
        $.promptBox("确认密码不正确", "#ffb848");
        return;
    }
    $.post("${ctx}/mpp/user/password/update",
        {oldPassword:MD5($("#oldPassword").val()), newPassword:MD5($("#newPassword").val())},
        function(data){
            if(0 == data.code){
                $.promptBox("密码修改成功", "green");
            }else{
                $.promptBox(data.msg, "#ffb848");
            }
        }
    );
}
</script>

<div class="c_nav">
    <div class="ti">修改密码</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table order list-->
    <table class="tab_in2" width="100%">
        <tr class="ti"><th colspan="2">详细信息</th></tr>
        <tr><th width="15%">原密码：</th><td><input class="inpte" type="password" id="oldPassword" name="oldPassword" maxlength="16"/></td></tr>
        <tr><th>新密码：</th><td><input class="inpte" type="password" id="newPassword" name="newPassword" maxlength="16"/></td></tr>
        <tr><th>确认密码：</th><td><input class="inpte" type="password" id="newPasswordConfirm" name="newPasswordConfirm" maxlength="16"/></td></tr>
    </table>
    <!--/Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="3">操作</th></tr>
        <tr><td class="txt_l"><a class="btn_g" href="javascript:submit();">保存</a></td></tr>
    </table>
</div>
<!--/Content-->

<jsp:include page="comm/footer.jsp"/>