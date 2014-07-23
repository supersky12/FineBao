package com.yujunkang.fangxinbao.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureType;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class TypeUtils {
	private static final String TAG = "TypeUtils";
	private static final int INT_CONVERT_ERROR = -1;
	private static final double DOUBLE_CONVERT_ERROR = -1d;
	private static final float FLOAT_CONVERT_ERROR = -1.0f;
	private static final long LONG_CONVERT_ERROR = -1l;
	private static final int BUFFER_SIZE = 1024;

	/*
	 * String to int
	 */
	public static int StringToInt(final String str) {
		if (TextUtils.isEmpty(str)) {
			return INT_CONVERT_ERROR;
		}
		if (!str.matches("^-?[0-9]\\d*$")) {
			return INT_CONVERT_ERROR;
		}
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return INT_CONVERT_ERROR;
	}

	/*
	 * object to int
	 */
	public static int ObjToInt(final Object obj) {
		try {
			return Integer.parseInt(obj.toString());
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return INT_CONVERT_ERROR;
	}

	/*
	 * string to double
	 */
	public static double StringToDouble(final String str) {
		if (TextUtils.isEmpty(str)) {
			return DOUBLE_CONVERT_ERROR;
		}
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return DOUBLE_CONVERT_ERROR;
	}

	/*
	 * string to float
	 */
	public static float StringToFloat(final String str) {
		if (TextUtils.isEmpty(str)) {
			return FLOAT_CONVERT_ERROR;
		}
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return FLOAT_CONVERT_ERROR;
	}

	/*
	 * string to long
	 */
	public static long StringToLong(final String str) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return LONG_CONVERT_ERROR;
	}

	/*
	 * string to long
	 */
	public static long StringToLong(final String str, long def) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return def;
	}

	/*
	 * obj to long
	 */
	public static long ObjToLong(final Object obj) {
		try {
			return Long.parseLong(obj.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return INT_CONVERT_ERROR;
	}

	/*
	 * get string from Map<String, Object>
	 */
	public static String StrFromObjMap(final Map<String, Object> map, String key) {
		if (map == null || !map.containsKey(key)) {
			return "";
		}
		Object obj = map.get(key);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/*
	 * get Map<String, Object> from Map<String, Object>
	 */
	public static Map<String, Object> MapFromObjMap(
			final Map<String, Object> map, String key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}

		Object obj = map.get(key);
		if (obj == null || !(obj instanceof Map)) {
			return null;
		}

		return (Map<String, Object>) obj;
	}

	/*
	 * get List<Map<String, Object> from Map<String, Object>
	 */
	public static List<Map<String, Object>> MapArrayFromObjMap(
			final Map<String, Object> map, String key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}

		Object obj = map.get(key);
		if (obj == null || !(obj instanceof List)) {
			return null;
		}

		return (List<Map<String, Object>>) obj;
	}

	public static <T> List<T> ArrayFromObjMap(final Map<String, Object> map,
			String key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}

		Object obj = map.get(key);
		if (obj == null || !(obj instanceof List)) {
			return null;
		}

		return (List<T>) obj;
	}

	/*
	 * get int from Map<String, Object>
	 */
	public static double doubleFromObjMap(final Map<String, Object> map,
			String key) {
		if (map == null || !map.containsKey(key)) {
			return DOUBLE_CONVERT_ERROR;
		}
		Object obj = map.get(key);
		if (obj == null) {
			return DOUBLE_CONVERT_ERROR;
		}
		return StringToDouble(obj.toString());
	}

	public static String convertInputStreamToString(InputStream in) {
		String result = "";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte buffer[] = new byte[BUFFER_SIZE];
		int len = -1;
		try {
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			result = new String(os.toByteArray(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return result;
	}

	public static byte[] convertInputStreamToBytes(InputStream in) {
		byte[] result = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = -1;
		try {
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			result = os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return result;
	}

	/*
	 * String to int
	 */
	public static int StringToInt(final String str, int defaultValue) {
		if (TextUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		return defaultValue;
	}

	/*
	 * 讲序列化对象转成字节流
	 */
	public static byte[] serializableToBytes(Serializable object) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 讲字节流转转为对象
	 */
	public static Object bytesToserializable(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bais != null) {
					bais.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * 将数组转换成集合数组
	 */
	public static <T> List<T> arrayToArrayList(T[] array) {
		if (array == null) {
			return null;
		}

		if (array.length <= 0) {
			return new ArrayList<T>();
		}

		List<T> arrayList = new ArrayList<T>();
		for (T t : array) {
			arrayList.add(t);
		}

		return arrayList;
	}

	/*
	 * 验证 {listKey:[{...},{...}]} 保证...里面数据有效
	 */
	public static boolean isArrayMapDataValid(Map<String, Object> result,
			String listKey) {
		if (result == null || result.size() <= 0
				|| !result.containsKey(listKey)) {
			return false;
		}

		List<Map<String, Object>> list = TypeUtils.MapArrayFromObjMap(result,
				listKey);
		if (list == null || list.size() <= 0) {
			return false;
		}

		Map<String, Object> obj = list.get(0);
		if (obj == null || obj.size() <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * 将Map转成Json
	 * 
	 * @param map可以多层嵌套
	 *            ，但是要求数据都是基本类型
	 * @return
	 */
	public static String mapToJsonStr(Map<?, ?> map) {
		try {
			GsonBuilder buidler = new GsonBuilder();
			Gson gson = buidler.create();
			return gson.toJson(map);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 将Map转成Json
	 * 
	 * @param map可以多层嵌套
	 *            ，但是要求数据都是基本类型
	 * @return
	 */
	public static Map<String, Object> jsonStrToMap(String str) {
		try {
			GsonBuilder buidler = new GsonBuilder();
			Gson gson = buidler.create();
			return gson.fromJson(str, Map.class);

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将Map集合转成Json
	 * 
	 * @param map可以多层嵌套
	 *            ，但是要求数据都是基本类型
	 * @return
	 */
	public static String mapArrayToJson(List<?> mapArray) {
		try {
			GsonBuilder buidler = new GsonBuilder();
			Gson gson = buidler.create();
			return gson.toJson(mapArray);
		} catch (Exception e) {
			return "";
		}
	}

	/*
	 * 从Map中获取Object
	 */
	public static Object objFromMap(Map<String, Object> map, String key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}

		return map.get(key);
	}

	/*
	 * 获取list
	 */
	public static List<Object> listFromMap(Map<String, Object> map, String key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}

		Object obj = map.get(key);
		if (obj == null || !(obj instanceof List)) {
			return null;
		}

		return (List<Object>) obj;
	}

	/*
	 * map转成集合
	 */
	public static <T> List<T> listToMap(Map<String, T> map) {
		List<T> list = new ArrayList<T>();
		if (map == null || map.size() <= 0) {
			return list;
		}

		for (Entry<String, T> entry : map.entrySet()) {
			list.add(entry.getValue());
		}

		return list;
	}

	public static String ensureNotNull(String value) {
		return value == null ? "" : value;
	}

	// Returns the next power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0 or
	// the answer overflows.
	public static int nextPowerOf2(int n) {
		if (n <= 0 || n > (1 << 30))
			throw new IllegalArgumentException();
		n -= 1;
		n |= n >> 16;
		n |= n >> 8;
		n |= n >> 4;
		n |= n >> 2;
		n |= n >> 1;
		return n + 1;
	}

	public static String getTemperatureScaleValue(int length, float value,
			Locale language, int temperatureType, Context context) {
		try {
			int temperatureFormat = R.string.temperature_format_c;
			if (value < 0) {
				value = DataConstants.DEFAULT_NORMAL_TEMPERATURE;
			}
			float valueF = value;
			// 没有设置过温度标准
			if (temperatureType == -1) {
				if (!language.getCountry().equals(
						DataConstants.DEFAULT_COUNTRY_CODE)) {
					valueF = Utils.temperatureCToF(value);
					temperatureFormat = R.string.temperature_format_f;
				}
			} else {
				if (temperatureType == TemperatureType.Fahrenheit.ordinal()) {
					valueF = Utils.temperatureCToF(value);
					temperatureFormat = R.string.temperature_format_f;
				}
			}
			BigDecimal decimal = new BigDecimal(valueF);

			String temperatureValue = String.valueOf(decimal.setScale(length,
					BigDecimal.ROUND_HALF_UP).doubleValue());
			return context.getString(temperatureFormat, temperatureValue);
		} catch (Exception ex) {

		}
		return "";
	}
}
