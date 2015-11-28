package edu.utd.sbrp.web.cyberminer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.utd.sbrp.web.cyberminer.dao.IndexDao;
import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;
import edu.utd.sbrp.web.cyberminer.domain.RestfulResult;
import edu.utd.sbrp.web.cyberminer.domain.RestfulResultStatus;
import edu.utd.sbrp.web.cyberminer.module.Alphabetizer;
import edu.utd.sbrp.web.cyberminer.module.CircularShift;
import edu.utd.sbrp.web.cyberminer.module.LineStorage;
import edu.utd.sbrp.web.cyberminer.module.NoiseFilter;
import edu.utd.sbrp.web.cyberminer.util.SolrPatchOperation;
import edu.utd.sbrp.web.cyberminer.util.StringUtil;

@Controller
@RequestMapping("/indices")
public class IndexController {

	@Autowired
	private IndexDao indexDao;

	private final Pattern urlPattern = Pattern.compile("http://\\w+[.]\\w+[.](edu|com|org|net)");
	private final Pattern descPattern = Pattern.compile("\\w+([ $.]|\\w+)*");
	
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody RestfulResult createIndex(@RequestBody KWICIndex kwicIndex) {
		RestfulResult result = new RestfulResult();

		// check url first
		if (StringUtil.isEmpty(kwicIndex.getUrl()) || 
				!urlPattern.matcher(kwicIndex.getUrl()).matches()) {
			result.fail("url provided (\"" + kwicIndex.getUrl() + "\")is not in the right format. must be in format \"" + urlPattern.pattern() + "\".");
		}
		// then check description
		if (StringUtil.isEmpty(kwicIndex.getDescription()) ||
				!descPattern.matcher(kwicIndex.getDescription()).matches()) {
			result.fail("Required field 'description' (" + kwicIndex.getDescription() + ") is not in the format \"" + descPattern.pattern() + "\".");
		}
		
		if(result.getStatus() == RestfulResultStatus.fail) {
			return result;
		}
		
		// create index
		String inputLine = kwicIndex.getDescription();
		// split on "newlines" (either reall carriage return or dollar sign '$')
		String[] lineSplit = inputLine.split(KWICIndex.DESC_DELIM);
		List<String> lines = new ArrayList<String>(Arrays.asList(lineSplit));
		LineStorage lineStorage = new LineStorage();
		CircularShift circularShift = new CircularShift(lineStorage);
		NoiseFilter noiseFilter = new NoiseFilter(circularShift, indexDao.noiseWords());
		Alphabetizer alphabetizer = new Alphabetizer(noiseFilter);

		int lineIndex = 0;
		for (String line : lines) {
			lineStorage.setLine(lineIndex++, line);
		}

		circularShift.process();
		noiseFilter.process();
		alphabetizer.process();
		kwicIndex.setIndexLines(alphabetizer.getLines());

		// store to solr
		try {
			indexDao.createIndex(kwicIndex);
			result.success(kwicIndex);
		} catch (SolrServerException e) {
			result.fail(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody RestfulResult searchIndex(@RequestParam(name="q", required=true) String queryString,
			@RequestParam(name="offset", defaultValue = "0") int offset,
			@RequestParam(name="limit", defaultValue = "50") int limit) {
		RestfulResult result = new RestfulResult();

		try {
			result.success(indexDao.searchIndex(queryString, offset, limit));
		} catch (Exception e) {
			result.error(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/spellcheck", method = RequestMethod.GET)
	public @ResponseBody RestfulResult spellCheck(@RequestParam(name="q", required=true) String queryString) {
		RestfulResult result = new RestfulResult();
		
		try {
			result.success(indexDao.spellCheck(queryString));
		} catch (Exception e) {
			result.error(e.getMessage());
		}
		
		return result;
	}

	@RequestMapping(value = "/search/suggest", method = RequestMethod.GET)
	public @ResponseBody RestfulResult suggestSearchIndex(@RequestParam(name="q", required=true) String queryString,
			@RequestParam(name="limit", defaultValue = "10") int limit) {
		RestfulResult result = new RestfulResult();

		try {
			result.success(indexDao.suggestSearchIndex(queryString, limit));
		} catch (Exception e) {
			result.error(e.getMessage());
		}

		return result;
	}

	// for noise word filtering
	@RequestMapping(value = "/noise", method = RequestMethod.POST)
	public @ResponseBody RestfulResult addNoiseWord(@RequestBody String noiseWord) {
		RestfulResult result = new RestfulResult();

		// check to see if it really is a word
		if(noiseWord.split("\\s+").length > 1) {
			result.fail("noise word should be a word (no space allowed)");
			return result;
		}

		try {
			if (!indexDao.updateWords(Arrays.asList(noiseWord), SolrPatchOperation.ADD)) {
				result.addMessage("noise word " + noiseWord + " already exists");
			}
			result.success(indexDao.noiseWords());
		} catch (Exception e) {
			result.error(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/noise", method = RequestMethod.GET)
	public @ResponseBody RestfulResult getNoiseWords() {
		RestfulResult result = new RestfulResult();
		
		try {
			result.success(indexDao.getNoiseWords());
		} catch (Exception e) {
			result.error(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/noise/{noiseWord}", method = RequestMethod.DELETE)
	public @ResponseBody RestfulResult deleteNoiseWords(@PathVariable(value="noiseWord") String noiseWord) {
		RestfulResult result = new RestfulResult();

		// check to see if it really is a word
		if(noiseWord.split("\\s+").length > 1) {
			result.fail("noise word should be a word (no space allowed)");
			return result;
		}
		
		try {
			indexDao.updateWords(Arrays.asList(noiseWord), SolrPatchOperation.REMOVE);
		} catch (Exception e) {
			result.error(e.getMessage());
		}
		return result;
	}


}
