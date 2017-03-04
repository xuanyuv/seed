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