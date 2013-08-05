package jp.co.flect.formvalidation.rules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import jp.co.flect.formvalidation.FormValidationException;

public class RegexRule extends Rule {
	
	private Pattern pattern;
	
	public RegexRule() {
		super("Please enter \"{0}\" format.");
	}
	
	protected RegexRule(String regex, String message) {
		super(message);
		this.pattern = Pattern.compile(regex);
	}
	
	@Override
	public boolean isBooleanRule() { return this.pattern != null;}
	
	@Override
	public void build(Object value) throws RuleException {
		if (this.pattern == null) {
			try {
				this.pattern = Pattern.compile(value.toString());
			} catch (PatternSyntaxException e) {
				throw new RuleException(e);
			}
		}
	}
	
	@Override
	public Rule newInstance(Object value, String message) throws RuleException {
		if (this.pattern != null && !Boolean.valueOf(value.toString())) {
			return null;
		} else {
			return super.newInstance(value, message);
		}
	}
	
	public boolean check(String value) throws FormValidationException {
		return this.pattern.matcher(value).matches();
	}
}
