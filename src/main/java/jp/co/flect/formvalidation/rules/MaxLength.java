package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class MaxLength extends Rule {
	
	private int len;
	
	public MaxLength() {
		super("Please enter no more than {0} characters.");
	}
	
	@Override
	public boolean check(String[] values) throws FormValidationException {
		if (values == null || values.length <= 1) {
			return super.check(values);
		}
		return values.length <= this.len;
	}
	
	public boolean check(String value) {
		return value != null && value.length() <= this.len;
	}
	
	public void build(Object value) throws RuleException {
		try {
			this.len = Integer.parseInt(value.toString());
			setMessageParams(this.len);
		} catch (NumberFormatException e) {
			throw new RuleException("Invalid length: " + value);
		}
	}
}
