package edu.utd.sbrp.web.cyberminer.module;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utd.sbrp.web.cyberminer.util.StringUtil;

public class CircularShift implements IndexModule {

	private final IndexModule formerModule;
	private List<String> csLines = new ArrayList<String>();
	
	private static final Pattern wordPtrn = Pattern.compile("\\b\\w+\\b");
	
	public CircularShift(IndexModule formerModule) {
		this.formerModule = formerModule;
	}
	
	public void setup() {
		// get lines from lineStorage
		int lineIndex = 0;
		do {
			String line = formerModule.getLine(lineIndex++);
			if(line == null) {
				break;
			} else {
				shift(line);
			}
			
		} while (true);
	}

	public void setLine(int l, String line) {
		int numToFill = csLines.size() - l + 1;
		for (int i = 0; i < numToFill; i++) {
			csLines.add(null);
		}
		csLines.set(l, line);
		
		System.out.println(l + "th cs-line: " + line );
	}

	public String getLine(int l) {
		String line = null;
		try {
			line = csLines.get(l);
		} catch (IndexOutOfBoundsException e) {
			// do nothing...
		}
		return line;
	}

	private void shift(String line) {
		if(line != null) {
			List<String> wordList = new ArrayList<String>();
			Matcher m = wordPtrn.matcher(line);
			while(m.find()) {
				wordList.add(m.group());
			}
			
			int numWords = wordList.size();
			int numLines = csLines.size();
			
			for(int i = 0; i < numWords; ++i) {
				setLine(numLines + i, StringUtil.toString(wordList));
				wordList.add(wordList.remove(0));
			}
		}
	}
	
}
