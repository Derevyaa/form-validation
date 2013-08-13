package jp.co.flect.formvalidation.salesforce;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import jp.co.flect.salesforce.SalesforceClient;
import jp.co.flect.salesforce.metadata.MetadataClient;
import jp.co.flect.salesforce.metadata.CustomObject;
import jp.co.flect.io.FileUtils;
import jp.co.flect.log.Level;

public class SalesforceTest {

	private static SalesforceClient CLIENT;
	private static MetadataClient META_CLIENT;
	private static final File PARTNER_WSDL = new File("../enq/conf/salesforce/partner.wsdl");
	private static final File METADATA_WSDL = new File("../enq/conf/salesforce/metadata.wsdl");
	private static final File JSON_FILE = new File("../enq/app/data/sampleForm.json");

	@BeforeClass
	public static void deleteObject() throws Exception{
		String username = System.getenv().get("SALESFORCE_USERNAME");
		String password = System.getenv().get("SALESFORCE_PASSWORD");
		String secret = System.getenv().get("SALESFORCE_SECURITY_TOKEN");
		SalesforceClient client = new SalesforceClient(PARTNER_WSDL);
		client.getLogger().setLevel(Level.TRACE);
		client.login(username, password, secret);

		CLIENT = client;
		META_CLIENT = client.createMetadataClient(METADATA_WSDL);

		CustomObject obj = new CustomObject();
		obj.setFullName("ccc__c");
		META_CLIENT.delete(obj);
	}	

	@Test 
	public void test() throws Exception {
		SalesforceObjectBuilder builder = new SalesforceObjectBuilder(CLIENT, METADATA_WSDL);
		SalesforceInfo info = new SalesforceInfo("ccc");
		String json = FileUtils.readFileAsString(JSON_FILE);
		builder.generate(info, json);
	}
}
