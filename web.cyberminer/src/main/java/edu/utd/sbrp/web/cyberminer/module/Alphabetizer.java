package edu.utd.sbrp.web.cyberminer.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Alphabetizer implements IndexModule {

	private final IndexModule formerModule;
	private List<String> aLines = new ArrayList<String>();
	
	public Alphabetizer(IndexModule formerModule) {
		this.formerModule = formerModule;
	}
	
	public void setup() {
		//get lines from formerModule
		int lineIndex = 0;
		List<String> tempList = new ArrayList<String>();
		do {
			String line = formerModule.getLine(lineIndex++);
			if(line == null) {
				break;
			} else {
				tempList.add(line);
			}
		} while (true);
		
		Collections.sort(tempList);
		for(int i = 0; i < tempList.size(); ++i) {
			setLine(i, tempList.get(i));;
		}
	}

	public void setLine(int l, String line) {
		int numToFill = aLines.size() - l + 1;
		for (int i = 0; i < numToFill; i++) {
			aLines.add(null);
		}
		aLines.set(l, line);
		
		System.out.println(l + "th a-line: " + line );
	}

	public String getLine(int l) {
		String line = null;
		try {
			line = aLines.get(l);
		} catch (IndexOutOfBoundsException e) {
			// do nothing...
		}
		return line;
	}

}
