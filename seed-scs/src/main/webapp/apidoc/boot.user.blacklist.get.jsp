<%@ page pageEncoding="UTF-8"%>
<jsp:include page="/apidoc/header.jsp"/>
<h1>黑名单查询.boot.user.blacklist.get</h1>
<ol type="1">
	<li>
		<h2>请求报文</h2>
		<table width="1260" border="0" cellspacing="1" bgcolor="#000000">
			<tr bgcolor="#8CB3E2">
				<th width="20%">名称</th>
				<th width="10%" align="center">类型</th>
				<th width="5%" align="center">必传</th>
				<th width="65%">说明</th>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>userName</td>
				<td align="center">VARCHAR2(16)</td>
				<td align="center">Y</td>
				<td>客户姓名</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>userPhone</td>
				<td align="center">CHAR(11)</td>
				<td align="center">Y</td>
				<td>客户手机号</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>userIdCard</td>
				<td align="center">VARCHAR2(18)</td>
				<td align="center">Y</td>
				<td>客户身份证号，15位或18位</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>period</td>
				<td align="center">NUMBER(6)</td>
				<td align="center">Y</td>
				<td>有效期，单位：小时</td>
			</tr>
		</table>
	</li>
	<li>
		<h2>响应报文</h2>
		<table width="1260" border="0" cellspacing="1" bgcolor="#000000">
			<tr bgcolor="#8CB3E2">
				<th width="20%">名称</th>
				<th width="10%" align="center">类型</th>
				<th width="5%" align="center">必传</th>
				<th width="65%">说明</th>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>status</td>
				<td align="center">CHAR(1)</td>
				<td align="center">Y</td>
				<td>是否黑名单：0--非黑名单，1--黑名单</td>
			</tr>
		</table>
	</li>
</ol>
<jsp:include page="/apidoc/footer.jsp"/>