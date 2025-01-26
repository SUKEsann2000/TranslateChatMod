package com.translate.translatechat;

import java.util.regex.Pattern;

class ReplaceText {

	/**
	 * @param args
	 */
	public static String main(String regex,String reql,String text) {
		return myReplaceAll(regex,reql,text);
	}
	/**
	 * 大文字小文字を区別せずにreplaceAllします
	 * @param regex 置き換えたい文字列
	 * @param reql 置換後文字列
	 * @param text 置換対象文字列
	 */
	public static String myReplaceAll(String regex ,String reql,String text){
	    String retStr = "";
		retStr = Pattern.compile(regex,Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(reql);
		return retStr;
	}
}