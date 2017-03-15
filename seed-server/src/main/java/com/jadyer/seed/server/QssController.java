package com.jadyer.seed.server;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping(value="/qss")
public class QssController {
	/**
	 * 验证动态密码是否正确
	 * <p>
	 *     每个动态密码有效期为10分钟
	 * </p>
	 * @return 动态密码正确则返回true，反之false
	 */
	private boolean verifyDynamicPassword(String dynamicPassword){
		String timeFlag = DateFormatUtils.format(new Date(), "HHmm").substring(0, 3) + "0";
		String generatePassword = DigestUtils.md5Hex(timeFlag + "https://jadyer.github.io/" + timeFlag);
		return StringUtils.isNotBlank(dynamicPassword) && generatePassword.equalsIgnoreCase(dynamicPassword);
	}
}