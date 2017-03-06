<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" scope="session"/>

<!DOCTYPE HTML>
<html>
<head>
	<title>定时系统</title>
	<meta charset="UTF-8">
	<link href="${ctx}/css/common.css" rel="stylesheet"/>
	<script src="${ctx}/js/common.js"></script>
	<script src="${ctx}/js/jquery-1.11.3.min.js"></script>
</head>
<script>
/**
 * 新增定时任务时的表单校验
 */
function formValidate(){
	if(isEmpty($("#dynamicPassword").val())){
		alert("动态密码不能为空");
		return false;
	}
	if($("#dynamicPassword").val() == "请输入动态密码（动态密码索取请联系玄玉）"){
		alert("请输入动态密码");
		return false;
	}
	if(isEmpty($("#name").val())){
		alert("任务名称不能为空");
		return false;
	}
	if(isEmpty($("#url").val())){
		alert("任务URL不能为空");
		return false;
	}
	if(isEmpty($("#cron").val())){
		alert("任务Cron表达式不能为空");
		return false;
	}
	return true;
}

/**
 * 新增定时任务
 */
function addTask(){
	if(formValidate()){
		$.post("${ctx}/qss/add",
				{status:"0", concurrent:$("#concurrent").val(), name:$("#name").val(), group:$("#group").val(), url:$("#url").val(), cron:$("#cron").val(), desc:$("#desc").val(), dynamicPassword:$("#dynamicPassword").val()},
				function(jsonData){
					if(1000==jsonData.code){
						location.reload();
					}else{
						alert(jsonData.message);
					}
				}
		);
	}
}

/**
 * 删除定时任务
 */
function deleteTask(id){
	if(inputDynamicPassword()){
		if(confirm("确定删除此任务么？")){
			$.get("${ctx}/qss/delete/"+id+"/"+dynamicPassword,function(data){
				if(1000==data.code){
					location.reload();
				}else{
					alert(data.message);
				}
			});
		}
	}
}

/**
 * 立即执行定时任务
 */
function triggerJob(id){
	if(inputDynamicPassword()){
		if(confirm("确定立即执行此任务么？")){
			$.get("${ctx}/qss/triggerJob/"+id+"/"+dynamicPassword,function(data){
				if(1000==data.code){
					location.reload();
				}else{
					alert(data.message);
				}
			});
		}
	}
}

/**
 * 停止0/启动1/挂起2/恢复3定时任务
 */
function updateStatus(id, status){
	if(inputDynamicPassword()){
		$.get("${ctx}/qss/updateStatus?id="+id+"&status="+status+"&dynamicPassword="+dynamicPassword,function(data){
			if(1000==data.code){
				location.reload();
			}else{
				alert(data.message);
			}
		});
	}
}

/**
 * 更新定时任务Cron表达式
 */
function updateCron(id, cron){
	if(inputDynamicPassword()){
		var cron = prompt("请输入CronExpression", cron);
		if(cron){
			$.get("${ctx}/qss/updateCron?id="+id+"&cron="+cron+"&dynamicPassword="+dynamicPassword,function(data){
				if(1000==data.code){
					location.reload();
				}else{
					alert(data.message);
				}
			});
		}
	}
}

/**
 * 提示用户输入动态密码
 * @see http://tool.oschina.net/encrypt?type=2
 */
var dynamicPassword = null;
function inputDynamicPassword(){
	var _dynamicPassword = prompt("请输入动态密码", "动态密码索取请联系玄玉");
	if(!isEmpty(_dynamicPassword) && _dynamicPassword!="动态密码索取请联系玄玉"){
		dynamicPassword = _dynamicPassword;
		return true;
	}else{
		return false;
	}
}
</script>
<body>
<div align="center">
    <h1><span style="color:red;">定时系统(QSS--Quartz Scheduler System)</span></h1>
    <br>
	<table border="9">
		<tr>
			<th style="width:12%;">任务名</th>
			<th style="width:4%;">状态</th>
			<th style="width:4%;">并发</th>
			<th style="width:20%;">URL</th>
			<th style="width:10%;">Cron</th>
			<th style="width:11%;">下次触发时间</th>
			<th style="width:11%;">上次触发时间</th>
			<th style="width:18%;">操作</th>
		</tr>
		<c:forEach items="${taskList}" var="task">
			<tr>
				<td>${task.name}</td>
				<td>${task.status eq 1 ? '已启动' : task.status eq 2 ? '已挂起' : task.status eq 3 ? '已恢复':'已停止'}</td>
				<td>${task.concurrent eq 1 ? '允许' : '不允许'}</td>
				<td style="word-break:break-all;">${task.url}</td>
				<td>${task.cron}</td>
				<td><fmt:formatDate value="${task.nextFireTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${task.previousFireTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
					<c:if test="${task.status eq '0'}">
						<a href="javascript:updateStatus('${task.id}',1);">开启</a>
						挂起
						立即执行
					</c:if>
					<c:if test="${task.status eq '1'}">
						<a href="javascript:updateStatus('${task.id}',0);">停止</a>
						<a href="javascript:updateStatus('${task.id}',2);">挂起</a>
						<a href="javascript:triggerJob('${task.id}');">立即执行</a>
					</c:if>
					<c:if test="${task.status eq '2'}">
						停止
						<a href="javascript:updateStatus('${task.id}',3);">恢复</a>
						立即执行
					</c:if>
					<c:if test="${task.status eq '3'}">
						<a href="javascript:updateStatus('${task.id}',0);">停止</a>
						<a href="javascript:updateStatus('${task.id}',2);">挂起</a>
						<a href="javascript:triggerJob('${task.id}');">立即执行</a>
					</c:if>
					<a href="javascript:updateCron('${task.id}','${task.cron}');">更新Cron</a>
					<a href="javascript:deleteTask('${task.id}');">删除</a>
				</td>
			</tr>
		</c:forEach>
		<tr>
			<form id="addForm" method="post">
				<input type="hidden" name="status" value="0"/>
				<td><input type="text" name="name" id="name" size="16"/></td>
				<td>停止</td>
				<td>
					<select name="concurrent" id="concurrent">
							<option value="0" selected="selected">不允许</option>
							<option value="1">允许</option>
					</select>
				</td>
				<td><input type="text" name="url" id="url" size="32"/></td>
				<td><input type="text" name="cron" id="cron" value="0 0/10 * * * ?" size="12"/></td>
				<td colspan="2"><input type="text" name="dynamicPassword" id="dynamicPassword" value="请输入动态密码（动态密码索取请联系玄玉）" size="34"/></td>
				<td><input type="button" onclick="addTask()" value="保存"/></td>
			</form>
		</tr>
	</table>
</div>
</body>
</html>