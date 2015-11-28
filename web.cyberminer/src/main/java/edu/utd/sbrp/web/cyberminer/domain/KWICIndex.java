package edu.utd.sbrp.web.cyberminer.domain;

import java.util.Date;
import java.util.List;

public class KWICIndex {
	
	public static final String DESC_DELIM = "[\\$(\\r?\\n)]";

	private String url;
	private String description;
	private List<String> indexLines;
	private Date createdDate;
	
	public KWICIndex() {
	}
	
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
		if(description != null) {
			this.description = description.trim();
		}
	}
	public List<String> getIndexLines() {
		return indexLines;
	}
	public void setIndexLines(List<String> indexLines) {
		this.indexLines = indexLines;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}
