package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.mgr.user.model.MenuInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuInfoRepository extends BaseRepository<MenuInfo, Long> {
    @Query("FROM MenuInfo WHERE uid=?1")
    List<MenuInfo> findMenuListByUID(long uid);

    @Modifying
    @Transactional
    @Query("UPDATE MenuInfo SET menuJson=?1 WHERE type=3 AND uid=?2")
    int updateJson(String menuJson, long uid);
}