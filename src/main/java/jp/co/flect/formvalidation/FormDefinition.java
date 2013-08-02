package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import jp.co.flect.json.JsonUtils;
import jp.co.flect.json.JsonException;

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
	
	public ValidationResult validate(Map<String, String[]> map) throws FormValidationException {
		ValidationResult ret = new ValidationResult();
		for (FormItem item : this.items.values()) {
			String[] values = map.get(item.getName());
			List<String> msgs = item.validate(map, values);
			if (msgs != null && msgs.size() > 0) {
				ret.addMessages(msgs);
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

