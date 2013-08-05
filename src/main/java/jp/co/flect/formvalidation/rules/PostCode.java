package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class PostCode extends RegexRule {
	
	public PostCode() {
		super(
			"^\\d{3}-\\d{4}$",
			"Please enter a valid postcode."
		);
	}
	
}
