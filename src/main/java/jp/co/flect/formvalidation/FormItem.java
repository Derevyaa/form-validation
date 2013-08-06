package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Required;
import jp.co.flect.formvalidation.rules.RequiredIf;
import jp.co.flect.formvalidation.rules.RuleManager;
import jp.co.flect.formvalidation.rules.RuleException;
import jp.co.flect.formvalidation.rules.ValueList;

public class FormItem {
	
	private String name;
	private String type;
	private String label;
	
	private Required required;
	private RequiredIf requiredIf;
	private List<Rule> rules = new ArrayList<Rule>();
	
	public FormItem(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() { return this.name;}
	public String getType() { return this.type;}
	
	public String getLabel() { return this.label;}
	public void setLabel(String s) { this.label = s;}
	
	public String getDisplayName() { return this.label == null ? this.name : this.label;}
	
	private String isRequired(Map<String, String[]> map) {
		if (this.required != null) {
			return this.required.getMessage();
		} else if (this.requiredIf != null) {
			return this.requiredIf.validate(map, null);
		} else {
			return null;
		}
	}
	
	public List<String> validate(Map<String, String[]> map, String[] values) throws FormValidationException {
		List<String> list = new ArrayList<String>();
		if (Rule.isEmpty(values)) {
			String msg = isRequired(map);
			if (msg != null) {
				list.add(msg);
			}
			return list;
		}
		for (Rule rule : this.rules) {
			String msg = rule.validate(map, values);
			if (msg != null) {
				list.add(msg);
			}
		}
		return list;
	}
	
	public void addRule(Rule rule) {
		if (rule instanceof Required) {
			this.required = (Required)rule;
		} else if (rule instanceof RequiredIf) {
			this.requiredIf = (RequiredIf)rule;
		} else {
			this.rules.add(rule);
		}
	}
	
	public static FormItem fromMap(String name, Map<String, Object> origin) throws FormValidationException {
		Map<String, Object> map = new LinkedHashMap<String, Object>(origin);
		String type = (String)map.remove("type");
		if (type == null) {
			type = "text";
		}
		FormItem item = new FormItem(name, type);
		
		String label = (String)map.remove("label");
		if (label != null) {
			item.setLabel(label);
		}
		List values = (List)map.remove("values");
		if (values != null) {
			makeValues(values, item);
		}
		makeRules(map, item);
		return item;
	}
	
	private static void makeValues(List values, FormItem item) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Object o : values) {
			String value = null;
			String text = null;
			if (o instanceof String) {
				String str = (String)o;
				int idx = str.indexOf(':');
				if (idx != -1) {
					value = str.substring(0, idx);
					text = str.substring(idx + 1);
				} else {
					value = str;
					text = str;
				}
			} else if (o instanceof Map) {
				Map<String, Object> childMap = (Map<String, Object>)o;
				value = (String)childMap.get("value");
				text = (String)childMap.get("text");
			}
			if (value != null && text != null) {
				map.put(value, text);
			}
		}
		item.addRule(new ValueList(map));
	}
	
	private static void makeRules(Map<String, Object> map, FormItem item) throws RuleException {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			if ("rules".equals(name)) {
				makeRules((Map<String, Object>)entry.getValue(), item);
			} else {
				Rule rule = RuleManager.getRule(name);
				if (rule != null) {
					String message = null;
					Object value = null;
					if (entry.getValue() instanceof Map) {
						Map ruleMap = (Map)entry.getValue();
						message = (String)ruleMap.get("message");
						value = ruleMap.get("value");
					} else if (entry.getValue() instanceof String) {
						if (rule.isBooleanRule()) {
							message = (String)entry.getValue();
							value = true;
						} else {
							value = entry.getValue();
						}
					} else {
						value = entry.getValue();
					}
					item.addRule(rule.newInstance(value, message));
				}
			}
		}
	}
	
	public int hashCode() { return this.name.hashCode();}
	public boolean equals(Object o) {
		if (o instanceof FormItem) {
			return ((FormItem)o).name.equals(this.name);
		}
		return false;
	}
}

