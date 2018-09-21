package com.jadyer.seed.seedoc.web.service;

import com.jadyer.seed.seedoc.web.model.Api;
import com.jadyer.seed.seedoc.web.repository.ApiRepository;
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
 * API
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2017/11/15 17:42.
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class ApiService {
    @Resource
    private ApiRepository apiRepository;

    /**
     * 分页查询
     * @param pageNo 页码，起始值为0，未传此值则默认取0
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public Page<Api> list(String pageNo){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), 10, sort);
        //Condition<Api> spec = Condition.and();
        //spec.eq("id", 2);
        //return apiRepository.findAll(spec, pageable);
        return apiRepository.findAll(pageable);
    }


    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public Api get(long id){
        return apiRepository.findOne(id);
    }


    public void delete(long id){
        apiRepository.delete(id);
    }


    public Api upsert(Api api){
        return apiRepository.saveAndFlush(api);
    }
}