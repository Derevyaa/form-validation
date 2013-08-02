package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;

public class Max extends Rule {
	
	private BigDecimal num;
	
	public Max() {
		super("Please enter a value less than or equal to {0}.");
	}
	
	public boolean check(String[] values) {
		for (String s : values) {
			try {
				BigDecimal v = new BigDecimal(s);
				if (this.num.compareTo(v) > 0) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	public void build(Object value) throws RuleException {
		try {
			this.num = new BigDecimal(value.toString());
		} catch (NumberFormatException e) {
			throw new RuleException("Invalid number: " + value);
		}
	}
}
