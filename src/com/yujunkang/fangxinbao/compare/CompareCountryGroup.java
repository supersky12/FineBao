package com.yujunkang.fangxinbao.compare;

import java.util.Comparator;

import org.apache.http.NameValuePair;

import com.yujunkang.fangxinbao.model.Group;

/**
 * 
 * @date 2014-5-30
 * @author xieb
 * 
 */
public class CompareCountryGroup implements Comparator<Group> {

	@Override
	public int compare(Group arg0, Group arg1) {
		// TODO Auto-generated method stub
		return arg0.getType().compareTo(arg1.getType());
	}

}
