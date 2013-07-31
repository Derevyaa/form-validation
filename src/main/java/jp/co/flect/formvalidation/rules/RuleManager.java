package jp.co.flect.formvalidation.rules;

import java.util.Map;
import java.util.HashMap;

public class RuleManager {
	
	private static final Map<String, Rule> map = new HashMap<String, Rule>();
	
	static {
	}
	
	public static void addRule(String name, Rule rule) {
		map.put(name, rule);
	}
	
	public static Rule getRule(String name) {
		return map.get(name);
	}
}
