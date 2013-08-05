package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Katakana extends RegexRule {
	
	public Katakana() {
		super(
			"^([ァ-ヶー]+)$",
			"Please enter only Katakana."
		);
	}
	
}
