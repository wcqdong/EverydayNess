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
		OutputStream.registerSerializeWriteFunc(-1065350591, SerializerRegister::CallIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(326933455, SerializerRegister::CallPointIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(1593330110, SerializerRegister::CallResultIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(322098688, SerializerRegister::ChunkIOSerializerWrite);
		OutputStream.registerSerializeWriteFunc(1944252859, SerializerRegister::TickTimerIOSerializerWrite);
	}
	/**
	* 注册反序列化
	*/
	private static void registerRead(){
		InputStream.registerSerializeReadFunc(-1065350591, SerializerRegister::CallIOSerializerRead);
		InputStream.registerSerializeReadFunc(326933455, SerializerRegister::CallPointIOSerializerRead);
		InputStream.registerSerializeReadFunc(1593330110, SerializerRegister::CallResultIOSerializerRead);
		InputStream.registerSerializeReadFunc(322098688, SerializerRegister::ChunkIOSerializerRead);
		InputStream.registerSerializeReadFunc(1944252859, SerializerRegister::TickTimerIOSerializerRead);
	}
	/**
	* 注册反序列化枚举
	*/
	private static void registerReadEnum(){
	}

	public static void CallIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.runtime.call.CallIOSerializer.write(out, (org.evd.game.runtime.call.Call)ser);
	}
	public static void CallPointIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.runtime.call.CallPointIOSerializer.write(out, (org.evd.game.runtime.call.CallPoint)ser);
	}
	public static void CallResultIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.runtime.call.CallResultIOSerializer.write(out, (org.evd.game.runtime.call.CallResult)ser);
	}
	public static void ChunkIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.runtime.ChunkIOSerializer.write(out, (org.evd.game.runtime.Chunk)ser);
	}
	public static void TickTimerIOSerializerWrite(OutputStream out, ISerializable ser) throws IOException{
		org.evd.game.runtime.TickTimerIOSerializer.write(out, (org.evd.game.runtime.TickTimer)ser);
	}

	public static ISerializable CallIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.runtime.call.Call call = new org.evd.game.runtime.call.Call();
		org.evd.game.runtime.call.CallIOSerializer.read(in, call);
		return call;
	}
	public static ISerializable CallPointIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.runtime.call.CallPoint callPoint = new org.evd.game.runtime.call.CallPoint();
		org.evd.game.runtime.call.CallPointIOSerializer.read(in, callPoint);
		return callPoint;
	}
	public static ISerializable CallResultIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.runtime.call.CallResult callResult = new org.evd.game.runtime.call.CallResult();
		org.evd.game.runtime.call.CallResultIOSerializer.read(in, callResult);
		return callResult;
	}
	public static ISerializable ChunkIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.runtime.Chunk chunk = new org.evd.game.runtime.Chunk();
		org.evd.game.runtime.ChunkIOSerializer.read(in, chunk);
		return chunk;
	}
	public static ISerializable TickTimerIOSerializerRead(InputStream in) throws IOException{
		org.evd.game.runtime.TickTimer tickTimer = new org.evd.game.runtime.TickTimer();
		org.evd.game.runtime.TickTimerIOSerializer.read(in, tickTimer);
		return tickTimer;
	}

}