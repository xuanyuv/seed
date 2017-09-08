<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page language="java" import="javacommon.util.safe.*" %>
<%@ include file="/commons/taglibs.jsp" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String MenuNo = request.getParameter("MenuNo");
int CurrentID =SafeUtils.getInt( request.getParameter("id"),0);
int IsView = SafeUtils.getInt(request.getParameter("isview"),0);
String returnurl = SafeUtils.getString(request.getParameter("returnurl"));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>${title}</title> 
	<meta http-equiv="pragma" content="no-cache">
	<!--create by donghc-->
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link href="lib/css/style.css" rel="stylesheet" type="text/css" />

	<!-- 通用框架 -->
	<script src="lib/js/My97DatePicker/WdatePicker.js" type="text/javascript" ></script>	
    <script src="lib/js/jqGrid/js/jquery-1.6.2.min.js" type="text/javascript" ></script> 
    <script src="lib/js/jquery.form.js" type="text/javascript"></script>

    <!-- 验证框架 --> 
    <link href="lib/js/jQueryValidationEngine/css/validationEngine.jquery.css"  rel="stylesheet" type="text/css"  />
    <script src="lib/js/jQueryValidationEngine/js/jquery.validationEngine.js" type="text/javascript"></script>
    <script src="lib/js/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js" type="text/javascript"></script>

    <script src="lib/js/json2.js" type="text/javascript"></script>
    <script src="lib/js/CM.js" type="text/javascript"></script>

  </head>
<body>
<form method="post" name="mainform" id="mainform">
	<input type="hidden" id="micet_returnurl" name="micet_returnurl" value="<%=returnurl%>" />
	<s:hidden id="id" name="id" />
	<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" valign="top">
				<table class="TabMain" BORDER="0" CELLPADDING="0" CELLSPACING="0">
					<tr>
						<td align="center" valign="top">
							<table width="100%" border="0">
								<tr class="TabMain">
									<td height="30" align="center">
										<span class="tab_title">${title}</span>
									</td>
								</tr>
							</table>
							<table width="100%" border="0" align="center" cellpadding="2" cellspacing="1" class="tab1">
								$!{SQL.DetailPageModel}
							</table>
						</TD>
					</TR>
					<TR>
						<TD height="40" align="center">
							<input name="bt_ok" id="bt_ok" type="button" class="b2_ok" onClick="f_save();" value="保存">
							<input name="bt_cancel" id="bt_cancel" type="button" class="b2_cancel" onClick="f_cancel();" value="返回">
						</TD>
					</TR>
				</TABLE>
			</td>
		</tr>
	</table>
</form>
	<script type="text/javascript">
	$(document).ready(function(){ 
		jQuery("#mainform").validationEngine({
			promptPosition:"topLeft"
			});
		f_loaded();
	});
	</script>
    <script type="text/javascript">
    	//相对路径
    	var rootPath = "<%= basePath %>";
        //当前ID
        var currentID = '<%= CurrentID %>';
        //是否新增状态
        var isAddNew = currentID == "" || currentID == "0" ;
        //是否查看状态
        var isView = <%=IsView %>;
        //是否编辑状态
        var isEdit = !isAddNew && !isView;

        //创建表单结构
        var mainform = $("#mainform");  
        var actionRoot = "";
        if(isEdit){
        	actionRoot =  rootPath + "Action/${className}/Modify.do";
            mainform.attr("action", actionRoot );
        }
        if (isAddNew) {
        	actionRoot =  rootPath + "Action/${className}/Add.do";
            mainform.attr("action", actionRoot );
        }
		function f_loaded() {
            if(!isView) return;
            //查看状态，控制不能编辑
            $("input,select,textarea",mainform).attr("readonly", "readonly");
            $("#bt_ok").hide();
        }
		function f_save() {
			if( mainform.validationEngine("validate") == true) {
	            CM.submitForm(mainform, function (data) {
	                var win = parent || window;
	                if (data.iserror) {  
	                    alert('错误:' + data.message);
	                }
	                else { 
	                    alert('保存成功');
	                    f_return();
	                }
	            });
			}
        }

        function f_return() {
			 if(window.parent!=null) {
				 safeclosewindow(); 
			 }
			 else {
				 if(mainform.micet_returnurl.value=="")
					 window.history.go(-1);
				 else 
					 window.location.href =mainform.micet_returnurl.value;
			 }
		 }
		 function safeclosewindow() { 
			 var retvalue = new Array();
			 if(window.parent!=undefined) { 
				 window.opener.f_reloadgrid();
			 } 
			 else { 
				  window.returnValue = retvalue;
			 } 
			 window.close();
		 }
		 /* 返回 */
		 function f_cancel(){
			 window.close();
		 }

  </script>
</body>
</html>

