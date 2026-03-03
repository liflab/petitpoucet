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
package ca.uqac.lif.petitpoucet;

import static org.junit.Assert.*;

import org.junit.Test;

public class VertexFactoryTest
{
	@Test
	public void test1()
	{
		VertexFactory f = new VertexFactory();
		Vertex v1 = f.getAnd();
		Vertex v2 = f.getAnd();
		assertNotSame(v1, v2);
	}
	
	@Test
	public void test2()
	{
		VertexFactory f = new VertexFactory();
		Vertex v1 = f.getOr();
		Vertex v2 = f.getOr();
		assertNotSame(v1, v2);
	}
	
	@Test
	public void test3()
	{
		Object o = new Object();
		VertexFactory f = new VertexFactory();
		Vertex v1 = f.getPart(new CompositePartTest.DummyPart("a"), o);
		Vertex v2 = f.getPart(new CompositePartTest.DummyPart("a"), o);
		assertSame(v1, v2);
	}
	
	@Test
	public void test4()
	{
		Object o = new Object();
		VertexFactory f = new VertexFactory();
		Vertex v1 = f.getPart(new CompositePartTest.DummyPart("a"), o);
		Vertex v2 = f.getPart(new CompositePartTest.DummyPart("b"), o);
		assertNotSame(v1, v2);
	}
}
