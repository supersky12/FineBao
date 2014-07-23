package com.yujunkang.fangxinbao.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;



/**
 * 
 * @date 2014-5-31
 * @author xieb
 * 
 */
public class SpecialTextUtils {
	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		try {
			char[] c = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (c[i] == 12288) {
					c[i] = (char) 32;
					continue;
				}
				if (c[i] > 65280 && c[i] < 65375)
					c[i] = (char) (c[i] - 65248);
			}
			return new String(c);
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 半角转全角，经试验表明正确，解决TextView自动换行问题
	 * 
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) {

		char c[] = input.toCharArray();

		for (int i = 0; i < c.length; i++) {

			if (c[i] == ' ') {

				c[i] = '\u3000';

			} else if (c[i] < '\177') {

				c[i] = (char) (c[i] + 65248);

			}

		}

		return new String(c);

	}

	// 替换、过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}


