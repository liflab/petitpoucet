package ca.uqac.lif.petitpoucet.function.strings;

import static org.junit.Assert.*;

import org.junit.Test;

public class RemoveLineTest
{
	protected static final String CRLF = System.getProperty("line.separator");
	protected static final int CRLF_S = CRLF.length();
	
	@Test
	public void test1()
	{
		RemoveLine f = new RemoveLine(1);
		String s = "abc" + CRLF + "def" + CRLF + "ghi";
		String out = (String) f.evaluate(s)[0];
		assertEquals("abc" + CRLF + "ghi", out);
	}
	
	@Test
	public void test2()
	{
		RemoveLine f = new RemoveLine(1);
		String s = "abc" + CRLF + CRLF + "ghi";
		String out = (String) f.evaluate(s)[0];
		assertEquals("abc" + CRLF + "ghi", out);
	}
	
	@Test
	public void test3()
	{
		RemoveLine f = new RemoveLine(3);
		String s = "abc" + CRLF + CRLF + "ghi" + CRLF;
		String out = (String) f.evaluate(s)[0];
		assertEquals("abc" + CRLF + CRLF + "ghi", out);
	}
}
