package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;
import jp.co.flect.formvalidation.FormItem;

public class MinLength extends Rule {
	
	private int len;
	
	public MinLength() {
		super("Please enter at least {0} characters.");
	}
	
	public int length() { return this.len;}
	
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
			setMessageParams(this.len);
		} catch (NumberFormatException e) {
			throw new RuleException("Invalid length: " + value);
		}
	}
	
	@Override
	protected String doGetSalesforceErrorCondition(FormItem item, String name) {
		return "LEN(" + name + ")<" + this.len;
	}
}
