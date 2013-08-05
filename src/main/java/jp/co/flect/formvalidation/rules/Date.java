package jp.co.flect.formvalidation.rules;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Date extends BooleanRule {
	
	private SimpleDateFormat fmt;
	private int len;
	
	public Date() {
		this("yyyy-MM-dd", "Please enter a valid date.");
	}
	
	public Date(String format, String message) {
		super(message);
		this.fmt = new SimpleDateFormat(format);
		this.fmt.setLenient(false);
	}
	
	public boolean check(String value) {
		try {
			this.fmt.parse(value);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
}
