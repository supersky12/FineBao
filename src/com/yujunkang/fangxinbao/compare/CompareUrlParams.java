package com.yujunkang.fangxinbao.compare;

import java.util.Comparator;

import org.apache.http.NameValuePair;

/**
 * 
 * @date 2014-5-22
 * @author xieb
 * 
 */
public class CompareUrlParams implements Comparator<NameValuePair> {

	@Override
	public int compare(NameValuePair lhs, NameValuePair rhs) {
		// TODO Auto-generated method stub
		return lhs.getName().compareTo(rhs.getName());
	}

}
