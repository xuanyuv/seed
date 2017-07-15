package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MppUserInfoRepository extends BaseRepository<MppUserInfo, Long> {
    MppUserInfo findByUsernameAndPassword(String username, String password);

    List<MppUserInfo> findByBindStatus(int indStatus);

    @Query("FROM MppUserInfo WHERE mptype=1 AND mpid=?1")
    MppUserInfo findByWxid(String mpid);

    @Query("FROM MppUserInfo WHERE mptype=2 AND mpid=?1")
    MppUserInfo findByQqid(String mpid);
}