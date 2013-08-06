package jp.co.flect.formvalidation;

import java.util.LinkedHashMap;
import java.util.List;

public class ValidationResult {
	
	private LinkedHashMap<FormItem, List<String>> errors = new LinkedHashMap<FormItem, List<String>>();
	
	public void addError(FormItem item, List<String> msgs) {
		this.errors.put(item, msgs);
	}
	
	public LinkedHashMap<FormItem, List<String>> getErrors() { return this.errors;}
	
	public boolean hasError() { return this.errors.size() > 0;}
	
	public boolean isSuccess() { return this.errors.size() == 0;}
}
