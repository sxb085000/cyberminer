package edu.utdallas.sbrp.kwic.util;

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
	
}