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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;

/**
 * Factory for creating vertices. This factory is used to ensure that the same
 * vertex is not created twice, and to keep track of the vertices created.
 * Additionally, it allows to create sub-factories that share the same vertices,
 * but can create new vertices without disturbing the parent factory.
 * @author Sylvain Hallé 
 */
public class IdentityVertexFactory implements VertexFactory
{
	/**
	 * The parent factory, if any. If this factory is a sub-factory, it shares the
	 * same vertices as the parent factory, but can create new vertices without
	 * disturbing the parent factory. If this factory is not a sub-factory,
	 * this field is null.
	 */
	/*@ null @*/ protected final IdentityVertexFactory m_parent;
	
	/**
	 * The sub-factories created from this factory. This map is used to keep
	 * track of the sub-factories created from this factory, so that they can be properly
	 * disposed of when this factory is disposed of. This list is never null, but can be empty.
	 */
	/*@ non_null @*/ protected final Map<Object,IdentityVertexFactory> m_children;
	
	/**
	 * The list of vertices created by this factory. This list is used to keep track of the
	 * vertices created by this factory, so that they can be properly disposed of when this
	 * factory is disposed of. This list is never null, but can be empty.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_vertices;
	
	/**
	 * Indicates whether the factory should cut the graph when creating vertices. 
	 */
	protected final boolean m_shouldCut;
	
	/**
	 * Creates a new vertex factory. This constructor is used to create the root factory, which
	 * has no parent factory.
	 */
	public IdentityVertexFactory()
	{
		this(false);
	}
	
	/**
	 * Creates a new vertex factory. This constructor is used to create the root factory, which
	 * has no parent factory.
	 * @param should_cut Indicates whether the factory should cut the graph when creating
	 * vertices
	 */
	public IdentityVertexFactory(boolean should_cut)
	{
		this(null, should_cut);
	}
	
	/**
	 * Creates a new vertex factory. This constructor is used to create a
	 * sub-factory, which has a parent factory.
	 * @param vf The parent factory. This parameter can be null, in which case this factory
	 * is the root factory.
	 * @param should_cut Indicates whether the factory should cut the graph when creating
	 * vertices
	 */
	protected IdentityVertexFactory(/*@ null @*/ IdentityVertexFactory vf, boolean should_cut)
	{
		super();
		m_parent = vf;
		m_children = new IdentityHashMap<>();
		m_vertices = new ArrayList<>();
		m_shouldCut = should_cut;
	}
	
	@Override
	public PartVertex getPart(/*@ non_null @*/Part p, /*@ null @*/ Object s)
	{
		return getPart(new PartVertex(p, s));
	}
	
	@Override
	public PartVertex getPart(/*@ non_null @*/ PartVertex v)
	{
		int i = m_vertices.indexOf(v);
		if (i < 0)
		{
			m_vertices.add(v);
			return v;
		}
		return (PartVertex) m_vertices.get(i);
	}
	
	@Override
	public AndVertex getAnd()
	{
		return new AndVertex();
	}
	
	@Override
	public OrVertex getOr()
	{
		return new OrVertex();
	}
	
	@Override
	public IdentityVertexFactory subfactory(Object key)
	{
		if (m_children.containsKey(key))
		{
			return m_children.get(key);
		}
		IdentityVertexFactory vf = new IdentityVertexFactory(this, m_shouldCut);
		m_children.put(key, vf);
		return vf;
	}
	
	@Override
	public Subgraph subgraph()
	{
		return new Subgraph(m_vertices.get(0).findRoot(), m_vertices);
	}
	
	@Override
	/*@ pure @*/ public boolean contains(Part p, Object o)
	{
		return m_vertices.contains(new PartVertex(p, o));
	}
	
	@Override
	public boolean contains(Vertex v)
	{
		return m_vertices.contains(v);
	}
	
	@Override
	public void clear()
	{
		m_children.clear();
		m_vertices.clear();
	}
}