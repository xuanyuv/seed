package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.mpp.web.model.MppReplyInfo;
import com.jadyer.seed.mpp.web.repository.MppReplyInfoRepository;
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
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/15 13:10.
 */
@Service
public class MppReplyService {
    @Resource
    private MppReplyInfoRepository mppReplyInfoRepository;

    /**
     * 查询指定类别的回复内容
     * @param uid      平台用户ID
     * @param category 回复的类别：0--通用的回复，1--关注后回复，2--关键字回复
     */
    public MppReplyInfo getByUidAndCategory(long uid, int category){
        List<MppReplyInfo> mppReplyInfoList = mppReplyInfoRepository.findByUidAndCategory(uid, category);
        if(!mppReplyInfoList.isEmpty()){
            return mppReplyInfoList.get(0);
        }
        return new MppReplyInfo();
    }


    /**
     * 查询指定类别的回复内容
     * @param uid      平台用户ID
     * @param category 回复的类别：0--通用的回复，1--关注后回复，2--关键字回复
     */
    public MppReplyInfo getByUidAndKeyword(long uid, String keyword){
        MppReplyInfo mppReplyInfo = mppReplyInfoRepository.findByUidAndKeyword(uid, keyword);
        if(null == mppReplyInfo){
            mppReplyInfo = new MppReplyInfo();
        }
        return mppReplyInfo;
    }


    /**
     * 更新关注后回复的内容
     */
    @Transactional(rollbackFor=Exception.class)
    public MppReplyInfo upsertFollow(MppReplyInfo mppReplyInfo){
        mppReplyInfo.setCategory(1);
        mppReplyInfo.setType(0);
        return mppReplyInfoRepository.saveAndFlush(mppReplyInfo);
    }


    /**
     * 分页查询关键字回复列表
     * @param page zero-based page index
     */
    public Page<MppReplyInfo> listViaPage(long uid, String pageNo){
        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        //分页
        Pageable pageable = PageRequest.of(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //条件
        Condition<MppReplyInfo> spec = Condition.and();
        spec.eq("uid", uid);
        spec.eq("category", 2);
        //执行
        return mppReplyInfoRepository.findAll(spec, pageable);
    }


    /**
     * 查询关键字回复的内容
     */
    public MppReplyInfo getKeyword(long id){
        return mppReplyInfoRepository.findById(id).orElse(null);
    }


    /**
     * delete关键字
     */
    @Transactional(rollbackFor=Exception.class)
    public void deleteKeyword(long id){
        mppReplyInfoRepository.deleteById(id);
    }


    /**
     * saveOrUpdate关键字
     */
    @Transactional(rollbackFor=Exception.class)
    public MppReplyInfo upsertKeyword(MppReplyInfo mppReplyInfo){
        return mppReplyInfoRepository.saveAndFlush(mppReplyInfo);
    }
}