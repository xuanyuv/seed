package com.jadyer.seed.comm.tmp.id;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class IDUtils {
    private static final IDUtils INASTNCE = new IDUtils();
    private static final Logger logger = LoggerFactory.getLogger(IDUtils.class);
    private static final long epoch = 1504842098117L;
    private static final long workerIdBits = 10L;
    private static final long tenantIdBits = 3L;
    private static final long maxWorkerId = 1023L;
    private static final long maxTenantId = 7L;
    private static long sequence = 0L;
    private static final long sequenceBits = 10L;
    private static final Random random = new Random();
    private static final long timestampBits = 40L;
    private static final long workerIdShift = 10L;
    private static final long timestampShift = 20L;
    private static final long tenantIdShift = 60L;
    private static final long sequenceMask = 1023L;
    private static long lastTimestamp = -1L;

    public IDUtils() {
    }

    public static IDUtils getInstance() {
        return INASTNCE;
    }

    public static synchronized long nextId(long workerId, long tenantCode) {
        if (workerId <= 1023L && workerId >= 0L) {
            if (tenantCode <= 7L && tenantCode >= 0L) {
                long timestamp = System.currentTimeMillis();
                if (lastTimestamp == timestamp) {
                    sequence = sequence + 1L & 1023L;
                    if (sequence == 0L) {
                        timestamp = nextMillis(lastTimestamp);
                    }
                } else {
                    sequence = (long)random.nextInt(128);
                }

                if (timestamp < lastTimestamp) {
                    logger.error(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
                    throw new RuntimeException(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
                } else {
                    lastTimestamp = timestamp;
                    return tenantCode << 60 | timestamp - 1504842098117L << 20 | workerId << 10 | sequence;
                }
            } else {
                throw new RuntimeException(String.format("tenant Id can't be greater than %d or less than 0", 7L));
            }
        } else {
            throw new RuntimeException(String.format("worker Id can't be greater than %d or less than 0", 1023L));
        }
    }

    private static long nextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = System.currentTimeMillis(); timestamp <= lastTimestamp; timestamp = System.currentTimeMillis()) {
        }

        return timestamp;
    }


    public static Long getWorkerId() {
        String workerId = null;
        try {
            if (StringUtils.isEmpty(workerId)) {
                String hostAddress = LocalIpAddress.resolveLocalIp();
                if (StringUtils.isNotBlank(hostAddress)) {
                    String[] ipSegments = hostAddress.split("\\.");
                    if (ipSegments != null && ipSegments.length == 4) {
                        workerId = String.valueOf(Integer.parseInt(ipSegments[0]) + Integer.parseInt(ipSegments[1]) + Integer.parseInt(ipSegments[2]) + Integer.parseInt(ipSegments[3]));
                    }
                }
            }
        } catch (Exception var5) {
            logger.error("error ip generate app.worker.id.", var5);
        } finally {
            if (StringUtils.isEmpty(workerId)) {
                logger.warn("use random to generate generate app.worker.id");
                workerId = String.valueOf(random.nextInt(1023));
            }

        }
        return Long.valueOf(workerId);
    }


    /**
     * 这里生成的ID是连续数字的
     */
    public static void main(String[] args) {
        Long tenantCode = 1L;
        Long workerId = getWorkerId();
        System.out.println(nextId(workerId, tenantCode));
        System.out.println(nextId(workerId, tenantCode));
        System.out.println(nextId(workerId, tenantCode));
    }
}