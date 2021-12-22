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
 * Unit tests for {@link Remove}.
 */
public class RemoveTest
{
	@Test
	public void test1()
	{
		Remove f = new Remove(2, 7);
		String s = "abcdefg";
		String out = (String) f.evaluate(s)[0];
		assertEquals("ab", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 1, 0, 1)
				), f.getMapping());
	}
	
	@Test
	public void test2()
	{
		Remove f = new Remove(2, 7);
		String s = "abcdefghij";
		String out = (String) f.evaluate(s)[0];
		assertEquals("abhij", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 1, 0, 1),
				new RangePair(7, 9, 2, 4)
				), f.getMapping());
	}
	
	@Test
	public void test3()
	{
		Remove f = new Remove(2, 10);
		String s = "abcdefg";
		String out = (String) f.evaluate(s)[0];
		assertEquals("ab", out);
		assertEquals(new RangeMapping(
				new RangePair(0, 1, 0, 1)
				), f.getMapping());
	}

	@Test
	public void test4()
	{
		Remove f = new Remove(0, 3);
		String s = "abcdefg";
		String out = (String) f.evaluate(s)[0];
		assertEquals("defg", out);
		assertEquals(new RangeMapping(
				new RangePair(3, 6, 0, 3)
				), f.getMapping());
	}
	
	@Test
	public void test5()
	{
		Remove f = new Remove(0, 20);
		String s = "abcdefg";
		String out = (String) f.evaluate(s)[0];
		assertEquals("", out);
		assertTrue(f.getMapping().isEmpty());
	}
}
