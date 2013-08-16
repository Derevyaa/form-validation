package jp.co.flect.formvalidation.salesforce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import jp.co.flect.salesforce.SalesforceClient;
import jp.co.flect.salesforce.metadata.AsyncResult;
import jp.co.flect.salesforce.metadata.MetadataClient;
import jp.co.flect.salesforce.metadata.CustomObject;
import jp.co.flect.salesforce.metadata.CustomField;
import jp.co.flect.salesforce.metadata.ValidationRule;
import jp.co.flect.salesforce.metadata.BaseMetadata;
import jp.co.flect.salesforce.metadata.Picklist;
import jp.co.flect.salesforce.metadata.PicklistValue;
import jp.co.flect.formvalidation.FormDefinition;
import jp.co.flect.formvalidation.FormItem;
import jp.co.flect.formvalidation.FormValidationException;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Required;
import jp.co.flect.formvalidation.rules.RequiredIf;
import jp.co.flect.formvalidation.rules.MaxLength;
import jp.co.flect.formvalidation.rules.Url;
import jp.co.flect.formvalidation.rules.Email;
import jp.co.flect.formvalidation.rules.Number;
import jp.co.flect.formvalidation.rules.Digits;
import jp.co.flect.formvalidation.rules.EqualTo;
import jp.co.flect.formvalidation.rules.Tel;
import jp.co.flect.soap.SoapException;
import jp.co.flect.log.Logger;

public class SalesforceObjectBuilder {
	
	private SalesforceClient client;
	private MetadataClient metaClient;
	
	public SalesforceObjectBuilder(SalesforceClient client, File metadataWsdl) {
		this.client = client;
		try {
			this.metaClient = client.createMetadataClient(metadataWsdl);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public Logger getLogger() { return this.metaClient.getLogger();}
	
	public void generate(SalesforceInfo info, String jsonStr) throws FormValidationException, IOException, SoapException {
		FormDefinition form = FormDefinition.fromJson(jsonStr, true);
		generate(info, form);
	}
	
	public void generate(SalesforceInfo info, FormDefinition form) throws FormValidationException, IOException, SoapException {
		//Object
		CustomObject obj = new CustomObject();
		obj.setFullName(info.getObjectName());
		obj.setLabel(info.getLabel());
		if (info.getDescription() != null) {
			obj.setDescription(info.getDescription());
		}
		FormItem nameItem = form.getItem("name");
		CustomField nameField = null;
		if (nameItem == null) {
			nameField = new CustomField(CustomField.FieldType.AutoNumber, info.getObjectName() + ".Name", "Name");
			nameField.setDisplayFormat("{00000}");
		} else {
			nameField = createField(info, nameItem);
		}
		obj.setNameField(nameField);
		checkStatus(this.metaClient.create(obj));
		
		//Field
		List<BaseMetadata> list = new ArrayList<BaseMetadata>();
		for (FormItem item : form.getItems()) {
			if (item == nameItem) {
				continue;
			}
			CustomField field = createField(info, item);
			if (field != null) {
				list.add(field);
			}
			checkList(list);
		}
		if (list.size() > 0) {
			checkStatus(this.metaClient.create(list));
			list.clear();
		}
		//ValidationRule
		int ruleIndex = 0;
		for (Rule rule : form.getRules()) {
			ValidationRule vr = createValidationRule(info, null, ++ruleIndex, rule);
			if (vr != null) {
				list.add(vr);
				checkList(list);
			}
		}
		for (FormItem item : form.getItems()) {
			RequiredIf requiredIf = item.getRule(RequiredIf.class);
			if (requiredIf != null) {
				ValidationRule vr = createValidationRule(info, item, ++ruleIndex, requiredIf);
				list.add(vr);
				checkList(list);
			}
			for (Rule rule : item.getRules()) {
				ValidationRule vr = createValidationRule(info, item, ++ruleIndex, rule);
				if (vr != null) {
					list.add(vr);
					checkList(list);
				}
			}
		}
		if (list.size() > 0) {
			checkStatus(this.metaClient.create(list));
		}
	}
	
	private ValidationRule createValidationRule(SalesforceInfo info, FormItem item, int ruleIndex, Rule rule) {
		String formula = rule.getSalesforceErrorCondition(item);
		if (formula == null) {
			return null;
		}
		ValidationRule vr = new ValidationRule();
		String ruleName = (item == null ? "commonRule" : item.getName()) + ruleIndex;
		vr.setFullName(info.getObjectName() + "." + ruleName);
		vr.setErrorConditionFormula(formula);
		vr.setErrorMessage(rule.getMessage());
		return vr;
	}
	
	private void checkList(List<BaseMetadata> list) throws IOException, SoapException {
		if (list.size() == MetadataClient.MAX_REQUEST_COUNT) {
			checkStatus(this.metaClient.create(list));
			list.clear();
		}
	}
	
	private void checkStatus(List<AsyncResult> list) throws IOException, SoapException {
		checkStatus(list.get(0));
	}
	
	private void checkStatus(AsyncResult result) throws IOException, SoapException {
		int interval = 0;
		while (result.getState() == AsyncResult.AsyncRequestState.Queued || result.getState() == AsyncResult.AsyncRequestState.InProgress) {
			if (interval > 10) {
				return;
			}
			if (interval > 0) {
				try {
					Thread.sleep(interval * 1000);
				} catch (InterruptedException e) {
				}
			}
			result = this.metaClient.checkStatus(result);
			interval++;
		}
	}
	
	private CustomField createField(SalesforceInfo info, FormItem item) {
		CustomField.FieldType type = getFieldType(item);
		if (type == null) {
			return null;
		}
		boolean lengthAdded = type != CustomField.FieldType.Text && type != CustomField.FieldType.EncryptedText;
		CustomField field = new CustomField(type, info.getObjectName() + "." + item.getName() + "__c", item.getDisplayName());
		if (item.getSalesforceMap() != null) {
			for (Map.Entry<String, String> entry : item.getSalesforceMap().entrySet()) {
				field.set(entry.getKey(), entry.getValue());
				if ("length".equals(entry.getKey())) {
					lengthAdded = true;
				}
			}
		}
		if (!lengthAdded) {
			int len = 255;
			MaxLength maxLen = item.getRule(MaxLength.class);
			if (maxLen != null) {
				len = maxLen.length();
			}
			field.setLength(len);
		}
		if (type == CustomField.FieldType.EncryptedText) {
			field.setMaskChar(CustomField.EncryptedFieldMaskChar.asterisk);
			field.setMaskType(CustomField.EncryptedFieldMaskType.all);
		}
		if (type == CustomField.FieldType.MultiselectPicklist && field.getVisibleLines() == 0) {
			field.setVisibleLines(4);
		}
		if (type != CustomField.FieldType.Picklist && type != CustomField.FieldType.Checkbox) {
			field.setRequired(item.hasRule(Required.class));
		}
		LinkedHashMap<String, String> valueMap = item.getValues();
		if (valueMap != null && type != CustomField.FieldType.Checkbox) {
			Picklist picklist = new Picklist();
			for (Map.Entry<String, String> entry : valueMap.entrySet()) {
				PicklistValue pv = new PicklistValue();
				pv.setFullName(entry.getKey());
				pv.setDescription(entry.getValue());
				pv.setDefault(false);
				
				picklist.addPicklistValue(pv);
			}
			field.setPicklist(picklist);
		}
		return field;
	}
	
	private CustomField.FieldType getFieldType(FormItem item) {
		String strType = item.getType();
		if ("date".equals(strType)) return CustomField.FieldType.Date;
		if ("password".equals(strType)) {
			if (item.hasRule(EqualTo.class)) return null;
			return CustomField.FieldType.EncryptedText;
		}
		if ("radio".equals(strType)) return CustomField.FieldType.Picklist;
		if ("textarea".equals(strType)) return CustomField.FieldType.TextArea;
		if ("checkbox".equals(strType)) {
			LinkedHashMap<String, String> values = item.getValues();
			if (values == null || values.size() <= 1) {
				if (item.getDisplayName().trim().length() == 0 && values != null && values.size() == 1) {
					for (String s : values.values()) {
						item.setLabel(s);
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
		
		//text
		if (item.hasRule(Url.class)) return CustomField.FieldType.Url;
		if (item.hasRule(Email.class)) return CustomField.FieldType.Email;
		if (item.hasRule(Number.class) || item.hasRule(Digits.class)) return CustomField.FieldType.Number;
		if (item.hasRule(Tel.class)) return CustomField.FieldType.Phone;
		
		return CustomField.FieldType.Text;
	}
	
	private static String getString(Map<String, Object> origin, String key, String category) {
		if (category != null && origin.get(category) != null) {
			Map<String, Object> catMap = (Map<String, Object>)origin.get(category);
			if (catMap.get(key) != null) {
				return catMap.get(key).toString();
			}
		}
		Object ret = origin.get(key);
		return ret == null ? null : ret.toString();
	}
	
	public static Map<String, String> buildSalesforceMap(Map<String, Object> origin, FormItem item) {
		Map<String, String> map = new HashMap<String, String>();
		if (origin.get("salesforce") != null) {
			Map<String, Object> sfMap = (Map<String, Object>)origin.get("salesforce");
			for (Map.Entry<String, Object> entry : sfMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue().toString());
			}
		}
		if ("select".equals(item.getType()) && getString(origin, "multiple", "attrs") != null) {
			item.setType("multiSelect");
			String strSize = getString(origin, "size", "attrs");
			if (strSize == null) {
				map.put("visibleLines", strSize);
			}
		}
		if ("checkbox".equals(item.getType()) && (item.getValues() == null || item.getValues().size() <= 1)) {
			String initialValue = null;
			boolean checked = false;
			List values = (List)origin.get("values");
			if (values != null && values.size() == 1 && values.get(0) instanceof Map) {
				Object objChecked = ((Map)values.get(0)).get("checked");
				checked = objChecked != null && Boolean.valueOf(objChecked.toString());
			}
			if (initialValue != null && !checked) {
				String strChecked = getString(origin, "checked", null);
				checked = strChecked != null && strChecked.indexOf(initialValue) != -1;
			}
			map.put("defaultValue", checked ? "true" : "false");
		}
		String strTitle = getString(origin, "title", "attrs");
		if (strTitle != null) {
			map.put("inlineHelpText", strTitle);
		}
		return map;
	}
	
}
