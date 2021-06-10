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
package ca.uqac.lif.petitpoucet.function.vector;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class NthElementTest
{
	@Test
	public void testReplace1()
	{
		Part cd = ComposedPart.compose(new NthElement(0), NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertTrue(d instanceof ComposedPart);
		ComposedPart n_cd = (ComposedPart) d;
		assertEquals(2, n_cd.size());
		assertEquals(new NthElement(10), n_cd.get(0));
		assertEquals(NthInput.FIRST, n_cd.get(1));
	}
	
	@Test
	public void testReplace2()
	{
		Part cd = ComposedPart.compose(new NthElement(0), new NthElement(25), NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertTrue(d instanceof ComposedPart);
		ComposedPart n_cd = (ComposedPart) d;
		assertEquals(3, n_cd.size());
		assertEquals(new NthElement(0), n_cd.get(0));
		assertEquals(new NthElement(10), n_cd.get(1));
		assertEquals(NthInput.FIRST, n_cd.get(2));
	}
	
	@Test
	public void testReplace3()
	{
		Part cd = ComposedPart.compose(NthInput.FIRST, NthOutput.FIRST);
		Part d = NthElement.replaceNthOutputByNthInput(cd, 10);
		assertEquals(cd, d);
	}
}
