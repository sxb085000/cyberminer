package edu.utd.sbrp.web.cyberminer.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;
import edu.utd.sbrp.web.cyberminer.util.SolrPatchOperation;

@Repository
public class IndexDao {
	
	// solr field names
	private final String SOLR_FIELD_ID = "id";
	private final String SOLR_FIELD_VERSION = "_version_";
	private final String SOLR_FIELD_DESCRIPTION = "description";
	private final String SOLR_FIELD_DESCRIPTIONS = "desc_index";
	private final String SOLR_FIELD_CREATION_DATE = "created_tdt";
	private final String SOLR_FIELD_NOISE_WORDS = "noise_words_ss";
	
	// solr doc id's
	private final String SOLR_DOC_ID_NOISE_WORD = "noise.word";
	
	// solr commitWitnin //TODO: parameterize this
	private final int commitWithinMs = 1000;
	
	// default values
	private final String[] SOLR_RESERVED_WORDS = {"AND", "OR", "NOT"};
	private final Set<String> reservedWords = new HashSet<String>(Arrays.asList(SOLR_RESERVED_WORDS));

	private final String[] DEFAULT_NOISE_WORDS = {"a", "the", "of"};
	private Set<String> noiseWords = new HashSet<String>(Arrays.asList(DEFAULT_NOISE_WORDS));
	public Set<String> noiseWords() {
		return noiseWords;
	}

	
	@Autowired
	private SolrClient solrClient;

	@Autowired
	private ResourceLoader resourceLoader;
	
	@PostConstruct
	public void init() {
		// bootstrap noisewords
		try {
			noiseWords = getNoiseWords();
			if(noiseWords.isEmpty()) {
				updateWords(new HashSet<String>(Arrays.asList(DEFAULT_NOISE_WORDS)), SolrPatchOperation.ADD);
			}
		} catch (SolrServerException | IOException e) {
			System.err.println("Cannot bootstrap noisewords from solr. Default noiseWords " + DEFAULT_NOISE_WORDS + " assumed");
			e.printStackTrace();
		}
	}
	
	
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
		solrInputdoc.setField(SOLR_FIELD_CREATION_DATE, new Date());
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
				if(queryWord.endsWith(")") && queryWord.length() > 1) {
					queryWord.replace(")", "*)");
				} else {
					queryWord += "*";
				}
				wordAppended = true;
			} else {
				wordAppended = false;
			}
			query += queryWord + " ";
		}
			
		SolrQuery solrQuery = new SolrQuery(query.trim());
		solrQuery.set("df", SOLR_FIELD_DESCRIPTIONS);
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		
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

	public String spellCheck(String queryString) throws SolrServerException, IOException {
		String similarWord = "";
		
		if(queryString == null || queryString.length() < 1) {
			return similarWord;
		}
		
		String[] queryTokens = queryString.split("\\s+");
		
		// prepare query string append '*' after each line
		SolrQuery solrQuery = new SolrQuery("\"" + queryString.trim() + "\""); // fuzzy search
		solrQuery.set("df", SOLR_FIELD_DESCRIPTIONS);
		
		QueryResponse response = solrClient.query(solrQuery);
		SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();

		for (String token : queryTokens) {
			String suggestion = spellCheckResponse.getFirstSuggestion(token);
			similarWord += " " + ((suggestion != null) ? suggestion : token);
		}
		
		return similarWord.trim();
	}
	
	public List<String> suggestSearchIndex(String queryString, int limit) throws SolrServerException, IOException {
		List<String> suggestions = new ArrayList<String>();
		Set<String> suggestionSet = new HashSet<String>();
		
		SolrQuery solrQuery = new SolrQuery(queryString + "*");
		solrQuery.set("df", SOLR_FIELD_DESCRIPTIONS);
		solrQuery.setRows(limit);

		QueryResponse response = solrClient.query(solrQuery);
		SolrDocumentList solrDocs = response.getResults();
		for (SolrDocument solrDoc : solrDocs) {
			@SuppressWarnings("unchecked")
			List<String> descriptions = (List<String>) solrDoc.getFieldValue(SOLR_FIELD_DESCRIPTIONS);
			if(descriptions != null && descriptions.size() > 0) {
				for (String description : descriptions) {
					if (description.indexOf(queryString) == 0) { // if it starts with the query string
						// extract the word
						String suggestion = extractContainedWord(description, queryString);
						System.out.println(suggestion);
						suggestionSet.add(suggestion);
					}
				}
			}
		}
		
		suggestions.addAll(suggestionSet);
		Collections.sort(suggestions);
		return suggestions;
	} 
	
	public boolean updateWords(Collection<String> noiseWords, SolrPatchOperation op) throws SolrServerException, IOException {
		boolean success = false;
		
		if(op == SolrPatchOperation.ADD && this.noiseWords.containsAll(noiseWords)) {
			// don't do anything
			return success;
		}
		
		SolrInputDocument solrInputDoc = new SolrInputDocument();
		solrInputDoc.setField(SOLR_FIELD_ID, SOLR_DOC_ID_NOISE_WORD);
		solrInputDoc.setField(SOLR_FIELD_NOISE_WORDS, getPatchValue(noiseWords, op));
		
		solrClient.add(solrInputDoc, commitWithinMs);
		success = true;
		
		//update local cache as well
		switch (op) {
			case ADD :
				this.noiseWords.addAll(noiseWords);
				break;
			case REMOVE :
				this.noiseWords.removeAll(noiseWords);
			default :
				break;
		}
		
		return success;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getNoiseWords() throws SolrServerException, IOException { 
		SolrDocument solrDoc = solrClient.getById(SOLR_DOC_ID_NOISE_WORD);
		if(solrDoc != null) {
			noiseWords = new HashSet<String>((List<String>) solrDoc.getFieldValue(SOLR_FIELD_NOISE_WORDS));
		} else {
			noiseWords = new HashSet<String>();	
		}
		
		return noiseWords;
		
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
		Date createdDate = (Date) solrDocument.getFieldValue(SOLR_FIELD_CREATION_DATE);
		
		// initialize kwicIndex
		kwicIndex.setUrl(url);
		kwicIndex.setDescription(description);
		kwicIndex.setCreatedDate(createdDate);
		
		return kwicIndex;
	}
	
	private String extractContainedWord(String line, String query) {
		String containedWord = "";
		
		int queryLength = query.length();
		
		if(line.length() <= queryLength) {
			return query;
		}
		
		// if you have another word after this,
		int nextWordIndex = line.indexOf(' ', queryLength);
		if(nextWordIndex > queryLength - 1) {
			containedWord = line.substring(0, nextWordIndex);
		} else {
			containedWord = line;
		}
		
		return containedWord;
	}
}