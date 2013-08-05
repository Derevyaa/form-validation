package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Url extends RegexRule {
	
	public Url() {
		//Copy from playframework 1.2.5
		super(
			"^(http|https|ftp)\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~\\!])*$",
			"Please enter a valid URL."
		);
	}
	
}
