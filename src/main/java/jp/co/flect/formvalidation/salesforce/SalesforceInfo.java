package jp.co.flect.formvalidation.salesforce;

public class SalesforceInfo {
	
	private String name;
	private String label;
	private String desc;
	
	public SalesforceInfo(String name) {
		this.name = name;
	}
	
	public String getName() { return this.name;}
	public String getObjectName() { 
		return this.name.toLowerCase().endsWith("__c") ? this.name : this.name + "__c";
	}
	
	public String getLabel() { return this.label == null ? this.name : this.label;}
	public void setLabel(String s) { this.label = s;}
	
	public String getDescription() { return this.desc;}
	public void setDescription(String s) { this.desc = s;}
}
