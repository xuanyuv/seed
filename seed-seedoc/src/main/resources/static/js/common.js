/**
 * 判断是否为空
 * @see null对象和空格也会被认为是空
 * @return true--空,false--非空
 */
function isEmpty(str){
    return null==str || /^\s*$/.test(str);
}


/**
 * 判断是否为非数字
 * @see 2/0/-2均会被认为是数字
 * @see 空格也会被认为是数字,故建议调用该函数前先调用isEmpty()函数
 * @return true--非数字,false--数字
 */
function isNotNumber(num){
    return isNaN(num);
}


/**
 * 判断是否是移动设备
 */
function isMobile(){
    return navigator.userAgent.match(/iPhone|iPad|iPod|Android|android|BlackBerry|IEMobile/i) ? true : false;
}


/**
 * 全部替换
 */
String.prototype.replaceAll = function(str, newStr, ignoreCase){
    if(isEmpty(str)){
        return '';
    }
    if(!RegExp.prototype.isPrototypeOf(str)){
        return this.replace(new RegExp(str, (ignoreCase ? "gi": "g")), newStr);
    }else{
        return this.replace(str, newStr);
    }
}
String.prototype.replaceAll = function(str, newStr){
    return this.replaceAll(str, newStr, false);
}