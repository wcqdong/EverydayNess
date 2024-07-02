package org.evd.game.base;

import java.io.IOException;

/**
 * 可序列化接口
 * @author zenghongming
 * @date 2020/02/09 16:59
 */
public interface ISerializable {
    /**
     * 写之前
     * @param out OutputStream
     * @throws IOException IOException
     */
    default void beforeWrite(OutputStreamBase out) throws IOException {

    }

    /**
     * 将自己写入流中
     * @param out OutputStream
     * @throws IOException IOException
     */
    default void writeTo(OutputStreamBase out) throws IOException {

    }

    /**
     * 写入之后
     * @param out OutputStream
     * @throws IOException IOException
     */
    default void afterWrite(OutputStreamBase out) throws IOException {

    }

    /**
     * 读之前
     * @param in InputStream
     * @throws IOException IOException
     */
    default void beforeRead(InputStreamBase in) throws IOException {

    }

    /**
     * 从流中读取
     * @param in InputStream
     * @throws IOException IOException
     */
    default void readFrom(InputStreamBase in) throws IOException {

    }

    /**
     *  读完之后
     * @param in InputStream
     * @throws IOException IOException
     */
    default void afterRead(InputStreamBase in) throws IOException {

    }
}
