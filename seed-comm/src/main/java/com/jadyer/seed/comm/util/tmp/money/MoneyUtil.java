package com.jadyer.seed.comm.util.tmp.money;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class MoneyUtil {

    private static String[] CH = {"", "", "拾", "佰", "仟", "万", "", "", "", "亿", "", "",
            "", "兆"};

    private static String[] CHS_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    /**
     * @return
     * @deprecated 请使用Money.zero()
     */
    public static Money Zero() {
        return new Money();
    }

    /**
     * 格式化金额为1,000,001.65的形式
     *
     * @param money
     * @return
     */
    public static String format(Money money) {
        if (money == null) {
            throw new IllegalArgumentException("金额不能为null");
        }
        DecimalFormat fmt = new DecimalFormat("##,###,###,###,###.00");
        String result = fmt.format(money.getAmount().doubleValue());
        if (result.indexOf(".") == 0) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * 传入数字金额字符串，返回数字金额对应的中文表示法
     * 例如：壹仟陆佰柒拾万元零伍分
     *
     * @param money 金额字符串
     * @return 金额中文大写
     */
    public static String getCHSNumber(Money m) {
        if (m == null) {
            throw new IllegalArgumentException("金额不能为null");
        }
        String money = m.getAmount().toString();
        String chs = "";

        String tmp_int = money.substring(0, money.indexOf("."));
        String tmp_down = money.substring(money.indexOf(".") + 1, money.length());

        char[] tmp_int_char = tmp_int.toCharArray();
        String[] tmp_chs = new String[tmp_int_char.length];

        int tab = 0;
        for (int i = 0; i < tmp_int_char.length; i++) {

            tab = tmp_int_char.length - i - 1;

            if (tmp_int_char.length <= 5) {
                tmp_chs[tab] = CHS_NUMBER[(int) Float.parseFloat(tmp_int_char[i] + ".0")];

                if (!tmp_chs[tab].equals("零")) {

                    // tmp_int_char.length - i 为数字所在的位数
                    chs = chs + tmp_chs[tab] + CH[tmp_int_char.length - i];
                } else {// 当数字中有零时就在后加上零，如果超过１个以上的零也只加一个零
                    if (!chs.endsWith("零") && tab != 0) {
                        chs = chs + tmp_chs[tab];

                    } else if (chs.endsWith("零") && tab == 0) {
                        chs = chs.substring(0, chs.length() - 1);
                    }

                }
            }

            // 　如果数字的位数大于５和小于９时
            if (tmp_int_char.length > 5 && tmp_int_char.length < 9) {
                tmp_chs[tab] = CHS_NUMBER[(int) Float.parseFloat(tmp_int_char[i] + ".0")];

                // 如：123,1234分成两部分
                // 第１部分123：万以上亿以下
                if (tab >= 4) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 3];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 4) {
                            chs = chs + "万";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 4) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);
                                chs = chs + "万";
                            } else {
                                // 　如果没有零就直接加上"万"
                                chs = chs + "万";
                            }
                        }
                    }
                }

                // 如：123,1234分成两部分
                // 第１部分1234：万以下
                if (tab < 4) {

                    if (!tmp_chs[tab].equals("零")) {

                        // tmp_int_char.length - i 为数字所在的位数
                        chs = chs + tmp_chs[tab] + CH[tmp_int_char.length - i];
                    } else {// 当数字中有零时就在后加上零，如果超过１个以上的零也只加一个零
                        if (!chs.endsWith("零") && tab != 0) {
                            chs = chs + tmp_chs[tab];

                        }

                        if (chs.endsWith("零") && tab == 0) {
                            chs = chs.substring(0, chs.length() - 1);
                        }
                    }
                }
            }

            // 　如果数字的位数大于５和小于９时
            if (tmp_int_char.length >= 9 && tmp_int_char.length <= 12) {
                tmp_chs[tab] = CHS_NUMBER[(int) Float.parseFloat(tmp_int_char[i] + ".0")];

                if (tab >= 8 && tab < 12) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 7];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 8) {
                            chs = chs + "亿";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 8) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);
                                chs = chs + "亿";
                            } else {
                                // 　如果没有零就直接加上"万"
                                chs = chs + "亿";
                            }
                        }
                    }
                }
                // 如：123,1234分成两部分
                // 第１部分123：万以上亿以下
                if (tab >= 4 && tab < 8) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 3];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 4) {
                            chs = chs + "万";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 4) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);

                                if (!chs.endsWith("亿"))
                                    chs = chs + "万";
                            } else {
                                // 　如果没有零就直接加上"万"
                                if (!chs.endsWith("亿"))
                                    chs = chs + "万";
                            }
                        }
                    }
                }

                // 如：123,1234分成两部分
                // 第１部分1234：万以下
                if (tab < 4) {

                    if (!tmp_chs[tab].equals("零")) {

                        // tmp_int_char.length - i 为数字所在的位数
                        chs = chs + tmp_chs[tab] + CH[tmp_int_char.length - i];
                    } else {// 当数字中有零时就在后加上零，如果超过１个以上的零也只加一个零
                        if (!chs.endsWith("零") && tab != 0) {
                            chs = chs + tmp_chs[tab];

                        }

                        if (chs.endsWith("零") && tab == 0) {
                            chs = chs.substring(0, chs.length() - 1);
                        }
                    }
                }
            }

            // 　如果数字的位数大于12和小于16时
            if (tmp_int_char.length > 12 && tmp_int_char.length <= 16) {
                tmp_chs[tab] = CHS_NUMBER[(int) Float.parseFloat(tmp_int_char[i] + ".0")];

                if (tab >= 12 && tab < 16) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 11];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 12) {
                            chs = chs + "兆";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 12) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);
                                chs = chs + "兆";
                            } else {
                                // 　如果没有零就直接加上"万"
                                chs = chs + "兆";
                            }
                        }
                    }
                }

                if (tab >= 8 && tab < 12) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 7];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 8) {
                            chs = chs + "亿";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 8) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);
                                if (!chs.endsWith("兆"))
                                    chs = chs + "亿";
                            } else {
                                // 　如果没有零就直接加上"万"
                                if (!chs.endsWith("兆"))
                                    chs = chs + "亿";
                            }
                        }
                    }
                }
                // 如：123,1234分成两部分
                // 第１部分123：万以上亿以下
                if (tab >= 4 && tab < 8) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 3];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 4) {
                            chs = chs + "万";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 4) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);

                                if (!chs.endsWith("亿"))
                                    if (!chs.endsWith("兆"))
                                        if (!chs.endsWith("兆"))
                                            chs = chs + "万";
                            } else {
                                // 　如果没有零就直接加上"万"
                                if (!chs.endsWith("亿"))
                                    if (!chs.endsWith("兆"))
                                        chs = chs + "万";
                            }
                        }
                    }
                }

                // 如：123,1234分成两部分
                // 第１部分1234：万以下
                if (tab < 4) {

                    if (!tmp_chs[tab].equals("零")) {

                        // tmp_int_char.length - i 为数字所在的位数
                        chs = chs + tmp_chs[tab] + CH[tmp_int_char.length - i];
                    } else {// 当数字中有零时就在后加上零，如果超过１个以上的零也只加一个零
                        if (!chs.endsWith("零") && tab != 0) {
                            chs = chs + tmp_chs[tab];

                        }

                        if (chs.endsWith("零") && tab == 0) {
                            chs = chs.substring(0, chs.length() - 1);
                        }
                    }
                }
            }

            // 　如果数字的位数大于16
            if (tmp_int_char.length > 16) {
                tmp_chs[tab] = CHS_NUMBER[(int) Float.parseFloat(tmp_int_char[i] + ".0")];

                if (tab >= 12) {
                    chs = chs + tmp_chs[tab];

                    // 　当第１部分算完时在加上"万"
                    if (tab == 12) {
                        chs = chs + "兆";
                    }

                }

                if (tab >= 8 && tab < 12) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 7];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 8) {
                            chs = chs + "亿";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 8) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);
                                if (!chs.endsWith("兆"))
                                    chs = chs + "亿";
                            } else {
                                // 　如果没有零就直接加上"万"
                                if (!chs.endsWith("兆"))
                                    chs = chs + "亿";
                            }
                        }
                    }
                }
                // 如：123,1234分成两部分
                // 第１部分123：万以上亿以下
                if (tab >= 4 && tab < 8) {
                    // 当前数字不是大小零时
                    if (!tmp_chs[tab].equals("零")) {
                        chs = chs + tmp_chs[tab] + CH[tab - 3];

                        // 　当第１部分算完时在加上"万"
                        if (tab == 4) {
                            chs = chs + "万";
                        }

                    } else {
                        // 当前数字为大小"零"时
                        // 判断前一次形成在字符串结尾有没有零
                        // 　如果没有零就加上零
                        if (!chs.endsWith("零")) {
                            chs = chs + tmp_chs[tab];

                        }

                        // 当第１部分算完时

                        if (tab == 4) {
                            // 　先判断字符串有没有零
                            // 　如果有零时就把零去掉再加上"万"
                            if (chs.endsWith("零")) {
                                chs = chs.substring(0, chs.length() - 1);

                                if (!chs.endsWith("兆"))

                                    if (!chs.endsWith("亿"))

                                        chs = chs + "万";
                            } else {
                                // 　如果没有零就直接加上"万"
                                if (!chs.endsWith("兆"))

                                    if (!chs.endsWith("亿"))
                                        chs = chs + "万";
                            }
                        }
                    }
                }

                // 如：123,1234分成两部分
                // 第１部分1234：万以下
                if (tab < 4) {

                    if (!tmp_chs[tab].equals("零")) {

                        // tmp_int_char.length - i 为数字所在的位数
                        chs = chs + tmp_chs[tab] + CH[tmp_int_char.length - i];
                    } else {// 当数字中有零时就在后加上零，如果超过１个以上的零也只加一个零
                        if (!chs.endsWith("零") && tab != 0) {
                            chs = chs + tmp_chs[tab];

                        }

                        if (chs.endsWith("零") && tab == 0) {
                            chs = chs.substring(0, chs.length() - 1);
                        }
                    }
                }
            }
        }

        if (tmp_down != null) {
            char[] tmp = tmp_down.toCharArray();

            if (tmp.length == 1) {
                if (tmp[0] != '0')
                    chs = chs + "元" + CHS_NUMBER[(int) Float.parseFloat(tmp[0] + ".0")] + "角整";
                else
                    chs = chs + "元整";
            } else {
                if (tmp[1] != '0' && tmp[0] != '0') {
                    chs = chs + "元" + CHS_NUMBER[(int) Float.parseFloat(tmp[0] + ".0")] + "角"
                            + CHS_NUMBER[(int) Float.parseFloat(tmp[1] + ".0")] + "分";
                } else if (tmp[1] != '0' && tmp[0] == '0') {
                    chs = chs + "元零" + CHS_NUMBER[(int) Float.parseFloat(tmp[1] + ".0")] + "分";
                } else if (tmp[1] == '0' && tmp[0] != '0') {
                    chs = chs + "元" + CHS_NUMBER[(int) Float.parseFloat(tmp[0] + ".0")] + "角";
                }else if (tmp[1] == '0' && tmp[0] == '0') {
                    chs = chs + "元整" ;
                }
            }

        } else {
            chs = chs + "元整";
        }

        return chs;
    }
    
    
	/**
	 * @param money
	 * @return
	 */
	public static String moneyToCent(Money money) {
		if (money == null) {
			return null;
		}
		return String.valueOf(money.getCent());
	}
	
	/**
	 * @param money
	 * @return
	 */
	public static String moneyToYuan(Money money) {
		if (money == null) {
			return null;
		}
		return new BigDecimal(money.getCent()).divide(new BigDecimal(100)).toString();
	}
	
	/**
	 * 将money对象转换成BigDecimal类型
	 * @param 	money	金额
	 * @return	BigDecimal
	 */
	public static BigDecimal moneyToYuanDecimal(Money money) {
		if (money == null) {
			return null;
		}
		
		return new BigDecimal(money.getCent()).divide(new BigDecimal(100));
	}
	
	/**
	 * @param cent
	 * @return
	 */
	public static Money centToMoney(String cent) {
		return getMoney(Long.valueOf(cent));
	}
	
	/**
	 * 生成Money
	 *
	 * @param cent 单位：分
	 * @return
	 */
	public static Money getMoney(Long cent) {
		
		Money money = new Money();
		//注意钱可能有负值
		//if (cent != null && cent > 0) {
		if (cent != null) {
			money.setCent(cent);
		}
		
		return money;
	}
	
	/**
	 * 获取money的绝对值
	 * 
	 * @param money
	 * @return
	 */
	public static Money absMoney(Money money) {
		if (money.getCent() >= 0) {
			return money;
		}
		return new Money().subtractFrom(money);
	}

	/**
	 * 判断money对象是否在范围之间
	 * 
	 * @param money	
	 * @param min
	 * @param max
	 * @return 
	 * @return boolean min<=money<=max 返回true 
	 * @exception 
	 * @since  1.0.0
	 */
	public static boolean isBetween(Money money,double min,double max){
		boolean flag = false;
		BigDecimal bMoney = money.getAmount();
		BigDecimal bMin = new BigDecimal(min);
		BigDecimal bMax = new BigDecimal(max);
		int minRet = bMoney.compareTo(bMin);
		int maxRet = bMoney.compareTo(bMax);
		if(minRet>=0&&maxRet<=0){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断money对象是否在范围之间
	 * 
	 * @param money	
	 * @param min
	 * @param max
	 * @return 
	 * @return boolean min<=money<=max 返回true 
	 * @exception 
	 * @since  1.0.0
	 */
	public static boolean isBetween(Money money,String min,String max){
		boolean flag = false;
		BigDecimal bMoney = money.getAmount();
		BigDecimal bMin = new BigDecimal(min);
		BigDecimal bMax = new BigDecimal(max);
		int minRet = bMoney.compareTo(bMin);
		int maxRet = bMoney.compareTo(bMax);
		if(minRet>=0&&maxRet<=0){
			flag = true;
		}
		return flag;
	}
}
