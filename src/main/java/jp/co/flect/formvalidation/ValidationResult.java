package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	
	private List<String> msgs = new ArrayList<String>();
	
	public void addMessage(String s) {
		if (s != null && !msgs.contains(s)) {
			msgs.add(s);
		}
	}
	
	public void addMessages(List<String> list) {
		if (list != null) {
			for (String s : list) {
				addMessage(s);
			}
		}
	}
	
	public List<String> getMessages() { return this.msgs;}
	
	public boolean hasError() { return this.msgs.size() > 0;}
}
