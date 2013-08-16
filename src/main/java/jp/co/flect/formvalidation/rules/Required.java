package jp.co.flect.formvalidation.rules;

import jp.co.flect.formvalidation.FormItem;

public class Required extends BooleanRule {
	
	public Required() {
		super("This field is required.");
	}
	
	public boolean check(String value) {
		return value != null && value.length() > 0;
	}
	
	@Override
	protected String doGetSalesforceErrorCondition(FormItem item, String name) {
		return "ISNULL(" + name + ")";
	}
}
