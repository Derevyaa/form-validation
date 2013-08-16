package jp.co.flect.formvalidation.rules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jp.co.flect.formvalidation.FormValidationException;
import jp.co.flect.formvalidation.FormItem;

public class RequiredOne extends Rule {
	
	private String[] items;
	
	public RequiredOne() {
		super("At least onf of {0} is required.");
	}
	
	@Override
	public void build(Object value) throws RuleException {
		if (value instanceof List) {
			List<String> list = (List<String>)value;
			this.items = list.toArray(new String[list.size()]);
		} else {
			this.items = value.toString().split(",");
		}
		setMessageParams(Arrays.toString(this.items));
	}
	
	@Override
	public boolean check(String value) throws FormValidationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String validate(Map<String, String[]> map, String[] values) throws FormValidationException {
		if (this.items == null || this.items.length == 0) {
			return null;
		}
		for (String name : this.items) {
			String[] v = map.get(name);
			if (!isEmpty(v)) {
				return null;
			}
		}
		return getMessage();
	}
	
	@Override
	protected String doGetSalesforceErrorCondition(FormItem item, String name) {
		boolean bFirst = true;
		StringBuilder buf = new StringBuilder();
		buf.append("AND(");
		for (String s : this.items) {
			if (!bFirst) {
				buf.append(",");
			}
			buf.append("ISNULL(" + getSalesforceFieldName(s) + ")");
			bFirst = false;
		}
		buf.append(")");
		return buf.toString();
	}
}
