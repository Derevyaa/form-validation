package jp.co.flect.formvalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Required;
import jp.co.flect.formvalidation.rules.RequiredIf;
import jp.co.flect.formvalidation.rules.Url;
import jp.co.flect.formvalidation.rules.Email;
import jp.co.flect.formvalidation.rules.Number;
import jp.co.flect.formvalidation.rules.Digits;
import jp.co.flect.formvalidation.rules.EqualTo;
import jp.co.flect.formvalidation.rules.Tel;
import jp.co.flect.formvalidation.rules.RuleManager;
import jp.co.flect.formvalidation.rules.RuleException;
import jp.co.flect.formvalidation.rules.ValueList;
import jp.co.flect.formvalidation.salesforce.SalesforceObjectBuilder;
import jp.co.flect.salesforce.metadata.CustomField;

public class FormItem {
	
	private FormDefinition owner;
	private String name;
	private String type;
	private String label;
	private boolean follow;
	
	private Required required;
	private RequiredIf requiredIf;
	private List<Rule> rules = new ArrayList<Rule>();
	private Map<String, String> sfMap = null;
	
	public FormItem(FormDefinition owner, String name, String type) {
		this.owner = owner;
		this.name = name;
		this.type = type;
	}
	
	public FormDefinition getOwner() { return this.owner;}
	
	public String getName() { return this.name;}
	
	public String getType() { return this.type;}
	public void setType(String s) { this.type = s;}
	
	public String getLabel() { return this.label;}
	public void setLabel(String s) { this.label = s;}
	
	public boolean isFollow() { return this.follow;}
	
	public String getDisplayName() { return this.label == null ? this.name : this.label;}
	
	public String getSalesforceFieldName() {
		return "name".equalsIgnoreCase(this.name) ? "Name" : this.name + "__c";
	}
	
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
		rule.setOwner(this.owner);
		if (rule instanceof Required) {
			this.required = (Required)rule;
		} else if (rule instanceof RequiredIf) {
			this.requiredIf = (RequiredIf)rule;
		} else {
			this.rules.add(rule);
		}
	}
	
	public List<Rule> getRules() { return this.rules;}
	
	public <T extends Rule> boolean hasRule(Class<T> rule) {
		return getRule(rule) != null;
	}
	
	public <T extends Rule> T getRule(Class<T> rule) {
		if (rule == Required.class) {
			return (T)this.required;
		}
		if (rule == RequiredIf.class) {
			return (T)this.requiredIf;
		}
		for (Rule r : getRules()) {
			if (rule.isAssignableFrom(r.getClass())) {
				return (T)r;
			}
		}
		return null;
	}
	
	public LinkedHashMap<String, String> getValues() {
		ValueList vl = null;
		for (Rule rule : this.rules) {
			if (rule instanceof ValueList) { 
				vl = (ValueList)rule;
				break;
			}
		}
		return vl == null ? null : vl.getValues();
	}
	
	public static FormItem fromMap(FormDefinition form, String name, Map<String, Object> origin, boolean includeSalesforceInfo) throws FormValidationException {
		Map<String, Object> map = new LinkedHashMap<String, Object>(origin);
		String type = (String)map.remove("type");
		if (type == null) {
			type = "text";
		}
		FormItem item = new FormItem(form, name, type);
		
		String label = (String)map.remove("label");
		if (label != null) {
			item.setLabel(label);
		}
		List values = (List)map.remove("values");
		if (values != null) {
			makeValues(values, item);
		}
		Boolean follow = (Boolean)map.remove("follow");
		if (follow != null) {
			item.follow = follow.booleanValue();
		}
		makeRules(map, item);
		if (includeSalesforceInfo) {
			makeSalesforce(map, item);
		}
		return item;
	}
	
	private static void makeValues(List values, FormItem item) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
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
					item.addRule(rule.newInstance(value, message));
				}
			}
		}
	}
	
	private static void makeSalesforce(Map<String, Object> origin, FormItem item) {
		item.sfMap = SalesforceObjectBuilder.buildSalesforceMap(origin, item);
	}
	
	public Map<String, String> getSalesforceMap() { return this.sfMap;}
	
	public int hashCode() { return this.name.hashCode();}
	public boolean equals(Object o) {
		if (o instanceof FormItem) {
			return ((FormItem)o).name.equals(this.name);
		}
		return false;
	}
	
	public CustomField.FieldType getSalesforceFieldType() {
		String strType = getType();
		if ("date".equals(strType)) return CustomField.FieldType.Date;
		if ("password".equals(strType)) {
			if (hasRule(EqualTo.class)) return null;
			return CustomField.FieldType.EncryptedText;
		}
		if ("radio".equals(strType)) return CustomField.FieldType.Picklist;
		if ("textarea".equals(strType)) return CustomField.FieldType.TextArea;
		if ("checkbox".equals(strType)) {
			//ToDo 副作用があるので別のメソッドに移動した方が良い
			LinkedHashMap<String, String> values = getValues();
			if (values == null || values.size() <= 1) {
				if (getDisplayName().trim().length() == 0 && values != null && values.size() == 1) {
					for (String s : values.values()) {
						setLabel(s);
					}
				}
				return CustomField.FieldType.Checkbox;
			} else {
				return CustomField.FieldType.MultiselectPicklist;
			}
		}
		if ("select".equals(strType)) return CustomField.FieldType.Picklist;
		if ("multiSelect".equals(strType)) return CustomField.FieldType.MultiselectPicklist;
		if ("file".equals(strType)) return null;
		if ("hidden".equals(strType)) return null;
		
		//text
		if (hasRule(Url.class)) return CustomField.FieldType.Url;
		if (hasRule(Email.class)) return CustomField.FieldType.Email;
		if (hasRule(Number.class) || hasRule(Digits.class)) return CustomField.FieldType.Number;
		if (hasRule(Tel.class)) return CustomField.FieldType.Phone;
		
		return CustomField.FieldType.Text;
	}
	
}

