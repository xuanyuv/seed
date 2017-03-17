package com.jadyer.seed.mpp.mgr.fans;

import com.jadyer.seed.mpp.mgr.fans.model.FansInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FansInfoDao extends JpaRepository<FansInfo, Integer>, JpaSpecificationExecutor<FansInfo> {
	/**
	 * 查询平台某用户的所有粉丝信息
	 */
	@Query("FROM FansInfo WHERE uid=?1")
	List<FansInfo> findByUid(int uid);

	/**
	 * 查询某个粉丝的信息
	 */
	@Query("FROM FansInfo WHERE uid=?1 AND openid=?2")
	FansInfo findByUidAndOpenid(int uid, String openid);

	/**
	 * 更新粉丝的关注状态
	 */
	@Modifying
	@Transactional(timeout=10)
	@Query("UPDATE FansInfo SET subscribe=?1 WHERE uid=?2 AND openid=?3")
	int updateSubscribe(String subscribe, int uid, String openid);
}