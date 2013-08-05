package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Alpha extends RegexRule {
	
	public Alpha() {
		super(
			"^([a-zA-z¥s]+)$",
			"Please enter only alphabet."
		);
	}
	
}
