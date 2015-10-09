package edu.utdallas.sbrp.kwic;

public interface IndexModule {

	void setup();
	void setLine(int l, String line);
	String getLine(int l);
	
	
}
