package edu.utd.sbrp.web.cyberminer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.utd.sbrp.web.cyberminer.dao.IndexDao;
import edu.utd.sbrp.web.cyberminer.domain.RestfulResult;

@Controller
@RequestMapping("indices/")
public class IndexController {

	@Autowired
	private IndexDao indexDao;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody RestfulResult test() {
		RestfulResult result = new RestfulResult();
		
		result.success("hello world!");
		
		return result;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody RestfulResult createIndex(String inputLine) {
		RestfulResult result = new RestfulResult();
		
//		indexDao.createIndex(null);

		return result;
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody RestfulResult searchIndex(@RequestParam(name="q", required=true) String queryString) {
		RestfulResult result = new RestfulResult();
		
		
		return result;
	}
}
