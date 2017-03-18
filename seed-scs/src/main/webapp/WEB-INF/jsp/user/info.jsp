<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script src="${ctx}/js/ajaxfileupload.js"></script>

<script>
/*
$(function(){
    $.post('${ctx}/notice/list',
        {pageNo:'${pageNo}'},
        function(data){
            if(0 == data.code){
                for(var i=0; i<data.data.content.length; i++){
                    //var statusCheck = $('input:radio[name="status"]').is(":checked");
                    //$("input:radio[name=status][value=" + data.data.status + "]").attr('checked','true');
                    var notice = data.data.content[i];
                    var tr = '<tr>';
                    tr += '<td><span>' + notice.title + '</span></td>';
                    tr += '<td><span>' + (notice.content.length > 32 ? notice.content.substring(0,32)+'...' : notice.content) + '</span></td>';
                    tr += '<td><span>' + notice.author + '</span></td>';
                    tr += '<td><span>' + (notice.target==0 ? '未登录用户' : data.data.content[i].target==1 ? '已登录用户' : '所有用户') + '</span></td>';
                    tr += '<td><span>' + (notice.status==0 ? '<span class="cf30 fw">无效</span>' : '<span class="cgre fw">有效</span>') +'</span></td>';
                    tr += '<td><span>' + notice.updateTime + '</span></td>';
                    tr += '<td>';
                    tr += '<a class="c09f mr_15" href="${ctx}/view?url=notice/get&id=' + notice.id + '">查看</a>';
                    tr += '<a class="c09f mr_15" href="${ctx}/view?url=notice/get&o=update&id=' + notice.id + '">编辑</a>';
                    tr += '<a class="c09f" href="javascript:updateStatus(' + (notice.status==0?1:0) + ',' + notice.id + ');">修改状态</a>';
                    tr += '</td>';
                    tr += '</tr>';
                    $("#noticeTable").append(tr);
                }
            }else{
                $.promptBox(data.msg, "#ffb848");
            }
        }
    );
});
*/
<!--
AjaxFileUpload简介
官网：http://phpletter.com/Our-Projects/AjaxFileUpload/
简介：jQuery插件AjaxFileUpload能够实现无刷新上传文件，并且简单易用，它的使用人数很多，非常值得推荐
注意：引入js的顺序（它依赖于jQuery）和页面中并无表单（只是在按钮点击的时候触发ajaxFileUpload()方法）
常见错误及解决方案如下
1)SyntaxError: missing ; before statement
  --检查URL路径是否可以访问
2)SyntaxError: syntax error
  --检查处理提交操作的JSP文件是否存在语法错误
3)SyntaxError: invalid property id
  --检查属性ID是否存在
4)SyntaxError: missing } in XML expression
  --检查文件域名称是否一致或不存在
5)其它自定义错误
  --可使用变量$error直接打印的方法检查各参数是否正确，比起上面这些无效的错误提示还是方便很多
-->
function uploadIcons(){
    //开始上传文件时显示一个图片,文件上传完成将图片隐藏
    //$("#loading").ajaxStart(function(){$(this).show();}).ajaxComplete(function(){$(this).hide();});
    //执行上传文件操作的函数
    $.ajaxFileUpload({
        //处理文件上传操作的服务器端地址（可以传参数，比如/sample/uploadImg?username=jadyer，已亲测可用）
        url:'${ctx}/sample/uploadImg/1024',
        secureuri:false,                          //是否启用安全提交，默认为false
        fileElementId:'id_imgData',               //文件选择框的id属性
        dataType:'text',                          //服务器返回的格式，可以是json或xml等
        error:function(data, status, e){          //服务器响应失败时的处理函数
            alert('图片上传失败');
        },
        success:function(data, status){           //服务器响应成功时的处理函数
            data = data.replace(/<pre.*?>/g, ''); //ajaxFileUpload会对服务器响应回来的text内容加上<pre style="...">text</pre>前后缀
            data = data.replace(/<PRE.*?>/g, '');
            data = data.replace("<PRE>", '');
            data = data.replace("</PRE>", '');
            data = data.replace("<pre>", '');
            data = data.replace("</pre>", '');    //本例中设定上传文件完毕后，服务端会返回给前台[0`filepath]
            if(data.substring(0, 1) == 0){        //0表示上传成功（后跟上传后的文件路径），1表示失败（后跟失败描述）
                $('#icons').val(data.substring(2));
                $('#id_img').attr("src", data.substring(2));
                alert('图片上传成功-->[' + data.substring(2) + ']');
            }else{
                alert(data.substring(2));
            }
        }
    });
}
function validateForm(){
    //var imgSrc = $('#id_img').attr("src");
	if(isEmpty($("#appid").val())){
		$.promptBox("请填入appid", "#ffb848");
	}else if(isEmpty($("#appsecret").val())){
		$.promptBox("请填入appsecret", "#ffb848");
	}else if(isEmpty($("#mpid").val())){
		$.promptBox("请填入公众号原始ID", "#ffb848");
	}else{
		return true;
	}
}
function submit(){
	if(validateForm()){
		alert("操作成功！！\r\n请于公众号回复\“你最牛逼\”完成绑定");
	}
}
</script>

<div class="c_nav">
	<div class="ti">个人资料</div>
</div>
<!--Content-->
<div class="c_content">
	<!--Table order list-->
	<table class="tab_head tab_in tab_list2" width="100%">
		<tr class="ti"><th colspan="2">个人信息</th></tr>
		<tr><th width="15%">用户名：</th><td>${not empty uname ? uname : user.username}</td></tr>
		<tr>
		    <th>平台类型：</th>
		    <td>
		        <c:if test="${fn:length(user.mptype) gt 32}">
                    ${fn:substring(user.mptype,0,32)}...
                </c:if>
                <c:if test="${fn:length(user.mptype) le 32}">
                    ${advice.content}
                </c:if>
		    </td>
		</tr>
		<tr><th>绑解状态：</th><td>${user.bindStatus eq 0 ? '<span class="cf30 fw">未绑定</span>' : '<span class="cgre fw">已绑定</span>'}</td></tr>
		<tr><th>绑解时间：</th><td><fmt:formatDate value="${user.bindTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
	</table>
	<!--/Table order list-->
	<form id="userBindForm">
		<table class="tab_head tab_in tab_list2" width="100%">
			<tr class="ti"><th colspan="2">公众信息</th></tr>
			<tr><th width="15%">URL：</th><td>${mpurl}</td></tr>
			<tr><th>Token：</th><td>${token}</td></tr>
			<tr><th>appid：</th><td><input class="inpte" type="text" id="appid" name="appid" value="${user.appid}" maxlength="32"/></td></tr>
			<tr><th>appsecret：</th><td><input class="inpte" type="text" id="appsecret" name="appsecret" value="${user.appsecret}" maxlength="64"/></td></tr>
			<tr><th>原始ID：</th><td><input class="inpte" type="text" id="mpid" name="mpid" value="${user.mpid}" maxlength="32"/></td></tr>
			<tr><th>公众号：</th><td><input class="inpte" type="text" id="mpno" name="mpno" value="${user.mpno}" maxlength="32"/></td></tr>
			<tr><th>公众名：</th><td><input class="inpte" type="text" id="mpname" name="mpname" value="${user.mpname}" maxlength="32"/></td></tr>
			<tr>
                <th>
                    <%--
                    1)<img>不属于<form>元素，故需隐藏域接收图片
                    2)取消<a>标签的点击效果，是因为实际点击到的是file域
                    3)file域不需要设置display:none，而是通过opacity:0让它完全透明，实际它是浮在<a>标签之上的
                    4)file域设置position:absolute后要给left:0、top:0，否则它不会吻合覆盖<a>标签导致点击按钮的时候点不到file域
                    --%>
                    <a class="btn_g" href="javascript:void(0);" style="position:relative;display:block;">
                        公众图标
                        <input type="file" id="id_imgData" onchange="javascript:uploadIcons();" name="imgData" style="position:absolute;left:0;top:0;width:100%;height:100%;z-index:999;opacity:0;"/>
                    </a>
                </th>
                <td>
                    <img id="id_img" src="${ctx}/img/qrcode.jpg" style="width:200px; height:100px;">
                    <input type="hidden" id="icons" name="icons" src="${ctx}/img/qrcode.jpg"/>
                </td>
            </tr>
		</table>
		<table class="tab_head tab_in tab_list2" width="100%">
			<tr class="ti"><th colspan="3">操作</th></tr>
			<tr><td class="txt_l"><a class="btn_g" href="javascript:submit();">绑定公众号</a></td></tr>
		</table>
	</form>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>