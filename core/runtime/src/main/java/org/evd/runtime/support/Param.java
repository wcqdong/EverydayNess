package org.evd.runtime.support;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 *
 * 参数传递简单包装对象
 * 当传递参数数量不确定时，可以使用本类。
 *
 * 设置值：new Param("k1", v1, "k2", v2);
 *         new Param().put("k1", v1).put("k2", v2);
 *
 * 获取值：Human m = param.get("human");	//这里会自动转换类型，无需添加(User)显示强转
 */
public class Param{
	/** 当玩家只传入一个数据项时，自动用此KEY值，简化操作 */
	private static final String KEY_SINGLE = "KEY_SINGLE_PARAM";

	/**
	 * key -> value
	 */
	private final Map<String, Object> datas = new HashMap<>();

	public Param() {
		
	}

	public Param(Param param) {
		datas.putAll(param.datas);
	}

	/**
	 * 有参构造函数
	 * 内容为Key：Val形式 例如：new Param("user", user, "param", param)
	 * @param params
	 */
	public Param(Object...params) {
		// 无参 返回空即可
		if (params == null || params.length == 0) {
			return;
		}

		// 当数据仅有一项是 使用默认KEY作为值 简化操作
		if (params.length == 1) {
			put(KEY_SINGLE, params[0]);
		// 处理成对参数
		} else {
			int len = params.length;
			for (int i = 0; i < len; i += 2) {
				String key = (String) params[i];
				Object val = (Object) params[i + 1];

				put(key, val);
			}
		}
	}
	
	public Param put(Param param) {
		if (param != null) {			
			datas.putAll(param.datas);
		}
		return this;
	}

	/**
	 * 给对象添加新键值
	 * @param key
	 * @param value
	 * @return
	 */
	public Param put(String key, Object value) {
		datas.put(key, value);
		return this;
	}

	/**
	 * 返回相应键值返回的value
	 * 会自动根据接收值进行类型转换，无需强制转换处理。
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K> K get(String key) {
		return (K) datas.get(key);
	}

	/**
	 * 根据key获取对应的value，如果不存在key，返回defaultValue
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K> K getOrElse(String key, K defaultValue) {
		K result = (K) datas.get(key);
		if (result == null) {
			result = defaultValue;
		}

		return result;
	}

	/**
	 * 当只有单一数据项时 可以用此方法获取
	 * @return
	 */
	public <K> K get() {
		return get(KEY_SINGLE);
	}

	/**
	 * 返回一个布尔型数据
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		return get(key);
	}

	/**
	 * 当只有单一数据项时 可以用此方法获取
	 * 返回一个布尔型数据
	 * @return
	 */
	public boolean getBoolean() {
		return get();
	}

	/**
	 * 返回一个int型数据
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return get(key);
	}

	/**
	 * 当只有单一数据项时 可以用此方法获取
	 * 返回一个int型数据
	 * @return
	 */
	public int getInt() {
		return get();
	}

	/**
	 * 返回一个long型数据
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		return get(key);
	}

	/**
	 * 当只有单一数据项时 可以用此方法获取
	 * 返回一个long型数据
	 * @return
	 */
	public long getLong() {
		return get();
	}

	/**
	 * 返回一个String型数据
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return get(key);
	}

	/**
	 * 当只有单一数据项时 可以用此方法获取
	 * 返回一个String型数据
	 * @return
	 */
	public String getString() {
		return get();
	}

	/**
	 * 删除并返回指定key对应的值
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K> K remove(String key) {
		return (K) datas.remove(key);
	}

	/**
	 * 数据条数
	 * @return
	 */
	public int size() {
		return datas.size();
	}

	/**
	 * 是否包含某个key键
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return datas.containsKey(key);
	}

	/**
	 * 转化为数组
	 * @return
	 */
	public Object[] toArray() {
		Object[] arr;

		// 数据 返回空数组
		if (datas.isEmpty()) {
			arr = new Object[0];
		// 只有一个数据的是默认索引
		} else if (datas.size() == 1 && datas.containsKey(KEY_SINGLE)) {
			arr = new Object[] {datas.get(KEY_SINGLE)};
		// 成对出现的常规参数
		} else {
			// 返回数组长度是map的2倍
			arr = new Object[datas.size() * 2];
			// 转化为数组
			int index = 0;
			for (Entry<String, Object> e : datas.entrySet()) {
				arr[index++] = e.getKey();
				arr[index++] = e.getValue();
			}
		}

		return arr;
	}

	/**
	 * 获取全部Key值
	 * @return
	 */
	public Set<String> keySet() {
		return datas.keySet();
	}

	@Override
	public String toString() {
		return datas.toString();
	}

	/**
	 * 转成jsonString
	 * @return jsonString
	 */
	public String toJSONString() {
		return JSONObject.toJSONString(datas);
	}
}