package edu.utd.sbrp.web.cyberminer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.utd.sbrp.web.cyberminer.dao.IndexDao;
import edu.utd.sbrp.web.cyberminer.domain.KWICIndex;
import edu.utd.sbrp.web.cyberminer.domain.RestfulResult;
import edu.utd.sbrp.web.cyberminer.module.Alphabetizer;
import edu.utd.sbrp.web.cyberminer.module.CircularShift;
import edu.utd.sbrp.web.cyberminer.module.LineStorage;
import edu.utd.sbrp.web.cyberminer.module.Output;

@Controller
@RequestMapping("/indices")
public class IndexController {

	@Autowired
	private IndexDao indexDao;


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody RestfulResult createIndex(@RequestBody KWICIndex kwicIndex) {
		RestfulResult result = new RestfulResult();

		// create index
		String inputLine = kwicIndex.getDescription();
		// split on "newlines" (either reall carriage return or dollar sign '$')
		String[] lineSplit = inputLine.split(KWICIndex.DESC_DELIM);
		List<String> lines = new ArrayList<String>(Arrays.asList(lineSplit));
		LineStorage lineStorage = new LineStorage();
		CircularShift circularShift = new CircularShift(lineStorage);
		Alphabetizer alphabetizer = new Alphabetizer(circularShift);
		Output output = new Output(circularShift, alphabetizer);

		int lineIndex = 0;
		for (String line : lines) {
			lineStorage.setLine(lineIndex++, line);
		}

		circularShift.setup();
		alphabetizer.setup();
		output.setup();
		kwicIndex.setIndexLines(output.getALines());

		// store to solr
		indexDao.createIndex(kwicIndex);
		
		result.success(kwicIndex);

		return result;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody RestfulResult searchIndex(@RequestParam(name="q", required=true) String queryString,
			@RequestParam(name="offset", defaultValue = "0") int offset,
			@RequestParam(name="limit", defaultValue = "50") int limit) {
		RestfulResult result = new RestfulResult();



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
			indexDao.addNoiseWord(noiseWord);

			//			result.success();
		} catch (Exception e) {
		}

		return result;
	}

	@RequestMapping(value = "/noise", method = RequestMethod.GET)
	public @ResponseBody RestfulResult getNoiseWords() {
		RestfulResult result = new RestfulResult();

		//		indexDao.createIndex(null);

		return result;
	}

	@RequestMapping(value = "/noise/{noiseWord}", method = RequestMethod.DELETE)
	public @ResponseBody RestfulResult deleteNoiseWords(@PathVariable(value="noiseWord") String noiseWord) {
		RestfulResult result = new RestfulResult();

		//		indexDao.createIndex(null);

		return result;
	}


}
