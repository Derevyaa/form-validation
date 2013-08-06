package jp.co.flect.formvalidation.rules;

import java.util.Map;
import java.text.MessageFormat;
import jp.co.flect.formvalidation.FormValidationException;

public abstract class Rule implements Cloneable {
	
	public static boolean isEmpty(String[] strs) {
		if (strs == null || strs.length == 0) {
			return true;
		}
		for (String s : strs) {
			if (s != null && s.length() > 0) {
				return false;
			}
		}
		return true;
	}
	
	private String message;
	
	public boolean isBooleanRule() { return false;}
	
	public abstract void build(Object value) throws RuleException;
	
	public String validate(Map<String, String[]> map, String[] values) throws FormValidationException {
		return check(values) ? null : getMessage();
	}
	
	public boolean check(String[] values) throws FormValidationException {
		if (values == null || values.length == 0) {
			return false;
		}
		for (String s : values) {
			if (!check(s)) {
				return false;
			}
		}
		return true;
	}
	
	public abstract boolean check(String value) throws FormValidationException;
	
	protected Rule(String message) {
		this.message = message;
	}
	
	public String getMessage() { return this.message;}
	public void setMessage(String s) { this.message = s;}
	
	public String getFormattedMessage(Object... args) {
		return MessageFormat.format(getMessage(), args);
	}
	
	public Rule newInstance(Object value, String message) throws RuleException{
		try {
			Rule ret = (Rule)super.clone();
			if (message != null) {
				ret.setMessage(message);
			}
			ret.build(value);
			return ret;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}
	
}
