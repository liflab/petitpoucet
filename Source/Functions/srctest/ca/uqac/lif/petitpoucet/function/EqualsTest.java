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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.strings.Range;
import ca.uqac.lif.petitpoucet.function.strings.StringEquals;

/**
 * Unit tests for {@link Equals}.
 */
public class EqualsTest
{
	/*
	 * Checks that the explanation for equality defined by a Function
	 * correctly connects inputs and outputs to those of {@link Equals}.
	 */
	@Test
	public void testStrings1()
	{
		Equals eq = new Equals();
		boolean b = (Boolean) eq.evaluate("abc", "abcde")[0];
		assertFalse(b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child_1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(NthOutput.FIRST, child_1.getPart());
		assertTrue(child_1.getSubject() instanceof StringEquals);
		PartNode child_2 = (PartNode) child_1.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(3, 4), NthInput.SECOND), child_2.getPart());
		assertTrue(child_2.getSubject() instanceof StringEquals);
		PartNode child_3 = (PartNode) child_2.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(3, 4), NthInput.SECOND), child_3.getPart());
		assertEquals(eq, child_3.getSubject());
	}
}
