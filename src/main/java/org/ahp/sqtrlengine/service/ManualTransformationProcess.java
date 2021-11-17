package org.ahp.sqtrlengine.service;

import java.util.List;

import org.ahp.sqtrlengine.model.TransformationRule;

public class ManualTransformationProcess extends TransformationProcess {

	public ManualTransformationProcess(List<TransformationRule> rules, String query, String sparqlEndpoint) {
		super(rules, query, sparqlEndpoint);
	}

}
