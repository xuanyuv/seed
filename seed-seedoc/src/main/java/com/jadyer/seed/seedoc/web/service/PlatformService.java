package com.jadyer.seed.seedoc.web.service;

import com.jadyer.seed.seedoc.web.model.Platform;
import com.jadyer.seed.seedoc.web.repository.PlatformRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 平台
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/11/15 15:45.
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PlatformService {
    @Resource
    private PlatformRepository platformRepository;

    /**
     * 分页查询
     * @param pageNo 页码，起始值为0，未传此值则默认取0
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public Page<Platform> list(String pageNo){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //Condition<Platform> spec = Condition.and();
        //spec.eq("id", 2);
        //return platformRepository.findAll(spec, pageable);
        return platformRepository.findAll(pageable);
    }


    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public Platform get(long id){
        return platformRepository.findOne(id);
    }


    public void delete(long id){
        platformRepository.delete(id);
    }


    public Platform upsert(Platform platform){
        return platformRepository.saveAndFlush(platform);
    }
}