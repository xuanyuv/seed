package com.jadyer.seed.comm.tag.bak.permission;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class PermissionTag extends TagSupport {
	private static final long serialVersionUID = -2113870345735996769L;
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int doStartTag() throws JspException {
		String permission = this.getName();
		if(null==permission || permission.trim().length()==0){
			throw new JspException("The 'name' tag attribute must be set.");
        }
		if(this.showTagBody(permission)){
			return TagSupport.EVAL_BODY_INCLUDE;
		}else{
			return TagSupport.SKIP_BODY;
		}
	}
	
	boolean isPermitted(String permission){
//		org.apache.shiro.subject.Subject subject = org.apache.shiro.SecurityUtils.getSubject();
//		if(null == subject){
//			return false;
//		}
//		com.jadyer.web.user.User user = (User)subject.getSession().getAttribute(com.jadyer.common.base.Constants.USER);
//		//非操作员登录则无需权限判断(即普通用户拥有页面链接或按钮的查看权限,故直接返回true)
//		if(null!=user && 0==user.getOperator().getId()){
//			return true;
//		}else{
//			return subject.isPermitted(permission);
//		}
		return true;
	}
	
	protected abstract boolean showTagBody(String permission);
}


//<%@ taglib prefix="wth" uri="http://www.weitehui.cn/tags"%>
//<wth:hasPermission name="<%=PermissionConstants.WTP_VIEW.getCode()%>">
//	<td><a href="${ctx}/vote/edit.do?voteId=${vote.voteId}&pageNo=${pager.pageNo}&look=yes">${vote.voteName}</a></td>
//</wth:hasPermission>
//<wth:lacksPermission name="<%=PermissionConstants.WTP_VIEW.getCode()%>">
//	<td>${vote.voteName}</td>
//</wth:lacksPermission>


//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationInfo;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.SimpleAuthenticationInfo;
//import org.apache.shiro.authc.UnknownAccountException;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.subject.Subject;
//
//import com.fxpgy.common.util.ConfigUtil;
//import com.fxpgy.common.util.DateUtil;
//import com.fxpgy.common.util.LogUtil;
//import com.fxpgy.common.util.StringUtil;
//import com.fxpgy.wth.controller.vo.UserInfo;
//import com.fxpgy.wth.dao.dataobject.UserPermissionDO;
//import com.fxpgy.wth.dao.impl.UserPermissionDAOImpl;
//import com.fxpgy.wth.service.UserService;
//import com.fxpgy.wth.service.constants.Constants;
//
///**
// * 登陆认证以及授权
// * @create Oct 9, 2013 1:21:15 PM
// * @author 玄玉<https://jadyer.github.io/>
// */
//public class WthRealm extends AuthorizingRealm {
//	@Resource
//	private UserService userService;
//	@Resource
//	private UserPermissionDAOImpl userPermissionDAOImpl;
//	
//	/**
//	 * Subject认证
//	 */
//	@Override
//	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException{
//		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
//		LogUtil.getLogger().info("准备认证当前登录的用户[" + token.getPrincipal() + "]");
//		UserInfo userInfo = null;
//		try{
//			userInfo = userService.login(token.getUsername(), String.valueOf(token.getPassword()));
//			if(null == userInfo){
//				throw new UnknownAccountException("用户名或密码不正确");
//			}
//			if(DateUtil.string2Date(DateUtil.getNextDay(userInfo.getEndTime())).before(DateUtil.now())) {
//				throw new AuthenticationException("用户已过期");
//			}
//			if(StringUtil.equals(userInfo.getIsStop(), "Y")){
//				throw new AuthenticationException("用户已停用");
//			}
//		}catch(Exception e){
//			throw new UnknownAccountException("用户名或密码不正确", e);
//		}
//		//注意:操作员登录的情况下,这里储存userInfo里的用户名和密码就是操作员的登录用户名和登录密码
//		setSessionTimeout();
//		setSession(Constants.USER_INFO, userInfo);
//		setSession(Constants.USER_ID, userInfo.getUserId());
//		setSession(Constants.WX_NO, userInfo.getWxNo());
//		//按照Shiro的思想,这里应该这么写
//		//token.setPassword(MD5Util.encoderByMd5(String.valueOf(token.getPassword())).toCharArray());
//		//AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(userInfo.getUsername(), userInfo.getPassword(), getName());
//		//但我们没必要这么做,因为上面已经userService.login()查出来用户信息了,说明用户名和密码是正确的
//		//但又要遵照Shiro的验证机制,所以尽管有些画蛇添足,但我们也要这样写,即返回一个AuthenticationInfo对象
//		return new SimpleAuthenticationInfo(token.getUsername(), token.getPassword(), getName());
//	}
//
//	
//	/**
//	 * Subject授权
//	 */
//	@Override
//	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
//		Subject currentUser = SecurityUtils.getSubject();
//		Session session = null;
//		if(null == currentUser){
//			return new SimpleAuthorizationInfo();
//		}else{
//			session = currentUser.getSession();
//		}
//		UserInfo userinfo = (UserInfo)session.getAttribute(Constants.USER_INFO);
//		List<String> permissionList = new ArrayList<String>();
//		//针对操作员的权限处理
//		if(null!=userinfo && 0!=userinfo.getUserOperatorInfo().getOperatorId()){
//			List<UserPermissionDO> permissionDOList = userPermissionDAOImpl.findUserPermissionForPageByIds(userinfo.getUserOperatorInfo().getOperatorPermissionIds().replaceAll("\\`", ","));
//			for(UserPermissionDO userdo : permissionDOList){
//				permissionList.add(userdo.getValue());
//			}
//		}
////		//针对普通用户的权限处理
////		if(null!=userinfo && 0==userinfo.getUserOperatorInfo().getOperatorId()){
////			for(String permissionValue : userPermissionDAOImpl.findUserPermissionForPage()){
////				permissionList.add(permissionValue);
////			}
////		}
//		SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
//		simpleAuthorInfo.addStringPermissions(permissionList);
//		StringBuilder values = new StringBuilder("[");
//		for(String v : permissionList){
//			values.append(v).append("`");
//		}
//		values.append("]");
//		LogUtil.getLogger().info("已为当前登录的操作员赋予权限" + values.toString());
//		return simpleAuthorInfo;
//	}
//
//	
//	/**
//	 * 将一些数据放到ShiroSession中,以便于其它地方使用
//	 * @see 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
//	 */
//	private void setSession(String key, Object value) {
//		Subject currentUser = SecurityUtils.getSubject();
//		if (null != currentUser) {
//			Session session = currentUser.getSession();
//			LogUtil.getLogger().info("当前的Session默认超时时间为[" + session.getTimeout() + "]毫秒");
//			if (null != session) {
//				session.setAttribute(key, value);
//			}
//		}
//	}
//	
//	
//	/**
//	 * 手动设置Session超时时间
//	 * @see 时间取自配置文件,单位:毫秒
//	 */
//	private void setSessionTimeout(){
//		Subject currentUser = SecurityUtils.getSubject();
//		if (null != currentUser) {
//			Session session = currentUser.getSession();
//			session.setTimeout(Long.parseLong(ConfigUtil.INSTANCE.getProperty("dmt.session.timeout")));
//		}
//	}
//}