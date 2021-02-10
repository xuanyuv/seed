package com.jadyer.seed.comm.jpa;

import com.jadyer.seed.comm.util.IDUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2021/2/10 9:05.
 */
public class BaseEntitySnowflakeId implements IdentifierGenerator {
    // 需要显式定义空构造方法
    public BaseEntitySnowflakeId(){}

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return IDUtil.INSTANCE.nextId();
    }
}