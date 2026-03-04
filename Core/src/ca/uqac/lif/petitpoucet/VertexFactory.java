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
public class VertexFactory
{
	/**
	 * The parent factory, if any. If this factory is a sub-factory, it shares the
	 * same vertices as the parent factory, but can create new vertices without
	 * disturbing the parent factory. If this factory is not a sub-factory,
	 * this field is null.
	 */
	/*@ null @*/ protected final VertexFactory m_parent;
	
	/**
	 * The sub-factories created from this factory. This map is used to keep
	 * track of the sub-factories created from this factory, so that they can be properly
	 * disposed of when this factory is disposed of. This list is never null, but can be empty.
	 */
	/*@ non_null @*/ protected final Map<Object,VertexFactory> m_children;
	
	/**
	 * The list of vertices created by this factory. This list is used to keep track of the
	 * vertices created by this factory, so that they can be properly disposed of when this
	 * factory is disposed of. This list is never null, but can be empty.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_vertices;
	
	/**
	 * Creates a new vertex factory. This constructor is used to create the root factory, which
	 * has no parent factory.
	 */
	public VertexFactory()
	{
		this(null);
	}
	
	/**
	 * Creates a new vertex factory. This constructor is used to create a
	 * sub-factory, which has a parent factory.
	 * @param vf The parent factory. This parameter can be null, in which case this factory
	 * is the root factory.
	 */
	protected VertexFactory(/*@ null @*/ VertexFactory vf)
	{
		super();
		m_parent = vf;
		m_children = new IdentityHashMap<>();
		m_vertices = new ArrayList<>();
	}
	
	/**
	 * Gets a part vertex for the given part and source. If a vertex with the same part and source
	 * already exists, it is returned. Otherwise, a new vertex is created and returned.
	 * @param p The part for which to get the vertex. This parameter cannot be null.
	 * @param s The source of the part. This parameter can be null.
	 * @return A part vertex for the given part and source.
	 */
	/*@ non_null @*/ public PartVertex getPart(/*@ non_null @*/Part p, /*@ null @*/ Object s)
	{
		return getPart(new PartVertex(p, s));
	}
	
	/**
	 * Gets a part vertex for the given part vertex. If a vertex with the same part and source
	 * already exists, it is returned. Otherwise, the given vertex is added to the list
	 * of vertices and returned.
	 * @param v The part vertex for which to get the vertex. This parameter cannot be null.
	 * @return A part vertex for the given part vertex.
	 */
	/*@ non_null @*/public PartVertex getPart(/*@ non_null @*/ PartVertex v)
	{
		int i = m_vertices.indexOf(v);
		if (i < 0)
		{
			m_vertices.add(v);
			return v;
		}
		return (PartVertex) m_vertices.get(i);
	}
	
	/**
	 * Gets an AND vertex. This method always returns a new vertex, as AND
	 * vertices are not identified by their content, but by their position in
	 * the graph.
	 * @return An AND vertex
	 */
	/*@ non_null @*/ public AndVertex getAnd()
	{
		return new AndVertex();
	}
	
	/**
	 * Gets an OR vertex. This method always returns a new vertex, as OR
	 * vertices are not identified by their content, but by their position in
	 * the graph.
	 * @return An OR vertex
	 */
	/*@ non_null @*/ public OrVertex getOr()
	{
		return new OrVertex();
	}
	
	/**
	 * Creates a new sub-factory associated to a given object.
	 * @param key The object to associate to the sub-factory. This parameter
	 * cannot be null.
	 * @return The corresponding sub-factory
	 */
	/*@ non_null @*/ public VertexFactory subfactory(Object key)
	{
		if (m_children.containsKey(key))
		{
			return m_children.get(key);
		}
		VertexFactory vf = new VertexFactory(this);
		m_children.put(key, vf);
		return vf;
	}
	
	/**
	 * Determines if the factory already contains a given part vertex.
	 * @param p The part to look for
	 * @param o The object to look for
	 * @return {@code true} if the vertex is present, {@code false} otherwise
	 */
	/*@ pure @*/ public boolean contains(Part p, Object o)
	{
		return m_vertices.contains(new PartVertex(p, o));
	}
}
