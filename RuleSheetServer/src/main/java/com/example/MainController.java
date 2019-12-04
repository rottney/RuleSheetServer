package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.Utils;

@Controller
@RequestMapping(path="/home")
public class MainController {

	@Autowired
	private RuleSheetRepository ruleSheetRepository;

	// Input Format:
	// curl localhost:5000/home/add -d name=NAME -d contents="CONTENTS"
	@PostMapping(path="/add")
	public @ResponseBody String addNewRuleSheet(@RequestParam String name, 
			@RequestParam String contents) {

		if (Utils.isFileFormatSupported(name)) {
			return "This application only supports ExpenseRouting, Compliance, "
					+ "and SubmitCompliance rules, in .txt format.\n"
					+ "Format:\t<RuleType>_<CustomerID>";
		}
		
		if (!Utils.isCustomerIdInRange(name)) {
			return "Your CustomerID is out of range.\n"
					+ "We only support values between 0 and 1499.";
		}
		
		// Handle emojis by simply removing them from file contents
		contents = Utils.removeEmojisFromContents(contents);
		
		// By design, versioning starts at 1.
		int currVersion = Utils.setVersion(ruleSheetRepository, name);
		
		Utils.saveToDatabase(ruleSheetRepository, name, currVersion, contents);
		return "File '"  + name + "', version " + currVersion + " has been added.";
	}

	// Return (up to) 10 most recent results, by descending id
	@GetMapping(path = "/view")
	public @ResponseBody Iterable<RuleSheet> viewRuleSheets() {	
		return Utils.getRecentRuleSheets(ruleSheetRepository);
	}

}
