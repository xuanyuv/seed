package com.jadyer.seed.open;

import org.springframework.stereotype.Service;

@Service
public class RouterService970 extends RouterService100 {
    ///**
    // * ------------------------------------------------------------------------------------
    // * 本来appid=970的业务接口中有一个是boot.loan.get，但其实现细节，在整个平台都是公共的
    // * 故对于没有定制化需求的，就可以像本类一样，不定义具体实现（其默认会使用继承过来的父类实现）
    // * ------------------------------------------------------------------------------------
    // * 若存在定制需求，那么便需要自定义实现，此时写法见下方注释
    // * 注：这种情况下，@OpenMethod注解可写可不写，最终执行的仍是这里的逻辑（不会执行父类的）
    // * 注：这是因为OpenAnnotationProcessor处理器的method.invoke传的是子类的对象，不是父类的
    // * ------------------------------------------------------------------------------------
    // */
    //@Override
    //public CommResult<Map<String, Object>> loanGet(ReqData reqData) {
    //    Map<String, Object> resultMap = new HashMap<>();
    //    resultMap.put("userName", "玄玉");
    //    resultMap.put("userPhone", "13600000000");
    //    return CommResult.success(resultMap);
    //}
}