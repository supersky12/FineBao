package com.yujunkang.fangxinbao.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.yujunkang.fangxinbao.app.FangXinBaoSettings;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class DBConstants {
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static final String TAG = "Notify";
	public static final String AUTHORITY = "com.yujunkang.fangxinbao.providers";

	// 不能实例
	private DBConstants() {
	}

	/**
	 * country table
	 */
	public static final class CountryTable implements BaseColumns {
		// 不能实例
		private CountryTable() {
		}

		/**
		 * 共享数据的路径
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/country");
		public static final String TABLE_NAME = "country";

		/**
		 * 中文名称
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";
		/**
		 * 英文 文名称
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ENGNAME = "engname";
		/**
		 * 创建时间
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CREATED_DATE = "created";
		/**
		 * 代码
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String COUNTRY_CODE = "country_code";

		/**
		 * 国家简称代码
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String COUNTRY_SIMPLE_CODE = "country_simple_code";

		/**
		 * 拼音首字母
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String COUNTRY_FIRST_LETTER = "country_first_letter";
		/**
		 * 默认的数据排序方式
		 */
		public static final String DEFAULT_SORT_ORDER = CREATED_DATE + " desc";
	}

	/**
	 * country table
	 */
	public static final class TemperatureHistoryRecordTable implements BaseColumns {
		// 不能实例
		private TemperatureHistoryRecordTable() {
		}

		/**
		 * 共享数据的路径
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/temperaturerecord");
		public static final String TABLE_NAME = "temperaturerecord";
		/**
		 * 宝宝id
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String BABY_ID = "babyid";
		/**
		 * 用户id
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String USER_ID = "userid";
		/**
		 * 创建时间
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CREATED_DATE = "created";
		/**
		 * 温度
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TEMPERATURE = "temperature";

		/**
		 * 默认的数据排序方式
		 */
		public static final String DEFAULT_SORT_ORDER = CREATED_DATE + " desc";
	}
}
