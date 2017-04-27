<%@ page pageEncoding="UTF-8"%>
<jsp:include page="/apidoc/header.jsp"/>
<h1>附录A：状态码</h1>
<table width="1300" border="0" cellspacing="1" bgcolor="#000000">
    <tr bgcolor="#8CB3E2">
        <th width="20%" align="left">名称</th>
        <th width="10%">编码</th>
        <th width="70%" align="left">描述</th>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td rowspan="8">申请单状态</td>
        <td align="center">U</td>
        <td>正在处理<font color="green">&nbsp;&nbsp;&nbsp;（审核中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">R</td>
        <td>拒绝<font color="green">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（审核未通过）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">A</td>
        <td>通过<font color="green">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（审核通过，待确认合同）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">C</td>
        <td>风控取消<font color="green">&nbsp;&nbsp;&nbsp;（审核未通过）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">N</td>
        <td>签署<font color="green">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（合同已签署）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">J</td>
        <td>放弃<font color="green">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（已取消）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">Q</td>
        <td>客户主动取消<font color="green">（已取消）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">B</td>
        <td>被驳回<font color="green">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（待资料重传）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td rowspan="9">合同状态</td>
        <td align="center">A</td>
        <td>合同贷款结清<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（已结清）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">B</td>
        <td>合同逾期<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（已逾期）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">C</td>
        <td>已放款（未结清，未逾期），当前有应还款额<font color="#FF00FF">（还款中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">D</td>
        <td>已放款（已跑批），但当前无应还款额<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（还款中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">E</td>
        <td>放款成功但未跑批<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（处理中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">F</td>
        <td>放款联机交易处理中<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（处理中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">G</td>
        <td>开户但未放款<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（处理中）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">H</td>
        <td>开户未放款且合同已过期<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（已过期）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">I</td>
        <td>退货<font color="#FF00FF">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（已退货）</font></td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td rowspan="8">订单状态</td>
        <td align="center">0</td>
        <td>待审批</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">1</td>
        <td>审批通过</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">2</td>
        <td>审批未通过</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">3</td>
        <td>待支付</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">4</td>
        <td>已支付</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">5</td>
        <td>已取消</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">6</td>
        <td>已退货</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">7</td>
        <td>已首付</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td rowspan="4">RA状态</td>
        <td align="center">0</td>
        <td>待完善资料</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">1</td>
        <td>待激活</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">2</td>
        <td>正常</td>
    </tr>
    <tr bgcolor="#FFFFFF">
        <td align="center">3</td>
        <td>已冻结</td>
    </tr>
</table>
<jsp:include page="/apidoc/footer.jsp"/>