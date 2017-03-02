<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--
使用该通用分页有三个约束条件
1.后台分页的方法中用于表示页码的参数名应为pageNo,可参考com.jadyer.sdk.demo.weixin.fans.FansController.java
2.request.setAttribute("page", fansPage);即返回给前台的分页对象名称应为page
3.<jsp:include page="/page.jsp?requestURI=${ctx}/fans/list"/>即引用时需传入requestURI
--%>

<script>
/**
 * 输入框中的this.value=this.value.replace(/\D/g,'')可保证只能输入零或正整数
 * 输入中文等其它字符时,$("#go").val()得到的是空的值(不是null)
 * 输入负数时,$("#go").val()得到的是对应的正数,比如输入-3,$("#go").val()得到的就是3
 */
function pageSubmit(pageNo){
	if(-1 == pageNo){
		pageNo = $("#go").val();
		if(isEmpty(pageNo) || isNotNumber(pageNo)){
			$.promptBox("请填写页数", "#ffb848");
			return;
		}
		if(pageNo > parseInt("${page.totalPages}")){
			$("#go").val("${page.totalPages}");
			$.promptBox("最大页数${page.totalPages}页", "#ffb848");
			return;
		}
		if(pageNo >= 1){
			pageNo = pageNo-1;
		}
	}
	$("#pageForm").attr("action", "${param.requestURI}?pageNo=" + pageNo);
	$("#pageForm").submit();
}
</script>

<!--Paging-->
<form id="pageForm" method="post">
	<div class="paging">
		<!-- <a href="javascript:pageSubmit(0)" class="curr">首页</a> -->
		<a href="javascript:pageSubmit(0)">首页</a>
		<c:choose>
			<c:when test="${page.number eq 0}">
				<a href="javascript:void(0);">上页</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:pageSubmit(${page.number-1})">上页</a>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${page.number eq page.totalPages-1}">
				<a href="javascript:void(0);">下页</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:pageSubmit(${page.number+1})">下页</a>
			</c:otherwise>
		</c:choose>
		<a href="javascript:pageSubmit('${page.totalPages-1}')">尾页</a>
		<span class="pl_10">
			<em class="va_m">跳转到</em>
			&nbsp;<input class="inpte" type="text" maxlength="3" id="go" onchange="this.value=this.value.replace(/\D/g,'')"/>
			&nbsp;<input type="button" class="btn" onclick="pageSubmit(-1)" value="GO">
			<b class="va_m pl_10">第&nbsp;${page.number+1}&nbsp;页，共&nbsp;${page.totalPages}&nbsp;页，共&nbsp;${page.totalElements}&nbsp;条</b>
		</span>
	</div>
</form>
<!--/Paging-->