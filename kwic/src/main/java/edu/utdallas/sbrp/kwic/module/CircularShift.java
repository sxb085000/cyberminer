package edu.utdallas.sbrp.kwic.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utdallas.sbrp.kwic.IndexModule;
import edu.utdallas.sbrp.kwic.util.StringUtil;

public class CircularShift implements IndexModule {

	private final IndexModule formerModule;
	private List<String> csLines = new ArrayList<String>();
	
	
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
			String[] words = line.split("\\s+");
			List<String> wordList = new ArrayList<String>(Arrays.asList(words));
			
			int numLines = csLines.size();
			
			for(int i = 0; i < words.length; ++i) {
				setLine(numLines + i, StringUtil.toString(wordList));
				wordList.add(wordList.remove(0));
			}
		}
	}
	
}
