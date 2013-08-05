package jp.co.flect.formvalidation.rules;

import java.util.Map;

public class RequiredIf extends BooleanRule {
	
	public RequiredIf() {
		super("This field is required.");
	}
	
	public boolean check(String value) {
		return value != null && value.length() > 0;
	}
	
	public String validate(Map<String, String[]> map, String[] values) {
		//ToDo
		return getMessage();
	}
}
