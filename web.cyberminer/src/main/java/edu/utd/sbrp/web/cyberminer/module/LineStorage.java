package edu.utd.sbrp.web.cyberminer.module;

import java.util.ArrayList;
import java.util.List;

public class LineStorage implements IndexModule {
	
	List<String> lines = new ArrayList<String>();
	
	public LineStorage() {
	}
	
	public void process() {
		// not used
	}

	public void setLine(int l, String line) {
		// fill it with null
		int numToFill = (l - lines.size());
		
		for(int i = 0; i < numToFill; ++i) {
			lines.add(null); // init lines up to l-th line 
		}
		
		lines.add(line);
	}


	public String getLine(int l) {
		String line = null;
		try {
			line = lines.get(l);
		} catch (IndexOutOfBoundsException e) {
			// do nothing...
		}
		return line;
	}

}
