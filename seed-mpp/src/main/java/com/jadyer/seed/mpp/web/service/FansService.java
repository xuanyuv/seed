package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.MppFansInfo;
import com.jadyer.seed.mpp.web.repository.FansInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/15 14:02.
 */
@Service
public class FansService {
    @Resource
    private FansInfoRepository fansInfoRepository;

    /**
     * 查询平台某用户的所有粉丝信息
     */
    public List<MppFansInfo> getByUid(long uid){
        return fansInfoRepository.findByUid(uid);
    }


    /**
     * 查询某个粉丝的信息
     */
    public MppFansInfo getByUidAndOpenid(long uid, String openid){
        return fansInfoRepository.findByUidAndOpenid(uid, openid);
    }


    /**
     * 更新粉丝的关注状态
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean unSubscribe(long uid, String openid){
        return 1 == fansInfoRepository.updateSubscribe("0", uid, openid);
    }


    /**
     * saveOrUpdate粉丝信息
     */
    @Transactional(rollbackFor=Exception.class)
    public MppFansInfo upsert(MppFansInfo mppFansInfo){
        return fansInfoRepository.saveAndFlush(mppFansInfo);
    }


    /**
     * 分页查询关键字回复列表
     * @param page zero-based page index
     */
    public Page<MppFansInfo> listViaPage(long uid, String pageNo){
        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = PageRequest.of(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件
        Condition<MppFansInfo> spec = Condition.<MppFansInfo>and().eq("uid", uid);
        //执行
        return fansInfoRepository.findAll(spec, pageable);
    }
}