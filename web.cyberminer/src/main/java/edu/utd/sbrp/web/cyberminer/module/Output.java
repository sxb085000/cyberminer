package edu.utd.sbrp.web.cyberminer.module;

import java.util.ArrayList;
import java.util.List;

public class Output {

	private IndexModule cs;
	private IndexModule alph;
	
	private List<String> csLines = new ArrayList<String>();
	private List<String> aLines = new ArrayList<String>();
	
	public Output(IndexModule cs, IndexModule alph) {
		this.cs = cs;
		this.alph = alph;
	}

	public void setup() {
		// get all csLines
		int csIndex = 0;
		String csLine = cs.getLine(csIndex++);
		do {
			csLines.add(csLine);
			csLine = cs.getLine(csIndex++);
		} while (csLine != null);
		
		
		// get all aLines;
		int aIndex = 0;
		String aLine = aLines.get(aIndex++);
		do {
			aLines.add(aLine);
			aLine = alph.getLine(aIndex++);
		} while (aLine != null);
	}
	

	public List<String> getCSLines() {
		return csLines;
	}
	
	public List<String> getALines() {
		return aLines;
	}
	
	
	
}
