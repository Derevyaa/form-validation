package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class CreditCard extends RegexRule {
	
	public CreditCard() {
		super(
			"^([0-9 \\-]+)$",
			"Please enter a valid credit card number."
		);
	}
	
}
