package edu.utd.sbrp.web.cyberminer.util;

public enum SolrPatchOperation {
	ADD("add"),
	SET("set"),
	REMOVE("remove"),
	REMOVE_REGEX("removeregex"),
	INCREMENT("inc");
	
	
	private String operation;
	
	private SolrPatchOperation(String operation) {
		this.operation = operation;
	}
	
	/**
	 * returns operation string for solr
	 * @return
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * returns operation string for solr. this method returns same value as
	 * {@link #getOperation()}
	 */
	@Override
	public String toString() {
		return operation;
	}
	
}