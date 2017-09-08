package ${entityPackage}.dao;

import javacommon.base.BaseIbatis3Dao;

import org.springframework.stereotype.Repository;


/**
 * 
 * <br>
 * <b>功能：</b>${className}Dao<br>
 * <b>作者：</b>${userName}<br>
 * <b>生成日期：</b> ${dateTime} <br>
 * <b>版权所有：<b>版权所有(C) 2015, PUKKA<br>
 */ 
@Repository
public class ${className}Dao extends javacommon.base.BaseIbatis3Dao<${className},java.lang.Integer>{

	@Override
	public String getIbatisMapperNamesapce() {
		return "${className}";
	}


	public void saveOrUpdate(${className} entity) {
		if(entity.getId() == null) 
			save(entity);
		else 
			update(entity);
	}
	

}
