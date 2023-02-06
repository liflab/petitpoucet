/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.function.strings;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.function.strings.RangeMapping.RangePair;

/**
 * Unit tests for {@link InsertAt}.
 */
public class InsertAtTest
{
	@Test
	public void test1()
	{
		InsertAt f = new InsertAt("foo", 0);
		String out = (String) f.evaluate("Hello")[0];
		assertEquals("fooHello", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 4, 3, 7)), f.getMapping());
	}
	
	@Test
	public void test2()
	{
		InsertAt f = new InsertAt("foo", 1);
		String out = (String) f.evaluate("Hello")[0];
		assertEquals("Hfooello", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 0, 0, 0),
				new RangePair(1, 4, 4, 7)), f.getMapping());
	}
	
	@Test
	public void test3()
	{
		InsertAt f = new InsertAt("foo", 5);
		String out = (String) f.evaluate("Hello")[0];
		assertEquals("Hellofoo", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 4, 0, 4)), f.getMapping());
	}
	
	@Test
	public void test4()
	{
		InsertAt f = new InsertAt("foo", 9);
		String out = (String) f.evaluate("Hello")[0];
		assertEquals("Hellofoo", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 4, 0, 4)), f.getMapping());
	}
	
	@Test
	public void test5()
	{
		InsertAt f = new InsertAt("big ", 35);
		String out = (String) f.evaluate("The quick brown fox jumps over the lazy dog.")[0];
		assertEquals("The quick brown fox jumps over the big lazy dog.", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 34, 0, 34),
				new RangePair(35, 43, 39, 47)), f.getMapping());
	}
}
