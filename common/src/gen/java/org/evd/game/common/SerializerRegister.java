package org.evd.game.common;

import org.evd.game.base.ISerializable;
import org.evd.game.runtime.serialize.OutputStream;
import org.evd.game.runtime.serialize.InputStream;
import java.io.IOException;


/**
*
* 注册序列化和反序列化函数指针
*/
final class SerializerRegister{

	/**
	* 注册
	*/
	static void register(){
		registerWrite();
		registerRead();
		registerReadEnum();
	}
	/**

	/**
	* 注册序列化
	*/
	private static void registerWrite(){
		OutputStream.registerSerializeWriteFunc(2005701922, SerializerRegister::TestSerIOSerializerWrite);
	}
	/**
	* 注册反序列化
	*/
	private static void registerRead(){
		InputStream.registerSerializeReadFunc(2005701922, SerializerRegister::TestSerIOSerializerRead);
	}
	/**
	* 注册反序列化枚举
	*/
	private static void registerReadEnum(){
	}

	public static void TestSerIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.common.TestSerIOSerializer.write(out, (org.evd.game.common.TestSer)ser);
	}

	public static ISerializable TestSerIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.common.TestSer testSer = new org.evd.game.common.TestSer();
		org.evd.game.common.TestSerIOSerializer.read(in, testSer);
		return testSer;
	}

}