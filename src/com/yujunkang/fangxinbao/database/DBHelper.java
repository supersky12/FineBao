package com.yujunkang.fangxinbao.database;

import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.database.DBConstants.CountryTable;
import com.yujunkang.fangxinbao.database.DBConstants.TemperatureHistoryRecordTable;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class DBHelper {
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "fangxinbao.db";
	private static final int DATABASE_VERSION = 5;
	private static boolean isLock = false;

	private static DataBaseHelper DBHelper;
	private SQLiteDatabase m_DB;
	private static DBHelper instance;

	private DBHelper(Context ctx) {
		if (DBHelper == null) {
			DBHelper = new DataBaseHelper(ctx);
		}
	}

	public boolean IsReady() {
		return DBHelper != null ? true : false;
	}

	public void close() {
		if (DBHelper != null) {
			DBHelper.close();
		}
	}

	public void clearCache() {
		close();
		DBHelper = null;
	}

	public static DBHelper getDBInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	/**
	 * 批量插入一组国家信息
	 * 
	 * @param message
	 * @return
	 */
	public boolean batchInsertCountry(Collection<Country> datas) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertCountry()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			m_DB.beginTransaction();
			for (Country item : datas) {
				insertOrUpdateCountry(item);
			}
			m_DB.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
			return false;
		} finally {
			m_DB.endTransaction();
		}

	}

	/**
	 * 判断是否存在此温度记录 (babyid和时间戳可以确定唯一)
	 * 
	 * @param babyid
	 * @param time
	 * @return
	 */
	private boolean hasExistData(String babyid, String time) {
		Cursor dataCursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			dataCursor = m_DB.query(TemperatureHistoryRecordTable.TABLE_NAME,
					null, TemperatureHistoryRecordTable.BABY_ID + "=" + babyid
							+ " and "
							+ TemperatureHistoryRecordTable.CREATED_DATE + "="
							+ time, null, null, null, null);
			return dataCursor.moveToFirst();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "hasExistData： " + e.getMessage());
			}
			return false;
		} finally {
			if (dataCursor != null) {
				dataCursor.close();
			}

		}
	}

	/**
	 * 批量从服务器插入一组温度信息
	 * 
	 * @param message
	 * @return
	 */
	public void batchInsertTemperatureData(String userid, String babyid,
			Group<TemperatureData> datas) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertTemperatureData()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			if (datas != null && datas.size() > 0) {
				int dataCount = datas.size();
				for (int index = 0; index < dataCount; index++) {
					m_DB.beginTransaction();
					TemperatureData tempData = datas.get(index);
					long effectRowCount = m_DB
							.delete(TemperatureHistoryRecordTable.TABLE_NAME,
									TemperatureHistoryRecordTable.BABY_ID
											+ "="
											+ babyid
											+ " and "
											+ TemperatureHistoryRecordTable.CREATED_DATE
											+ "=" + tempData.getTime(), null);
					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"delete temperature = %s,time = %s",
								tempData.getTemperature(), tempData.getTime()));
					}
					effectRowCount = m_DB
							.insert(TemperatureHistoryRecordTable.TABLE_NAME,
									null,
									generateTemperatureContent(userid, babyid,
											tempData));

					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"insert temperature = %s,time = %s",
								tempData.getTemperature(), tempData.getTime()));
					}
					m_DB.setTransactionSuccessful();
				}
			} else {
				LoggerTool.d(TAG, "no data need to insert.");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (m_DB != null && m_DB.inTransaction()) {
				m_DB.endTransaction();
			}
		}
	}

	/**
	 * 插入温度信息
	 * 
	 * @param message
	 * @return
	 */
	public void InsertTemperatureData(String userid, String babyid,
			TemperatureData data) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertTemperatureData()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			if (data != null) {
				long effectRowCount = m_DB.insert(
						TemperatureHistoryRecordTable.TABLE_NAME, null,
						generateTemperatureContent(userid, babyid, data));
				if (effectRowCount > 0) {
					LoggerTool.d(
							TAG,
							String.format("temperature = %s,time = %s",
									data.getTemperature(), data.getTime()));
				}
			} else {
				LoggerTool.d(TAG, "no data need to insert.");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}

	}

	private ContentValues generateTemperatureContent(String userid,
			String babyid, TemperatureData data) {
		try {
			if (data != null) {
				ContentValues cv = new ContentValues();
				String temperature = data.getTemperature();
				String time = data.getTime();
				cv.put(TemperatureHistoryRecordTable.USER_ID, userid);
				cv.put(TemperatureHistoryRecordTable.BABY_ID, babyid);
				cv.put(TemperatureHistoryRecordTable.CREATED_DATE, time);
				cv.put(TemperatureHistoryRecordTable.TEMPERATURE, temperature);
				return cv;
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * 根据时间查询温度数据
	 * 
	 * @param babyid
	 * @param startdate  日期:yyyy-MM-dd
	 */
	public Group<TemperatureData> queryTemperatureDataByDate(String babyid,
			String startdate) {
		LoggerTool.d(TAG, "METHOD BEGIN: queryTemperatureData()");
		Group<TemperatureData> result = null;
		try {

			m_DB = DBHelper.getReadableDatabase();
			// 日期函数date(1092941466, 'unixepoch')
			String where = TemperatureHistoryRecordTable.BABY_ID + " = ? and "
					+ "date(" + TemperatureHistoryRecordTable.CREATED_DATE
					+ ", ? ,?) = ?";
			// 保存到数据库的时间是long型
			String sql = "select strftime(?,datetime("
					+ TemperatureHistoryRecordTable.CREATED_DATE
					+ ", ?,?)) as createtime , "
					+ TemperatureHistoryRecordTable.TEMPERATURE + " from "
					+ TemperatureHistoryRecordTable.TABLE_NAME + " where "
					+ where;
			Cursor cursor = m_DB.rawQuery(sql, new String[] { "%H:%M",
					"unixepoch", "localtime", babyid, "unixepoch", "localtime",
					startdate });
			if (cursor != null && cursor.moveToFirst()) {
				result = new Group<TemperatureData>();
				while (!cursor.isAfterLast()) {
					TemperatureData item = new TemperatureData();
					item.setTemperature(cursor.getString(cursor
							.getColumnIndex(TemperatureHistoryRecordTable.TEMPERATURE)));
					item.setTime(cursor.getString(cursor
							.getColumnIndex("createtime")));
					result.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}
		return result;
	}

	/**
	 * 根据拼音首字母获取国家信息
	 * 
	 * @param letter
	 * @return
	 */
	public Group<Country> queryCountryByFirstLetter(String letter) {
		Group<Country> result = new Group<Country>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_FIRST_LETTER + " = ?",
					new String[] { letter.toLowerCase() }, null, null,
					CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.setCountryCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_CODE)));
					country.setCountrySimpleCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
					country.setEngName(cursor.getString(cursor
							.getColumnIndex(CountryTable.ENGNAME)));
					country.setName(cursor.getString(cursor
							.getColumnIndex(CountryTable.NAME)));
					country.setFirstLetter(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
					result.add(country);
					cursor.moveToNext();
				}
			}
			return result;
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public Country queryCountryBySimpleCode(String simplecode) {
		Country result = new Country();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_SIMPLE_CODE + " = ?",
					new String[] { simplecode.toUpperCase() }, null, null,
					CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				result.setCountryCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_CODE)));
				result.setCountrySimpleCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
				result.setEngName(cursor.getString(cursor
						.getColumnIndex(CountryTable.ENGNAME)));
				result.setName(cursor.getString(cursor
						.getColumnIndex(CountryTable.NAME)));
				result.setFirstLetter(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
				return result;
			}

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 根据国家代码获取国家信息
	 * 
	 * @param message
	 * @return
	 */
	public Country queryCountryByCode(String code) {
		Country result = new Country();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_CODE + " = ?", new String[] { code },
					null, null, CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {

				result.setCountryCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_CODE)));
				result.setCountrySimpleCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
				result.setEngName(cursor.getString(cursor
						.getColumnIndex(CountryTable.ENGNAME)));
				result.setName(cursor.getString(cursor
						.getColumnIndex(CountryTable.NAME)));
				result.setFirstLetter(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
				return result;
			}

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 获取所有国家信息
	 * 
	 * @return
	 */
	public Group<Country> queryAllCountries() {
		Group<Country> result = new Group<Country>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_CODE + " is not null", null, null,
					null, CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.setCountryCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_CODE)));
					country.setCountrySimpleCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
					country.setEngName(cursor.getString(cursor
							.getColumnIndex(CountryTable.ENGNAME)));
					country.setName(cursor.getString(cursor
							.getColumnIndex(CountryTable.NAME)));
					country.setFirstLetter(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
					result.add(country);
					cursor.moveToNext();
				}
			}
			return result;
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	/**
	 * 插入或更新一条城市机场信息
	 * 
	 * @param message
	 * @return
	 */
	public void insertOrUpdateCountry(Country data) {
		LoggerTool.d(TAG, "METHOD BEGIN: insertOrUpdateCountry()");
		deleteCountry(data);
		insertCountry(data);

	}

	/**
	 * 删除一条国家信息
	 * 
	 * @param country
	 */
	public void deleteCountry(Country country) {
		LoggerTool.d(TAG, "METHOD BEGIN: deleteCountry()");
		try {
			if (m_DB.inTransaction() == false) {
				m_DB = DBHelper.getWritableDatabase();
			}
			String where = CountryTable.COUNTRY_CODE + " =? ";
			String[] whereValue = { country.getCountryCode() };
			int effectRow = m_DB.delete(CountryTable.TABLE_NAME, where,
					whereValue);
			if (effectRow > 0) {
				LoggerTool.d(TAG, "METHOD deleteCountry: successful");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		} finally {

		}

	}

	/**
	 * 插入一条国家信息
	 * 
	 * @param message
	 * @return
	 */
	public void insertCountry(Country country) {
		LoggerTool.d(TAG, "METHOD BEGIN: insertCountry()");
		try {
			if (m_DB.inTransaction() == false) {
				m_DB = DBHelper.getWritableDatabase();
			}
			ContentValues cv = new ContentValues();
			String countryCode = country.getCountryCode();
			cv.put(CountryTable.COUNTRY_CODE, countryCode);
			cv.put(CountryTable.COUNTRY_SIMPLE_CODE,
					country.getCountrySimpleCode());
			cv.put(CountryTable.CREATED_DATE, VeDate.getStringDateShort());
			cv.put(CountryTable.NAME, country.getName());
			cv.put(CountryTable.ENGNAME, country.getEngName());
			cv.put(CountryTable.COUNTRY_FIRST_LETTER, country.getFirstLetter());
			long effectRowCount = m_DB
					.insert(CountryTable.TABLE_NAME, null, cv);
			if (effectRowCount > 0) {
				LoggerTool.d(TAG, "insertSuccessful: insertCountry()");
			}

		} catch (Exception ex) {

		}

	}

	public static class DataBaseHelper extends SQLiteOpenHelper {
		DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (DEBUG) {
				Log.d(TAG, "DataBaseHelper Initializing.");
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				Log.d(TAG, "create table");

				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ CountryTable.TABLE_NAME + " (" + CountryTable._ID
						+ " INTEGER PRIMARY KEY," + CountryTable.COUNTRY_CODE
						+ " TEXT," + CountryTable.COUNTRY_SIMPLE_CODE
						+ " TEXT," + CountryTable.CREATED_DATE + " TEXT,"
						+ CountryTable.COUNTRY_FIRST_LETTER + " TEXT,"
						+ CountryTable.NAME + " TEXT," + CountryTable.ENGNAME
						+ " TEXT" + ");");
				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ TemperatureHistoryRecordTable.TABLE_NAME + " ("
						+ TemperatureHistoryRecordTable._ID
						+ " INTEGER PRIMARY KEY,"
						+ TemperatureHistoryRecordTable.BABY_ID + " TEXT,"
						+ TemperatureHistoryRecordTable.USER_ID + " TEXT,"
						+ TemperatureHistoryRecordTable.TEMPERATURE + " TEXT,"
						+ TemperatureHistoryRecordTable.CREATED_DATE
						+ " INTEGER" + ");");

			} catch (SQLException e) {
				// TODO: handle exception
				Log.e(TAG, e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + CountryTable.TABLE_NAME);
			onCreate(db);
		}
	}

}
