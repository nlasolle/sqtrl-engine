package org.ahp.sqtrlengine.service;

import java.util.List;

import org.ahp.sqtrlengine.model.TransformationRule;
import org.apache.jena.query.Query;

public class ManualTransformationProcess extends TransformationProcess {

	public ManualTransformationProcess(List<TransformationRule> rules, Query query, String sparqlEndpoint) {
		super(rules, query, sparqlEndpoint);
	}

}
