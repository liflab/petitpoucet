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

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.function.strings.RangeMapping.RangePair;

/**
 * Unit tests for {@link RangeMapping}.
 */
public class RangeMappingTest
{
	@Test
	public void testInvert1()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(0, 5));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 5), r);
	}
	
	@Test
	public void testInvert2()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(6, 9));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(7, 10), r);
	}
	
	@Test
	public void testInvert3()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(1, 2));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(1, 2), r);
	}
	
	@Test
	public void testInvert4()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(7, 9));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(8, 10), r);
	}
	
	@Test
	public void testInvert5()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(4, 8));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 5), r);
		r = inversion.get(1);
		assertEquals(new Range(7, 9), r);
	}
	
	@Test
	public void testInvert6()
	{
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(10, 20));
		assertEquals(0, inversion.size());
	}
	
	@Test
	public void testInvert7()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.invert(new Range(4, 12));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 5), r);
		r = inversion.get(1);
		assertEquals(new Range(7, 10), r);
	}
	
	@Test
	public void testInvert8()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 3), new Range(2, 5)),
				new RangePair(new Range(4, 10), new Range(8, 14)));
		List<Range> inversion = rp.invert(new Range(2, 14));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 10), r);
	}
	
	@Test
	public void testInvertNonBijective1()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(0, 3));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 1), r);
	}
	
	@Test
	public void testInvertNonBijective2()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(4, 4));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(2, 2), r);
	}
	
	@Test
	public void testInvertNonBijective3()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(5, 5));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(3, 3), r);
	}
	
	@Test
	public void testInvertNonBijective4()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(6, 7));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 7), r);
	}
	
	@Test
	public void testInvertNonBijective5()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(1, 1));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 1), r);
	}
	
	@Test
	public void testInvertNonBijective6()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(3, 4));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 2), r);
	}
	
	@Test
	public void testInvertNonBijective7()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.invert(new Range(5, 7));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(3, 7), r);
	}
}
