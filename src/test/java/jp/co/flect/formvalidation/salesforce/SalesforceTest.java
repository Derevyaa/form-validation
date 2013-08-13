package jp.co.flect.formvalidation.salesforce;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import jp.co.flect.salesforce.SalesforceClient;

public class SalesforceTest {
	
	@Test 
	public void test() throws Exception {
		String username = System.getProperty("SALESFORCE_USERNAME");
		String password = System.getProperty("SALESFORCE_PASSWORD");
		String secret = System.getProperty("SALESFORCE_SECURITY_TOKEN");
		
		SalesforceClient client = new SalesforceClient(new File("../enq/conf/salesforce/partner.wsdl"));
		client.login(username, password, secret);
	}
}
