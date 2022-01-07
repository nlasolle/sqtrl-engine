package org.ahp.sqtrlengine.utils;

import java.util.ArrayList;
import java.util.List;

import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;

public class RuleUtils {

	public static void replacePrefixes(List<TransformationRule> rules, List<Prefix> prefixes) {
		for(TransformationRule rule : rules) {
			for(Prefix prefix : prefixes) {
				rule.setContext(replaceAllPrefixes(rule.getContext(), prefix));
				rule.setLeft(replaceAllPrefixes(rule.getLeft(), prefix));
				rule.setRight(replaceAllPrefixes(rule.getRight(), prefix));

				List<String> exceptions = new ArrayList<String>();

				System.out.println("rule " + rule.getLabel());
				for(String exception : rule.getExceptions()) {
					System.out.println("exception " + exception);
					exceptions.add(replaceAllPrefixes(exception, prefix));
				}

				rule.setExceptions(exceptions);
			}
		}
	}

	/**
	 * Replace all prefix occurrences with full IRI value within a string
	 * @param field 
	 * @param prefix 
	 * @return
	 */
	private static String replaceAllPrefixes(String field, Prefix prefix) {
		while(field.contains(prefix.getPrefix())) {
			int prefixIndex = field.indexOf(prefix.getPrefix());
			int propertyIndex = prefixIndex + prefix.getPrefix().length();
			int nextSpaceIndex = field.indexOf(" ", propertyIndex);
			
			if (nextSpaceIndex == -1) {
				nextSpaceIndex = field.length(); 
			}
			
			field = field.substring(0, 
					prefixIndex)
					+ "<"
					+ prefix.getNamespace()
					+ field.substring(propertyIndex, nextSpaceIndex)
					+ ">"
					+ field.substring(nextSpaceIndex);
		}
		
		System.out.println("FIELD " + field);
		return field;
	}
}
