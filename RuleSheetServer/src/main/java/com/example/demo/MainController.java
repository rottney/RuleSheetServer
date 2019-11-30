package com.example.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/home")
public class MainController {

	@Autowired
	private RuleSheetRepository ruleSheetRepository;

	/*
	 * Input Format:
	 * curl localhost:8080/home/add -d name=NAME -d contents="CONTENTS"
	 * */
	@PostMapping(path="/add")
	public @ResponseBody String addNewRuleSheet(@RequestParam String name, 
			@RequestParam String contents) {

		// Supported file name formats
		String expenseRoutingRegex = "ExpenseRouting_[0-9]+.txt";
		String complianceRegex = "Compliance_[0-9]+.txt";
		String submitComplianceRegex = "SubmitCompliance_[0-9]+.txt";

		if (!Pattern.matches(expenseRoutingRegex, name) 
				&& !Pattern.matches(complianceRegex, name) 
				&& !Pattern.matches(submitComplianceRegex, name)) {
			return "This application only supports ExpenseRouting, Compliance, "
					+ "and SubmitCompliance rules, in .txt format.\n"
					+ "Format:\t<RuleType>_<CustomerID>";
		}
		
		// Handle emojis by removing all emojis from contents
		String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(contents);
		contents = matcher.replaceAll("");

		// If rule sheet of the same name exists, create a new version.
		// Else, create version 1.
		Iterable<RuleSheet> result = ruleSheetRepository.findAll();
		Iterator<RuleSheet> iter = result.iterator();
		
		int currVersion = 0;
		while (iter.hasNext()) {
			RuleSheet rs = iter.next();
			if (rs.getName().compareTo(name) == 0) {
				if (rs.getVersion() > currVersion) {
					currVersion = rs.getVersion();
				}
			}
		}
		currVersion++;

		// Save rule sheet to database
		RuleSheet rs = new RuleSheet();
		rs.setName(name);
		rs.setVersion(currVersion);
		rs.setContents(contents);
		ruleSheetRepository.save(rs);
		return "File '"  + name + "', version " + currVersion + " has been added.";
	}

	// Return 10 most recent results
	@GetMapping(path = "/view")
	public @ResponseBody Iterable<RuleSheet> getRecentRuleSheets() {
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = (int) (ruleSheetRepository.count() - 9); i <= ruleSheetRepository.count(); i++) {
			ids.add((Integer) i);
		}
		return ruleSheetRepository.findAllById(ids);
	}

}
