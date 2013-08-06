package jp.co.flect.formvalidation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class ValidationResult {
	
	private LinkedHashMap<FormItem, List<String>> errors = new LinkedHashMap<FormItem, List<String>>();
	private List<String> commonErrors = new ArrayList<String>();
	
	public void addError(FormItem item, List<String> msgs) {
		this.errors.put(item, msgs);
	}
	
	public void addCommonError(String msg) {
		if (!this.commonErrors.contains(msg)) {
			this.commonErrors.add(msg);
		}
	}
	
	public LinkedHashMap<FormItem, List<String>> getErrors() { return this.errors;}
	public List<String> getCommonErrors() { return this.commonErrors;}
	
	public boolean hasErrors() { return this.errors.size() + this.commonErrors.size() > 0;}
	
	public boolean isSuccess() { return !hasErrors();}
}
