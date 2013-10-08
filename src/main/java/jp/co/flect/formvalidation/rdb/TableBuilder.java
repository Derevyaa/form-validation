package jp.co.flect.formvalidation.salesforce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import jp.co.flect.formvalidation.FormDefinition;
import jp.co.flect.formvalidation.FormItem;
import jp.co.flect.formvalidation.FormValidationException;
import jp.co.flect.formvalidation.rules.Email;
import jp.co.flect.formvalidation.rules.Url;
import jp.co.flect.formvalidation.rules.Rule;
import jp.co.flect.formvalidation.rules.Required;
import jp.co.flect.formvalidation.rules.RequiredIf;
import jp.co.flect.formvalidation.rules.MaxLength;
import jp.co.flect.soap.SoapException;
import jp.co.flect.log.Logger;

public class TableBuilder {
	
	public String generateCreateStatement(FormDefinition form, String tableName) {
		tableName = tableName.toLowerCase();
		
		StringBuilder buf = new StringBuilder();
		buf.append("CREATE TABLE ").append(tableName).append(" (\n  ");
		buf.append(tableName).append("_id SERIAL PRIMARY KEY");
		for (FormItem item : form.getItems()) {
			buf.append(",\n  ")
				.append(item.getName().toLowerCase())
				.append(" ")
				.append(getType(item));
			if (item.isRequired()) {
				buf.append(" NOT NULL");
			}
		}
		buf.append(")");
		return buf.toString();
	}
	
	public String getType(FormItem item) {
		//ToDo
		return "VARCHAR";
	}
}
