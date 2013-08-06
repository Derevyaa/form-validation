package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;

public class Min extends Rule {
	
	private BigDecimal num;
	private String str;
	
	public Min() {
		super("Please enter a value greater than or equal to {0}.");
	}
	
	public boolean check(String value) {
		try {
			if (this.num != null) {
				BigDecimal v = new BigDecimal(value);
				return this.num.compareTo(v) <= 0;
			}
		} catch (NumberFormatException e) {
		}
		return this.str.compareTo(value) <= 0;
	}
	
	public void build(Object value) throws RuleException {
		try {
			this.num = new BigDecimal(value.toString());
		} catch (NumberFormatException e) {
		}
		this.str = value.toString();
	}
}
