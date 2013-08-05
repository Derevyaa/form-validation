package jp.co.flect.formvalidation.rules;

import java.util.Map;
import jp.co.flect.formvalidation.FormValidationException;

public class EqualTo extends Rule {
	
	private String target;
	
	public EqualTo() {
		super("Please enter the same value again.");
	}
	
	public String validate(Map<String, String[]> map, String[] values) throws FormValidationException {
		String[] targetValues = map.get(target);
		return check(values, targetValues) ? null : getMessage();
	}
	
	private boolean check(String[] v1, String[] v2) {
		if (v1 == null || v2 == null) {
			return v1 == null && v2 == null;
		}
		if (v1.length != v2.length) {
			return false;
		}
		for (int i=0; i<v1.length; i++) {
			if (!v1[i].equals(v2[i])) {
				return false;
			}
		}
		return true;
	}
	
	public void build(Object value) throws RuleException {
		this.target = value.toString();
	}
	
	public boolean check(String value) throws FormValidationException {
		throw new UnsupportedOperationException();
	}
}
