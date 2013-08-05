package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Tel extends RegexRule {
	
	public Tel() {
		super(
			"^[0-9-]{3,13}$",
			"Please enter a valid telephone number."
		);
	}
	
}
