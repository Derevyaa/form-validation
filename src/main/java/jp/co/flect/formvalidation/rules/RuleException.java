package jp.co.flect.formvalidation.rules;

import jp.co.flect.formvalidation.FormValidationException;

public class RuleException extends FormValidationException {
	
	public RuleException(String msg) {
		super(msg);
	}
	
	public RuleException(Exception e) {
		super(e);
	}
}
