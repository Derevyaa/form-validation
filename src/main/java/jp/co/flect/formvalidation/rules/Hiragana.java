package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Hiragana extends RegexRule {
	
	public Hiragana() {
		super(
			"^([ぁ-んー]+)$",
			"Please enter only Hiragana."
		);
	}
	
}
