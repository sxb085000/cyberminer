package edu.utdallas.sbrp.kwic.module;

import java.util.ArrayList;
import java.util.List;

public class LineStorageChar {
	
	List<List<List<Character>>> lines = new ArrayList<List<List<Character>>>();
	
	public LineStorageChar() {
	}
	
	public void setup() {
		// not used
	}

	public void setChar(int l, int w, int c, char ch) {
		initLine(l);
		initWord(l,w);
		List<Character> word = lines.get(l).get(w);
		
		if(word == null) { // null check
			word = new ArrayList<Character>();
		}
		
		int numToFill = c - word.size();
		for(int i = 0; i < numToFill; ++i) {
			word.add(null);
		}
		word.add(ch);
	}

	public void getChar(int l, int w, int c) {
		// TODO Auto-generated method stub

	}

	public void getWord(int l, int w) {
		// TODO Auto-generated method stub

	}
	
	private void initLine(int l) {
		try {
			List<List<Character>> line = lines.get(l);
			if(line == null) {
				line = new ArrayList<List<Character>>();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// fill it with null
			int numToFill = (l - lines.size()) + 1;
			
			for(int i = 0; i < numToFill; ++i) {
				lines.add(new ArrayList<List<Character>>()); // init lines up to l-th line 
				lines.get(i).add(new ArrayList<Character>()); // init first word as well (for efficiency)
			}
		}
	}
	
	private void initWord(int l, int w) {
		List<List<Character>> line = null;
		try {
			line = lines.get(l);
		} catch (ArrayIndexOutOfBoundsException e) {
			initLine(l);
			line = lines.get(l);
		}
		
		try {
			List<Character> word = line.get(w);
			if(word == null) {
				word = new ArrayList<Character>();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// fill empty words
			int numToFill = (w - line.size()) + 1;
			
			for(int i = 0; i < numToFill; ++i) {
				line.add(new ArrayList<Character>()); 
			}
		}
	}

}
