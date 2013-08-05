package jp.co.flect.formvalidation.rules;

public class Digits extends RegexRule {
	
	public Digits() {
		super("^\\d+$", "Please enter only digits.");
	}
	
}
