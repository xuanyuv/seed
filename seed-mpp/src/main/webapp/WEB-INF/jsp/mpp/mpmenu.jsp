<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
$(function(){
    $.get("${pageContext.request.contextPath}/mpp/menu/getjson",
        function(data){
            if(0 == data.code){
                $("#RawJson").text(data.data);
            }else{
                $("#RawJson").text("读取自定义菜单JSON失败-->["+data.msg+"]");
            }
        }
    );
});

function deploy(){
    $.post("${ctx}/mpp/menu/create",
        {menuJson:$("#RawJson").val()},
        function(data){
            if(0 == data.code){
                $.promptBox("发布成功", "green");
            }else{
                $.promptBox(data.msg, "#ffb848");
            }
        }
    );
}
</script>

<div class="c_nav">
    <div class="ti">公众菜单</div>
</div>
<!--Content-->
<div class="c_content">
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="4">请注意</th></tr>
        <tr><td><span>1、自定义菜单最多包括3个一级菜单，每个一级菜单最多包含5个二级菜单。</span></td></tr>
        <tr><td><span>2、一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替。</span></td></tr>
        <tr><td><span>3、创建自定义菜单后，由于客户端缓存，需要24小时客户端才会展现出来。测试时可以尝试取消关注公众账号后再次关注，则可以看到创建后的效果。</span></td></tr>
        <tr>
            <td>
                <span><span style="color:red;">注：</span>以上限制来自<a href="http://mp.weixin.qq.com/wiki/13/43de8269be54a0a6f64413e4dfa94f39.html" target="_blank" style="color:blue;">微信或QQ公众平台，</a></span>
                <span>自定义菜单创建的JSON格式请访问<a href="http://mp.weixin.qq.com/wiki/13/43de8269be54a0a6f64413e4dfa94f39.html" target="_blank" style="color:blue;">微信或QQ公众平台开发者文档。</a></span>
            </td>
        </tr>
    </table>
    <!--Table order list-->
    <table class="tab_in2" width="100%">
        <tr class="ti"><th colspan="2">详细信息</th></tr>
        <tr>
            <th width="15%">菜单JSON：</th>
            <%--
            JSON格式化片段----------------------------------------start
            --%>
            <td>
                <link rel="stylesheet" href="${ctx}/js/codeformat/json/s.css"/>
                <script src="${ctx}/js/codeformat/json/c.js" id="codeformatJsonC" data="${ctx}/js/codeformat/json"></script>
                <textarea id="RawJson" style="height:800px;"></textarea>
                <div id="ControlsRow">
                    <input type="Button" class="btn_o mt_5" value="格式化" onclick="Process()"/>
                    <span id="TabSizeHolder">
                        缩进量
                        <select id="TabSize" onchange="TabSizeChanged()">
                            <option value="1">1</option>
                            <option value="2" selected="true">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                        </select>
                    </span>
                    <label for="QuoteKeys"><input type="checkbox" id="QuoteKeys" class="va_m" onclick="QuoteKeysClicked()" checked="true"/>引号</label>
                    &nbsp;
                    <a href="javascript:void(0);" onclick="SelectAllClicked()">全选</a>
                    &nbsp;
                    <span id="CollapsibleViewHolder"><label for="CollapsibleView"><input type="checkbox" id="CollapsibleView" class="va_m" onclick="CollapsibleViewClicked()" checked="true"/>显示控制</label></span>
                    <span id="CollapsibleViewDetail">
                        <a href="javascript:void(0);" onclick="ExpandAllClicked()">展开</a>
                        <a href="javascript:void(0);" onclick="CollapseAllClicked()">叠起</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(3)">2级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(4)">3级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(5)">4级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(6)">5级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(7)">6级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(8)">7级</a>
                        <a href="javascript:void(0);" onclick="CollapseLevel(9)">8级</a>
                    </span>
                </div>
                <br/>
                <div id="Canvas" class="Canvas"></div>
            </td>
            <%--
            JSON格式化片段----------------------------------------end
            --%>
        </tr>
    </table>
    <!--/Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="3">操作</th></tr>
        <tr><td class="txt_l"><a class="btn_g" href="javascript:deploy();">发布</a></td></tr>
    </table>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>