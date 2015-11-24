package edu.utd.sbrp.web.cyberminer.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;
import edu.utd.sbrp.web.cyberminer.util.SolrPatchOperation;

@Repository
public class IndexDao {
	
	// solr field names
	private final String SOLR_FIELD_ID = "id";
	private final String SOLR_FIELD_VERSION = "_version_";
	private final String SOLR_FIELD_DESCRIPTION = "description_s";
	private final String SOLR_FIELD_DESCRIPTIONS = "descriptions_ss";
	private final String SOLR_FIELD_NOISE_WORDS = "noise_words_ss";
	
	// solr doc id's
	private final String SOLR_DOC_ID_NOISE_WORD = "noise.word";
	
	// solr commitWitnin //TODO: parameterize this
	private final int commitWithinMs = 1000;
	
	private final String[] SOLR_RESERVED_WORDS = {"AND", "OR", "NOT"};
	private final Set<String> reservedWords = new HashSet<String>(Arrays.asList(SOLR_RESERVED_WORDS));
	
	@Autowired
	SolrClient solrClient;
	
	/**
	 * here our index building will be batch-mode
	 * @param index
	 */
	public void createIndex(KWICIndex index) throws SolrServerException {
		// Create input document
		SolrInputDocument solrInputdoc = new SolrInputDocument();
		solrInputdoc.setField(SOLR_FIELD_ID, index.getUrl());
		solrInputdoc.setField(SOLR_FIELD_DESCRIPTION, index.getDescription());
		solrInputdoc.setField(SOLR_FIELD_DESCRIPTIONS, index.getIndexLines());
		solrInputdoc.setField(SOLR_FIELD_VERSION, -1); // the document must NOT exist

		try {
			solrClient.add(solrInputdoc, commitWithinMs);
		} catch (Exception e) {
			if (e instanceof RemoteSolrException && ((RemoteSolrException) e).code() == SolrException.ErrorCode.CONFLICT.code) {
				throw new SolrServerException("index for url \"" + index.getUrl() + "\" exists already.");
			}
			throw new SolrServerException(e);
		}
	}
	
	public List<KWICIndex> searchIndex(String queryString, int offset, int limit) {
		List<KWICIndex> foundIndices = new ArrayList<KWICIndex>();
		
		// prepare query string append '*' after each line
		String query = "";
		String[] queryWords = queryString.split("\\s+");
		
		boolean wordAppended = false;
		for (String queryWord : queryWords) {
			if (!reservedWords.contains(queryWord)) {
				if(wordAppended) {
					queryWord = "AND " + queryWord;
				}
				queryWord += "*";
				wordAppended = true;
			} else {
				wordAppended = false;
			}
			query += queryWord + " ";
		}
			
		SolrQuery solrQuery = new SolrQuery(query.trim());
		solrQuery.set("df", SOLR_FIELD_DESCRIPTIONS);
		
		try {
			QueryResponse response = solrClient.query(solrQuery);
			SolrDocumentList solrDocs = response.getResults();
			for (SolrDocument solrDoc : solrDocs) {
				foundIndices.add(toKWICIndex(solrDoc));
			}
		} catch (Exception e) {
		}
		
		return foundIndices;
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

	
	private KWICIndex toKWICIndex(SolrDocument solrDocument) {
		KWICIndex kwicIndex = new KWICIndex();
		
		// extract values
		String url = (String) solrDocument.getFieldValue(SOLR_FIELD_ID);
		String description = (String) solrDocument.getFieldValue(SOLR_FIELD_DESCRIPTION);
		
		// initialize kwicIndex
		kwicIndex.setUrl(url);
		kwicIndex.setDescription(description);
		
		return kwicIndex;
	}
}