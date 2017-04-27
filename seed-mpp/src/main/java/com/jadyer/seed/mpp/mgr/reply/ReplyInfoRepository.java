package com.jadyer.seed.mpp.mgr.reply;

import com.jadyer.seed.comm.jpa.BaseRepository;
import com.jadyer.seed.mpp.mgr.reply.model.ReplyInfo;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyInfoRepository extends BaseRepository<ReplyInfo, Long> {
    /**
     * 根据分类查询回复内容
     * @param uid      平台用户ID
     * @param category 回复的类别：0--通用的回复，1--关注后回复，2--关键字回复
     */
    @Query("FROM ReplyInfo WHERE uid=?1 AND category=?2")
    List<ReplyInfo> findByCategory(long uid, int category);

    /**
     * 查询指定关键字的信息
     */
    @Query("FROM ReplyInfo WHERE uid=?1 AND keyword=?2")
    ReplyInfo findByKeyword(long uid, String keyword);
}