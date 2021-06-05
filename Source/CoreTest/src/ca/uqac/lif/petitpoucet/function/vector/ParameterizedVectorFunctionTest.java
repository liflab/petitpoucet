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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class ParameterizedVectorFunctionTest
{
	@Test
	public void testReplaceOutput1()
	{
		NthOutput d = (NthOutput) VectorOutputFunction.replaceElementByOutput(ComposedPart.create(new NthElement(1), NthOutput.FIRST));
		assertEquals(NthOutput.FIRST, d);
	}
	
	@Test
	public void testReplaceOutput2()
	{
		ComposedPart d = (ComposedPart) VectorOutputFunction.replaceElementByOutput(ComposedPart.create(new NthElement(4), new NthElement(1), NthOutput.FIRST));
		assertEquals(2, d.size());
		assertEquals(NthOutput.FIRST, d.head());
		assertEquals(new NthElement(4), d.tail());
	}
	
	@Test
	public void testReplaceInput1()
	{
		ComposedPart d = (ComposedPart) VectorOutputFunction.replaceInputByElement(NthInput.FIRST, 10);
		assertEquals(2, d.size());
		assertEquals(NthInput.FIRST, d.get(1));
		assertEquals(new NthElement(10), d.get(0));
	}
	
	@Test
	public void testReplaceInput2()
	{
		ComposedPart d = (ComposedPart) VectorOutputFunction.replaceInputByElement(ComposedPart.create(new NthElement(4), new NthElement(1), NthInput.FIRST), 10);
		assertEquals(4, d.size());
		assertEquals(NthInput.FIRST, d.get(3));
		assertEquals(new NthElement(10), d.get(2));
		assertEquals(new NthElement(1), d.get(1));
		assertEquals(new NthElement(4), d.get(0));
	}

}
