package jp.co.flect.formvalidation.rules;

public abstract class BooleanRule extends Rule {
	
	protected BooleanRule(String message) {
		super(message);
	}
	
	@Override
	public boolean isBooleanRule() { return true;}
	
	@Override
	public void build(Object value) {
		//Do nothing
	}
	
	@Override
	public Rule newInstance(Object value, String message) throws RuleException {
		if (!Boolean.valueOf(value.toString())) {
			return null;
		} else {
			return super.newInstance(value, message);
		}
	}
	
}
