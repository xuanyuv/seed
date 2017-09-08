package ${entityPackage}.action;

import java.util.Date;
import java.util.Hashtable;
import javacommon.util.ManageHelper;
import javacommon.util.SafeUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import cn.org.rapid_framework.beanutils.BeanUtils;
import cn.org.rapid_framework.web.scope.Flash;
import cn.org.rapid_framework.web.util.HttpUtils;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import cn.org.rapid_framework.util.*;
import cn.org.rapid_framework.web.util.*;
import cn.org.rapid_framework.page.*;
import cn.org.rapid_framework.page.impl.*;
import java.util.*;
import javacommon.base.*;
import javacommon.util.*;
import com.manage.model.*;
import com.manage.model.Result.*;
import com.manage.service.*;
import com.manage.util.*;


/**
 * 
 * <br>
 * <b>功能：</b>${className}Action<br>
 * <b>作者：</b>${userName}<br>
 * <b>生成日期：</b> ${dateTime} <br>
 * <b>版权所有：<b>版权所有(C) 2015, PUKKA<br>
 */ 
public class ${className}Action extends BaseAction {
	// 默认多列排序,example: username desc,createTime asc
	protected static final String DEFAULT_SORT_COLUMNS = null;
	protected static final String LIST_JSP = "/pages/${className}/${className}List.jsp";
	protected static final String DETAIL_JSP = "/pages/${className}/${className}Detail.jsp";
	protected static final String SelectList_JSP = "/pages/${className}/${className}SelectList.jsp";
	protected static final String Search_JSP = "/pages/${className}/${className}Search.jsp";


	private ${className}Manager ${classNameLowerCase}Manager;

	private ${className} ${classNameLowerCase};
	Integer id = null;
	// post删除多个对象
	String ids = null;
	// post获取列表参数
	String where = null;

	public void prepare() throws Exception {
		if (isNullOrEmptyString(id)) {
			${classNameLowerCase} = new ${className}();
		} else {
			${classNameLowerCase} = (${className}) ${classNameLowerCase}Manager.getById(id);
		}
	}

	/** 增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,注意大小写 */
	public void set${className}Manager(${className}Manager value) {
		this.${classNameLowerCase}Manager = value;
	}

	public Object getModel() {
		return ${classNameLowerCase};
	}
	public void setId(Integer value) {
		this.id = value;
	}
	public void setIds(String value) {
		this.ids = value;
	}
	public void setWhere(String value) {
		this.where = value;
	}

	/** 列表页 */
	public String ListPage() {
		/*设置权限*/
		this.setMyPrivs("t_${classNameLowerCase}");

		return LIST_JSP;
	}

	/** 新增修改查看页 */
	public String DetailPage() {
		return DETAIL_JSP;
	}

	/** 选择列表页 */
	public String SelectListPage() {
		return SelectList_JSP;
	}
	/** 高级搜索页 */
	public String SearchPage() {
		return Search_JSP;
	}

	/** 新增 */
	public String Add() {
		ResultAction<Integer> tmpresult = new ResultAction<Integer>();
		try {
			if(${classNameLowerCase}!=null){
			}	
			Integer tmpcount = ${classNameLowerCase}Manager.save(${classNameLowerCase});
			if (tmpcount > 0) {
				tmpresult.setIserror(false);
				tmpresult.setMessage("success");
				//tmpresult.setData(${classNameLowerCase}.getId());

			    /* 新增日志 */
			    //saveOperatorLog(Operate_Add, ${classNameLowerCase}.TABLE_ALIAS, ${classNameLowerCase}.toDomain(), ${classNameLowerCase}.toString());
			} else {
				tmpresult.setIserror(true);
				tmpresult.setMessage("fail");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			tmpresult.setIserror(true);
			tmpresult.setMessage("fail");
		} finally {
			putJSONResult(tmpresult);
		}
		return JSON;
	}

	/** 修改 */
	public String Modify() {
		ResultAction<Integer> tmpresult = new ResultAction<Integer>();
		String tmpObjDesc = "";
		try {
		    //修改前对象信息
		    tmpObjDesc += Operate_ModifyPre+${classNameLowerCase}Manager.getById(${classNameLowerCase}.getId()).toString();

			Integer tmpcount = ${classNameLowerCase}Manager.update(${classNameLowerCase});
			if (tmpcount > 0) {
				tmpresult.setIserror(false);
				tmpresult.setMessage("success");
				//tmpresult.setData(${classNameLowerCase}.getId());

			    //修改后对象信息
			    tmpObjDesc += Operate_ModifyNext + ${classNameLowerCase}.toString();

			    /* 修改日志 */
			    //saveOperatorLog(Operate_Modify, ${classNameLowerCase}.TABLE_ALIAS, ${classNameLowerCase}.toDomain(), tmpObjDesc);
			} else {
				tmpresult.setIserror(true);
				tmpresult.setMessage("fail");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			tmpresult.setIserror(true);
			tmpresult.setMessage("fail");
		} finally {
			putJSONResult(tmpresult);
		}
		return JSON;
	}

	/** 删除对象 */
	public String Delete() {
		ResultAction<Integer> tmpresult = new ResultAction<Integer>();
		try {
		    
		    /* 在删除前保存删除日志 */
		    saveIDsLog(ids,Operate_Delete);

			String tmpcond = "ID in (" + ids + ")";
			BaseQuery query = new BaseQuery();
			query.setWherecond(tmpcond);
			int tmpcount = ${classNameLowerCase}Manager.deleteByCond(query);
			if (tmpcount > 0) {
				tmpresult.setIserror(false);
				tmpresult.setMessage("success");
				tmpresult.setData(tmpcount);
			} else {
				tmpresult.setIserror(true);
				tmpresult.setMessage("fail");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			tmpresult.setIserror(true);
			tmpresult.setMessage("fail");
		} finally {
			putJSONResult(tmpresult);
		}
		return JSON;
	}

	/** 获取对象信息 */
	public String GetInfo() {
		ResultAction<${className}> tmpresult = new ResultAction<${className}>();
		try {
			if (!isNullOrEmptyString(id))
				${classNameLowerCase} = (${className}) ${classNameLowerCase}Manager.getById(id);
			if (${classNameLowerCase} != null) {
				tmpresult.setIserror(false);
				tmpresult.setMessage("success");
				tmpresult.setData(${classNameLowerCase});
			} else {
				tmpresult.setIserror(true);
				tmpresult.setMessage("fail");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			putJSONResult(tmpresult);
		}
		return JSON;
	}

	/** 获取对象列表 */
	public String GridList() {
		ResultGrid<${className}> tmpresult = new ResultGrid<${className}>();
		try {
			int pageIndex = SafeUtils.getInt( getRequest().getParameter("page"),1); // 取得当前页数,注意这是jqgrid自身的参数
	        int pageSize =SafeUtils.getInt( getRequest().getParameter("rows"),1); // 取得每页显示行数，,注意这是jqgrid自身的参数
	        String sortName = SafeUtils.getString( getRequest().getParameter("sidx")); //排序字段
	        String sortOrder = SafeUtils.getString( getRequest().getParameter("sord")); //Desc或Asc

			String tmpcond = "";
			String tmpsort = "";
			if (sortName != null && sortName != "")
				tmpsort = sortName + " " + sortOrder;
			FilterTranslator tmptrans = new FilterTranslator();
			tmpcond = tmptrans.getWhereString(where);

			BaseQuery query = new BaseQuery();
			query.setPageNumber(pageIndex);
			query.setPageSize(pageSize);
			query.setWherecond(tmpcond);
			query.setSortColumns(tmpsort);

			Page page = ${classNameLowerCase}Manager.findPage(query);
			tmpresult.setRows(page.getResult());
			tmpresult.setTotal(page.getTotalCount());//总页数
			tmpresult.setRecords(page.getTotalCount());//总记录数
			tmpresult.setPage(pageIndex);//当前页

		} catch (Exception ex) {
			log.error(ex.getMessage());
			tmpresult.setTotal(0);
		} finally {
			putJSONResult(tmpresult);
		}
		return JSON;
	}

	/** 保存多数据操作日志 */
	private void saveIDsLog(String ids,String operateModel) {
		String tmpcond = "";
	    String tmpObjName = "";
		String tmpObjDesc = operateModel+"IDs:"+ids+";";
	    BaseQuery query = new BaseQuery();

	    tmpcond = "ID in ("+ids+")";
	    query.setWherecond(tmpcond);
	    List<${className}> tmpList = ${classNameLowerCase}Manager.findList(query);
	    if(tmpList!=null&&tmpList.size()>0) {
		    for(${className} ${classNameLowerCase} : tmpList){
				//tmpObjName += ${classNameLowerCase}.toDomain()+";";
				tmpObjDesc += ${classNameLowerCase}.toString();
			}
			/* 操作日志 */
			saveOperatorLog(operateModel, ${classNameLowerCase}.toString, tmpObjName, tmpObjDesc);
		}
	}

}
