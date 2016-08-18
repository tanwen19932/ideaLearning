package tw.utils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 一致性Hash的一种算法 高效低碰撞率
 */
public class Murmurs {

    /**
     * murmur hash算法实现
     */
    public static Long hash(byte[] key) {

        ByteBuffer buf = ByteBuffer.wrap(key);
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    public static Long hash(String key) {
        return hash(key.getBytes());
    }

    /**
     * Long转换成无符号长整型（C中数据类型）
     */
    public static BigDecimal readUnsignedLong(long value) {
        if (value >= 0)
            return new BigDecimal(value);
        long lowValue = value & 0x7fffffffffffffffL;
        return BigDecimal.valueOf(lowValue).add(BigDecimal.valueOf(Long.MAX_VALUE)).add(BigDecimal.valueOf(1));
    }

    /**
     * 返回long对应的十六进制字符串
     */
    public static String hashStr(String key) {
        Long l = hash(key);
        String s = "";
        for (int i = 0; i < 64 - Long.toBinaryString(l).length(); i++) {
            s += "0";
        }
        s += Long.toBinaryString(l);
        return get16(s);
    }

    // 将64bit换成十六进制字符
    public static String get16(String binary) {
        String array = "";
        for (int i = 0; i < binary.length(); i = i + 4) {
            int c1 = binary.charAt(i) - '0';
            int c2 = binary.charAt(i + 1) - '0';
            int c3 = binary.charAt(i + 2) - '0';
            int c4 = binary.charAt(i + 3) - '0';
            int num = (c1 << 3) + (c2 << 2) + (c3 << 1) + c4;
            // System.out.println(num);
            // System.out.println(Integer.toHexString(num).toUpperCase());
            array += Integer.toHexString(num & 0xF).toUpperCase();
        }
        return array;
    }
}