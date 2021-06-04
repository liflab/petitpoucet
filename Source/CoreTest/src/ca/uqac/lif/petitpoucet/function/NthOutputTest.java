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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

public class NthOutputTest
{
	@Test
	public void testReplace1() 
	{
		ComposedPart d = (ComposedPart) NthOutput.replaceOutBy(ComposedPart.create(new NthElement(1), NthOutput.FIRST), Part.all);
		assertEquals(2, d.size());
		assertEquals(new NthElement(1), d.head());
		assertEquals(Part.all, d.tail());
	}
}
