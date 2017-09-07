package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.model.Column;
import com.jadyer.seed.simcoder.model.Table;
import com.jadyer.seed.simcoder.service.SimcoderHelper;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/5 14:40.
 */
public class SimcoderRun {
    public static void main(String[] args) {
        for(Table obj : SimcoderHelper.getTableList("mpp")){
            System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        }
        System.out.println("================================================");
        for(Column obj : SimcoderHelper.getColumnList("t_mpp_user_info")){
            System.out.println(ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE));
        }
    }
}