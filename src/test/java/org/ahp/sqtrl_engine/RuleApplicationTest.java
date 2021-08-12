package org.ahp.sqtrl_engine;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class RuleApplicationTest {

	
	@ParameterizedTest
	@ValueSource(strings = {"toto", "tota"})
	void testSimpleRuleApplication(String s) {
		System.out.println(s);
	}
}
