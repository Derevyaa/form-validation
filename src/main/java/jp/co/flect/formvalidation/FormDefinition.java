package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import jp.co.flect.json.JsonUtils;
import jp.co.flect.json.JsonException;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Required;
import jp.co.flect.formvalidation.rules.RequiredIf;

public class FormDefinition {
	
	private LinkedHashMap<String, FormItem> items;
	
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
	
	private boolean isNull(String[] strs) {
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
	public ValidationResult validate(Map<String, String[]> map) {
		ValidationResult ret = new ValidationResult();
		for (FormItem item : this.items.values()) {
			String[] values = map.get(item.getName());
			if (isNull(values)) {
				String msg = item.isRequired(map);
				if (msg != null) {
					ret.addMessage(msg);
				}
			} else {
				List<String> msgs = item.validate(map, values);
				if (msgs != null && msgs.size() > 0) {
					ret.addMessages(msgs);
				}
			}
		}
		return ret;
	}
	
	public static FormDefinition fromJson(String json) throws FormValidationException {
		try {
			LinkedHashMap<String, Object> map = JsonUtils.fromJson(json, LinkedHashMap.class);
			
			FormDefinition form = new FormDefinition();
			Map<String, Object> items = (Map<String, Object>)map.get("items");
			if (items != null) {
				for (Map.Entry<String, Object> entry : items.entrySet()) {
					String name = entry.getKey();
					FormItem item = null;
					if (entry.getValue() instanceof String) {
						item = new FormItem(name, (String)entry.getValue());
					} else if (entry.getValue() instanceof Map) {
						item = FormItem.fromMap(name, (Map<String, Object>)entry.getValue());
					}
					form.addItem(item);
				}
			}
			return form;
		} catch (JsonException e) {
			throw new FormValidationException(e);
		}
	}
}

class FormItem {
	
	private String name;
	private String type;
	private String label;
	private Map<String, String> values;
	
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
	
	public String isRequired(Map<String, String[]> map) {
		if (this.required != null) {
			return this.required.getMessage();
		} else if (this.requiredIf != null) {
			return this.requiredIf.validate(map, null);
		} else {
			return null;
		}
	}
	
	public List<String> validate(Map<String, String[]> map, String[] values) {
		List<String> list = new ArrayList<String>();
		for (Rule rule : this.rules) {
			String msg = rule.validate(map, values);
			if (msg != null) {
				list.add(msg);
			}
		}
		return list;
	}
	
	public static FormItem fromMap(String name, Map<String, Object> map) throws FormValidationException {
		String type = (String)map.get("type");
		if (type == null) {
			type = "text";
		}
		FormItem item = new FormItem(name, type);
		
		String label = (String)map.get("label");
		if (label != null) {
			item.setLabel(label);
		}
		return item;
	}
	
	private static Map<String, String> makeValues(List values) {
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
		return map;
	}
}

