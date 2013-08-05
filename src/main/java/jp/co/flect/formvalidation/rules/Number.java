package jp.co.flect.formvalidation.rules;

public class Number extends RegexRule {
	
	public Number() {
		super("^-?(?:\\d+|\\d{1,3}(?:,\\d{3})+)?(?:\\.\\d+)?$", "Please enter a valid number.");
	}
	
}
