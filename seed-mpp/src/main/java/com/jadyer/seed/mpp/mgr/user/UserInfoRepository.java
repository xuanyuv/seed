package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoRepository extends BaseRepository<UserInfo, Long> {
	@Query("FROM UserInfo WHERE username=?1 AND password=?2")
	UserInfo findByUsernameAndPassword(String username, String password);

	@Query("FROM UserInfo WHERE mptype=1 AND mpid=?1")
	UserInfo findByWxid(String mpid);

	@Query("FROM UserInfo WHERE mptype=2 AND mpid=?1")
	UserInfo findByQqid(String mpid);
}