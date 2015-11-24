package edu.utd.sbrp.web.cyberminer.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoiseFilter implements IndexModule {

	private final IndexModule formerModule;
	private final Set<String> noiseWords;
	private List<String> filteredLines = new ArrayList<String>();
	
	public NoiseFilter(IndexModule formerModule, Set<String> noiseWords) {
		super();
		this.formerModule = formerModule;
		
		// store in lower case
		this.noiseWords = new HashSet<String>();
		for (String noiseWord : noiseWords) {
			this.noiseWords.add(noiseWord.toLowerCase());
		}
	}

	@Override
	public void process() {
		//get lines from formerModule
		int lineIndex = 0;
		int filteredLineIndex = 0;
		do {
			String line = formerModule.getLine(lineIndex++);
			if(line == null) {
				break;
			} else {
				if(!shouldFilter(line)) {
					setLine(filteredLineIndex++, line);
				}
			}
		} while (true);
	}

	@Override
	public void setLine(int l, String line) {
		int numToFill = filteredLines.size() - l + 1;
		for (int i = 0; i < numToFill; i++) {
			filteredLines.add(null);
		}
		filteredLines.set(l, line);
		
		System.out.println(l + "th filtered-line: " + line );
	}

	@Override
	public String getLine(int l) {
		String line = null;
		
		try {
			line = filteredLines.get(l);
		} catch (Exception e) {
			// do nothing...
		}
		return line;
	}
	
	private boolean shouldFilter(String line) {
		boolean result = false;
		
		// get the first word
		try {
			String firstWord = line.split("\\s+")[0];
			result = noiseWords.contains(firstWord.toLowerCase());
		} catch (Exception e) {
			System.err.println("Cannot filter line \"" + line + "\"");
		}
		
		return result;
	}

}
