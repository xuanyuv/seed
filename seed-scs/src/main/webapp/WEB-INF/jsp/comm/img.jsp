<%@ page pageEncoding="UTF-8"%>

<script type="text/javascript" src="${ctx}/js/jquery-1.11.3.min.js"></script>
<script>
$(function(){
	$("#img").attr("src", "${ctx}/sample/file/get?filePath=${param.path}");
});
</script>

<%--
图片放大浏览功能，用法如下
<a href="${ctx}/view?url=comm/img&path=图片地址" target="_blank">
    <img src="${ctx}/sample/file/get?filePath=图片地址" height="200px" width="300px">
</a>
--%>
<img id="img" width="1500" height="800">