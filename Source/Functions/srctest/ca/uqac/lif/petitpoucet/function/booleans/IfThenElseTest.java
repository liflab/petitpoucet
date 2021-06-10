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
package ca.uqac.lif.petitpoucet.function.booleans;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

public class IfThenElseTest
{
	@Test
	public void test1()
	{
		IfThenElse ite = new IfThenElse();
		Object result = ite.evaluate(true, "foo", "bar")[0];
		assertEquals("foo", result);
		PartNode root = ite.getExplanation(NthOutput.FIRST);
		AndNode and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, and.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) and.getOutputLinks(0).get(0).getNode();
		assertEquals(NthInput.FIRST, pn1.getPart());
		PartNode pn2 = (PartNode) and.getOutputLinks(0).get(1).getNode();
		assertEquals(NthInput.SECOND, pn2.getPart());
	}
	
	@Test
	public void test2()
	{
		IfThenElse ite = new IfThenElse();
		Object result = ite.evaluate(false, "foo", "bar")[0];
		assertEquals("bar", result);
		PartNode root = ite.getExplanation(NthOutput.FIRST);
		AndNode and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, and.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) and.getOutputLinks(0).get(0).getNode();
		assertEquals(NthInput.FIRST, pn1.getPart());
		PartNode pn2 = (PartNode) and.getOutputLinks(0).get(1).getNode();
		assertEquals(NthInput.THIRD, pn2.getPart());
	}
	
	@Test
	public void test3()
	{
		IfThenElse ite = new IfThenElse();
		List<String> list1 = (List<String>) Arrays.asList("foo", "bar");
		List<String> list2 = (List<String>) Arrays.asList("baz", "qwe");
		Object result = ite.evaluate(true, list1, list2)[0];
		assertEquals(list1, result);
		PartNode root = ite.getExplanation(ComposedPart.compose(new NthElement(1), NthOutput.FIRST));
		AndNode and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, and.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) and.getOutputLinks(0).get(0).getNode();
		assertEquals(NthInput.FIRST, pn1.getPart());
		PartNode pn2 = (PartNode) and.getOutputLinks(0).get(1).getNode();
		assertEquals(ComposedPart.compose(new NthElement(1), NthInput.SECOND), pn2.getPart());
	}
}
