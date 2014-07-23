package com.yujunkang.fangxinbao.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import com.yujunkang.fangxinbao.utility.TypeUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/*
 * 用于缓存应用程序周期内的http client 连接的cookies
 */
public class PersistentCookieStore implements CookieStore {

	private static final String COOKIE_PREFS = "CookiePrefsFile";
	private static final String COOKIE_NAME_STORE = "names";
	private static final String COOKIE_NAME_PREFIX = "cookie_";

	private SharedPreferences _cookiePrefs;
	private Context _context;

	private static PersistentCookieStore _instance;

	public synchronized static PersistentCookieStore getInstance(Context context) {
		if (_instance == null) {
			_instance = new PersistentCookieStore(context);
		}
		return _instance;
	}

	private final ConcurrentHashMap<String, Cookie> _cookies;

	public PersistentCookieStore(Context context) {
		_context = context;
		_cookiePrefs = _context.getSharedPreferences(COOKIE_PREFS, 0);
		_cookies = new ConcurrentHashMap<String, Cookie>();
		
		clearExpired(new Date());
	}

	@Override
	public void addCookie(Cookie cookie) {
		String name = cookie.getName() + cookie.getDomain() + cookie.getPath();

		// Save cookie into local store, or remove if expired
		if (!cookie.isExpired(new Date())) {
			_cookies.put(name, cookie);
		} else {
			_cookies.remove(name);
		}
		
		// Save cookie into persistent store
		SharedPreferences.Editor prefsWriter = _cookiePrefs.edit();
        prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", _cookies.keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        prefsWriter.commit();
	}

	@Override
	public void clear() {
		
		// Clear cookies from persistent store
		SharedPreferences.Editor prefsWriter = _cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.commit();
		
     // Clear cookies from local store
		_cookies.clear();
	}

	@Override
	public boolean clearExpired(Date date) {
		boolean clearedAny = false;
		SharedPreferences.Editor prefsWriter = _cookiePrefs.edit();

		for (ConcurrentHashMap.Entry<String, Cookie> entry : _cookies.entrySet()) {
			String name = entry.getKey();
			Cookie cookie = entry.getValue();
			if (cookie.isExpired(date)) {
				
				// Clear cookies from local store
                _cookies.remove(name);

                // Clear cookies from persistent store
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);

                // We've cleared at least one
                clearedAny = true;
			}
		}
		
		// Update names in persistent store
        if(clearedAny) {
            prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", _cookies.keySet()));
        }
        
        prefsWriter.commit();
        
		return clearedAny;
	}

	@Override
	public List<Cookie> getCookies() {
		
		if (_cookies == null || _cookies.size() <= 0) {
			// 避免应用程序回收，内存被释放
	        String storedCookieNames = _cookiePrefs.getString(COOKIE_NAME_STORE, null);
	        if(storedCookieNames != null) {
	            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
	            for(String name : cookieNames) {
	                String encodedCookie = _cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
	                if(encodedCookie != null) {
	                    Cookie decodedCookie = decodeCookie(encodedCookie);
	                    if(decodedCookie != null) {
	                        _cookies.put(name, decodedCookie);
	                    }
	                }
	            }
	        }
		}
		return _cookies == null ? null : new ArrayList<Cookie>(_cookies.values());
	}
	
	/*
	 * 获取cookie的json字符串
	 */
	public String getCookieJson() {
		List<Cookie> cookies = getCookies();
		if (cookies == null || cookies.size() <= 0) {
			return "";
		}
		
		List<Map<String, String>> cookiesMap = new ArrayList<Map<String, String>>();
		for (Cookie cookie : cookies) {
			Map<String, String> cookieMap = new HashMap<String, String>();
			cookieMap.put("domain", cookie.getDomain());
			cookieMap.put("name", cookie.getName());
			cookieMap.put("path", cookie.getPath());
			cookieMap.put("value", cookie.getValue());
			cookiesMap.add(cookieMap);
		}
		return TypeUtils.mapArrayToJson(cookiesMap);
	}
	
	
	
	
	
	
	
	
	
	
	/* ==================================================
	 * 									cookies 加密&序列化
	 *  ==================================================
	 */ 
	protected String encodeCookie(SerializableCookie cookie) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (Exception e) {
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected Cookie decodeCookie(String cookieStr) {
        byte[] bytes = hexStringToByteArray(cookieStr);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
           ObjectInputStream ois = new ObjectInputStream(is);
           cookie = ((SerializableCookie)ois.readObject()).getCookie();
        } catch (Exception e) {
           e.printStackTrace();
        }

        return cookie;
    }
    
    @SuppressLint("DefaultLocale")
	protected String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if(v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i=0; i<len; i+=2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}