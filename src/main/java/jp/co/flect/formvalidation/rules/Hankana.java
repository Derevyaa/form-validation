package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Hankana extends RegexRule {
	
	public Hankana() {
		super(
			"^([ｧ-ﾝﾞﾟ]+)$",
			"Please enter only hankaku kana."
		);
	}
	
}
