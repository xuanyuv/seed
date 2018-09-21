package com.jadyer.seed.mpp.web.service;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.repository.MppUserInfoRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MppUserService {
    @Resource
    private MppUserInfoRepository mppUserInfoRepository;

    private String buildEncryptPassword(String password){
        return DigestUtils.md5Hex(password + "https://jadyer.cn/");
    }


    public MppUserInfo findByUsernameAndPassword(String username, String password){
        return mppUserInfoRepository.findByUsernameAndPassword(username, buildEncryptPassword(password));
    }


    /**
     * 查询所有已绑定的公众号信息
     */
    public List<MppUserInfo> getHasBindStatus(){
        return mppUserInfoRepository.findByBindStatus(1);
    }


    public MppUserInfo findByWxid(String mpid){
        return mppUserInfoRepository.findByWxid(mpid);
    }


    public MppUserInfo findByQqid(String mpid){
        return mppUserInfoRepository.findByQqid(mpid);
    }


    public MppUserInfo findOne(long id){
        return mppUserInfoRepository.findOne(id);
    }


    public List<MppUserInfo> findAll(){
        return mppUserInfoRepository.findAll();
    }


    @Transactional(rollbackFor=Exception.class)
    public MppUserInfo upsert(MppUserInfo mppUserInfo){
        return mppUserInfoRepository.saveAndFlush(mppUserInfo);
    }


    /**
     * 修改密码
     * @param mppUserInfo HttpSession中的当前登录用户信息
     * @param oldPassword 用户输入的旧密码
     * @param newPassword 用户输入的新密码
     */
    @Transactional(rollbackFor=Exception.class)
    public MppUserInfo passwordUpdate(MppUserInfo mppUserInfo, String oldPassword, String newPassword){
        if(!mppUserInfo.getPassword().equals(buildEncryptPassword(oldPassword))){
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "原密码不正确");
        }
        mppUserInfo.setPassword(buildEncryptPassword(newPassword));
        return mppUserInfoRepository.saveAndFlush(mppUserInfo);
    }
}