package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.util.Date;

/**
 * 本类为{@link IDUtil}的迷你版
 * ----------------------------------------------------------------------------------------------------------------
 * 由于JavaScript支持的最大安全整数为9007199254740991，故编写此类，用于生成固定长度为15的ID
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2022/3/2 16:55.
 */
public final class IDMiniUtil {
    // private static final long twepoch = 3818419199000L;
    private static final long twepoch = 1646211632645L;
    // private static final long twepoch = 0L;
    private static long workerId;
    private static final long workerIdBits = 5L;
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long sequenceBits = 12L;
    private static final long workerIdShift = sequenceBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);
    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    static {
        workerId = getWorkerId();
        LogUtil.getLogger().info("初始化得到：工作机器ID=[{}]", workerId);
    }


    private static long getWorkerId() {
        long datacenterId = 1L;
        StringBuilder sb = new StringBuilder();
        sb.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotEmpty(name)) {
            sb.append(name.split("@")[0]);
        }
        return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }


    public static void setDatacenterWorkerId(long _workerId){
        if(_workerId>maxWorkerId || _workerId<0){
            throw new RuntimeException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        workerId = _workerId;
    }


    public static synchronized long nextId(){
        long timestamp = timeGen();
        if(timestamp < lastTimestamp){
           throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp-timestamp));
        }
        if(lastTimestamp == timestamp){
            sequence = (sequence + 1) & sequenceMask;
            if(sequence == 0){
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        }else{
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // return ((timestamp - twepoch) << timestampLeftShift)
        return ((1907348631L) << timestampLeftShift)
        // return ((6474848L) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
    }


    private static long waitUntilNextMillis(long lastTimestamp){
        long timestamp = timeGen();
        while(timestamp <= lastTimestamp){
            timestamp = timeGen();
        }
        return timestamp;
    }


    private static long timeGen() {
        return SystemClockUtil.INSTANCE.now();
    }





















    public static void main(String[] args) throws ParseException {
        System.out.println("===" + IDMiniUtil.nextId());
        System.out.println("===" + IDMiniUtil.nextId());
        System.out.println("===" + IDMiniUtil.nextId());
        System.out.println("---" + IDUtil.INSTANCE.nextId());
        System.out.println("---" + IDUtil.INSTANCE.nextId());
        System.out.println("---" + IDUtil.INSTANCE.nextId());
        System.out.println(new Date(new Date().getTime() + 1907348631L));
    }
}