package jp.co.flect.formvalidation.rules;

public class Required extends BooleanRule {
	
	public Required() {
		super("This field is required.");
	}
	
	public boolean check(String value) {
		return value != null && value.length() > 0;
	}
	
}
