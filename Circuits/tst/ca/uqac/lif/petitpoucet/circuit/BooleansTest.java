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

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.Vertex.and;
import static ca.uqac.lif.petitpoucet.Vertex.or;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.IdentityVertexFactory;

/**
 * Unit tests for the classes in the {@link Lists} class.
 * @author Sylvain Hallé
 */
@SuppressWarnings("unused")
public class BooleansTest
{
	@Test
	public void testAnd1()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		Connectable.connect(new Constant(false), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(false, o);
	}

	@Test
	public void testAnd2()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		Connectable.connect(new Constant(true), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(true, o);
	}

	@Test
	public void testAnd3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		Connectable.connect(new Constant(true), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		f.compute();
		AbstractVertex ae = f.explain(OutputPart.FIRST);
		factory.clear();
		Vertex e = AbstractVertex.get(ae);
		e.render(System.out);
		assertEqualGraphs(e, tree(factory.getPart(OutputPart.FIRST, f),
				and(
						factory.getPart(new Connectable.InputPart(0), f),
						factory.getPart(new Connectable.InputPart(1), f)))
				);
	}

	@Test
	public void testAnd4() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		Connectable.connect(new Constant(false), 0, f, 0);
		Connectable.connect(new Constant(false), 0, f, 1);
		f.compute();
		AbstractVertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, factory.getPart(new Connectable.InputPart(0), f));
	}

	@Test
	public void testOr1()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		Connectable.connect(new Constant(false), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(true, o);
	}

	@Test
	public void testOr2()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		Connectable.connect(new Constant(false), 0, f, 0);
		Connectable.connect(new Constant(false), 0, f, 1);
		Object o = f.compute();
		assertEquals(false, o);
	}

	@Test
	public void testOr3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		Connectable.connect(new Constant(true), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		f.compute();
		AbstractVertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, or(
				factory.getPart(new Connectable.InputPart(0), f),
				factory.getPart(new Connectable.InputPart(1), f)));
	}

	@Test
	public void testOr4() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		Connectable.connect(new Constant(true), 0, f, 0);
		Connectable.connect(new Constant(true), 0, f, 1);
		f.compute();
		AbstractVertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, factory.getPart(new Connectable.InputPart(0), f));
	}
}
