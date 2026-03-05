/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.circuit.Lists.ElementAt;
import ca.uqac.lif.petitpoucet.circuit.Lists.NthElement;

/**
 * Unit tests for the classes in the {@link Lists} class.
 * @author Sylvain Hallé
 */
public class ListsTest
{
	@Test
	public void testElementAt1()
	{
		ElementAt f = new ElementAt(0);
		Connectable.connect(new Constant(Arrays.asList("a", "b", "c")), 0, f, 0);
		Object o = f.compute();
		assertEquals("a", o);
	}
	
	@Test
	public void testElementAt2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		ElementAt f = new ElementAt(0);
		Connectable.connect(new Constant(Arrays.asList("a", "b", "c")), 0, f, 0);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0));
		e.render(System.out);
		assertEqualGraphs(e, tree(factory.getPart(new CompositePart(new NthElement(0), new Connectable.InputPart(0)), f)));
	}
}
