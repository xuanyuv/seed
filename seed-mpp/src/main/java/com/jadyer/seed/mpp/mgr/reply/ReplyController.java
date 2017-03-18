package com.jadyer.seed.mpp.mgr.reply;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.mgr.reply.model.ReplyInfo;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/reply")
public class ReplyController{
	@Resource
	private ReplyInfoDao replyInfoDao;

	/**
	 * 查询通用的回复内容
	 */
	@RequestMapping("/common/get")
	public String getCommon(HttpServletRequest request){
		int uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		List<ReplyInfo> replyInfoList = replyInfoDao.findByCategory(uid, "0");
		if(!replyInfoList.isEmpty()){
			request.setAttribute("replyInfo", replyInfoList.get(0));
		}
		return "reply/common";
	}


	/**
	 * 查询关注后回复的内容
	 */
	@RequestMapping("/follow/get")
	public String getFollow(HttpServletRequest request){
		int uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		List<ReplyInfo> replyInfoList = replyInfoDao.findByCategory(uid, "1");
		if(!replyInfoList.isEmpty()){
			request.setAttribute("replyInfo", replyInfoList.get(0));
		}
		return "reply/follow";
	}


	/**
	 * 更新关注后回复的内容
	 */
	@ResponseBody
	@RequestMapping("/follow/save")
	public CommonResult saveFollow(ReplyInfo replyInfo, HttpServletRequest request){
		replyInfo.setUid(((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId());
		replyInfo.setCategory("1");
		replyInfo.setType("0");
		return new CommonResult(replyInfoDao.saveAndFlush(replyInfo));
	}


	/*
	//查询关键字回复列表
	@RequestMapping("/keyword/list")
	public String listKeyword(HttpServletRequest request){
		int uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		List<ReplyInfo> replyInfoList = replyInfoDao.findByCategory(uid, "2");
		request.setAttribute("replyInfoList", replyInfoList);
		return "reply/keywordList";
	}
	*/


	/**
	 * 分页查询关键字回复列表
	 * @param page zero-based page index
	 */
	@RequestMapping("/keyword/list")
	public String listViaPage(String pageNo, HttpServletRequest request){
		final int uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		//排序
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		//分页
		Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
		//条件
		Map<String, Object> params = new HashMap<>();
		params.put("uid:eq", uid);
		params.put("category", 2);
		Condition<ReplyInfo> spec = Condition.<ReplyInfo>create().and(params);
		//执行
		Page<ReplyInfo> keywordPage = replyInfoDao.findAll(spec, pageable);
		request.setAttribute("page", keywordPage);
		return "reply/keywordList";
	}


	/**
	 * 查询关键字回复的内容
	 */
	@ResponseBody
	@RequestMapping("/keyword/get/{id}")
	public CommonResult getKeyword(@PathVariable int id){
		return new CommonResult(replyInfoDao.findOne(id));
	}


	/**
	 * delete关键字
	 */
	@ResponseBody
	@RequestMapping("/keyword/delete/{id}")
	public CommonResult deleteKeyword(@PathVariable int id){
		replyInfoDao.delete(id);
		return new CommonResult();
	}


	/**
	 * saveOrUpdate关键字
	 */
	@ResponseBody
	@RequestMapping("/keyword/save")
	public CommonResult saveKeyword(ReplyInfo replyInfo){
		replyInfoDao.saveAndFlush(replyInfo);
		return new CommonResult();
	}
}