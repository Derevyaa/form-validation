package jp.co.flect.formvalidation;

public class FormValidationException extends Exception {
	
	public FormValidationException(String msg) {
		super(msg);
	}
	
	public FormValidationException(Exception e) {
		super(e);
	}
	
}
