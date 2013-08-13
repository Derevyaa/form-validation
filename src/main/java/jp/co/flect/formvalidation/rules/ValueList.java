package jp.co.flect.formvalidation.rules;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValueList extends Rule {
	
	private LinkedHashMap<String, String> valueMap;
	
	public ValueList(LinkedHashMap<String, String> map) {
		super("Invalid value: {0}");
		this.valueMap = map;
	}
	
	public void build(Object value) {
		throw new UnsupportedOperationException();
	}
	
	public LinkedHashMap<String, String> getValues() { return this.valueMap;}
	
	public boolean check(String value) {
		return this.valueMap.get(value) != null;
	}
	
	public String validate(Map<String, String[]> map, String[] values) {
		for (String s : values) {
			if (this.valueMap.get(s) == null) {
				return getFormattedMessage(s);
			}
		}
		return null;
	}
}
