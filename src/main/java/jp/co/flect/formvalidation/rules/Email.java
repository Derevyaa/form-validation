package jp.co.flect.formvalidation.rules;

import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormValidationException;

public class Email extends RegexRule {
	
	public Email() {
		//Copy from playframework 1.2.5
		super(
			"[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[a-zA-Z0-9](?:[\\w-]*[\\w])?",
			"Please enter a valid email address."
		);
	}
	
}
