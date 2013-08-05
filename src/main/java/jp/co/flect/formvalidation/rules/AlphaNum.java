package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class AlphaNum extends RegexRule {
	
	public AlphaNum() {
		super(
			"^([a-zA-Z0-9]+)$",
			"Please enter only alphabet or number."
		);
	}
	
}
