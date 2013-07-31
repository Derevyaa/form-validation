package jp.co.flect.formvalidation.rules;

public class Required extends BooleanRule {
	
	public Required() {
		super("This field is required.");
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
