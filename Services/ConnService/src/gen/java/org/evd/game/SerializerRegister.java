package org.evd.game;

import org.evd.game.base.ISerializable;
import org.evd.game.runtime.serialize.OutputStream;
import org.evd.game.runtime.serialize.InputStream;
import java.io.IOException;


/**
*
* 注册序列化和反序列化函数指针
*/
public final class SerializerRegister{

	/**
	* 注册
	*/
	public static void register(){
		registerWrite();
		registerRead();
		registerReadEnum();
	}
	/**

	/**
	* 注册序列化
	*/
	private static void registerWrite(){
		OutputStream.registerSerializeWriteFunc(39258998, SerializerRegister::CoInfoIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(131608374, SerializerRegister::ConnInfoIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(-180281561, SerializerRegister::ConnInfoBaseIOSerializerWrite);
	}
	/**
	* 注册反序列化
	*/
	private static void registerRead(){
		InputStream.registerSerializeReadFunc(39258998, SerializerRegister::CoInfoIOSerializerRead);
		InputStream.registerSerializeReadFunc(131608374, SerializerRegister::ConnInfoIOSerializerRead);
		InputStream.registerSerializeReadFunc(-180281561, SerializerRegister::ConnInfoBaseIOSerializerRead);
	}
	/**
	* 注册反序列化枚举
	*/
	private static void registerReadEnum(){
		InputStream.registerSerializeReadEnumFunc(-441921041, SerializerRegister::TestEnumReadEnum);
	}

	public static void CoInfoIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.ConnService.CoInfoIOSerializer.write(out, (org.evd.game.ConnService.CoInfo)ser);
	}
	public static void ConnInfoIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.ConnService.ConnInfoIOSerializer.write(out, (org.evd.game.ConnService.ConnInfo)ser);
	}
	public static void ConnInfoBaseIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.ConnService.ConnInfoBaseIOSerializer.write(out, (org.evd.game.ConnService.ConnInfoBase)ser);
	}

	public static ISerializable CoInfoIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.ConnService.CoInfo coInfo = new org.evd.game.ConnService.CoInfo();
		org.evd.game.ConnService.CoInfoIOSerializer.read(in, coInfo);
		return coInfo;
	}
	public static ISerializable ConnInfoIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.ConnService.ConnInfo connInfo = new org.evd.game.ConnService.ConnInfo();
		org.evd.game.ConnService.ConnInfoIOSerializer.read(in, connInfo);
		return connInfo;
	}
	public static ISerializable ConnInfoBaseIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.ConnService.ConnInfoBase connInfoBase = new org.evd.game.ConnService.ConnInfoBase();
		org.evd.game.ConnService.ConnInfoBaseIOSerializer.read(in, connInfoBase);
		return connInfoBase;
	}

	public static Enum<?> TestEnumReadEnum(InputStream in, int ordinal) throws IOException{
		return org.evd.game.ConnService.TestEnum.values()[ordinal];
	}
}