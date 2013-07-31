package jp.co.flect.formvalidation;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import jp.co.flect.io.FileUtils;
import jp.co.flect.json.JsonUtils;

public class JsonTest {
	
	private static final String TESTDATA_PATH = "../enq/app/data";
	@Test 
	public void readSample() throws Exception {
		String json = FileUtils.readFileAsString(new File(TESTDATA_PATH, "sampleForm.json"));
		FormDefinition form = FormDefinition.fromJson(json);
		assertNotNull(form.getItems());
	}
}
