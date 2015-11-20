package edu.utd.sbrp.web.cyberminer.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;

@Repository
public class IndexDao {
	
	@Autowired
	SolrClient solrClient;
	
	public void createIndex(KWICIndex index) {
	}
	
}
