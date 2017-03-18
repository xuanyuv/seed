package com.jadyer.seed.mpp.mgr.fans;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.mgr.fans.model.FansInfo;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/fans")
public class FansController{
	@Resource
	private FansInfoRepository fansInfoRepository;

	/**
	 * 分页查询粉丝信息
	 * @param pageNo zero-based page index
	 */
	@RequestMapping("/list")
	public String listViaPage(String pageNo, HttpServletRequest request){
		final long uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		//排序
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		//分页
		Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
		//条件
		Condition<FansInfo> spec = Condition.<FansInfo>create().and("uid", Condition.Operator.EQ, uid);
		//执行
		Page<FansInfo> fansPage = fansInfoRepository.findAll(spec, pageable);
		request.setAttribute("page", fansPage);
		return "fans.list";
	}
}