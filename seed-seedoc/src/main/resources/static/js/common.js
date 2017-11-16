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
 * 全局替换
 * @see 1.javascript中的replace()只会替换第一个匹配到的
 * @see 2.调用时要注意：'$'会被正则，若待替换的字符串含'$'，则应转义'\\$'
 * @see   console.log('abc@{ctxPath}===@{aaa}123'.replaceAll('@{', '\\@{'));
 * @see   console.log('abc${ctxPath}===${aaa}123'.replaceAll('\\${', '\\\${'));
 * @see   console.log('abc${ctxPath}===${aaa}123'.replaceAll(/[$]{/g, '\\\${'));
 * @return 替换后的字符串
 */
String.prototype.replaceAll = function(str, newStr, ignoreCase){
    //待替换字符串为空则不替换而原样返回
    if(isEmpty(str)){
        return this;
    }
    //判断待替换字符串是否为正则表达式
    if(!RegExp.prototype.isPrototypeOf(str)){
        return this.replace(new RegExp(str, (ignoreCase ? "gi": "g")), newStr);
    }else{
        return this.replace(str, newStr);
    }
}