package jp.co.flect.formvalidation.rules;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import jp.co.flect.formvalidation.FormDefinition;
import jp.co.flect.formvalidation.FormItem;
import jp.co.flect.salesforce.metadata.CustomField;

public class RequiredIf extends Rule {
	
	private static abstract class Op {
		
		private String value;
		
		protected Op(String value) {
			this.value = value;
		}
		
		public abstract boolean evaluate(String[] v1, String[] v2);
		
		public String getValue() { return this.value;}
	}
	
	private static class Equal extends Op {
		
		private Equal() { super("==");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1) || isEmpty(v2)) {
				return isEmpty(v1) && isEmpty(v2);
			}
			if (v1.length != v2.length) {
				return false;
			}
			for (int i=0; i<v1.length; i++) {
				if (!v1[i].equals(v2[i])) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static class NotEqual extends Op {
		
		private NotEqual() { super("!=");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1)) {
				return !isEmpty(v2);
			} else if (isEmpty(v2)) {
				return true;
			}
			if (v1.length != v2.length) {
				return true;
			}
			for (int i=0; i<v1.length; i++) {
				if (!v1[i].equals(v2[i])) {
					return true;
				}
			}
			return false;
		}
	}
	
	private static class GreaterEqual extends Op {
		
		private GreaterEqual() { super(">=");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1) || isEmpty(v2)) {
				return false;
			}
			for (int i=0; i<v1.length; i++) {
				int n = 0;
				try {
					BigDecimal n1 = new BigDecimal(v1[i]);
					BigDecimal n2 = new BigDecimal(v2[i]);
					n = n1.compareTo(n2);
				} catch (NumberFormatException e) {
					n = v1[i].compareTo(v2[i]);
				}
				return n >= 0;
			}
			return false;
		}
	}
	
	private static class Greater extends Op {
		
		private Greater() { super(">");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1) || isEmpty(v2)) {
				return false;
			}
			for (int i=0; i<v1.length; i++) {
				int n = 0;
				try {
					BigDecimal n1 = new BigDecimal(v1[i]);
					BigDecimal n2 = new BigDecimal(v2[i]);
					n = n1.compareTo(n2);
				} catch (NumberFormatException e) {
					n = v1[i].compareTo(v2[i]);
				}
				if (n == 0) {
					continue;
				} else {
					return n > 0;
				}
			}
			return false;
		}
	}
	
	private static class LessEqual extends Op {
		
		private LessEqual() { super("<=");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1) || isEmpty(v2)) {
				return false;
			}
			for (int i=0; i<v1.length; i++) {
				int n = 0;
				try {
					BigDecimal n1 = new BigDecimal(v1[i]);
					BigDecimal n2 = new BigDecimal(v2[i]);
					n = n1.compareTo(n2);
				} catch (NumberFormatException e) {
					n = v1[i].compareTo(v2[i]);
				}
				return n <= 0;
			}
			return false;
		}
	}
	
	private static class Less extends Op {
		
		private Less() { super("<");}
		
		public boolean evaluate(String[] v1, String[] v2) {
			if (isEmpty(v1) || isEmpty(v2)) {
				return false;
			}
			for (int i=0; i<v1.length; i++) {
				int n = 0;
				try {
					BigDecimal n1 = new BigDecimal(v1[i]);
					BigDecimal n2 = new BigDecimal(v2[i]);
					n = n1.compareTo(n2);
				} catch (NumberFormatException e) {
					n = v1[i].compareTo(v2[i]);
				}
				if (n == 0) {
					continue;
				} else {
					return n < 0;
				}
			}
			return false;
		}
	}
	
	private static Op[] ops = {
		new Equal(),
		new NotEqual(),
		new GreaterEqual(),
		new Greater(),
		new LessEqual(),
		new Less()
	};
	
	private static Op getOp(String value) {
		for (Op op : ops) {
			if (op.getValue().equals(value)) {
				return op;
			}
		}
		return null;
	}
	
	private interface Evaluator {
		public boolean evaluate(Map<String, String[]> map);
		public String getSalesforceErrorCondition(FormDefinition form);
	}
	
	private static class Combine implements Evaluator {
		
		private boolean bAnd;
		private List<Evaluator> list = new ArrayList<Evaluator>();
		
		public Combine(boolean bAnd) {
			this.bAnd = bAnd;
		}
		
		public void add(Evaluator ev) {
			this.list.add(ev);
		}
		
		public boolean evaluate(Map<String, String[]> map) {
			for (Evaluator ev : this.list) {
				boolean b = ev.evaluate(map);
				if (this.bAnd) {
					if (!b) {
						return false;
					}
				} else {
					if (b) {
						return true;
					}
				}
			}
			return bAnd;
		}
		
		public String getSalesforceErrorCondition(FormDefinition form) {
			StringBuilder buf = new StringBuilder();
			buf.append(bAnd ? "AND(" : "OR(");
			boolean bFirst = true;
			for (Evaluator expr : this.list) {
				if (!bFirst) {
					buf.append(",");
				}
				buf.append(expr.getSalesforceErrorCondition(form));
				bFirst = false;
			}
			buf.append(")");
			return buf.toString();
		}
	}
	
	private static class Expr implements Evaluator {
		
		private String name;
		private String[] compareValues;
		private Op op;
		
		public Expr(String name, String value, Op op) {
			this.name = name;
			this.compareValues = new String[1];
			this.compareValues[0] = value == null ? "" : value;
			this.op = op;
		}
		
		public boolean evaluate(Map<String, String[]> map) {
			String[] targetValues = map.get(this.name);
			return op.evaluate(targetValues, compareValues);
		}
		
		public String getSalesforceErrorCondition(FormDefinition form) {
			if (isEmpty(compareValues)) {
				String ret = "ISNULL(" + getSalesforceFieldName(name) + ")";
				if (op instanceof Equal) {
					return ret;
				} else if (op instanceof NotEqual) {
					return "NOT(" + ret + ")";
				} else {
					return null;
				}
			}
			String value = compareValues[0];
			FormItem item = form.getItem(name);
			if (item != null && op instanceof Equal) {
				CustomField.FieldType ft = item.getSalesforceFieldType();
				if (ft == CustomField.FieldType.Picklist) {
					return "ISPICKVAL(" + getSalesforceFieldName(name) + ", \"" + value + "\")";
				}
				if (ft == CustomField.FieldType.MultiselectPicklist) {
					return "INCLUDES(" + getSalesforceFieldName(name) + ", \"" + value + "\")";
				}
			}
			StringBuilder buf = new StringBuilder();
			buf.append(getSalesforceFieldName(name)).append(op.getValue());
			try {
				BigDecimal num = new BigDecimal(value);
				buf.append(value);
			} catch (NumberFormatException e) {
				buf.append("\"").append(value).append("\"");
			}
			return buf.toString();
		}
	}
	
	private Evaluator evaluator;
	
	public RequiredIf() {
		super("This field is required.");
	}
	
	public String validate(Map<String, String[]> map, String[] values) {
		boolean bRequired = this.evaluator.evaluate(map);
		if (bRequired && isEmpty(values)) {
			return getMessage();
		} else {
			return null;
		}
	}
	
	public boolean check(String value) {
		return value != null && value.length() > 0;
	}
	
	public void build(Object value) throws RuleException {
		if (value instanceof String) {
			this.evaluator = parse((String)value);
		} else if (value instanceof Map) {
			this.evaluator = build((Map)value);
		} else {
			throw new RuleException("Invalid object: " + value);
		}
	}
	
	private Evaluator parse(String str) throws RuleException {
		String[] andArray = str.split("&&");
		String[] orArray = str.split("\\|\\|");

		if (andArray.length > 1 && orArray.length > 1) {
			throw new RuleException("Can not use both of '&&' and '||'");
		}
		String[] array = orArray.length > 1 ? orArray : andArray;
		Combine comb = new Combine(!(orArray.length > 1));
		for (String s : array) {
			comb.add(parseExpr(s.trim()));
		}
		return comb;
	}
	
	private Evaluator parseExpr(String str) throws RuleException {
		Op op = null;
		int idx = -1;
		for (Op targetOp : ops) {
			idx = str.indexOf(targetOp.getValue());
			if (idx > 0) {
				op = targetOp;
				break;
			}
		}
		if (op == null) {
			throw new RuleException("Invalid expr: " + str);
		}
		String name = str.substring(0, idx).trim();
		String value = str.substring(idx + op.getValue().length()).trim();
		if (op.getValue().equals("==") && "*".equals(value)) {
			op = getOp("!=");
			value = null;
		}
		return new Expr(name, value, op);
	}
	
	private Evaluator build(Map map) throws RuleException {
		Evaluator ret = null;
		String strOp = (String)map.get("op");
		Object objCond = map.get("cond");
		
		String name = (String)map.get("name");
		Object value = map.get("value");
		if (objCond instanceof List) {
			boolean bAnd = !"||".equals(strOp);
			Combine comb = new Combine(bAnd);
			for (Object obj : (List)objCond) {
				comb.add(build((Map)obj));
			}
			ret = comb;
		} else if (name != null) {
			Op op = getOp(strOp == null ? "==" : strOp);
			if (op == null) {
				throw new RuleException("Invalid op: " + strOp);
			}
			ret = new Expr(name, value == null ? null : value.toString(), op);
		}
		return ret;
	}
	
	@Override
	protected String doGetSalesforceErrorCondition(FormItem item, String name) {
		return "AND(ISNULL(" + name + ")," + this.evaluator.getSalesforceErrorCondition(getOwner()) + ")";
	}
}
