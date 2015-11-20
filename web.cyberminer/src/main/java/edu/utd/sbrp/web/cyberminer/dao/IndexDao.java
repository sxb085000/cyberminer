package edu.utd.sbrp.web.cyberminer.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;
import edu.utd.sbrp.web.cyberminer.util.SolrPatchOperation;

@Repository
public class IndexDao {
	
	// solr field names
	private final String SOLR_FIELD_ID = "id";
	private final String SOLR_FIELD_NOISE_WORDS = "noise_words_ss";
	
	// solr doc id's
	private final String SOLR_DOC_ID_NOISE_WORD = "noise.word";
	
	// solr commitWitnin //TODO: parameterize this
	private final int commitWithinMs = 1000;
	
	@Autowired
	SolrClient solrClient;
	
	public void createIndex(KWICIndex index) {
	}
	
	
	public void addNoiseWord(String noiseWord) throws SolrServerException, IOException {
		SolrInputDocument solrInputDoc = new SolrInputDocument();
		solrInputDoc.setField(SOLR_FIELD_ID, SOLR_DOC_ID_NOISE_WORD);
		solrInputDoc.setField(SOLR_FIELD_NOISE_WORDS, getPatchValue(noiseWord, SolrPatchOperation.ADD));
		
		solrClient.add(solrInputDoc, commitWithinMs);
	}
	
	
	// helper methods
	public Map<String, Object> getPatchValue(Object value, SolrPatchOperation op) {
		Map<String, Object> patchValue = new HashMap<String, Object>();
		patchValue.put(op.getOperation(), value);
		return patchValue;
	}

}