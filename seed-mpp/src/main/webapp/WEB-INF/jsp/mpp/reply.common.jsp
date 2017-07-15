<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">通用的回复</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table input-->
    <table class="tab_in2" width="100%">
        <tr class="ti">
            <th colspan="2">
                <span class="fl">
                    ${replyInfo.type eq 4 ? '自动转发多客服' : '<span style="color:red;">未知设定</span>'}
                </span>
            </th>
        </tr>
    </table>
    <!--/Table input-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>