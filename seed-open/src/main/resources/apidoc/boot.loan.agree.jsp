<%@ page pageEncoding="UTF-8"%>
<jsp:include page="/apidoc/header.jsp"/>
<h1>申请单协议.boot.loan.agree</h1>
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
                <td>applyNo</td>
                <td align="center">VARCHAR2(32)</td>
                <td align="center">Y</td>
                <td>开放平台申请单号</td>
            </tr>
            <tr bgcolor="#FFFFFF">
                <td>type</td>
                <td align="center">CHAR(1)</td>
                <td align="center">Y</td>
                <td>协议类型：1--贷款合同，2--合同重要提示，3--借款人告知书</td>
            </tr>
        </table>
    </li>
    <li>
        <h2>响应页面</h2>
        返回贷款协议及合同试算等信息的HTML5字符串，供合作方嵌入显示给未签约申请单的贷款用户查看，若发生无此申请单等异常情况则返回公共应答报文。
    </li>
</ol>
<jsp:include page="/apidoc/footer.jsp"/>