package ${entityPackage}.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manage.dao.${className}Dao;


/**
 * 
 * <br>
 * <b>功能：</b>${className}Manager<br>
 * <b>作者：</b>${userName}<br>
 * <b>生成日期：</b> ${dateTime} <br>
 * <b>版权所有：<b>版权所有(C) 2015, PUKKA<br>
 */ 
@Service
@Transactional
public class ${className}Manager extends javacommon.base.BaseManager{

	private ${className}Dao ${classNameLowerCase}Dao;
	public void set${className}Dao(${className}Dao dao) {
		this.${classNameLowerCase}Dao = dao;
	}
	public javacommon.base.EntityDao getEntityDao() {
		return this.${classNameLowerCase}Dao;
	}

	

}

