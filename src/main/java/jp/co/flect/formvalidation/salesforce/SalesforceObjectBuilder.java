package jp.co.flect.formvalidation.salesforce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import jp.co.flect.salesforce.SalesforceClient;
import jp.co.flect.salesforce.metadata.MetadataClient;
import jp.co.flect.salesforce.metadata.CustomObject;
import jp.co.flect.salesforce.metadata.CustomField;
import jp.co.flect.salesforce.metadata.BaseMetadata;
import jp.co.flect.salesforce.metadata.Picklist;
import jp.co.flect.salesforce.metadata.PicklistValue;
import jp.co.flect.formvalidation.FormDefinition;
import jp.co.flect.formvalidation.FormItem;
import jp.co.flect.formvalidation.FormValidationException;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Url;
import jp.co.flect.formvalidation.rules.Email;
import jp.co.flect.formvalidation.rules.Number;
import jp.co.flect.formvalidation.rules.Digits;
import jp.co.flect.formvalidation.rules.EqualTo;
import jp.co.flect.formvalidation.rules.Tel;
import jp.co.flect.soap.SoapException;

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
	
	public void generate(SalesforceInfo info, String jsonStr) throws FormValidationException, IOException, SoapException {
		FormDefinition form = FormDefinition.fromJson(jsonStr, true);
		generate(info, form);
	}
	
	public void generate(SalesforceInfo info, FormDefinition form) throws FormValidationException, IOException, SoapException {
		List<BaseMetadata> list = new ArrayList<BaseMetadata>();
		
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
		for (FormItem item : form.getItems()) {
			if (item == nameItem) {
				continue;
			}
			CustomField field = createField(info, item);
			if (field != null) {
				list.add(field);
			}
		}
		this.metaClient.create(obj);
		this.metaClient.create(list);
	}
	
	private CustomField createField(SalesforceInfo info, FormItem item) {
		CustomField.FieldType type = getFieldType(item);
		if (type == null) {
			return null;
		}
		CustomField field = new CustomField(type, info.getObjectName() + "." + item.getName() + "__c", item.getDisplayName());
		if (item.getSalesforceMap() != null) {
			for (Map.Entry<String, String> entry : item.getSalesforceMap().entrySet()) {
				field.set(entry.getKey(), entry.getValue());
			}
		}
		LinkedHashMap<String, String> valueMap = item.getValues();
		if (valueMap != null) {
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
			if (hasRule(item, EqualTo.class)) return null;
			return CustomField.FieldType.EncryptedText;
		}
		if ("radio".equals(strType)) return CustomField.FieldType.Picklist;
		if ("textarea".equals(strType)) return CustomField.FieldType.TextArea;
		if ("checkbox".equals(strType)) {
			LinkedHashMap<String, String> values = item.getValues();
			if (values == null || values.size() <= 1) {
				return CustomField.FieldType.Checkbox;
			} else {
				return CustomField.FieldType.MultiselectPicklist;
			}
		}
		if ("select".equals(strType)) return CustomField.FieldType.Picklist;
		if ("multiSelect".equals(strType)) return CustomField.FieldType.MultiselectPicklist;
		if ("file".equals(strType)) return null;
		
		//text
		if (hasRule(item, Url.class)) return CustomField.FieldType.Url;
		if (hasRule(item, Email.class)) return CustomField.FieldType.Email;
		if (hasRule(item, Number.class) || hasRule(item, Digits.class)) return CustomField.FieldType.Number;
		if (hasRule(item, Tel.class)) return CustomField.FieldType.Phone;
		
		return CustomField.FieldType.Text;
	}
	
	private <T extends Rule> boolean hasRule(FormItem item, Class<T> rule) {
		return rule.isAssignableFrom(item.getClass());
	}
}
