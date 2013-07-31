package jp.co.flect.formvalidation.rules;

import java.util.Map;

public class RequiredIf extends BooleanRule {
	
	public RequiredIf() {
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
	
	public String validate(Map<String, String[]> map, String[] values) {
		//ToDo
		return getMessage();
	}
}
