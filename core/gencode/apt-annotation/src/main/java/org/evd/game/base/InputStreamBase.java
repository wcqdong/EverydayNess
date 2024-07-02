package org.evd.game.base;

import java.io.IOException;

/**
 * 输入流
 * @author zenghongming
 * @date 2020/02/09 16:58
 */
public interface InputStreamBase {
    /**
     * 读一个布尔值
     * 与{@link OutputStreamBase}的writeBoolean配对使用
     * @return boolean值
     * @throws IOException IOException
     */
    boolean readBoolean() throws IOException;

    /**
     * 读一个字节
     * 与{@link OutputStreamBase}的writeByte配对使用
     * @return byte值
     * @throws IOException IOException
     */
    byte readByte() throws IOException;

    /**
     * 读一个字节数组
     * 与{@link OutputStreamBase}的writeBytes配对使用
     * @return byte数组
     * @throws IOException IOException
     */
    byte[] readBytes() throws IOException;

    /**
     * 读一个短整型
     * 与{@link OutputStreamBase}的writeByte配对使用
     * @return short值
     * @throws IOException IOException
     */
    short readShort() throws IOException;

    /**
     * 读一个整型
     * 与{@link OutputStreamBase}的writeInt配对使用
     * @return int值
     * @throws IOException IOException
     */
    int readInt() throws IOException;

    /**
     * 读一个单精度浮点型
     * 与{@link OutputStreamBase}的writeFloat配对使用
     * @return float值
     * @throws IOException IOException
     */
    float readFloat() throws IOException;

    /**
     * 读一个长整型
     * 与{@link OutputStreamBase}的writeLong配对使用
     * @return long值
     * @throws IOException IOException
     */
    long readLong() throws IOException;

    /**
     * 读一个双精度浮点型
     * 与{@link OutputStreamBase}的writeDouble配对使用
     * @return double值
     * @throws IOException IOException
     */
    double readDouble() throws IOException;

    /**
     * 读一个字符串
     * 与{@link OutputStreamBase}的writeString配对使用
     * @return String值
     * @throws IOException IOException
     */
    String readString() throws IOException;

    /**
     * 从流中读取一个可序列化的对象
     * @return T 可序列化的对象
     * @throws IOException IOException
     */
    <T> T read() throws IOException;

}
