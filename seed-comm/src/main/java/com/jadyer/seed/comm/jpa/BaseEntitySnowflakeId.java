package com.jadyer.seed.comm.jpa;

import com.jadyer.seed.comm.util.IDUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IncrementGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2021/2/10 9:05.
 */
// public class BaseEntitySnowflakeId implements IdentifierGenerator {
//     // 需要显式定义空构造方法
//     public BaseEntitySnowflakeId(){}
//
//     @Override
//     public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
//         return IDUtil.INSTANCE.nextId();
//     }
// }
public class BaseEntitySnowflakeId extends IncrementGenerator {
    private final List<String> IntegerModelNameList = new ArrayList<String>(){
        private static final long serialVersionUID = 3329951447390837417L;
        {
            add("PayChannel");
            add("AcquirerInfo");
        }
    };

    @Override
    public synchronized Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // 对于ID是int自增的，单独判断一下，还是走默认的
        if(IntegerModelNameList.contains(object.getClass().getSimpleName())){
            return super.generate(session, object);
        }
        return IDUtil.INSTANCE.nextId();
    }
}