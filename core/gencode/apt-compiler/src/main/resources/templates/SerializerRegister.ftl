package ${packageName};

import org.evd.game.base.ISerializable;
import org.evd.game.runtime.serialize.OutputStream;
import org.evd.game.runtime.serialize.InputStream;
import java.io.IOException;

<#if importPackages??>
<#list importPackages as package>
import ${package};
</#list>
</#if>

/**
*
* 注册序列化和反序列化函数指针
*/
final class ${className}{

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
<#list fields as field>
		OutputStream.registerSerializeWriteFunc(${field.key}, ${className}::${field.serializerName}Write);
</#list>
	}
	/**
	* 注册反序列化
	*/
	private static void registerRead(){
	<#list fields as field>
		InputStream.registerSerializeReadFunc(${field.key}, ${className}::${field.serializerName}Read);
	</#list>
	}
	/**
	* 注册反序列化枚举
	*/
	private static void registerReadEnum(){
	<#list enums as field>
		InputStream.registerSerializeReadEnumFunc(${field.key}, ${className}::${field.className}ReadEnum);
	</#list>
	}

<#list fields as field>
	public static void ${field.serializerName}Write(OutputStream out, ISerializable ser) throws IOException{
		${field.serializerFullName}.write(out, (${field.classFullName})ser);
	}
</#list>

<#list fields as field>
	public static ISerializable ${field.serializerName}Read(InputStream in) throws IOException{
		${field.classFullName} ${field.className?uncap_first} = new ${field.classFullName}();
		${field.serializerFullName}.read(in, ${field.className?uncap_first});
		return ${field.className?uncap_first};
	}
</#list>

<#list enums as field>
	public static Enum<?> ${field.className}ReadEnum(InputStream in, int ordinal) throws IOException{
		return ${field.classFullName}.values()[ordinal];
	}
</#list>
}