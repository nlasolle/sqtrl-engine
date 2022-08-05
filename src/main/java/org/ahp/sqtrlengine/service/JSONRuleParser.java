package org.ahp.sqtrlengine.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import org.ahp.sqtrlengine.exception.InvalidFileTypeException;
import org.ahp.sqtrlengine.exception.InvalidRuleFileException;
import org.ahp.sqtrlengine.model.Prefix;
import org.ahp.sqtrlengine.model.TransformationRule;
import org.ahp.sqtrlengine.utils.RuleUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class dedicated to the validation and the parsing
 * of a transformation rule file under JSON syntax
 * @author Nicolas Lasolle
 *
 */
public class JSONRuleParser implements RuleParser {
	private static final Logger logger = LogManager.getLogger(JSONRuleParser.class);
	
	private JSONObject jsonContent; 

	public JSONRuleParser(File ruleFile) throws IOException {
		loadJsonContent(ruleFile);
	}

	public void loadJsonContent(File ruleFile) throws IOException {
		InputStream is = new FileInputStream(ruleFile);

		String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
		jsonContent = new JSONObject(jsonTxt);

		is.close();
	}

	@Override
	public boolean isRuleFileValid() throws FileNotFoundException, InvalidFileTypeException {

		return true;
	}

	@Override
	public List<TransformationRule> parseRuleFile()
			throws IOException, InvalidRuleFileException {
		List<String> existingIri = new ArrayList<>();
		List<TransformationRule> rules = new ArrayList<>();
		JSONArray jsonRules = jsonContent.getJSONObject("SQTRule").getJSONArray("rules");

		for (int i = 0; i < jsonRules.length(); i++) {
			JSONObject jsonRule = jsonRules.getJSONObject(i);

			TransformationRule rule = new TransformationRule(
					jsonRule.getString("iri"),
					jsonRule.getString("label")
					);

			//First check, iri must be unique. If not, we skip the rule
			if(rule.getIri() != null && existingIri.contains(rule.getIri())) {
				logger.info("A rule with iri {tempRule.getIri()} has already been defined. Skipping this rule");
				break;
			}
			
			rule.setContext(jsonRule.getString("context"));
			rule.setLeft(jsonRule.getString("left"));
			rule.setRight(jsonRule.getString("right"));
			rule.setCost((float) jsonRule.getDouble("cost"));
			rule.setExplanation(jsonRule.getString("explanation"));

			if(jsonRule.has("exceptions")) {
				JSONArray jsonRuleExceptions = jsonRule.getJSONArray("exceptions");

				List<String> exceptions = new ArrayList<>();
				for (int j = 0; j < jsonRuleExceptions.length(); j++) {
					exceptions.add(jsonRuleExceptions.getString(j));
				}

				rule.setExceptions(exceptions);
			}
			rules.add(RuleUtils.formatRule(rule));
			existingIri.add(rule.getIri());
		}
		return rules;

	}

	@Override
	public List<Prefix> parsePrefixes() {
		List<Prefix> prefixes = new ArrayList<>();

		JSONArray jsonPrefixes = jsonContent.getJSONObject("SQTRule").getJSONArray("prefixes");

		for (int i = 0; i < jsonPrefixes.length(); i++) {
			prefixes.add(new Prefix(
					jsonPrefixes.getJSONObject(i).getString("iri"),
					jsonPrefixes.getJSONObject(i).getString("label")
					));
		}

		return prefixes;
	}

}
