<%@ page pageEncoding="UTF-8"%>

<jsp:include page="comm/header.jsp"/>

<link rel="stylesheet" href="${ctx}/js/wangEditor-2.1.22/css/wangEditor.min.css">
<style>
/*
重写这俩标签是由于
全局CSS定义了它们，导致wangEditor中加粗和倾斜字体时，没有效果
尽管源码查看，发现其实wangEditor已经给文字添加了相应b、i标签
i {font-style:italic !important;}
*/
b {font-weight:bold !important;}
</style>

<div class="c_nav">
    <div class="ti">公众菜单</div>
</div>
<!--Content-->
<div class="c_content">
    <form id="userBindForm">
        <table class="tab_head tab_in tab_list2" width="100%">
            <tr class="ti"><th colspan="2">公众信息</th></tr>
            <tr><th>公众号：</th><td><input class="inpte" type="text" id="mpno" name="mpno" value="${user.mpno}" maxlength="32"/></td></tr>
            <!-- 注意：textarea的值应该是被p标签包裹了的值，否则会出现wangEditor里面编辑文字时（比如加粗），文字自动换行的现象 -->
            <tr><th>公众名：</th><td style="width:90%"><textarea id="mpname" style="height:300px;"><p>这是初始化内容</p></textarea></td></tr>
        </table>
        <table class="tab_head tab_in tab_list2" width="100%">
            <tr class="ti"><th colspan="3">操作</th></tr>
            <tr><td class="txt_l"><a class="btn_g" href="javascript:submit();">绑定公众号</a></td></tr>
        </table>
    </form>
</div>
<!--/Content-->

<jsp:include page="comm/footer.jsp"/>

<script src="${ctx}/js/wangEditor-2.1.22/js/wangEditor.min.js"></script>
<script>
//获取元素
var textarea = document.getElementById('mpname');

//生成编辑器
var editor = new wangEditor(textarea);

//字体
editor.config.familys = [
    '宋体', '黑体', '楷体', '微软雅黑',
    'Courier New', 'Consolas'
];

//关闭菜单栏吸顶
editor.config.menuFixed = false;

//普通的自定义菜单
editor.config.menus = [
    'source',
    '|',
    'bold',
    'forecolor',
    'bgcolor',
    '|',
    'fontfamily',
    'fontsize',
    'head',
    'alignleft',
    'aligncenter',
    'alignright',
    '|',
    'link',
    'unlink',
    'table',
    'img'
];

//隐藏掉插入网络图片功能（该配置仅在配置了图片上传功能后才可用）
//editor.config.hideLinkImg = true;
//上传图片的配置（支持自定义参数）
editor.config.uploadImgUrl = '${ctx}/sample/wangEditor/uploadImg';
editor.config.uploadImgFileName = 'minefile';
editor.config.uploadParams = {
    username: 'jadyer',
    password: 'http://jadyer.cn/'
};

//创建编辑器
editor.create();

function submit(){
    //获取编辑器区域完整html代码
    //text()和formatText()方法得到的都是没有HTML标签的文本
    var html = editor.$txt.html();
    //赋值（使用富文本编辑器时，就不能再用传统的$("mpname").val("赋值的内容")赋值了）
    //editor.$txt.html("赋值的内容");
    $.post("${ctx}/sample/wangEditor/submit",
        {mpno:$('#mpno').val(), mpname:html},
        function(data){
            alert(data.msg);
        }
    );
}
</script>