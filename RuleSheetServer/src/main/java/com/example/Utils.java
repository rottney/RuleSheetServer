package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	// POST HELPERS
	
	public static boolean isFileFormatSupported(String name) {
		String expenseRoutingRegex = "ExpenseRouting_[0-9]+.txt";
		String complianceRegex = "Compliance_[0-9]+.txt";
		String submitComplianceRegex = "SubmitCompliance_[0-9]+.txt";
		
		if (Pattern.matches(expenseRoutingRegex, name) 
				|| Pattern.matches(complianceRegex, name) 
				|| Pattern.matches(submitComplianceRegex, name)) {
			return false;
		}
		return true;	
	}
	
	public static boolean isCustomerIdInRange(String name) {
		if (Integer.valueOf(name.split("_")[1].split("\\.")[0]) < 0
				|| Integer.valueOf(name.split("_")[1].split("\\.")[0]) > 1499) {
			return false;
		}
		return true;
	}
	
	public static String removeEmojisFromContents(String contents) {
		String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(contents);
		return matcher.replaceAll("");
	}
	
	/* NOTE:  This implementation is not time-efficient (O(n)),
	 * but it's the best I could do with pure CRUD. */
	public static int setVersion(RuleSheetRepository ruleSheetRepository, String name) {
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
		
		return currVersion;
	}
	
	public static void saveToDatabase(RuleSheetRepository ruleSheetRepository, String name, 
			int currVersion, String contents) {
		RuleSheet rs = new RuleSheet();
		rs.setName(name);
		rs.setVersion(currVersion);
		rs.setContents(contents);
		ruleSheetRepository.save(rs);
	}
	
	
	// GET HELPER
	
	public static Iterable<RuleSheet> getRecentRuleSheets(RuleSheetRepository ruleSheetRepository) {
		
		Iterable<RuleSheet> all = ruleSheetRepository.findAll();
		Iterator<RuleSheet> iter = all.iterator();
		
		if (all.equals(null)) {
			return all;
		}
		
		/* This implementation cycles through all IDs in case any are deleted. */
		List<Integer> allIds = new ArrayList<Integer>();
		while (iter.hasNext()) {
			RuleSheet rs = iter.next();
			allIds.add(rs.getId());
		}
		Collections.sort(allIds);


		List<Integer> top10Ids = new ArrayList<Integer>();
		if (allIds.size() >= 10) {
			for (int i = allIds.size() - 1; i >= allIds.size() - 10; i--) {
				top10Ids.add(allIds.get(i));
			}
		}
		else {
			for (int i = allIds.size() - 1; i >= 0; i--) {
				top10Ids.add(allIds.get(i));
			}
		}

		
		List<RuleSheet> result = new ArrayList<RuleSheet>();
		for (int i = 0; i < top10Ids.size(); i++) {
			RuleSheet rs = new RuleSheet();
			try {
				Optional<RuleSheet> temp = ruleSheetRepository.findById(top10Ids.get(i));
				rs = temp.get();
				result.add(rs);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
