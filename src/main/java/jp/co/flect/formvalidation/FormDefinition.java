package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import jp.co.flect.json.JsonUtils;
import jp.co.flect.json.JsonException;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.RuleManager;

public class FormDefinition {
	
	private String title;
	
	private LinkedHashMap<String, FormItem> items;
	private List<Rule> rules;
	private HashMap<String, String> options = new HashMap<String, String>();
	
	public String getOption(String name) { return this.options.get(name);}
	public void setOption(String name, String value) { this.options.put(name, value);}
	
	public String getTitle() { return getOption("title");}
	
	public List<FormItem> getItems() { return new ArrayList<FormItem>(this.items.values());}
	public void setItems(List<FormItem> list) { 
		LinkedHashMap<String, FormItem> map = new LinkedHashMap<String, FormItem>();
		for (FormItem item : list) {
			map.put(item.getName(), item);
		}
		this.items = map;
	}
	
	public void addItem(FormItem item) {
		if (this.items == null) {
			this.items = new LinkedHashMap<String, FormItem>();
		}
		this.items.put(item.getName(), item);
	}
	
	public ValidationResult validate(String jsonStr) throws FormValidationException {
		try {
			Map<String, Object> temp = JsonUtils.fromJsonToMap(jsonStr);
			Map<String, String[]> map = new LinkedHashMap<String, String[]>();
			for (Map.Entry<String, Object> entry : temp.entrySet()) {
				String[] array = null;
				Object o = entry.getValue();
				if (o instanceof List) {
					List list = (List)o;
					array = new String[list.size()];
					int idx = 0;
					for (Object value : list) {
						array[idx++] = value.toString();
					}
				} else if (o != null) {
					array = new String[1];
					array[0] = o.toString();
				}
				map.put(entry.getKey(), array);
			}
			return validate(map);
		} catch (JsonException e) {
			throw new FormValidationException(e);
		}
	}
	
	public ValidationResult validate(Map<String, String[]> map) throws FormValidationException {
		ValidationResult ret = new ValidationResult();
		for (FormItem item : this.items.values()) {
			String[] values = map.get(item.getName());
			List<String> msgs = item.validate(map, values);
			if (msgs != null && msgs.size() > 0) {
				ret.addError(item, msgs);
			}
		}
		if (this.rules != null) {
			for (Rule rule : this.rules) {
				String msg = rule.validate(map, null);
				if (msg != null) {
					ret.addCommonError(msg);
				}
			}
		}
		return ret;
	}
	
	public void addRule(Rule rule) {
		if (this.rules == null) {
			this.rules = new ArrayList<Rule>();
		}
		this.rules.add(rule);
	}
	
	public FormItem getItem(String name) {
		for (Map.Entry<String, FormItem> entry : this.items.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public List<Rule> getRules() {
		return this.rules == null ? Collections.<Rule>emptyList() : this.rules;
	}
	
	public static FormDefinition fromJson(String json) throws FormValidationException {
		return fromJson(json, false);
	}
	
	public static FormDefinition fromJson(String json, boolean includeSalesforceInfo) throws FormValidationException {
		try {
			LinkedHashMap<String, Object> map = JsonUtils.fromJson(json, LinkedHashMap.class);
			
			FormDefinition form = new FormDefinition();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				if (key.equals("items") || key.equals("rules") || entry.getValue() == null) {
					continue;
				}
				form.setOption(key, entry.getValue().toString());
			}
			Map<String, Object> items = (Map<String, Object>)map.get("items");
			if (items != null) {
				for (Map.Entry<String, Object> entry : items.entrySet()) {
					String name = entry.getKey();
					FormItem item = null;
					if (entry.getValue() instanceof String) {
						item = new FormItem(form, name, (String)entry.getValue());
					} else if (entry.getValue() instanceof Map) {
						item = FormItem.fromMap(form, name, (Map<String, Object>)entry.getValue(), includeSalesforceInfo);
					}
					form.addItem(item);
				}
			}
			Map<String, Object> rules = (Map<String, Object>)map.get("rules");
			if (rules != null) {
				buildRules(form, rules);
			}
			return form;
		} catch (JsonException e) {
			throw new FormValidationException(e);
		}
	}
	
	private static void buildRules(FormDefinition form, Map<String, Object> map) throws FormValidationException {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			Rule rule = RuleManager.getRule(name);
			if (rule != null) {
				rule.setOwner(form);
				String message = null;
				Object value = null;
				if (entry.getValue() instanceof Map) {
					Map ruleMap = (Map)entry.getValue();
					message = (String)ruleMap.get("message");
					value = ruleMap.get("value");
					if (value == null) {
						value = ruleMap;
					}
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
				form.addRule(rule.newInstance(value, message));
			} else if (entry.getValue() instanceof Map) {
				buildRules(form, (Map<String, Object>)entry.getValue());
			}
		}
	}
}

