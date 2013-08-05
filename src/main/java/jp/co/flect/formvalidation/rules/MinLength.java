package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class MinLength extends Rule {
	
	private int len;
	
	public MinLength() {
		super("Please enter at least {0} characters.");
	}
	
	@Override
	public boolean check(String[] values) throws FormValidationException {
		if (values == null || values.length <= 1) {
			return super.check(values);
		}
		return values.length >= this.len;
	}
	
	public boolean check(String value) {
		return value != null && value.length() >= len;
	}
	
	public void build(Object value) throws RuleException {
		try {
			this.len = Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			throw new RuleException("Invalid length: " + value);
		}
	}
}
