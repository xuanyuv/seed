<%@ page pageEncoding="UTF-8"%>
<%@ page import="com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder"%>
<%
out.println(WeixinTokenHolder.getWeixinAccessToken());
out.print(WeixinTokenHolder.getWeixinJSApiTicket());
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="UTF-8">
<title>JSSDKDemo</title>
<script src="//cdn.bootcdn.net/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="//res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script>
$(function(){
    $.post("${pageContext.request.contextPath}/weixin/helper/jssdk/sign",
        {appid:'', url:window.location.href.split("#")[0]},
        function(data){
            wx.config({
                debug: false,
                appId: data.appid,
                timestamp: data.timestamp,
                nonceStr: data.noncestr,
                signature: data.signature,
                jsApiList: ["chooseImage", "uploadImage", "downloadImage", "scanQRCode"]
            });
        }
    );
});
wx.error(function(res){
    alert("wx.config失败时会在这里打印错误信息-->" + res.errMsg);
});
var images = {
    localId: [],
    serverId: []
};
wx.ready(function(){
    wx.checkJsApi({
        jsApiList: ["chooseImage", "uploadImage", "downloadImage", "scanQRCode"],
        success: function(res){
            alert("这是接口支持性的校验结果-->" + JSON.stringify(res));
        }
    });
    document.querySelector("#chooseImage").onclick = function(){
        wx.chooseImage({
            sizeType: ["compressed"],
            sourceType: ["camera"],
            success: function(res){
                images.localId = res.localIds;
                alert("已选择" + res.localIds.length + "张图片");
                $("#mediaImg").attr("src", images.localId);
            }
        });
    };
    document.querySelector("#uploadImage").onclick = function(){
        if(images.localId.length == 0){
            alert("请先拍照");
            return;
        }
        var i = 0;
        var length = images.localId.length;
        images.serverId = [];
        function upload(){
            wx.uploadImage({
                localId: images.localId[i],
                success: function(res){
                    i++;
                    //alert("已上传：" + i + "/" + length);
                    images.serverId.push(res.serverId);
                    $("#mediaId").val(images.serverId);
                    if(i < length){
                        upload();
                    }
                },
                fail: function(res){
                    alert(JSON.stringify(res));
                }
            });
        }
        upload();
    };
    /**
     * 微信扫一扫
     */
    document.querySelector("#scanQRCode").onclick = function(){
        wx.scanQRCode({
            needResult: 1, //默认为0,扫描结果由微信处理,1则直接返回扫描结果
            scanType: ["qrCode","barCode"], //可以指定扫二维码还是一维码,默认二者都有
            success: function(res){
                alert("扫码结果为-->["+res.resultStr+"]");
            }
        });
    };
});
</script>
</head>
<body>
    <span>拍照或从手机相册中选图接口</span>
    <br/>
    <button id="chooseImage">我要拍照</button>
    <br/>
    <br/>
    <button id="uploadImage">我要上传</button>
    <br/>
    <br/>
    得到的media_id为<input type="text" id="mediaId">
    <br/>
    <br/>
    <img id="mediaImg" src="" width="160px" height="160px">
    <br/>
    <br/>
    <button id="scanQRCode">我要扫码</button>
</body>
</html>