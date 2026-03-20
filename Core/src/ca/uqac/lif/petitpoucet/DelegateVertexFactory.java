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

import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;

/**
 * A vertex factory that delegates all calls to another factory. This
 * class is useful to create factories with different settings, using
 * the <em>Decorator</em> design pattern.
 */
public class DelegateVertexFactory implements VertexFactory
{
	@Override
	public AndVertex and(Vertex... vertices)
	{
		return m_factory.and(vertices);
	}

	@Override
	public OrVertex or(Vertex... vertices)
	{
		return m_factory.or(vertices);
	}

	@Override
	public Vertex tree(Vertex root, Vertex... vertices)
	{
		return m_factory.tree(root, vertices);
	}

	/**
	 * The factory to which all calls are delegated. 
	 */
	/*@ non_null @*/ protected final VertexFactory m_factory;

	/**
	 * Creates a new delegate factory.
	 * @param factory The factory to which all calls will be delegated
	 */
	public DelegateVertexFactory(/*@ non_null @*/ VertexFactory factory)
	{
		m_factory = factory;
	}

	@Override
	public OrVertex getOr()
	{
		return m_factory.getOr();
	}

	@Override
	public AndVertex getAnd()
	{
		return m_factory.getAnd();
	}

	@Override
	public PartVertex getPart(Part p, Object o)
	{
		return m_factory.getPart(p, o);
	}

	@Override
	public ConcreteVertex getPart(PartVertex v)
	{
		return m_factory.getPart(v);
	}

	@Override
	public VertexFactory subfactory(Object o)
	{
		return new DelegateVertexFactory(m_factory.subfactory(o));
	}

	@Override
	public boolean contains(Part p, Object o)
	{
		return m_factory.contains(p, o);
	}

	@Override
	public boolean contains(Vertex v)
	{
		return m_factory.contains(v);
	}

	@Override
	public void clear()
	{
		m_factory.clear();
	}

	@Override
	public Subgraph subgraph()
	{
		return m_factory.subgraph();
	}
}
