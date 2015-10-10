package edu.utdallas.sbrp.kwic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import edu.utdallas.sbrp.kwic.module.Alphabetizer;
import edu.utdallas.sbrp.kwic.module.CircularShift;
import edu.utdallas.sbrp.kwic.module.LineStorage;
import edu.utdallas.sbrp.kwic.module.Output;


public class MasterControl {

	public static void main(String[] args) {

		LineStorage ls = new LineStorage();
		CircularShift cs = new CircularShift(ls);
		Alphabetizer alph = new Alphabetizer(cs);
		Output output = new Output(cs, alph);
		
		
		System.out.println("type lines to create index ($ to indicate new line). Type \"EXIT\" to finish typing:");
		
		Scanner sc = new Scanner(System.in);
		
		int lineIndex = 0;
		while(sc.hasNextLine()) {
			String line = sc.nextLine(); 
			if(line.equalsIgnoreCase("EXIT")) {
				break;
			} else {
				List<String> lines = new ArrayList<String>();
				// look for '$' symbol
				if(line.contains("$")) {
					// split on "$"
					String[] lineSplit = line.split("$");
					lines = Arrays.asList(lineSplit);
				} else {
					lines.add(line);
				}
				
				for(int i = 0; i < lines.size(); ++i) {
					ls.setLine(lineIndex++, line);
				}
			}
		}
		sc.close();

		cs.setup();
		alph.setup();
	}
	
}
