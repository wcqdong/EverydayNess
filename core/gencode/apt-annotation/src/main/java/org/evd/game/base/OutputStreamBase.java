package org.evd.game.base;

import java.io.IOException;

/**
 * 输出流
 * @author zenghongming
 * @date 2020/02/09 16:59
 */
public interface OutputStreamBase {
    /**
     * 写一个布尔值
     * 与{@link InputStreamBase}的readBoolean配对使用
     * @param value bool值
     * @throws IOException IOException
     */
    void writeBoolean(boolean value) throws IOException;

    /**
     * 写一个字节
     * 与{@link InputStreamBase}的readByte配对使用
     * @param value byte值
     * @throws IOException IOException
     */
    void writeByte(byte value) throws IOException;

    /**
     * 写字节数组
     * @param buf 字节数组
     * @param offset 偏移
     * @param length 长度
     * @throws IOException IOException
     */
    void writeBytes(byte[] buf, int offset, int length) throws IOException;

    /**
     * 写一个短整型值
     * 与{@link InputStreamBase}的readShort配对使用
     * @param value short值
     * @throws IOException IOException
     */
    void writeShort(short value) throws IOException;

    /**
     * 写一个整型值
     * 与{@link InputStreamBase}的readInt配对使用
     * @param value int值
     * @throws IOException IOException
     */
    void writeInt(int value) throws IOException;

    /**
     * 写一个单精度浮点型
     * 与{@link InputStreamBase}的readFloat配对使用
     * @param value float值
     * @throws IOException IOException
     */
    void writeFloat(float value) throws IOException;

    /**
     * 写一个长整型值
     * 与{@link InputStreamBase}的readLong配对使用
     * @param value long值
     * @throws IOException IOException
     */
    void writeLong(long value) throws IOException;

    /**
     * 写一个双精度浮点型
     * 与{@link InputStreamBase}的readDouble配对使用
     * @param value double值
     * @throws IOException IOException
     */
    void writeDouble(double value) throws IOException;

    /**
     * 写一个字符串
     * 与{@link InputStreamBase}的readString配对使用
     * @param str String值
     * @throws IOException IOException
     */
    void writeString(String str) throws IOException;

    /**
     * 将可序列化对象写入数据到流中
     * 与{@link InputStreamBase}的read配对使用
     * @param object 支持序列化的对象
     * @throws IOException IOException
     */
    void write(Object object) throws IOException;
}
