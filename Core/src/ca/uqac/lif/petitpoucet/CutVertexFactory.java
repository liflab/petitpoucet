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

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;

/**
 * A vertex factory that generates OR vertices accepting only one child.
 * All attempts to add more than one child to an OR vertex will be
 * ignored.
 */
public class CutVertexFactory extends DelegateVertexFactory
{
	/**
	 * Creates a new cut vertex factory.
	 * @param factory The factory to which all calls will be delegated
	 */
	public CutVertexFactory(VertexFactory factory)
	{
		super(factory);
	}
	
	/**
	 * Creates a new cut vertex factory.
	 * @param factory The factory to which all calls will be delegated
	 * @param parent The parent factory, if this factory is a sub-factory. This parameter can be null.
	 */
	public CutVertexFactory(VertexFactory factory, VertexFactory parent)
	{
		super(factory, parent);
	}
	
	@Override
	public OrVertex getOr()
	{
		return new CutOrVertex(null);
	}
	
	public VertexFactory subfactory(Object key)
	{
		if (m_children.containsKey(key))
		{
			return m_children.get(this);
		}
		CutVertexFactory f = new CutVertexFactory(m_factory.subfactory(key), this);
		m_children.put(key, f);
		return f;
	}
	
	/**
	 * An OR vertex that accepts only one child. Any attempt to add more
	 * than one child will be ignored.
	 */
	public class CutOrVertex extends LazyVertex implements OrVertex
	{
		/**
		 * Creates a new cut OR vertex.
		 */
		public CutOrVertex(Part p)
		{
			super(CutVertexFactory.this, p);
		}
		
		@Override
		public void addChild(Vertex v)
		{
			if (m_children.isEmpty())
			{
				m_children.add(v);
			}
		}
		
		@Override
		public int hashCode()
		{
			return 31 * super.hashCode() + 1;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o == null || getClass() != o.getClass())
			{
				return false;
			}
			CutOrVertex v = (CutOrVertex) o;
			return m_children.equals(v.m_children);
		}
		
		@Override
		public ConcreteVertex concretize(Part p, VertexFactory m_factory) throws ExplanationException
		{
			return m_children.isEmpty() ? null : Vertex.get(m_children.get(0));
		}
	}
}
