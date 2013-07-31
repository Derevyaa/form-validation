package jp.co.flect.formvalidation.rules;

public class Digits extends BooleanRule {
	
	public Digits() {
		super("Please enter only digits.");
	}
	
	public boolean check(String[] values) {
		if (values == null || values.length == 0) {
			return false;
		}
		for (String s : values) {
			if (s != null && s.length() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
