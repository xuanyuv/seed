package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.MppMenuInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MppMenuInfoRepository extends BaseRepository<MppMenuInfo, Long> {
    List<MppMenuInfo> findByUid(long uid);

    @Modifying
    @Transactional
    @Query("UPDATE MppMenuInfo SET menuJson=?1 WHERE type=3 AND uid=?2")
    int updateJson(String menuJson, long uid);
}