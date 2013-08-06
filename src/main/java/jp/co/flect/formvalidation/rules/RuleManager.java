package jp.co.flect.formvalidation.rules;

import java.util.Map;
import java.util.HashMap;

public class RuleManager {
	
	private static final Map<String, Rule> map = new HashMap<String, Rule>();
	
	static {
		addRule("required", new Required());
		addRule("number", new Number());
		addRule("digits", new Digits());
		addRule("min", new Min());
		addRule("max", new Max());
		addRule("minlength", new MinLength());
		addRule("maxlength", new MaxLength());
		addRule("email", new Email());
		addRule("url", new Url());
		addRule("date", new Date());
		addRule("creditcard", new CreditCard());
		addRule("equalTo", new EqualTo());
		addRule("hiragana", new Hiragana());
		addRule("katakana", new Katakana());
		addRule("hankana", new Hankana());
		addRule("alpha", new Alpha());
		addRule("alphanum", new AlphaNum());
		addRule("postcode", new PostCode());
		addRule("tel", new Tel());
		addRule("regexp", new RegexRule());
		addRule("requiredIf", new RequiredIf());
		addRule("requiredOne", new RequiredOne());
	}
	
	public static void addRule(String name, Rule rule) {
		map.put(name, rule);
	}
	
	public static Rule getRule(String name) {
		return map.get(name);
	}
}
