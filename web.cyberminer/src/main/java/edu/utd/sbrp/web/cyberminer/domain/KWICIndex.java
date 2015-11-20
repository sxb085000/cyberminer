package edu.utd.sbrp.web.cyberminer.domain;

import java.util.List;

public class KWICIndex {
	
	public static final String DESC_DELIM = "[\\$(\\r?\\n)]";

	private String url;
	private String description;
	private List<String> indexLines;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getIndexLines() {
		return indexLines;
	}
	public void setIndexLines(List<String> indexLines) {
		this.indexLines = indexLines;
	}
	
}
