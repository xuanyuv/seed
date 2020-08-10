<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="UTF-8">
<title>微信支付</title>
<%--
<script src="//cdn.bootcdn.net/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="//res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
--%>
<script>
function onBridgeReady(){
    WeixinJSBridge.invoke(
        'getBrandWCPayRequest',
        ${wxpayData},
        function(res){
            if(res.err_msg == 'get_brand_wcpay_request:ok'){
                //使用以上方式判断前端返回，微信团队郑重提示：res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠
                alert('支付成功');
            }
        }
    );
}
if(typeof WeixinJSBridge == 'undefined'){
    if(document.addEventListener){
        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
    }else if(document.attachEvent){
        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
    }
}else{
    onBridgeReady();
}
</script>
</head>
</html>
<%--
发起一个微信支付请求
wx.chooseWXPay({
    timestamp: 0, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
    nonceStr: '', // 支付签名随机串，不长于 32 位
    package: '', // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
    signType: '', // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
    paySign: '', // 支付签名
    success: function (res) {
        // 支付成功后的回调函数
    }
});
备注：prepay_id 通过微信支付统一下单接口拿到，paySign 采用统一的微信支付 Sign 签名生成方法，注意这里 appId 也要参与签名，appId 与 config 中传入的 appId 一致，即最后参与签名的参数有appId, timeStamp, nonceStr, package, signType。
--%>