package com.jadyer.seed.mpp.web.repository;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.web.model.MppReplyInfo;

import java.util.List;

public interface MppReplyInfoRepository extends BaseRepository<MppReplyInfo, Long> {
    /**
     * 根据分类查询回复内容
     * @param uid      平台用户ID
     * @param category 回复的类别：0--通用的回复，1--关注后回复，2--关键字回复
     */
    List<MppReplyInfo> findByUidAndCategory(long uid, int category);

    /**
     * 查询指定关键字的信息
     */
    MppReplyInfo findByUidAndKeyword(long uid, String keyword);
}