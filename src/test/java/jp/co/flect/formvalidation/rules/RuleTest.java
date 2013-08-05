package jp.co.flect.formvalidation.rules;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.HashMap;

public class RuleTest {
	
	private static final String TESTDATA_PATH = "../enq/app/data";
	
	private boolean test(Rule rule, String value) throws Exception {
		String[] values = new String[1];
		values[0] = value;
		return rule.check(values);
	}
	
	@Test 
	public void alpha() throws Exception {
		Rule rule = new Alpha();
		assertTrue(test(rule, "abcdefghijklmnopqrstuvwxyz"));
		assertTrue(test(rule, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		
		assertFalse(test(rule, "abc1def"));
		assertFalse(test(rule, "123456"));
		assertFalse(test(rule, "abc#def"));
	}
	
	@Test 
	public void alphanum() throws Exception {
		Rule rule = new AlphaNum();
		assertTrue(test(rule, "abcdefghijklmnopqrstuvwxyz"));
		assertTrue(test(rule, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertTrue(test(rule, "abc1def"));
		assertTrue(test(rule, "123456"));
		
		assertFalse(test(rule, "abc#def"));
	}
	
	@Test 
	public void creditcard() throws Exception {
		Rule rule = new CreditCard();
		assertTrue(test(rule, "000-111-111-111"));
		assertTrue(test(rule, "000 111 111 111"));
		assertTrue(test(rule, "000111111111"));
		
		assertFalse(test(rule, "123a456"));
		assertFalse(test(rule, "123456a"));
	}
	
	@Test 
	public void date() throws Exception {
		Rule rule = new Date();
		assertTrue(test(rule, "2013-01-01"));
		assertFalse(test(rule, "2013-15-15"));
		assertFalse(test(rule, "2013/12/26"));
	}
	
	@Test 
	public void digits() throws Exception {
		Rule rule = new Digits();
		assertTrue(test(rule, "2013"));
		assertFalse(test(rule, "-15"));
		assertFalse(test(rule, "12.15"));
	}
	
	@Test 
	public void email() throws Exception {
		Rule rule = new Email();
		assertTrue(test(rule, "abc@test.com"));
		assertTrue(test(rule, "abc.def@test.com"));
		assertFalse(test(rule, "abc@test"));
		assertFalse(test(rule, "abc..def@test.com"));
	}
	
	@Test 
	public void equalTo() throws Exception {
		Rule rule = new EqualTo();
		rule.build("test1");
		
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("test1", new String[] { "test" });
		map.put("test2", new String[] { "test" });
		map.put("test3", new String[] { "test3" });
		
		assertNull(rule.validate(map, map.get("test2")));
		assertNotNull(rule.validate(map, map.get("test3")));
	}
	
	@Test 
	public void hankana() throws Exception {
		Rule rule = new Hankana();
		assertTrue(test(rule, "ｱｲｳ"));
		assertFalse(test(rule, "アイウ"));
		assertFalse(test(rule, "12.15"));
	}
	
	@Test 
	public void hiragana() throws Exception {
		Rule rule = new Hiragana();
		assertTrue(test(rule, "あいう"));
		assertFalse(test(rule, "アイウ"));
		assertFalse(test(rule, "12.15"));
	}
	
	@Test 
	public void katakana() throws Exception {
		Rule rule = new Katakana();
		assertTrue(test(rule, "アイウ"));
		assertFalse(test(rule, "あいう"));
		assertFalse(test(rule, "12.15"));
	}
	
	@Test 
	public void maxLength() throws Exception {
		Rule rule = new MaxLength();
		rule.build(5);
		
		assertTrue(test(rule, "アイウ"));
		assertTrue(test(rule, "アイウエオ"));
		assertFalse(test(rule, "アイウエオカ"));
		assertTrue(test(rule, "ABCDE"));
		assertFalse(test(rule, "ABCDEF"));
	}
	
	@Test 
	public void minLength() throws Exception {
		Rule rule = new MinLength();
		rule.build(5);
		
		assertFalse(test(rule, "アイウエ"));
		assertTrue(test(rule, "アイウエオ"));
		assertTrue(test(rule, "アイウエオカ"));
		assertFalse(test(rule, "ABCD"));
		assertTrue(test(rule, "ABCDE"));
		assertTrue(test(rule, "ABCDEF"));
	}
	
	@Test 
	public void max() throws Exception {
		Rule rule = new Max();
		rule.build(5);
		
		assertTrue(test(rule, "4"));
		assertTrue(test(rule, "5"));
		assertFalse(test(rule, "6"));
		assertTrue(test(rule, "-1"));
		assertTrue(test(rule, "0"));
		assertFalse(test(rule, "100"));
	}
	
	@Test 
	public void min() throws Exception {
		Rule rule = new Min();
		rule.build(5);
		
		assertFalse(test(rule, "4"));
		assertTrue(test(rule, "5"));
		assertTrue(test(rule, "6"));
		assertFalse(test(rule, "-1"));
		assertFalse(test(rule, "0"));
		assertTrue(test(rule, "100"));
	}
	
	@Test 
	public void number() throws Exception {
		Rule rule = new Number();
		assertTrue(test(rule, "2013"));
		assertTrue(test(rule, "-15"));
		assertTrue(test(rule, "12.15"));
		assertTrue(test(rule, "10,000,000"));
		assertFalse(test(rule, "10,0000,0000"));
		assertFalse(test(rule, "12.15.52"));
	}
	
	@Test 
	public void postcode() throws Exception {
		Rule rule = new PostCode();
		assertTrue(test(rule, "222-0033"));
		assertTrue(test(rule, "111-0000"));
		assertFalse(test(rule, "A05-0555"));
		assertFalse(test(rule, "2220033"));
		assertFalse(test(rule, "222-033"));
	}
	
	@Test 
	public void tel() throws Exception {
		Rule rule = new Tel();
		assertTrue(test(rule, "03-0000-1111"));
		assertTrue(test(rule, "090-1111-2222"));
		assertTrue(test(rule, "110"));
		assertFalse(test(rule, "090-11-11-11-11"));
		assertFalse(test(rule, "222-AAA"));
	}
	
	@Test 
	public void url() throws Exception {
		Rule rule = new Url();
		assertTrue(test(rule, "http://www.yahoo.co.jp"));
		assertTrue(test(rule, "https://www.yahoo.co.jp/"));
		assertTrue(test(rule, "ftp://www.yahoo.co.jp/hoge"));
		assertFalse(test(rule, "urn:test"));
		assertFalse(test(rule, "http://www.+hoge.com/"));
	}
	
	@Test 
	public void valueList() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "aaa");
		map.put("2", "bbb");
		map.put("3", "ccc");
		Rule rule = new ValueList(map);
		assertTrue(test(rule, "1"));
		assertTrue(test(rule, "2"));
		assertTrue(test(rule, "3"));
		assertFalse(test(rule, "4"));
	}
}
