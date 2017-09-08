<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String MenuNo = request.getParameter("MenuNo");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>${title}</title> 
	<meta name="keywords" content="Bootstrap后台管理系统" />
	<meta name="description" content="Bootstrap后台管理系统" />
    <!--create by donghc-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
	<meta name="author" content="">
	
    <link href="style/bootstrap.min.css" rel="stylesheet"/>
    <link href="style/bootstrap-table.css" rel="stylesheet">
    <link href="style/custom.css" rel="stylesheet">
    <link href="lib/js/jQueryValidationEngine/css/validationEngine.jquery.css"  rel="stylesheet" type="text/css"  />
    

    <!-- 验证框架 --> 
    <script src="lib/js/jQueryValidationEngine/js/jquery.validationEngine.js" type="text/javascript"></script>
    <script src="lib/js/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js" type="text/javascript"></script>
    
    <script src="lib/js/jqGrid/js/jquery-1.6.2.min.js" type="text/javascript" ></script> 
    <script src="lib/js/jquery.form.js" type="text/javascript"></script>
    <script src="lib/js/CM.js" type="text/javascript"></script>
    <script src="lib/js/json2.js" type="text/javascript"></script>
	<!-- bootstarp -->
    <script src="js/jquery.2.1.4.min.js"></script>
    <script src="js/bootstrap.v3.3.4.min.js"></script>
    <script src="js/bootstrap-table.1.11.0.js"></script>
    <script src="js/bootstrap-table-zh-CN.1.11.0.js"></script>
    <!-- 自定义js -->
    <script src="js/common.js"></script>
    
    <script type="text/javascript"> 
    
    $(document).ready(function () {          
//        调用函数，初始化表格
          initTable();  
      }); 
    function initTable() {  
    		$('#mytab').bootstrapTable('destroy');  
            $('#mytab').bootstrapTable({
                url: "Action/${className}/GridList.do",//数据源
                dataField: "rows",//服务端返回数据键值 就是说记录放的键值是rows，分页时使用总记录数的键值为total
                search: false,//是否搜索
                pagination: true,//是否分页
                pageSize: 5,//单页记录数
 				pageNumber: 1,
                pageList: [5, 10, 20, 50],//分页步进值
                pagination: true, //在表格底部显示分页工具栏  
                striped: true, //使表格带有条纹
                sidePagination: "server",//服务端分页
                contentType: "application/x-www-form-urlencoded",//请求数据内容格式 默认是 application/json 自己根据格式自行服务端处理
                dataType: "json",//期待返回数据类型
                method: "post",//请求方式
                searchAlign: "left",//查询框对齐方式
                idField: "ID",  //标识哪个字段为id主键 
                sortName:"id",
                sortOrder:"Desc",
                //设置为undefined可以获取pageNumber，pageSize，searchText，sortName，sortOrder  
	            //设置为limit可以获取limit, offset, search, sort, order  
	            queryParamsType : "undefined",   
 	            queryParams: queryParams,
                searchOnEnterKey: false,//回车搜索
                showRefresh: false,//刷新按钮
                showColumns: false,//列选择按钮
                buttonsAlign: "left",//按钮对齐方式
                toolbar: "#toolbar",//指定工具栏
                toolbarAlign: "right",//工具栏对齐方式
                columns: [
                	$!{SQL.listPageModel}
                ],
                locale: "zh-CN",//中文支持,
                detailView: false
            });
    }
    
    

     function tableHeight() {
         return $(window).height() - 500;
     }
     /**
      * 列的格式化函数 在数据从服务端返回装载前进行处理
      * @param  {[type]} value [description]
      * @param  {[type]} row   [description]
      * @param  {[type]} index [description]
      * @return {[type]}       [description]
      */
     function infoFormatter(value, row, index){
     	if(row.status==1){
     		return "启用";
     	}else{
     		return "禁用";
     	}
     }
        
        
    function queryParams(params) {   //设置查询参数  
    	var objarr = [ 'id' ];//可以填写多个参数以数组的形式
   		var param = {
   		 	pageSize:params.pageSize,//单页记录数
 		 	pageNumber: params.pageNumber,
		 	sortName: params.sortName, //排序列名
		 	sortOrder: params.sortOrder,//排位命令（desc，asc） 
			where : JSON2.stringify(bulidFilterGroup(objarr))
   		};    
   		return param;                   
  	}
        
    
	//工具条事件
    function toolbarBtnItemClick(id) {
    	var iWidth = 500;
		var iHeight = 300;
		var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
		var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
		var winframe="width=" + iWidth + ", height=" + iHeight + ",top=" + iTop + ",left=" + iLeft + ",toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no,alwaysRaised=yes,depended=yes";
    	switch (id) {
	        case "add":
	    		var tmpurl = "${ctx}/Action/${className}/DetailPage.do";
	    		window.open(tmpurl,"弹出窗口", winframe);
	            break;
	        case "modify":
	            var selecteds = $('#mytab').bootstrapTable('getSelections'); 
	            if (selecteds.length==0) { alert('请选择操作项!'); return }
	            if (selecteds.length>1) { alert('不支持多个操作!'); return }
	            var tmpurl = "${ctx}/Action/${className}/DetailPage.do?id="+selecteds[0].id;
	    		window.open(tmpurl,"弹出窗口", winframe);
	            break;
	        case "delete":
	      	  	var selecteds = $('#mytab').bootstrapTable('getSelections'); 
	            if (selecteds.length==0) { alert('请选择操作项!'); return }
	            if(window.confirm('您确认要删除选择的这些项吗?')) {
	            	f_delete();
	            }
	            break;
	        case "view":
	            var selecteds = $('#mytab').bootstrapTable('getSelections'); 
	            if (selecteds.length==0) { alert('请选择操作项!'); return }
	            if (selecteds.length>1) { alert('不支持多个操作!'); return }
	            var selected = $("#gridlist").jqGrid('getRowData',selecteds[0]);
	            var tmpurl = "${ctx}/Action/${className}/DetailPage.do?id="+selecteds[0].id+"&isview=1";
	    		window.open(tmpurl,window,winframe);
	            break;
    	}
	}

	/* 删除 */
	function f_delete() {
		var selecteds = $('#mytab').bootstrapTable('getSelections');
		var ids='';
		for(var i=0;i<selecteds.length;i++) {
			if(i==0) {
               	ids += selecteds[i].id;  
            } else {
       		  	ids += (',' + selecteds[i].id);
       		}
		}
		$.ajax({
                 type: 'post',
                 url:'Action/${className}/Delete.do',
                 data: { ids: ids },
                 success: function () {
                 	 f_reloadgrid();
                     alert("删除成功!");
                 },
                 error: function (message) {
                     alert("错误"+message);
                 }
             });
	}
	/* 重新加载Grid */
	function f_reloadgrid(){
		initTable();
	}


	/* 重置搜索框 */
	function reset(){
		$("#advancedsearch>input").val("");
		$("#advancedsearch>select").val("");
		initTable();
	}

</script>
</head>
  <body >
	
	<div>
        <div>
            <div class="col-*-12">
            	<div style="position:absolute;display:inline;"  id="advancedsearch" name="advancedsearch"  >
		                <span>id：</span>
		                <input id="id" name="id" value="" type="text">
						
                </div>
            
                <div id="toolbar" style="float:right;">
                	<input class="btn btn-primary" id="" type="button" value="搜索"  onclick="initTable()"/> 
					<input class="btn btn-primary" id="" type="button" value="重置"  onclick="reset()"/> 
                    <s:if test="tbitems!=null">
			   			<s:iterator  id="item"  value="%{tbitems}">
			   				<input class="btn btn-primary" name="bt_<s:property value="#item.btnno" />" id="bt_<s:property value="#item.btnno" />" type="button" value="<s:property value="#item.btnname" />"  onclick="toolbarBtnItemClick('<s:property value="#item.btnno" />')"/> 		
			   			</s:iterator>
			   		</s:if>
                </div>
                <table id="mytab" class="table table-hover" ></table>
            </div>
        </div>
    </div>
	
</body>
</html>

