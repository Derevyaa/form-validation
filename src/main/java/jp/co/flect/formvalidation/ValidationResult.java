package jp.co.flect.formvalidation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import jp.co.flect.json.JsonUtils;

public class ValidationResult {
	
	private LinkedHashMap<FormItem, List<String>> errors = new LinkedHashMap<FormItem, List<String>>();
	private List<String> commonErrors = new ArrayList<String>();
	
	public void addError(FormItem item, List<String> msgs) {
		this.errors.put(item, msgs);
	}
	
	public void addCommonError(String msg) {
		if (!this.commonErrors.contains(msg)) {
			this.commonErrors.add(msg);
		}
	}
	
	public LinkedHashMap<FormItem, List<String>> getErrors() { return this.errors;}
	public List<String> getCommonErrors() { return this.commonErrors;}
	
	public boolean hasErrors() { return this.errors.size() + this.commonErrors.size() > 0;}
	
	public boolean isSuccess() { return !hasErrors();}
	
	public String toJson() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("success", isSuccess());
		if (hasErrors()) {
			Map<String, List<String>> msgMap = new LinkedHashMap<String, List<String>>();
			for (Map.Entry<FormItem, List<String>> entry : this.errors.entrySet()) {
				msgMap.put(entry.getKey().getName(), entry.getValue());
			}
			if (this.commonErrors.size() > 0) {
				msgMap.put("#", this.commonErrors);
			}
			map.put("messages", msgMap);
		}
		return JsonUtils.toJson(map);
	}
}
