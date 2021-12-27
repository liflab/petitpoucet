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

import static ca.uqac.lif.petitpoucet.ComposedPart.compose;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.RangeMapping.RangePair;

public class StringMappingFunctionTest
{
	@Test
	public void testExplanation1()
	{
		RangeMapping rm = new RangeMapping(
				new RangePair(0, 34, 0, 34),
				new RangePair(35, 43, 39, 47));
		StringMappingFunction smf = new StringMappingFunction(rm, 44, 48);
		PartNode root = smf.getExplanation(compose(new Range(35, 37), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		Node n_leaf = root.getOutputLinks(0).get(0).getNode();
		assertTrue(n_leaf instanceof PartNode);
		PartNode leaf = (PartNode) n_leaf;
		assertTrue(leaf.getPart() instanceof Part.Nothing);
	}
}
