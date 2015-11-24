package edu.utd.sbrp.web.cyberminer.util;

import java.util.List;

public class StringUtil {

	/**
	 * concatenate list of strings with one whitespace(' ')
	 * @param wordList
	 * @return
	 */
	public static String toString(List<String> wordList) {
		String result = "";
		if(wordList != null) {
			StringBuilder sb = new StringBuilder();
			for (String word : wordList) {
				sb.append(word);
				sb.append(" ");
			}
			result = sb.toString().trim();
		}
		return result;
	}
	
	/**
	 * checks whether string is empty or not. here, empty means either
	 * null or empty string (after trimming)
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.trim().length() < 1;
	}
	
}
