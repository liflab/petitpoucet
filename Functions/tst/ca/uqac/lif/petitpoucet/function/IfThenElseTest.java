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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.function.ComputableConnector.connect;

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Connectable;
import ca.uqac.lif.petitpoucet.function.Constant;
import ca.uqac.lif.petitpoucet.function.IfThenElse;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;
import ca.uqac.lif.petitpoucet.IdentityVertexFactory;

/**
 * Unit tests for {@link IfThenElse}.
 * @author Sylvain Hallé
 */
@SuppressWarnings("unused")
public class IfThenElseTest
{
	@Test
	public void test1()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		IfThenElse f = new IfThenElse();
		connect(new Constant(false), 0, f, 0);
		connect(new Constant("a"), 0, f, 1);
		connect(new Constant("b"), 0, f, 2);
		Object o = f.compute();
		assertEquals("b", o);
	}
	
	@Test
	public void test2()
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		IfThenElse f = new IfThenElse();
		connect(new Constant(true), 0, f, 0);
		connect(new Constant("a"), 0, f, 1);
		connect(new Constant("b"), 0, f, 2);
		Object o = f.compute();
		assertEquals("a", o);
	}
	
	@Test
	public void test3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		IfThenElse f = new IfThenElse();
		connect(new Constant(true), 0, f, 0);
		connect(new Constant("a"), 0, f, 1);
		connect(new Constant("b"), 0, f, 2);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, factory.or(
				factory.getPart(new Connectable.InputPart(0), f),
				factory.getPart(new Connectable.InputPart(1), f)));
	}
	
	@Test
	public void test4() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory();
		IfThenElse f = new IfThenElse();
		connect(new Constant(false), 0, f, 0);
		connect(new Constant("a"), 0, f, 1);
		connect(new Constant("b"), 0, f, 2);
		f.compute();
		Vertex e = f.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(e, factory.or(
				factory.getPart(new Connectable.InputPart(0), f),
				factory.getPart(new Connectable.InputPart(2), f)));
	}
}
