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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.Part;

/**
 * Unit tests for {@link Range}.
 */
public class RangeTest
{
	@Test
	public void testOverlap1()
	{
		Range r1 = new Range(0, 5);
		Range r2 = new Range(0, 4);
		boolean o = true;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap2()
	{
		Range r1 = new Range(0, 5);
		Range r2 = new Range(1, 4);
		boolean o = true;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap3()
	{
		Range r1 = new Range(0, 5);
		Range r2 = new Range(5, 8);
		boolean o = true;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap4()
	{
		Range r1 = new Range(5, 8);
		Range r2 = new Range(1, 5);
		boolean o = true;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap5()
	{
		Range r1 = new Range(0, 5);
		Range r2 = new Range(3, 8);
		boolean o = true;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap6()
	{
		Range r1 = new Range(0, 5);
		Range r2 = new Range(7, 9);
		boolean o = false;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testOverlap7()
	{
		Range r1 = new Range(3, 8);
		Range r2 = new Range(1, 2);
		boolean o = false;
		assertEquals(o, r1.overlaps(r2));
		assertEquals(o, r2.overlaps(r1));
	}
	
	@Test
	public void testMentionedRange()
	{
		Range r = new Range(3, 8);
		assertEquals(r, Range.mentionedRange((Part)r));
	}
}
