<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx_apidoc" value="${pageContext.request.contextPath}/apidoc" scope="session"/>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="UTF-8">
<title>开放平台——接口文档</title>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js"></script>
<style type="text/css">
body      {background-color:#CBE0C9; font-size:14px; font-family:Courier New, 宋体;}
a:link    {color:#0000FF; text-decoration:none;}
a:visited {text-decoration:none; color:#009900;}
a:hover   {text-decoration:none;}
a:active  {text-decoration:none;}
</style>
<script>
$(function(){
    //禁止右键点击
    $(document).bind("contextmenu",function(e){
        return false;
    });
});
</script>
</head>
<body>
<h1 align="center"><font color="#0000FF">开放平台——接口文档<sub><font color="FF0000">&nbsp;&nbsp;&nbsp;编辑：玄玉</font></sub></font></h1>
<hr size="2">
<table width="1300" border="1" cellspacing="0" bordercolor="#BDD2FB" bgcolor="#EDF3FE">
    <tr>
        <td width="120"><a href="${ctx_apidoc}/boot.file.upload.jsp">文件上传</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.loan.submit.jsp">申请单提交</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.loan.get.jsp">申请单查询</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.loan.agree.jsp">申请单协议</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.loan.report.download.jsp">申请单报表下载</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.loan.sign.jsp">申请单签约</a></td>
        <td width="100"><a href="${ctx_apidoc}/boot.contract.get.jsp">合同查询</a></td>
        <td width="10" rowspan="4" bgcolor="#C2C2FE">&nbsp;</td>
        <td width="120" align="center"><a href="${ctx_apidoc}/code.rule.jsp"><font color="#009900">接口规则</font></a></td>
        <td width="10" rowspan="4" bgcolor="#C2C2FE">&nbsp;</td>
        <td align="center"><a href="http://jadyer.cn/" target="_blank"><font color="#009900">我的博客</font></a></td>
    </tr>
    <tr>
        <td><a href="${ctx_apidoc}/boot.user.blacklist.get.jsp">黑名单查询</a></td>
        <td><a href="${ctx_apidoc}/boot.user.bindcard.jsp">绑定银行卡</a></td>
        <td><a href="${ctx_apidoc}/boot.product.list.jsp">产品列表查询</a></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td align="center"><a href="${ctx_apidoc}/code.status.jsp"><font color="#009900">附录A：状态码</font></a></td>
        <td width="120" align="center"><a href="http://jadyer.cn" target="_blank"><font color="#009900">轻松一刻</font></a></td>
    </tr>
    <tr>
        <td><a href="${ctx_apidoc}/boot.apidoc.h5.jsp">接口文档页</a></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td align="center"><a href="${ctx_apidoc}/code.book.jsp"><font color="#009900">附录B：字典码</font></a></td>
        <td align="center"><a href="https://github.com/jadyer" target="_blank"><font color="#009900">我的Github</font></a></td>
    </tr>
    <tr>
        <td><a href="${ctx_apidoc}/partner.notify.apply.jsp">合作侧审批通知</a></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td align="center"><a href="${ctx_apidoc}/code.resp.jsp"><font color="#009900">附录C：应答码</font></a></td>
        <td align="center"><a href="mailto:jadyer@yeah.net"><font color="#009900">给我发邮件</font></a></td>
    </tr>
</table>
<hr size="2">