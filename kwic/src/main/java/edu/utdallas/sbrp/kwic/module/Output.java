package edu.utdallas.sbrp.kwic.module;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.sbrp.kwic.IndexModule;

public class Output {

	private IndexModule cs;
	private IndexModule alph;
	
	private List<String> csLines = new ArrayList<String>();
	private List<String> aLines = new ArrayList<String>();
	
	public Output(IndexModule cs, IndexModule alph) {
		this.cs = cs;
		this.alph = alph;
	}

	public void set() {
		// get all csLines
		int csIndex = 0;
		String csLine = "";
		do {
			csLine = cs.getLine(csIndex++);
			csLines.add(csLine);
		} while (csLine != null);
		
		
		// get all aLines;
		int aIndex = 0;
		String aLine = "";
		do {
			aLine = alph.getLine(aIndex++);
			aLines.add(aLine);
		} while (aLine != null);
	}
	
	public void print() {
		// go through each cs line and print to cs text area
		// go through each a line and print to alph text area
	}
	
}
