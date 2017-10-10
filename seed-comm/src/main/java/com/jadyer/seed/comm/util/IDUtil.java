package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 基于Twitter的Snowflake算法实现的分布式ID生成器
 * ----------------------------------------------------------------------------------------------------------------
 * 用法如下
 * 第一种：IDUtil.INSTANCE.nextId()
 * 第二种：IDUtil.INSTANCE.setWorkerId(6).nextId()
 * 注意：调用setWorkerId()之后，下一次可直接调用IDUtil.INSTANCE.nextId()，它会使用setWorkerId()的值，因为它是全局的
 * ----------------------------------------------------------------------------------------------------------------
 * 这里生成ID使用的是syncronized进行同步的，syncronized使用在非静态方法上时，锁住的是当前对象，故本类采用单例模式
 * 因为多个线程使用该工具类时，只有让它们都锁的同一个对象，才能保证生成的ID不重复（否则每个线程取时间戳可以同时进行）
 * ----------------------------------------------------------------------------------------------------------------
 * SnowFlake的结构如下（每部分用 - 分开）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * <ul>
 *     <li>1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0</li>
 *     <li>41位时间截（毫秒级），注意：41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截减去开始时间截得到的值）。。。。。。这里的开始时间截，一般是我们的ID生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69</li>
 *     <li>10位的数据机器位，可以部署在1024个节点，包括 5 位datacenterId和 5 位workerId</li>
 *     <li>12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒（同一机器，同一时间截）产生4096个ID序号，加起来刚好64位，为一个Long型</li>
 * </ul>
 * SnowFlake的优点是：整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由数据中心ID和机器ID作区分）
 * 并且效率较高，根据IDUtilTest.java测试，最高每秒能产生两百万左右个ID（linux的话，应该会更高）
 * ----------------------------------------------------------------------------------------------------------------
 * @version v1.0
 * @history v1.0-->新建
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/10/9 17:26.
 */
@SuppressWarnings("FieldCanBeLocal")
public enum IDUtil {
    INSTANCE;

    //开始时间截，也就是时间起始标记点。作为基准，一般取系统的最近时间（一旦确定不能变动）
    //通过Chrome控制台执行new Date(1507564800000)，回车后发现这里表示的是：Tue Oct 10 2017 00:00:00 GMT+0800 (中国标准时间)
    //由于Snowflake算法41bit最多支持69.7年，所以本工具类最多可支持到2017+69=2086年
    private final long twepoch = 1507564800000L;
    //机器ID所占的位数
    private final long workerIdBits = 5L;
    //数据标识ID所占的位数
    private final long datacenterIdBits = 5L;
    //支持的最大机器ID，结果是31（这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数）
    //private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxWorkerId = ~(-1L << workerIdBits);
    //支持的最大数据标识ID，结果是31
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    //序列在ID中占的位数
    private final long sequenceBits = 12L;
    //机器ID向左移12位
    private final long workerIdShift = sequenceBits;
    //数据标识ID向左移17位（12+5）
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    //时间截向左移22位（5+5+12）
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    //生成序列的掩码，这里为4095（0b111111111111 = 0xfff = 4095）
    private final long sequenceMask = ~(-1L << sequenceBits);
    //上次生成ID的时间截
    private long lastTimestamp = -1L;
    //毫秒内序列(0 ~ 4095）
    private long sequence = 0L;
    //工作机器ID（0 ~ 31）
    private long workerId;
    //数据中心ID(0 ~ 31）
    private long datacenterId;

    IDUtil(){
        this.datacenterId = this.getDatacenterId(maxDatacenterId);
        this.workerId = this.getWorkerId(datacenterId, maxWorkerId);
        LogUtil.getLogger().info("计算得到：数据中心ID=[{}]，工作机器ID=[{}]", this.datacenterId, this.workerId);
    }


    private long getDatacenterId(long maxDatacenterId){
        long id = 0L;
        try{
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if(null == network){
                id = 1L;
            }else{
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long)mac[mac.length-1]) | (0x0000FF00 & (((long)mac[mac.length-2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        }catch(Throwable t){
            //ignore
        }
        return id;
    }


    private long getWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder sb = new StringBuilder();
        sb.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotEmpty(name)) {
            //GET jvmPid
            sb.append(name.split("@")[0]);
        }
        //MAC + PID 的 hashcode 获取16个低位
        return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }


    /**
     * 手工设置datacenterId（设置之后其它线程获取到的对象的datacenterId值就是这里设置的值，它是全局的）
     */
    public IDUtil setDatacenterId(long datacenterId){
        this.datacenterId = datacenterId;
        return INSTANCE;
    }


    /**
     * 手工设置workerId（设置之后其它线程获取到的对象的workerId值就是这里设置的值，它是全局的）
     */
    public IDUtil setWorkerId(long workerId){
        this.workerId = workerId;
        return INSTANCE;
    }


    /**
     * 获得下一个ID（该方法是线程安全的）
     * @return SnowflakeId
     */
    public synchronized long nextId(){
        long timestamp = this.timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，此时应抛异常
        //if(timestamp < lastTimestamp){
        //    throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp-timestamp));
        //}
        if(timestamp < lastTimestamp){
            long offset = lastTimestamp - timestamp;
            if(offset <= 5){
                try{
                    this.wait(offset << 1);
                    timestamp = this.timeGen();
                    if(timestamp < lastTimestamp){
                        throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
                    }
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }else{
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
            }
        }
        if(lastTimestamp == timestamp){
            //如果是同一时间生成的，则进行毫秒内序列
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if(sequence == 0){
                //阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else{
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位，并通过或运算，拼到一起，组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }


    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp){
        long timestamp = this.timeGen();
        while(timestamp <= lastTimestamp){
            timestamp = this.timeGen();
        }
        return timestamp;
    }


    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间（毫秒）
     */
    private long timeGen() {
        //return System.currentTimeMillis();
        return SystemClockUtil.INSTANCE.now();
    }
}