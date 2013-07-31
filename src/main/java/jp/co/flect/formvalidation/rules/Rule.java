package jp.co.flect.formvalidation.rules;

import java.util.Map;

public abstract class Rule implements Cloneable {
	
	private String message;
	
	public boolean isBooleanRule() { return false;}
	
	public abstract void build(Object value);
	public abstract boolean check(String[] values);
	
	protected Rule(String message) {
		this.message = message;
	}
	
	public String getMessage() { return this.message;}
	public void setMessage(String s) { this.message = s;}
	
	public Rule newInstance(Object value, String message) {
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
	
	public String validate(Map<String, String[]> map, String[] values) {
		return check(values) ? null : getMessage();
	}
}
