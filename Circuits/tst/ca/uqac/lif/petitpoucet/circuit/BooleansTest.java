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

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.CutVertexFactory;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.circuit.Node.DownstreamConnection;
import ca.uqac.lif.petitpoucet.circuit.Node.UpstreamConnection;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;
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
		connect(new Constant(false), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(false, o);
	}

	@Test
	public void testAnd2()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		connect(new Constant(true), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(true, o);
	}

	@Test
	public void testAnd3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.And f = new Booleans.And(2);
		connect(new Constant(true), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		f.compute();
		Vertex ae = f.explain(OutputPart.FIRST);
		factory.clear();
		ConcreteVertex e = Vertex.get(ae);
		e.render(System.out);
		assertEqualGraphs(e, factory.tree(factory.getPart(OutputPart.FIRST, f),
				factory.and(
						factory.getPart(new Connectable.InputPart(0), f),
						factory.getPart(new Connectable.InputPart(1), f)))
				);
	}

	@Test
	public void testAnd4() throws ExplanationException
	{
		VertexFactory factory = new CutVertexFactory(new IdentityVertexFactory());
		Booleans.And f = new Booleans.And(2);
		connect(new Constant(false), 0, f, 0);
		connect(new Constant(false), 0, f, 1);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0), factory);
		ConcreteVertex ce = Vertex.get(e);
		factory.clear();
		assertEqualGraphs(ce, factory.getPart(new Connectable.InputPart(0), f));
	}

	@Test
	public void testOr1()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		connect(new Constant(false), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		Object o = f.compute();
		assertEquals(true, o);
	}

	@Test
	public void testOr2()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		connect(new Constant(false), 0, f, 0);
		connect(new Constant(false), 0, f, 1);
		Object o = f.compute();
		assertEquals(false, o);
	}

	@Test
	public void testOr3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		Booleans.Or f = new Booleans.Or(2);
		connect(new Constant(true), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, factory.or(
				factory.getPart(new Connectable.InputPart(0), f),
				factory.getPart(new Connectable.InputPart(1), f)));
	}

	@Test
	public void testOr4() throws ExplanationException
	{
		VertexFactory factory = new CutVertexFactory(new IdentityVertexFactory());
		Booleans.Or f = new Booleans.Or(2);
		connect(new Constant(true), 0, f, 0);
		connect(new Constant(true), 0, f, 1);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0), factory);
		ConcreteVertex ce = Vertex.get(e);
		ce.render(System.out);
		factory.clear();
		assertEqualGraphs(ce, factory.getPart(new Connectable.InputPart(0), f));
	}
	
	public static void connect(Connectable c1, int i1, Connectable c2, int i2)
	{
		UpstreamConnection uc = new UpstreamConnection(c1, i1);
		DownstreamConnection dc = new DownstreamConnection(c2, i2);
		Connectable.connect(uc, i1, dc, i2);
	}
}
