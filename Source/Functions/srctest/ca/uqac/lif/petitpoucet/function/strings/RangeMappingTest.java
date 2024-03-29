/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.function.strings.RangeMapping.RangePair;

/**
 * Unit tests for {@link RangeMapping}.
 */
public class RangeMappingTest
{
	@Test
	public void testRangePairEquals1()
	{
		RangePair rp1 = new RangePair(10, 18, 13, 13, false);
		RangePair rp2 = new RangePair(10, 18, 13, 13, false);
		assertTrue(rp1.equals(rp2));
		assertTrue(rp2.equals(rp1));
	}
	
	@Test
	public void testRangeMappingEquals1()
	{
		RangeMapping rm1 = new RangeMapping().add(new RangePair(10, 18, 13, 13, false));
		RangeMapping rm2 = new RangeMapping().add(new RangePair(10, 18, 13, 13, false));
		assertTrue(rm1.equals(rm2));
		assertTrue(rm2.equals(rm1));
	}
	
	@Test
	public void testTrackToOutput1()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(8, 20)));
		List<Range> inversion = rp.trackToOutput(new Range(3, 9));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(3, 5), r);
		r = inversion.get(1);
		assertEquals(new Range(8, 20), r);
	}
	
	@Test
	public void testTrackToOutput2()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 361), new Range(0, 361)),
				new RangePair(new Range(362, 570), new Range(465, 673)));
		List<Range> inversion = rp.trackToOutput(new Range(361, 367));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(361, 361), r);
		r = inversion.get(1);
		assertEquals(new Range(465, 470), r);
	}
	
	@Test
	public void testTrackToInput1()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(0, 5));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 5), r);
	}

	@Test
	public void testTrackToInput2()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(6, 9));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(7, 10), r);
	}

	@Test
	public void testTrackToInput3()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(1, 2));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(1, 2), r);
	}

	@Test
	public void testTrackToInput4()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(7, 9));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(8, 10), r);
	}

	@Test
	public void testTrackToInput5()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(4, 8));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 5), r);
		r = inversion.get(1);
		assertEquals(new Range(7, 9), r);
	}

	@Test
	public void testTrackToInput6()
	{
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(10, 20));
		assertEquals(0, inversion.size());
	}

	@Test
	public void testTrackToInput7()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 5), new Range(0, 5)),
				new RangePair(new Range(7, 10), new Range(6, 9)));
		List<Range> inversion = rp.trackToInput(new Range(4, 12));
		assertEquals(2, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 5), r);
		r = inversion.get(1);
		assertEquals(new Range(7, 10), r);
	}

	@Test
	public void testTrackToInput8()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 3), new Range(2, 5)),
				new RangePair(new Range(4, 10), new Range(8, 14)));
		List<Range> inversion = rp.trackToInput(new Range(2, 14));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 10), r);
	}
	
	@Test
	public void testTrackToInput9()
	{
		RangeMapping rp = new RangeMapping(
				new RangePair(0, 34, 0, 34),
				new RangePair(35, 43, 39, 47));
		List<Range> inversion = rp.trackToInput(new Range(35, 37));
		assertEquals(0, inversion.size());
	}

	@Test
	public void testTrackToInputNonBijective1()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(0, 3));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 1), r);
	}

	@Test
	public void testTrackToInputNonBijective2()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(4, 4));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(2, 2), r);
	}

	@Test
	public void testTrackToInputNonBijective3()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(5, 5));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(3, 3), r);
	}

	@Test
	public void testTrackToInputNonBijective4()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(6, 7));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(4, 7), r);
	}

	@Test
	public void testTrackToInputNonBijective5()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(1, 1));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 1), r);
	}

	@Test
	public void testTrackToInputNonBijective6()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(3, 4));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(0, 2), r);
	}

	@Test
	public void testTrackToInputNonBijective7()
	{
		Range r;
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		List<Range> inversion = rp.trackToInput(new Range(5, 7));
		assertEquals(1, inversion.size());
		r = inversion.get(0);
		assertEquals(new Range(3, 7), r);
	}

	@Test
	public void testUnite1()
	{
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		rp.uniteWith(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(7, 9), new Range(17, 19))
				), 10, 20);
		assertEquals(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)),
				new RangePair(new Range(10, 11), new Range(20, 23)),
				new RangePair(new Range(17, 19), new Range(37, 39))				
				), rp);
	}
	
	@Test
	public void testUnite2()
	{
		RangeMapping rp = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)));
		rp.uniteWith(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(7, 9), new Range(17, 19))
				), 10, 20);
		assertEquals(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 3)),
				new RangePair(new Range(2, 3), new Range(4, 5)),
				new RangePair(new Range(4, 7), new Range(7, 8)),
				new RangePair(new Range(10, 11), new Range(20, 23)),
				new RangePair(new Range(17, 19), new Range(37, 39))				
				), rp);
	}
	
	@Test
	public void testFragment1()
	{
		List<Range> list1 = Arrays.asList(new Range(0, 2), new Range(3, 7));
		List<Range> list2 = Arrays.asList(new Range(0, 5), new Range(6, 6), new Range(7, 10));
		List<Range> result = RangeMapping.fragment(list1, list2);
		assertEquals(5, result.size());
		assertEquals(new Range(0, 2), result.get(0));
		assertEquals(new Range(3, 5), result.get(1));
		assertEquals(new Range(6, 6), result.get(2));
		assertEquals(new Range(7, 7), result.get(3));
		assertEquals(new Range(8, 10), result.get(4));
	}
	
	@Test
	public void testCompose1()
	{
		RangeMapping rm1 = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 1)),
				new RangePair(new Range(2, 3), new Range(4, 5)));
		RangeMapping rm2 = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(2, 3)),
				new RangePair(new Range(4, 5), new Range(5, 6)));
		RangeMapping rm_c = RangeMapping.compose(rm1, rm2);
		assertEquals(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(2, 3)),
				new RangePair(new Range(2, 3), new Range(5, 6))
				), rm_c);
	}
	
	@Test
	public void testCompose2()
	{
		RangeMapping rm1 = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(0, 1)),
				new RangePair(new Range(2, 3), new Range(4, 5)));
		RangeMapping rm2 = new RangeMapping(
				new RangePair(new Range(0, 1), new Range(2, 3)));
		RangeMapping rm_c = RangeMapping.compose(rm1, rm2);
		assertEquals(new RangeMapping(
				new RangePair(new Range(0, 1), new Range(2, 3))
				), rm_c);
	}
	
	@Test
	public void testCompose3()
	{
		RangeMapping rm1 = new RangeMapping(
				new RangePair(new Range(0, 0), new Range(0, 0)),
				new RangePair(new Range(4, 7), new Range(0, 0)));
		RangeMapping rm2 = new RangeMapping(
				new RangePair(new Range(0, 0), new Range(0, 0)));
		RangeMapping rm_c = RangeMapping.compose(rm1, rm2);
		assertEquals(new RangeMapping(
				new RangePair(new Range(0, 0), new Range(0, 0)),
				new RangePair(new Range(4, 7), new Range(0, 0))), rm_c);
	}
}
